package org.molgenis.data.idcard.model;

import org.molgenis.data.Entity;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.StaticEntity;

import static org.molgenis.data.idcard.model.IdCardBiobankOrRegistryMetadata.*;

public abstract class IdCardBiobankOrRegistry extends StaticEntity
{
	public IdCardBiobankOrRegistry(Entity entity)
	{
		super(entity);
	}

	public IdCardBiobankOrRegistry(EntityType entityType)
	{
		super(entityType);
	}

	public IdCardBiobankOrRegistry(Integer identifier, EntityType entityType)
	{
		super(entityType);
		set(ORGANIZATION_ID, identifier);
	}

	public String getName()
	{
		return getString(NAME);
	}

	public void setName(String name)
	{
		set(NAME, name);
	}

	public String getNameOfHostInstitution()
	{
		return getString(NAME_OF_HOST_INSTITUTION);
	}

	public void setNameOfHostInstitution(String nameOfHostInstitution)
	{
		set(NAME_OF_HOST_INSTITUTION, nameOfHostInstitution);
	}

	public String getCity()
	{
		return getString(CITY);
	}

	public void setCity(String city)
	{
		set(CITY, city);
	}

	public String getCountry()
	{
		return getString(COUNTRY);
	}

	public void setCountry(String country)
	{
		set(COUNTRY, country);
	}

	public String getIdCardUrl()
	{
		return getString(ID_CARD_URL);
	}

	public void setIdCardUrl(String idCardUrl)
	{
		set(ID_CARD_URL, idCardUrl);
	}

	public String getOrganizationId()
	{
		return getString(ORGANIZATION_ID);
	}

	public void setOrganizationId(String organizationId)
	{
		set(ORGANIZATION_ID, organizationId);
	}
}
