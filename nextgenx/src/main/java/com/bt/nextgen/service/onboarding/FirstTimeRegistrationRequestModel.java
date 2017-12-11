/**
 * 
 */
package com.bt.nextgen.service.onboarding;

import com.bt.nextgen.service.security.model.HttpRequestParams;

/**
 * @author L055011
 *
 */
public class FirstTimeRegistrationRequestModel implements FirstTimeRegistrationRequest 
{
	private String lastName;
	private String postalCode;
	private ValidatePartyAndSmsAction action;
	private String registrationCode;
	private String userName;
	private HttpRequestParams httpRequestParams;
	private String deviceToken;



	public FirstTimeRegistrationRequestModel() {

	}

	public FirstTimeRegistrationRequestModel(String registrationCode, String lastName, String postalCode) {
		this.registrationCode = registrationCode;
		this.lastName = lastName;
		this.postalCode = postalCode;
	}

	public  FirstTimeRegistrationRequestModel(String registrationCode, String lastName, String postalCode, String userName) {
		this(registrationCode, lastName, postalCode);
		this.userName = userName;

	}

	
	/* (non-Javadoc)
	 * @see com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest#getLastName()
	 */
	@Override
	public String getLastName() {
		return lastName;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest#setLastName(java.lang.String)
	 */
	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest#getPostalCode()
	 */
	@Override
	public String getPostalCode() {
		return postalCode;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest#setPostalCode(java.lang.String)
	 */
	@Override
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest#getAction()
	 */
	@Override
	public ValidatePartyAndSmsAction getAction() {
		return action;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest#setAction(com.bt.nextgen.service.onboarding.ValidatePartyAndSmsAction)
	 */
	@Override
	public void setAction(ValidatePartyAndSmsAction action) {
		this.action = action;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest#getRegistrationCode()
	 */
	@Override
	public String getRegistrationCode() {
		return registrationCode;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest#setRegistrationCode(java.lang.String)
	 */
	@Override
	public void setRegistrationCode(String registrationCode) {
		this.registrationCode = registrationCode;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest#getUserName()
	 */
	@Override
	public String getUserName() {
		return userName;
	}
	/* (non-Javadoc)
	 * @see com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest#setUserName(java.lang.String)
	 */
	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the httpRequestParams
	 */
	public HttpRequestParams getHttpRequestParams() {
		return httpRequestParams;
	}
	/**
	 * @param httpRequestParams the httpRequestParams to set
	 */
	public void setHttpRequestParams(HttpRequestParams httpRequestParams) {
		this.httpRequestParams = httpRequestParams;
	}
	/**
	 * @return the deviceToken
	 */
	public String getDeviceToken() {
		return deviceToken;
	}
	/**
	 * @param deviceToken the deviceToken to set
	 */
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
}
