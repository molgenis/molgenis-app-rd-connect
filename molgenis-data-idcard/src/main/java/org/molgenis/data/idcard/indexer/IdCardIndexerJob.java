package org.molgenis.data.idcard.indexer;

import org.molgenis.data.DataService;
import org.molgenis.data.idcard.IdCardBiobankRepository;
import org.molgenis.data.idcard.IdCardRegistryRepository;
import org.molgenis.data.idcard.model.IdCardIndexingEvent;
import org.molgenis.data.idcard.model.IdCardIndexingEventFactory;
import org.molgenis.data.idcard.model.IdCardIndexingEventStatus;
import org.molgenis.data.idcard.settings.IdCardIndexerSettings;
import org.molgenis.security.core.runas.RunAsSystemProxy;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import static org.molgenis.data.idcard.model.IdCardIndexingEventMetaData.ID_CARD_INDEXING_EVENT;

@DisallowConcurrentExecution
public class IdCardIndexerJob implements Job
{
	private static final Logger LOG = LoggerFactory.getLogger(IdCardIndexerJob.class);

	public static final String JOB_USERNAME = "username";
	public static final String JOB_USERNAME_SYSTEM = "System";

	// Autowire by constructor not possible for Job classes
	@Autowired
	private IdCardBiobankRepository idCardBiobankRepository;

	@Autowired
	private IdCardRegistryRepository idCardRegistryRepository;

	@Autowired
	private IdCardIndexerSettings idCardIndexerSettings;

	@Autowired
	private DataService dataService;

	@Autowired
	private MailSender mailSender;

	@Autowired
	private IdCardIndexingEventFactory idCardIndexingEventFactory;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
	{
		try
		{
			LOG.info("Executing scheduled rebuild index job ...");
			String username = jobExecutionContext.getMergedJobDataMap().getString(JOB_USERNAME);
			rebuildIndex(username);
			LOG.info("Executed scheduled rebuild index job");
		}
		catch (Throwable t)
		{
			LOG.error("An error occured rebuilding index", t);
		}
	}

	private void rebuildIndex(String username)
	{
		IdCardIndexingEvent idCardIndexingEvent = idCardIndexingEventFactory.create();
		RuntimeException runtimeException = null;
		try
		{
			RunAsSystemProxy.runAsSystem(() ->
			{
				idCardBiobankRepository.rebuildIndex();
				idCardRegistryRepository.rebuildIndex();
			});
			idCardIndexingEvent.setStatus(IdCardIndexingEventStatus.SUCCESS);
			idCardIndexingEvent
					.setMessage(String.format("Index rebuild [%s]", username != null ? username : JOB_USERNAME_SYSTEM));
		}
		catch (RuntimeException e)
		{
			idCardIndexingEvent.setStatus(IdCardIndexingEventStatus.FAILED);
			idCardIndexingEvent.setMessage(e.getMessage());
			runtimeException = e;
		}

		RunAsSystemProxy.runAsSystem(() -> dataService.add(ID_CARD_INDEXING_EVENT, idCardIndexingEvent));

		if (idCardIndexingEvent.getStatus() == IdCardIndexingEventStatus.FAILED)
		{
			if (username == null)
			{
				mailSender.send(createMessage(idCardIndexingEvent));
			}
			if (runtimeException != null)
			{
				throw runtimeException;
			}
		}
	}

	private SimpleMailMessage createMessage(IdCardIndexingEvent idCardIndexingEvent)
	{
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(idCardIndexerSettings.getNotificationEmail());
		message.setReplyTo("no-reply@molgenis.org");
		message.setSubject("ID-Card index rebuild failed");
		message.setText("ID-Card index rebuild failed with message:\n" + idCardIndexingEvent.getMessage());
		return message;
	}
}