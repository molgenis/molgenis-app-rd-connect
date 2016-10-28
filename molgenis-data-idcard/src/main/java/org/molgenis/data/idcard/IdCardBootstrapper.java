package org.molgenis.data.idcard;

import org.molgenis.data.Entity;
import org.molgenis.data.idcard.indexer.IdCardIndexerService;
import org.molgenis.data.idcard.settings.IdCardIndexerSettings;
import org.molgenis.data.settings.SettingsEntityListener;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
public class IdCardBootstrapper implements ApplicationListener<ContextRefreshedEvent>, PriorityOrdered
{
	private static final Logger LOG = LoggerFactory.getLogger(IdCardBootstrapper.class);

	private final IdCardIndexerService idCardIndexerService;
	private final IdCardIndexerSettings idCardIndexerSettings;

	@Autowired
	public IdCardBootstrapper(IdCardIndexerService idCardIndexerService, IdCardIndexerSettings idCardIndexerSettings)
	{

		this.idCardIndexerService = requireNonNull(idCardIndexerService);
		this.idCardIndexerSettings = requireNonNull(idCardIndexerSettings);
	}

	private void bootstrap()
	{
		LOG.info("Bootstrapping RD-Connect application ...");
		idCardIndexerSettings.addListener(new SettingsEntityListener()
		{
			@Override
			public void postUpdate(Entity entity)
			{
				try
				{
					idCardIndexerService.updateIndexerScheduler(false);
				}
				catch (SchedulerException e)
				{
					throw new RuntimeException(e);
				}
			}
		});

		try
		{
			idCardIndexerService.updateIndexerScheduler(true);
		}
		catch (SchedulerException e)
		{
			throw new RuntimeException(e);
		}
		LOG.info("Bootstrapping RD-Connect application completed");
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event)
	{
		bootstrap();
	}

	@Override
	public int getOrder()
	{
		return PriorityOrdered.HIGHEST_PRECEDENCE + 1; // bootstrap application after MOLGENIS bootstrapper
	}
}
