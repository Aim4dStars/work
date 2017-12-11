package com.bt.nextgen.service.onboarding;

import com.bt.nextgen.service.security.model.HttpRequestParams;

public interface FirstTimeRegistrationRequest extends ValidatePartyRequest {

	/**
	 * @return the action
	 */
	ValidatePartyAndSmsAction getAction();

	/**
	 * @param action the action to set
	 */
	void setAction(ValidatePartyAndSmsAction action);

	/**
	 * @return the userName
	 */
	String getUserName();

	/**
	 * @param userName the userName to set
	 */
	void setUserName(String userName);

	/**
	 * @return the httpRequestParams
	 */
	HttpRequestParams getHttpRequestParams();
	/**
	 * @param httpRequestParams the httpRequestParams to set
	 */
	void setHttpRequestParams(HttpRequestParams httpRequestParams) ;
	/**
	 * @return the deviceToken
	 */
	String getDeviceToken();
	/**
	 * @param deviceToken the deviceToken to set
	 */
	void setDeviceToken(String deviceToken);
}