package com.bt.nextgen.service.avaloq.domain;

import java.util.List;

import com.bt.nextgen.core.validation.ValidationError;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.domain.ClientDetailsUpdate;

public class TaxImpl implements ClientDetailsUpdate
{
	private PersonKey personKey;
	private String personModificationNumber;
	private String countryTaxId;
	private String tfn;
	private String tfnExemptId;
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

	public String getCountryTaxId()
	{
		return countryTaxId;
	}

	public void setCountryTaxId(String countryTaxId)
	{
		this.countryTaxId = countryTaxId;
	}

	public String getTfn()
	{
		return tfn;
	}

	public void setTfn(String tfn)
	{
		this.tfn = tfn;
	}

	public String getTfnExemptId()
	{
		return tfnExemptId;
	}

	public void setTfnExemptId(String tfnExemptId)
	{
		this.tfnExemptId = tfnExemptId;
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
