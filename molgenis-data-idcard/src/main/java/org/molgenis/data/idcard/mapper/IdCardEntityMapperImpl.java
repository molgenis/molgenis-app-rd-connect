package org.molgenis.data.idcard.mapper;

import com.google.gson.stream.JsonReader;
import org.molgenis.data.idcard.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Component
public class IdCardEntityMapperImpl implements IdCardEntityMapper
{
	private static final Logger LOG = LoggerFactory.getLogger(IdCardEntityMapperImpl.class);

	private final IdCardBiobankFactory idCardBiobankFactory;
	private final IdCardRegistryFactory idCardRegistryFactory;

	@Autowired
	public IdCardEntityMapperImpl(IdCardBiobankFactory idCardBiobankFactory,
			IdCardRegistryFactory idCardRegistryFactory)
	{
		this.idCardBiobankFactory = requireNonNull(idCardBiobankFactory);
		this.idCardRegistryFactory = requireNonNull(idCardRegistryFactory);
	}

	@Override
	public IdCardBiobank toIdCardBiobank(JsonReader jsonReader) throws IOException
	{
		IdCardBiobank idCardBiobank = idCardBiobankFactory.create();
		populateIdCardBiobankOrRegistry(idCardBiobank, jsonReader);
		return idCardBiobank;
	}

	@Override
	public IdCardRegistry toIdCardRegistry(JsonReader jsonReader) throws IOException
	{
		IdCardRegistry idCardRegistry = idCardRegistryFactory.create();
		populateIdCardBiobankOrRegistry(idCardRegistry, jsonReader);
		return idCardRegistry;
	}

	private void populateIdCardBiobankOrRegistry(IdCardBiobankOrRegistry idCardBiobankOrRegistry, JsonReader jsonReader)
			throws IOException
	{
		jsonReader.beginObject();
		while (jsonReader.hasNext())
		{
			String name = jsonReader.nextName();
			switch (name)
			{
				case "Collections":
					jsonReader.skipValue(); // not used at the moment
					break;
				case "OrganizationID":
					idCardBiobankOrRegistry.setOrganizationId(jsonReader.nextString());
					break;
				case "idcardurl":
					idCardBiobankOrRegistry.setIdCardUrl(jsonReader.nextString());
					break;
				case "address":
					jsonReader.beginObject();
					while (jsonReader.hasNext())
					{
						switch (jsonReader.nextName())
						{
							case "name of host institution":
								idCardBiobankOrRegistry.setNameOfHostInstitution(jsonReader.nextString());
								break;
							case "country":
								idCardBiobankOrRegistry.setCountry(jsonReader.nextString());
								break;
							case "city":
								idCardBiobankOrRegistry.setCity(jsonReader.nextString());
								break;
							default:
								jsonReader.skipValue();
								break;
						}
					}
					jsonReader.endObject();
					break;
				case "name":
					idCardBiobankOrRegistry.setName(jsonReader.nextString());
					break;
				default:
					LOG.debug("Ignored property [{}] in root object", name);
					jsonReader.skipValue();
					break;
			}
		}
		jsonReader.endObject();
	}

	@Override
	public Iterable<IdCardBiobank> toIdCardBiobanks(JsonReader jsonReader) throws IOException
	{
		List<IdCardBiobank> idCardBiobanks = new ArrayList<>();

		jsonReader.beginArray();
		while (jsonReader.hasNext())
		{
			idCardBiobanks.add(toIdCardBiobank(jsonReader));
		}
		jsonReader.endArray();

		return idCardBiobanks;
	}

	@Override
	public Iterable<IdCardRegistry> toIdCardRegistries(JsonReader jsonReader) throws IOException
	{
		List<IdCardRegistry> idCardRegistries = new ArrayList<>();

		jsonReader.beginArray();
		while (jsonReader.hasNext())
		{
			idCardRegistries.add(toIdCardRegistry(jsonReader));
		}
		jsonReader.endArray();

		return idCardRegistries;
	}

	@Override
	public IdCardOrganization toIdCardOrganization(JsonReader jsonReader) throws IOException
	{
		IdCardOrganization idCardOrganization = new IdCardOrganization();

		jsonReader.beginObject();
		while (jsonReader.hasNext())
		{
			String name = jsonReader.nextName();
			switch (name)
			{
				case "Collections":
					jsonReader.skipValue(); // not used at the moment
					break;
				case "name":
					idCardOrganization.setName(jsonReader.nextString());
					break;
				case "ID":
					idCardOrganization.setId(jsonReader.nextString());
					break;
				case "OrganizationID":
					idCardOrganization.setOrganizationId(jsonReader.nextString());
					break;
				case "type":
					idCardOrganization.setType(jsonReader.nextString());
					break;
				default:
					LOG.warn("unknown property [{}] in root object", name);
					jsonReader.skipValue();
					break;
			}
		}
		jsonReader.endObject();

		return idCardOrganization;
	}

	@Override
	public Iterable<IdCardOrganization> toIdCardOrganizations(JsonReader jsonReader) throws IOException
	{
		List<IdCardOrganization> idCardOrganizations = new ArrayList<>();

		jsonReader.beginArray();
		while (jsonReader.hasNext())
		{
			idCardOrganizations.add(toIdCardOrganization(jsonReader));
		}
		jsonReader.endArray();

		return idCardOrganizations;
	}
}
