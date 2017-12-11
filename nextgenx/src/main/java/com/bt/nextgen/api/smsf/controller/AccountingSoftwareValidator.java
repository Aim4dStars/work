package com.bt.nextgen.api.smsf.controller;


import com.bt.nextgen.api.smsf.model.AccountingSoftwareDto;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AccountingSoftwareValidator implements Validator
{
	@Override
	public boolean supports(Class <?> clazz)
	{
		return clazz.isAssignableFrom(AccountingSoftwareDto.class);
	}

	@Override
	public void validate(Object object, Errors errors)
	{
		AccountingSoftwareDto accountingSoftwareDto = (AccountingSoftwareDto) object;

		if (!(accountingSoftwareDto.getFeedStatus().equalsIgnoreCase(SoftwareFeedStatus.AWAITING.getDisplayValue().toString()) ||
			  accountingSoftwareDto.getFeedStatus().equalsIgnoreCase(SoftwareFeedStatus.MANUAL.getDisplayValue().toString()) ||
			  accountingSoftwareDto.getFeedStatus().equalsIgnoreCase(SoftwareFeedStatus.REQUESTED.getDisplayValue().toString() )))
		{
			errors.rejectValue("status", "Invalid accounting software status");
		}
	}
}
