package org.molgenis.data.idcard.client;

import org.molgenis.data.idcard.model.IdCardBiobank;
import org.molgenis.data.idcard.model.IdCardRegistry;

/**
 * ID-Cards: http://rd-connect.eu/platform/biobanks/id-cards-linking-up-rare-disease-research-across-the-world/
 */
public interface IdCardClient
{
	Iterable<IdCardBiobank> getIdCardBiobanks();

	Iterable<IdCardBiobank> getIdCardBiobanks(long timeout);

	Iterable<IdCardRegistry> getIdCardRegistries();

	Iterable<IdCardRegistry> getIdCardRegistries(long timeout);

	IdCardBiobank getIdCardBiobank(String id);

	IdCardRegistry getIdCardRegistry(String id);
}