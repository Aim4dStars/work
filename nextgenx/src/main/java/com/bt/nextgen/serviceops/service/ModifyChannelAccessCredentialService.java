package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.service.ServiceErrors;

public interface ModifyChannelAccessCredentialService
{
	boolean blockUserAccess(String credentialId, ServiceErrors serviceErrors);

	boolean unblockUserAccess(String credentialId, ServiceErrors serviceErrors);
	
	String unblockUserAccessWithResetPassword(String credentialId, ServiceErrors serviceErrors);
}
