package org.molgenis.data.idcard;

import org.molgenis.data.Entity;
import org.molgenis.data.idcard.client.IdCardClient;
import org.molgenis.data.idcard.model.IdCardRegistry;
import org.molgenis.data.idcard.model.IdCardRegistryFactory;
import org.molgenis.data.idcard.model.IdCardRegistryMetadata;
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
public class IdCardRegistryRepository extends IdCardBiobankOrRegistryRepository
{
	private static final Logger LOG = LoggerFactory.getLogger(IdCardRegistryRepository.class);

	private final IdCardRegistryMetadata idCardRegistryMetadata;
	private final IdCardClient idCardClient;
	private final IndexService indexService;
	private final IdCardIndexerSettings idCardIndexerSettings;
	private final IdCardRegistryFactory idCardRegistryFactory;

	@Autowired
	public IdCardRegistryRepository(IdCardRegistryMetadata idCardRegistryMetadata,
			IdCardRegistryFactory idCardRegistryFactory, IdCardClient idCardClient, IndexService indexService,
			SearchService searchService, IdCardIndexerSettings idCardIndexerSettings)
	{
		super(searchService);
		this.idCardRegistryMetadata = requireNonNull(idCardRegistryMetadata);
		this.idCardRegistryFactory = requireNonNull(idCardRegistryFactory);
		this.idCardClient = requireNonNull(idCardClient);
		this.indexService = requireNonNull(indexService);
		this.idCardIndexerSettings = requireNonNull(idCardIndexerSettings);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Entity> iterator()
	{
		return (Iterator<Entity>) ((Iterator<?>) idCardClient.getIdCardRegistries().iterator());
	}

	public EntityType getEntityType()
	{
		return idCardRegistryMetadata;
	}

	@Override
	public Entity findOneById(Object id)
	{
		try
		{
			return idCardClient.getIdCardRegistry(id.toString());
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
		Iterable<? extends Entity> entities = idCardClient.getIdCardRegistries(
				idCardIndexerSettings.getIndexRebuildTimeout());

		EntityType entityType = getEntityType();
		if (!indexService.hasIndex(entityType))
		{
			indexService.createIndex(entityType);
		}
		indexService.index(entityType, stream(entities.spliterator(), false));
		LOG.debug("Indexed ID-Card biobanks");
	}

	private IdCardRegistry createErrorIdCardBiobank(Object id)
	{
		IdCardRegistry idCardRegistry = idCardRegistryFactory.create(Integer.valueOf(id.toString()));
		idCardRegistry.setName("Error loading data");
		return idCardRegistry;
	}
}
