package com.bt.nextgen.service.integration.registration.repository;


import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;

public interface UserRoleTermsAndConditionsRepository
{
	public UserRoleTermsAndConditions find(UserRoleTermsAndConditionsKey key);

	public void save(UserRoleTermsAndConditions userRole);
}