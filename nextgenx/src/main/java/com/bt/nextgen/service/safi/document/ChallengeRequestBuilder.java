package com.bt.nextgen.service.safi.document;

import au.com.rsa.ps.smsotp.SMSOTPChallengeRequest;

import com.rsa.csd.ws.AcspChallengeRequest;
import com.rsa.csd.ws.AcspChallengeRequestData;
import com.rsa.csd.ws.ActionTypeList;
import com.rsa.csd.ws.ChallengeRequest;
import com.rsa.csd.ws.ChallengeType;
import com.rsa.csd.ws.CredentialChallengeRequestList;
import com.rsa.csd.ws.DeviceRequest;
import com.rsa.csd.ws.GenericActionType;
import com.rsa.csd.ws.GenericActionTypeList;
import com.rsa.csd.ws.IdentificationData;
import com.rsa.csd.ws.MessageHeader;
import com.rsa.csd.ws.SecurityHeader;

public class ChallengeRequestBuilder 
{
	private CredentialChallengeRequestList credentialChallengeRequestList;
	private SMSOTPChallengeRequest smsOpChallengeRequest;
	private DeviceRequest deviceRequest;
	private IdentificationData identificationData;
	private MessageHeader messageHeader;
	private SecurityHeader securityHeader;

	
	public ChallengeRequestBuilder addCredentialChallengeRequestList(CredentialChallengeRequestList credentialChallengeRequestList) {
		this.credentialChallengeRequestList = credentialChallengeRequestList;
		return this;
	}

	public ChallengeRequestBuilder addSmsOpChallengeRequest(SMSOTPChallengeRequest smsOpChallengeRequest) {
		this.smsOpChallengeRequest = smsOpChallengeRequest;
		return this;
	}

	public ChallengeRequestBuilder addDeviceRequest(DeviceRequest deviceRequest) {
		this.deviceRequest = deviceRequest;
		return this;
	}

	public ChallengeRequestBuilder addIdentificationData(IdentificationData identificationData) {
		this.identificationData = identificationData;
		return this;
	}

	public ChallengeRequestBuilder addMessageHeader(MessageHeader messageHeader) {
		this.messageHeader = messageHeader;
		return this;
	}

	public ChallengeRequestBuilder addSecurityHeader(SecurityHeader securityHeader) {
		this.securityHeader = securityHeader;
		return this;
	}


	
	
	public ChallengeType build()
	{
		if (smsOpChallengeRequest == null ||
			deviceRequest == null || 
			identificationData == null || 
			messageHeader == null || 
			securityHeader == null)
		{
			throw new IllegalStateException("");
		}
		
		ChallengeRequest challengeRequest = new ChallengeRequest();
		ChallengeType challengeType = new ChallengeType();
		
		challengeRequest.setDeviceRequest(deviceRequest);
		challengeRequest.setIdentificationData(identificationData);
		challengeRequest.setMessageHeader(messageHeader);
		challengeRequest.setSecurityHeader(securityHeader);
		
		CredentialChallengeRequestList credentialChallengeRequestList = new CredentialChallengeRequestList();
		AcspChallengeRequestData acspChallengeRequestData = new AcspChallengeRequestData();
		AcspChallengeRequest acpsChallengeRequest = smsOpChallengeRequest;
		acspChallengeRequestData.setPayload(acpsChallengeRequest);
		credentialChallengeRequestList.setAcspChallengeRequestData(acspChallengeRequestData);
		
		challengeRequest.setCredentialChallengeRequestList(credentialChallengeRequestList);
		challengeType.setRequest(challengeRequest);
		
		return challengeType;
	}
}
