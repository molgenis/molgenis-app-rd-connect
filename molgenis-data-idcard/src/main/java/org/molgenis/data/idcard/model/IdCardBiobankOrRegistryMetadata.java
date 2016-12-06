package org.molgenis.data.idcard.model;

import org.molgenis.data.idcard.IdCardRepositoryCollection;
import org.molgenis.data.meta.SystemEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;
import static org.molgenis.data.idcard.model.IdCardPackage.PACKAGE_ID_CARD;
import static org.molgenis.data.meta.AttributeType.HYPERLINK;
import static org.molgenis.data.meta.model.EntityType.AttributeRole.*;
import static org.molgenis.data.meta.model.Package.PACKAGE_SEPARATOR;

@Component
public class IdCardBiobankOrRegistryMetadata extends SystemEntityType
{
	private static final String SIMPLE_NAME = "BiobankOrRegistry";
	public static final String ID_CARD_BIOBANK = PACKAGE_ID_CARD + PACKAGE_SEPARATOR + SIMPLE_NAME;

	public static final String NAME = "name";
	public static final String NAME_OF_HOST_INSTITUTION = "nameOfHostInstitution";
	public static final String CITY = "city";
	public static final String COUNTRY = "country";
	public static final String ID_CARD_URL = "idCardUrl";
	public static final String ORGANIZATION_ID = "organizationId";

	private final IdCardPackage idCardPackage;

	@Autowired
	IdCardBiobankOrRegistryMetadata(IdCardPackage idCardPackage)
	{
		super(SIMPLE_NAME, PACKAGE_ID_CARD);
		this.idCardPackage = requireNonNull(idCardPackage);
	}

	@Override
	public void init()
	{
		setPackage(idCardPackage);
		setAbstract(true);

		setBackend(IdCardRepositoryCollection.NAME);
		setLabel("Biobank/Registry");
		setDescription("Biobank/Registry data from ID-Card");

		addAttribute(NAME, ROLE_LABEL, ROLE_LOOKUP).setLabel("Name");
		addAttribute(NAME_OF_HOST_INSTITUTION).setLabel("Host institution");
		addAttribute(CITY).setLabel("City");
		addAttribute(COUNTRY).setLabel("Country");
		addAttribute(ID_CARD_URL).setLabel("ID Card").setDataType(HYPERLINK);
		addAttribute(ORGANIZATION_ID, ROLE_ID).setLabel("OrganizationID").setVisible(false);
	}
}