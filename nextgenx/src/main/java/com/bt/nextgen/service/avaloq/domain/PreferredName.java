package com.bt.nextgen.service.avaloq.domain;

import java.util.List;

import com.bt.nextgen.core.validation.ValidationError;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.domain.ClientDetailsUpdate;

public class PreferredName implements ClientDetailsUpdate
{
	private PersonKey personKey;
	private String personModificationNumber;
	private String preferredName;
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

	public String getPreferredName()
	{
		return preferredName;
	}

	public void setPreferredName(String preferredName)
	{
		this.preferredName = preferredName;
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
