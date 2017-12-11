package com.bt.nextgen.service.onboarding;

public interface FirstTimeRegistrationResponse extends ValidatePartyResponse {

	/**
	 * @param sessionId the sessionId to set
	 */
	void setSessionId(String sessionId);

	/**
	 * @return the transactionId
	 */
	String getTransactionId();

	/**
	 * @param transactionId the transactionId to set
	 */
	void setTransactionId(String transactionId);

	/**
	 * @return the deviceToken
	 */
	String getDeviceToken();

	/**
	 * @param deviceToken the deviceToken to set
	 */
	void setDeviceToken(String deviceToken);

	/**
	 * @return the deviceId
	 */
	String getDeviceId();

	/**
	 * @param deviceId the deviceId to set
	 */
	void setDeviceId(String deviceId);

}