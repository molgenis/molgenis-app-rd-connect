package org.molgenis.data.idcard;

import org.molgenis.data.Entity;
import org.molgenis.data.idcard.client.IdCardClient;
import org.molgenis.data.idcard.model.IdCardBiobank;
import org.molgenis.data.idcard.model.IdCardBiobankFactory;
import org.molgenis.data.idcard.model.IdCardBiobankMetadata;
import org.molgenis.data.idcard.settings.IdCardIndexerSettings;
import org.molgenis.data.index.IndexService;
import org.molgenis.data.index.SearchService;
import org.molgenis.data.meta.model.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Iterator;

import static java.util.Objects.requireNonNull;
import static java.util.stream.StreamSupport.stream;

@Repository
public class IdCardBiobankRepository extends IdCardBiobankOrRegistryRepository
{
	private static final Logger LOG = LoggerFactory.getLogger(IdCardBiobankRepository.class);

	private final IdCardBiobankMetadata idCardBiobankMetadata;
	private final IdCardClient idCardClient;
	private final IndexService indexService;
	private final IdCardIndexerSettings idCardIndexerSettings;
	private final IdCardBiobankFactory idCardBiobankFactory;

	@Autowired
	public IdCardBiobankRepository(IdCardBiobankMetadata idCardBiobankMetadata, IdCardClient idCardClient,
			IndexService indexService, SearchService searchService, IdCardIndexerSettings idCardIndexerSettings,
			IdCardBiobankFactory idCardBiobankFactory)
	{
		super(searchService);
		this.idCardBiobankMetadata = idCardBiobankMetadata;
		this.idCardClient = requireNonNull(idCardClient);
		this.indexService = requireNonNull(indexService);
		this.idCardIndexerSettings = requireNonNull(idCardIndexerSettings);
		this.idCardBiobankFactory = requireNonNull(idCardBiobankFactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Entity> iterator()
	{
		return (Iterator<Entity>) ((Iterator<?>) idCardClient.getIdCardBiobanks().iterator());
	}

	public EntityType getEntityType()
	{
		return idCardBiobankMetadata;
	}

	@Override
	public Entity findOneById(Object id)
	{
		try
		{
			return idCardClient.getIdCardBiobank(id.toString());
		}
		catch (RuntimeException e)
		{
			return createErrorIdCardBiobank(id);
		}
	}

	@Override
	public void rebuildIndex()
	{
		LOG.trace("Indexing ID-Card biobanks ...");
		Iterable<? extends Entity> entities = idCardClient
				.getIdCardBiobanks(idCardIndexerSettings.getIndexRebuildTimeout());

		EntityType entityType = getEntityType();
		if (!indexService.hasIndex(entityType))
		{
			indexService.createIndex(entityType);
		}
		indexService.index(entityType, stream(entities.spliterator(), false));
		LOG.debug("Indexed ID-Card biobanks");
	}

	private IdCardBiobank createErrorIdCardBiobank(Object id)
	{
		IdCardBiobank idCardBiobank = idCardBiobankFactory.create(Integer.valueOf(id.toString()));
		idCardBiobank.setName("Error loading data");
		return idCardBiobank;
	}
}
