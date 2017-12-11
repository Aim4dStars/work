package com.bt.nextgen.service.integration.domain;

import java.util.List;

import com.bt.nextgen.core.validation.ValidationError;
import com.btfin.panorama.core.security.integration.account.PersonKey;

public interface ClientDetailsUpdate
{
	public void setPersonKey(PersonKey personKey);

	public PersonKey getPersonKey();

	public void setPersonModificationNumber(String personModificationNumber);

	public String getPersonModificationNumber();

	public List <ValidationError> getValidationErrors();

	public void setValidationErrors(List <ValidationError> validationErrors);
}
