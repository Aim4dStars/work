package com.bt.nextgen.service.avaloq.domain;

import java.util.List;

import com.bt.nextgen.core.validation.ValidationError;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.domain.AddressUpdate;
import com.bt.nextgen.service.integration.domain.ClientDetailsUpdate;

public class ClientContactDetails implements ClientDetailsUpdate
{
	private PersonKey personKey;
	private String personModificationNumber;
	private List <AddressUpdate> addressDetails;
	private List <ValidationError> validationErrors;

	public PersonKey getPersonKey()
	{
		return personKey;
	}

	public void setPersonKey(PersonKey personKey)
	{
		this.personKey = personKey;
	}

	public String getPersonModificationNumber()
	{
		return personModificationNumber;
	}

	public void setPersonModificationNumber(String personModificationNumber)
	{
		this.personModificationNumber = personModificationNumber;
	}

	public List <AddressUpdate> getAddressDetails()
	{
		return addressDetails;
	}

	public void setAddressDetails(List <AddressUpdate> addressDetails)
	{
		this.addressDetails = addressDetails;
	}

	public List <ValidationError> getValidationErrors()
	{
		return validationErrors;
	}

	public void setValidationErrors(List <ValidationError> validationErrors)
	{
		this.validationErrors = validationErrors;
	}

}
