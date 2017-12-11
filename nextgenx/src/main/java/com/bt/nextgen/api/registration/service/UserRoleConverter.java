package com.bt.nextgen.api.registration.service;


import com.bt.nextgen.api.registration.model.UserRoleDto;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;

import java.util.Date;

public final class UserRoleConverter
{
	private UserRoleConverter()
	{

	}

	public static UserRoleTermsAndConditions toUserRoleTermsAndConditions(UserRoleDto userRoleDto)
	{
		UserRoleTermsAndConditionsKey userRoleKey = new UserRoleTermsAndConditionsKey(userRoleDto.getGcmId(), userRoleDto.getJobProfileId());

		UserRoleTermsAndConditions userRoleTermsAndConditions = new UserRoleTermsAndConditions();
		userRoleTermsAndConditions.setUserRoleTermsAndConditionsKey(userRoleKey);
		userRoleTermsAndConditions.setVersion(1);
		userRoleTermsAndConditions.setTncAcceptedOn(new Date());
		userRoleTermsAndConditions.setTncAccepted(userRoleDto.getAccepted());
		userRoleTermsAndConditions.setModifyDatetime(new Date());

		return userRoleTermsAndConditions;
	}
}