package com.bt.nextgen.service.security;

import java.io.UnsupportedEncodingException;

import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.rsa.csd.ws.*;

public interface SmsHelperService
{
	GenericActionTypeList setAnalyzeActionTypeListRequest();
	IdentificationData setAnalyzeIdentificationData();
	IdentificationData setIdentificationData();
	MessageHeader setMessageHeader(RequestType requestType);
	SecurityHeader setSecurityHeader();
	DeviceRequest setAnalyzeDeviceRequest();
	CredentialChallengeRequestList setCredentialChallengeRequestList();
	DeviceRequest setDeviceRequest();
	CredentialDataList setCredentialDataList(String smsCode);
	EventDataList setEventDataList(EventModel eventModel);
	
	//From Cash 4	
	IdentificationData setAnalyzeIdentificationData(String clientSessionId, String clientTransactionId);	
	IdentificationData setIdentificationData(String sessionId, String transactionId, String... userName);
	DeviceRequest setAnalyzeDeviceRequest(HttpRequestParams requestParams, String deviceToken);	
	CredentialChallengeRequestList setCredentialChallengeRequestList(String clientTransactionId) throws Exception;	
	DeviceRequest setAuthenticateDeviceRequest(HttpRequestParams requestParams, String devicePrint, String deviceToken);	
	DeviceRequest setChallengeDeviceRequest(HttpRequestParams requestParams, String devicePrint, String deviceToken);	
	CredentialDataList setCredentialDataList(String smsCode, SafiAnalyzeResult safiResponse) throws Exception;	
	CredentialDataList setCredentialDataList(String smsCode, SafiAnalyzeResult safiResponse, String samlToken, String transactionId) throws Exception;		
	RunRiskType setRunRiskType();	
	ChannelIndicatorType setChannelIndicatorType();	
	String decodeDeviceTokenString(String encodedDeviceToken) throws UnsupportedEncodingException;
	
	
}
