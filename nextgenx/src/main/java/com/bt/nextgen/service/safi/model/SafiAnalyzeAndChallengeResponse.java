package com.bt.nextgen.service.safi.model;

import com.rsa.csd.ws.IdentificationData;

import java.io.Serializable;

public class SafiAnalyzeAndChallengeResponse implements SafiAnalyzeAndChallengeBasicResponse,
        TwoFactorAuthenticationBasicResponse, Serializable
{

    private static final long serialVersionUID = -1650666953960996577L;
    private transient IdentificationData identificationData = new IdentificationData();
	private boolean actionCode;
	private String transactionId = "";						// Unique string representing this nextgen ui transaction
	private String deviceToken = "";
	private String devicePrint = "";
	private String userName;
	private String deviceId;
	private String samlAssertion;
	private String statusCode = "";
	private String zNumber;
	private String ruleId;
	private String eventType;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public IdentificationData getIdentificationData()
	{
		return identificationData;
	}

	public void setIdentificationData(IdentificationData identificationData)
	{
		this.identificationData = identificationData;
	}

	public boolean getActionCode()
	{
		return actionCode;
	}

	public void setActionCode(boolean actionCode)
	{
		this.actionCode = actionCode;
	}

	public String getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(String transactionId)
	{
		this.transactionId = transactionId;
	}

	public String getDeviceToken()
	{
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken)
	{
		this.deviceToken = deviceToken;
	}

	public String getDevicePrint()
	{
		return devicePrint;
	}

	public void setDevicePrint(String devicePrint)
	{
		this.devicePrint = devicePrint;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public String getSamlAssertion()
	{
		return samlAssertion;
	}

	public void setSamlAssertion(String samlAssertion)
	{
		this.samlAssertion = samlAssertion;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public String getStatus() {
		return statusCode;
	}

    public String getzNumber() {
        return zNumber;
    }

    public void setzNumber(String zNumber) {
        this.zNumber = zNumber;
    }

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
}
