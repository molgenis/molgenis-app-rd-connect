package org.molgenis.data.idcard.mapper;

import com.google.gson.stream.JsonReader;
import org.molgenis.data.idcard.model.IdCardBiobank;
import org.molgenis.data.idcard.model.IdCardOrganization;
import org.molgenis.data.idcard.model.IdCardRegistry;

import java.io.IOException;

public interface IdCardEntityMapper
{
	IdCardBiobank toIdCardBiobank(JsonReader jsonReader) throws IOException;

	Iterable<IdCardBiobank> toIdCardBiobanks(JsonReader jsonReader) throws IOException;

	IdCardRegistry toIdCardRegistry(JsonReader jsonReader) throws IOException;

	Iterable<IdCardRegistry> toIdCardRegistries(JsonReader jsonReader) throws IOException;

	IdCardOrganization toIdCardOrganization(JsonReader jsonReader) throws IOException;

	Iterable<IdCardOrganization> toIdCardOrganizations(JsonReader jsonReader) throws IOException;
}
