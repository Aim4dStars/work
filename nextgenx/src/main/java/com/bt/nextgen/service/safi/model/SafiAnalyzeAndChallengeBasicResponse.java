package com.bt.nextgen.service.safi.model;

import com.rsa.csd.ws.IdentificationData;

public interface SafiAnalyzeAndChallengeBasicResponse
{
	public IdentificationData getIdentificationData();
	
	public void setIdentificationData(IdentificationData identificationData);

	public boolean getActionCode();
	
	public void setActionCode(boolean actionCode);

	public String getTransactionId();

	public void setTransactionId(String transactionId);

	public String getDeviceToken();

	public void setDeviceToken(String deviceToken);

	public String getDevicePrint();

	public void setDevicePrint(String devicePrint);

	public String getUserName();

	public void setUserName(String userName);

	public String getDeviceId();

	public void setDeviceId(String deviceId);

	public String getSamlAssertion();

	public void setSamlAssertion(String samlAssertion);

	public String getStatusCode();

	public void setStatusCode(String statusCode);
}
