package com.bt.nextgen.service.group.customer;

import com.bt.nextgen.service.ServiceErrors;

public interface CustomerCredentialManagementIntegrationService
{
	CustomerCredentialManagementInformation refreshCredential(String websealAppServerId, ServiceErrors errors);
}
