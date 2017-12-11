package com.bt.nextgen.core.service;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;

import java.util.List;

public interface CredentialService
{
	/**
	 * Return the user name of the client passed.
	 * @return string value
	 */
	String getUserName(String clientId, ServiceErrors serviceErrors) throws Exception;

	UserAccountStatusModel lookupStatus(String userId, ServiceErrors serviceErrors);
	
	String getCredentialId(String customerNumber, ServiceErrors serviceErrors) throws Exception;

    String getZnumberForInvestor(String customerNumber) throws Exception;

	String getCISKey(String clientID);

	List<Roles> getCredentialGroups(String clientID);

	String getPPID(String clientID);

    /* TODO - These methods need to implemented as part of the Beta4-Beta 5 refactor
    void updatePassword() throws Exception;


    void updateUsername() throws Exception;
     */
}
