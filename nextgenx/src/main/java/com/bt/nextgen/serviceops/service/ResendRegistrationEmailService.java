package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.service.ServiceErrors;

public interface ResendRegistrationEmailService
{
	String resendRegistrationEmailForInvestor(String clientId, String gcmId, String role, ServiceErrors serviceErrors);
	
	String resendRegistrationEmailForAdviser(String clientId, String gcmId, String role, ServiceErrors serviceErrors);

	String resendRegistrationEmailWithExistingCodeForInvestor(String clientId, String gcmId, String role, ServiceErrors serviceErrors);

	String resendRegistrationEmailWithExistingCodeForAdviser(String clientId, String gcmId, String role, ServiceErrors serviceErrors);
}
