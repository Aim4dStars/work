package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;

public interface UserAccountStatusService
{
	UserAccountStatusModel lookupStatus(String userId, String safiDeviceId, boolean migratedCustomer);

	String createAccount(ServiceOpsModel serviceOps);
	
}
