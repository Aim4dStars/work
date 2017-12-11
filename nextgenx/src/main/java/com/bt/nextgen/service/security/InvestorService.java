package com.bt.nextgen.service.security;

import com.bt.nextgen.login.web.model.AccountStatusModel;

public interface InvestorService
{
	boolean isExistingInvestor(String userCode);

	boolean isValidPassword(String password);

	//ToDo -- may take the fields in a bean later
	boolean sendSmsCode(String registrationCode, String lastName, String postcode) throws Exception;

	//ToDo need to finalize interface with Webseal
	String createInvestor(String usercode, String password);

	boolean activateAccount(String userId);

	AccountStatusModel activationStatus(String userId, String accountId);

}
