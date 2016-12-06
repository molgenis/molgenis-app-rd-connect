package org.molgenis.data.idcard;

import org.molgenis.data.Entity;
import org.molgenis.data.Fetch;
import org.molgenis.data.Query;
import org.molgenis.data.RepositoryCapability;
import org.molgenis.data.aggregation.AggregateQuery;
import org.molgenis.data.aggregation.AggregateResult;
import org.molgenis.data.elasticsearch.ElasticsearchService;
import org.molgenis.data.support.AbstractRepository;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.molgenis.data.RepositoryCapability.*;

public abstract class IdCardBiobankOrRegistryRepository extends AbstractRepository
{
	private final ElasticsearchService elasticsearchService;

	IdCardBiobankOrRegistryRepository(ElasticsearchService elasticsearchService)
	{
		this.elasticsearchService = requireNonNull(elasticsearchService);
	}

	@Override
	public Set<RepositoryCapability> getCapabilities()
	{
		Set<RepositoryCapability> repoCapabilities = new HashSet<>();
		repoCapabilities.add(AGGREGATEABLE);
		repoCapabilities.add(QUERYABLE);
		return repoCapabilities;
	}

	@Override
	public long count(Query<Entity> q)
	{
		return elasticsearchService.count(q, getEntityType());
	}

	@Override
	public Stream<Entity> findAll(Query<Entity> q)
	{
		return elasticsearchService.searchAsStream(q, getEntityType());
	}

	@Override
	public Entity findOne(Query<Entity> q)
	{
		Iterator<Entity> it = findAll(q).iterator();
		return it.hasNext() ? it.next() : null;
	}

	@Override
	public Entity findOneById(Object id, Fetch fetch)
	{
		return findOneById(id);
	}

	@Override
	public AggregateResult aggregate(AggregateQuery aggregateQuery)
	{
		return elasticsearchService.aggregate(aggregateQuery, getEntityType());
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
