package org.molgenis.data.idcard.model;

import org.molgenis.data.idcard.IdCardRepositoryCollection;
import org.molgenis.data.meta.SystemEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;
import static org.molgenis.data.idcard.model.IdCardPackage.PACKAGE_ID_CARD;
import static org.molgenis.data.meta.model.Package.PACKAGE_SEPARATOR;

@Component
public class IdCardBiobankMetadata extends SystemEntityType
{
	private static final String SIMPLE_NAME = "Biobank";
	public static final String ID_CARD_BIOBANK = PACKAGE_ID_CARD + PACKAGE_SEPARATOR + SIMPLE_NAME;

	private final IdCardPackage idCardPackage;
	private final IdCardBiobankOrRegistryMetadata idCardBiobankOrRegistryMetadata;

	@Autowired
	IdCardBiobankMetadata(IdCardPackage idCardPackage, IdCardBiobankOrRegistryMetadata idCardBiobankOrRegistryMetadata)
	{
		super(SIMPLE_NAME, PACKAGE_ID_CARD);
		this.idCardPackage = requireNonNull(idCardPackage);
		this.idCardBiobankOrRegistryMetadata = idCardBiobankOrRegistryMetadata;
	}

	@Override
	public void init()
	{
		setPackage(idCardPackage);
		setExtends(idCardBiobankOrRegistryMetadata);

		setBackend(IdCardRepositoryCollection.NAME);
		setLabel("Biobank");
		setDescription("Biobank from ID-Card");
	}
}
