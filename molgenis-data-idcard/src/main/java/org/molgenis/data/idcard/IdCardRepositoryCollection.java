package org.molgenis.data.idcard;

import com.google.common.collect.Maps;
import org.molgenis.data.*;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.AbstractRepositoryCollection;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.immutableEnumSet;
import static java.util.Objects.requireNonNull;
import static org.molgenis.data.RepositoryCollectionCapability.WRITABLE;
import static org.molgenis.data.idcard.model.IdCardBiobankMetadata.ID_CARD_BIOBANK;
import static org.molgenis.data.idcard.model.IdCardRegistryMetadata.ID_CARD_REGISTRY;
import static org.molgenis.data.meta.MetaUtils.getEntityTypeFetch;
import static org.molgenis.data.meta.model.EntityTypeMetadata.BACKEND;
import static org.molgenis.data.meta.model.EntityTypeMetadata.ENTITY_TYPE_META_DATA;

@Component
public class IdCardRepositoryCollection extends AbstractRepositoryCollection
{
	public static final String NAME = "ID-Card";

	private final IdCardBiobankRepository idCardBiobankRepository;
	private final IdCardRegistryRepository idCardRegistryRepository;
	private final Map<String, Repository<Entity>> repositories;

	private final DataService dataService;

	public IdCardRepositoryCollection(IdCardBiobankRepository idCardBiobankRepository,
			IdCardRegistryRepository idCardRegistryRepository, DataService dataService)
	{
		this.idCardBiobankRepository = requireNonNull(idCardBiobankRepository);
		this.idCardRegistryRepository = requireNonNull(idCardRegistryRepository);
		this.dataService = requireNonNull(dataService);

		this.repositories = Maps.newLinkedHashMap();
		repositories.put(ID_CARD_BIOBANK, idCardBiobankRepository);
		repositories.put(ID_CARD_REGISTRY, idCardRegistryRepository);
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public Set<RepositoryCollectionCapability> getCapabilities()
	{
		return immutableEnumSet(EnumSet.of(WRITABLE));
	}

	@Override
	public Repository<Entity> createRepository(EntityType entityType)
	{
		String entityName = entityType.getFullyQualifiedName();
		if (!entityName.equals(ID_CARD_BIOBANK) && !entityName.equals(ID_CARD_REGISTRY))
		{
			throw new MolgenisDataException(String.format("Not a valid backend for entity [%s]", entityName));
		}
		return entityName.equals(ID_CARD_BIOBANK) ? idCardBiobankRepository : idCardRegistryRepository;
	}

	@Override
	public Iterable<String> getEntityIds()
	{
		return dataService.query(ENTITY_TYPE_META_DATA, EntityType.class).eq(BACKEND, NAME)
				.fetch(getEntityTypeFetch()).findAll().map(EntityType::getId)::iterator;
	}

	@Override
	public Repository<Entity> getRepository(String name)
	{
		return repositories.get(name);
	}

	@Override
	public Repository<Entity> getRepository(EntityType entityType)
	{
		return getRepository(entityType.getFullyQualifiedName());
	}

	@Override
	public boolean hasRepository(String name)
	{
		return repositories.containsKey(name);
	}

	@Override
	public Iterator<Repository<Entity>> iterator()
	{
		return repositories.values().iterator();
	}

	@Override
	public void deleteRepository(EntityType entityType)
	{
		repositories.remove(entityType.getFullyQualifiedName());
	}

	@Override
	public boolean hasRepository(EntityType entityType)
	{
		return hasRepository(entityType.getFullyQualifiedName());
	}
}
