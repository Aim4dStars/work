package com.bt.nextgen.service.avaloq.domain;

import java.util.List;

import com.bt.nextgen.core.validation.ValidationError;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.domain.ClientDetailsUpdate;

public class GSTStatus implements ClientDetailsUpdate
{

	private PersonKey personKey;
	private String personModificationNumber;
	private String gstStatusId;
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

	public String getGstStatusId()
	{
		return gstStatusId;
	}

	public void setGstStatusId(String gstStatusId)
	{
		this.gstStatusId = gstStatusId;
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
