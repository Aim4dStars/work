package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.service.ServiceErrors;

public interface ResetPasswordService
{
	/**
	 * @param credentialId
	 * @return temporary password generated for the user, otherwise returns null value
	 */
	public String resetPassword(String credentialId, String gcmId, ServiceErrors serviceErrors);

}
