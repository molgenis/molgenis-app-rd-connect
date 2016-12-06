package org.molgenis.data.idcard.model;

import org.molgenis.data.AbstractSystemEntityFactory;
import org.molgenis.data.populate.EntityPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdCardRegistryFactory extends AbstractSystemEntityFactory<IdCardRegistry, IdCardRegistryMetadata, Integer>
{
	@Autowired
	IdCardRegistryFactory(IdCardRegistryMetadata idCardRegistryMetadata, EntityPopulator entityPopulator)
	{
		super(IdCardRegistry.class, idCardRegistryMetadata, entityPopulator);
	}
}
