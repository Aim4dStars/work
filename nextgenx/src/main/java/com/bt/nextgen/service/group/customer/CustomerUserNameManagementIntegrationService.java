package com.bt.nextgen.service.group.customer;

import com.bt.nextgen.service.ServiceErrors;

public interface CustomerUserNameManagementIntegrationService
{
	CustomerCredentialManagementInformation updateUsername(CustomerUsernameUpdateRequest usernameRequest, ServiceErrors errors);

	CustomerCredentialManagementInformation createUsername(CustomerUsernameUpdateRequest usernameRequest, ServiceErrors errors);

     //   TODO implement these methods on service 0310
    CustomerCredentialManagementInformation blockUser(String credentialId, ServiceErrors errors);

	CustomerCredentialManagementInformation unblockUser(String credentialId, boolean isResetPassword, ServiceErrors errors);

}
