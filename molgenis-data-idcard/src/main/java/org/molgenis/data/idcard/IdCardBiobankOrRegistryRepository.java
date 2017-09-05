package org.molgenis.data.idcard;

import org.molgenis.data.Entity;
import org.molgenis.data.Fetch;
import org.molgenis.data.Query;
import org.molgenis.data.RepositoryCapability;
import org.molgenis.data.aggregation.AggregateQuery;
import org.molgenis.data.aggregation.AggregateResult;
import org.molgenis.data.index.SearchService;
import org.molgenis.data.support.AbstractRepository;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static org.molgenis.data.RepositoryCapability.WRITABLE;

public abstract class IdCardBiobankOrRegistryRepository extends AbstractRepository
{
	private static final Set<RepositoryCapability> REPOSITORY_CAPABILITIES = unmodifiableSet(
			EnumSet.of(RepositoryCapability.AGGREGATEABLE, RepositoryCapability.QUERYABLE));

	private final SearchService searchService;

	IdCardBiobankOrRegistryRepository(SearchService searchService)
	{
		this.searchService = requireNonNull(searchService);
	}

	@Override
	public Set<RepositoryCapability> getCapabilities()
	{
		return REPOSITORY_CAPABILITIES;
	}

	@Override
	public long count(Query<Entity> q)
	{
		return searchService.count(getEntityType(), q);
	}

	@Override
	public Stream<Entity> findAll(Query<Entity> q)
	{
		Stream<Object> entityIds = searchService.search(getEntityType(), q);
		return entityIds.map(this::findOneById);
	}

	@Override
	public Entity findOne(Query<Entity> q)
	{
		Object entityId = searchService.searchOne(getEntityType(), q);
		return findOneById(entityId);
	}

	@Override
	public Entity findOneById(Object id, Fetch fetch)
	{
		return findOneById(id);
	}

	@Override
	public AggregateResult aggregate(AggregateQuery aggregateQuery)
	{
		return searchService.aggregate(getEntityType(), aggregateQuery);
	}

	@Override
	public void update(Entity entity)
	{
		throw new UnsupportedOperationException(
				String.format("Repository [%s] is not %s", getName(), WRITABLE.toString()));
	}

	@Override
	public void delete(Entity entity)
	{
		throw new UnsupportedOperationException(
				String.format("Repository [%s] is not %s", getName(), WRITABLE.toString()));
	}

	@Override
	public void deleteById(Object id)
	{
		throw new UnsupportedOperationException(
				String.format("Repository [%s] is not %s", getName(), WRITABLE.toString()));
	}

	@Override
	public void deleteAll(Stream<Object> ids)
	{
		throw new UnsupportedOperationException(
				String.format("Repository [%s] is not %s", getName(), WRITABLE.toString()));
	}

	@Override
	public void deleteAll()
	{
		throw new UnsupportedOperationException(
				String.format("Repository [%s] is not %s", getName(), WRITABLE.toString()));
	}

	@Override
	public void add(Entity entity)
	{
		throw new UnsupportedOperationException(
				String.format("Repository [%s] is not %s", getName(), WRITABLE.toString()));
	}

	public abstract void rebuildIndex();
}
