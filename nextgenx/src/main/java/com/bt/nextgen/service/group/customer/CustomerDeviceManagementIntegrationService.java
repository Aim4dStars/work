package com.bt.nextgen.service.group.customer;

import com.bt.nextgen.service.ServiceErrors;

public interface CustomerDeviceManagementIntegrationService {

	CustomerCredentialManagementInformation updateUserMobileNumber(String mobileNumber, String safiDeviceId, String gcmId,
																		  String deviceProvisioningStatus, ServiceErrors serviceErrors);

	CustomerCredentialManagementInformation unBlockMobile(String userId, String safiDeviceId, String employeeId,
																 ServiceErrors serviceErrors);
}
