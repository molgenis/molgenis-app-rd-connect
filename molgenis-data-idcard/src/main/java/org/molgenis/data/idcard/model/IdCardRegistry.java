package org.molgenis.data.idcard.model;

import org.molgenis.data.Entity;
import org.molgenis.data.meta.model.EntityType;

import static org.molgenis.data.idcard.model.IdCardBiobankOrRegistryMetadata.ORGANIZATION_ID;

public class IdCardRegistry extends IdCardBiobankOrRegistry
{
	public IdCardRegistry(Entity entity)
	{
		super(entity);
	}

	public IdCardRegistry(EntityType entityType)
	{
		super(entityType);
	}

	public IdCardRegistry(Integer identifier, EntityType entityType)
	{
		super(entityType);
		set(ORGANIZATION_ID, identifier);
	}
}
