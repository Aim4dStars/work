package com.bt.nextgen.service.safi.document;

import au.com.rsa.ps.smsotp.SMSOTPAuthenticationRequest;
import au.com.rsa.ps.smsotp.SMSOTPChallengeRequest;

import com.rsa.csd.ws.AcspAuthenticationRequest;
import com.rsa.csd.ws.AcspAuthenticationRequestData;
import com.rsa.csd.ws.AcspChallengeRequest;
import com.rsa.csd.ws.AcspChallengeRequestData;
import com.rsa.csd.ws.ActionTypeList;
import com.rsa.csd.ws.AuthenticateRequest;
import com.rsa.csd.ws.AuthenticateType;
import com.rsa.csd.ws.ChallengeRequest;
import com.rsa.csd.ws.ChallengeType;
import com.rsa.csd.ws.CredentialChallengeRequestList;
import com.rsa.csd.ws.CredentialDataList;
import com.rsa.csd.ws.DeviceRequest;
import com.rsa.csd.ws.GenericActionType;
import com.rsa.csd.ws.GenericActionTypeList;
import com.rsa.csd.ws.IdentificationData;
import com.rsa.csd.ws.MessageHeader;
import com.rsa.csd.ws.SecurityHeader;

public class AuthenticateRequestBuilder 
{
	private CredentialDataList credentialDataList;
	private SMSOTPAuthenticationRequest smsOpAuthenticateRequest;
	private DeviceRequest deviceRequest;
	private IdentificationData identificationData;
	private MessageHeader messageHeader;
	private SecurityHeader securityHeader;
	private GenericActionTypeList actionTypeList;
	
	public AuthenticateRequestBuilder setActionType(GenericActionType genericActionType)
	{
		GenericActionTypeList genericActionTypeList = new GenericActionTypeList();
		genericActionTypeList.getGenericActionTypes().add(genericActionType);
		actionTypeList = genericActionTypeList;		
		return this;
	}	
	
	
	public AuthenticateRequestBuilder addCredentialChallengeRequestList(CredentialDataList credentialDataList) {
		this.credentialDataList = credentialDataList;
		return this;
	}

	public AuthenticateRequestBuilder addSmsOpAuthenticateRequest(SMSOTPAuthenticationRequest smsOpAuthenticateRequest) {
		this.smsOpAuthenticateRequest = smsOpAuthenticateRequest;
		return this;
	}

	public AuthenticateRequestBuilder addDeviceRequest(DeviceRequest deviceRequest) {
		this.deviceRequest = deviceRequest;
		return this;
	}

	public AuthenticateRequestBuilder addIdentificationData(IdentificationData identificationData) {
		this.identificationData = identificationData;
		return this;
	}

	public AuthenticateRequestBuilder addMessageHeader(MessageHeader messageHeader) {
		this.messageHeader = messageHeader;
		return this;
	}

	public AuthenticateRequestBuilder addSecurityHeader(SecurityHeader securityHeader) {
		this.securityHeader = securityHeader;
		return this;
	}


	
	
	public AuthenticateType build()
	{
		if (smsOpAuthenticateRequest == null ||
			deviceRequest == null || 
			identificationData == null || 
			messageHeader == null || 
			securityHeader == null)
		{
			throw new IllegalStateException("");
		}
		
		AuthenticateRequest authenticateRequest = new AuthenticateRequest();
		AuthenticateType authenticateType = new AuthenticateType();
		
		authenticateRequest.setActionTypeList(actionTypeList);
		authenticateRequest.setDeviceRequest(deviceRequest);
		authenticateRequest.setIdentificationData(identificationData);
		authenticateRequest.setMessageHeader(messageHeader);
		authenticateRequest.setSecurityHeader(securityHeader);
		
		CredentialDataList credentialDataList = new CredentialDataList();
		AcspAuthenticationRequestData acspAuthenticationRequestData = new AcspAuthenticationRequestData();
		AcspAuthenticationRequest acpsAuthenticationRequest = smsOpAuthenticateRequest;
		acspAuthenticationRequestData.setPayload(acpsAuthenticationRequest);
		credentialDataList.setAcspAuthenticationRequestData(acspAuthenticationRequestData);
		
		authenticateRequest.setCredentialDataList(credentialDataList);
		authenticateType.setRequest(authenticateRequest);
		
		return authenticateType;
	}
}
