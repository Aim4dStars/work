package com.bt.nextgen.logon.service;

import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.service.ServiceErrors;

public interface LogonService {
	
	//TODO : Methods written to call EAM service , will un comment once done integration with EAM. 
	//User registerUser(User userToRegister);
	
	/*Person getRoleAndDetails(String customerDefinedLoginName);*/
	
	//String updatePassword(String credentialID,UserReset userDetails,String requestedAction);
	
	String modifyUserAlias(UserReset userReset, ServiceErrors serviceErrors) throws Exception;
	
	//String changePassword(String credentialID,String newPassword);
	
	String validateUser(String credentialID,String lastName, int postCode);
	
	String verifySmsCode(String credentialID,String lastName, int postCode, String smsCode);
	
	//	String updatePassword(UserReset userDetails, ActionCode actionCode) throws Exception;
	
	String updatePassword(UserReset userDetails, ServiceErrors serviceErrors);
}
