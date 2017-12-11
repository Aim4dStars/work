package com.bt.nextgen.service.avaloq.payeedetails;

import java.math.BigDecimal;

import com.bt.nextgen.service.integration.account.VersionedObjectIdentifier;

public class VersionedObjectIdentifierImpl implements VersionedObjectIdentifier
{
	private BigDecimal modificationIdentifier;

	public BigDecimal getModificationIdentifier()
	{
		return modificationIdentifier;
	}

	public void setModificationIdentifier(BigDecimal modificationIdentifier)
	{
		this.modificationIdentifier = modificationIdentifier;
	}
}
