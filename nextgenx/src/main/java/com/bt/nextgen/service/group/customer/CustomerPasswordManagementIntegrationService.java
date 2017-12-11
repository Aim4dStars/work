package com.bt.nextgen.service.group.customer;

import com.bt.nextgen.service.ServiceErrors;

public interface CustomerPasswordManagementIntegrationService
{
	CustomerCredentialManagementInformation updatePassword(CustomerPasswordUpdateRequest usernameRequest, ServiceErrors errors);

}
