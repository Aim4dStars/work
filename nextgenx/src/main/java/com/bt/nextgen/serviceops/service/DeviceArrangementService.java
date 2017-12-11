package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;

public interface DeviceArrangementService
{
	boolean isDeviceDetailsFound(String deviceIdentifier);

	boolean updateUserMobileNumber(String mobileNumber, String customerId, String safiDeviceId, String employeeId,String clientId,
		UserAccountStatusModel userAccountStatusModel, ServiceErrors serviceErrors);

	public String unBlockMobile(ServiceOpsModel serviceOpsModel, String employeeId, ServiceErrors serviceErrors);
	
	boolean confirmMobileNumber(String mobileNumber, String customerId, String safiDeviceId, String employeeId,String clientId,
		UserAccountStatusModel customerEAMStatus, ServiceErrors serviceErrors);
}
