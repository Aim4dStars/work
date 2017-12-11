package com.bt.nextgen.service.safi.document;

import au.com.rsa.ps.smsotp.SMSOTPChallengeRequest;


public class SMSOTPChallengeRequestBuilder
{
	private String deviceId;
	private String networkId;
	private Boolean initDevice;
	private String transactionId;
	private String organisationId;
	private String brandSilo;
	private String requestingUserId;
	private String messageText;
	private String samlAssertion;

	public SMSOTPChallengeRequestBuilder setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}

	public SMSOTPChallengeRequestBuilder setNetworkId(String networkId) {
		this.networkId = networkId;
		return this;
	}

	public SMSOTPChallengeRequestBuilder setInitDevice(Boolean initDevice) {
		this.initDevice = initDevice;
		return this;
	}

	public SMSOTPChallengeRequestBuilder setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}

	public SMSOTPChallengeRequestBuilder setOrganisationId(String organisationId) {
		this.organisationId = organisationId;
		return this;
	}

	public SMSOTPChallengeRequestBuilder setBrandSilo(String brandSilo) {
		this.brandSilo = brandSilo;
		return this;
	}

	public SMSOTPChallengeRequestBuilder setRequestingUserId(String requestingUserId) {
		this.requestingUserId = requestingUserId;
		return this;
	}

	public SMSOTPChallengeRequestBuilder setMessageText(String messageText) {
		this.messageText = messageText;
		return this;
	}

	public SMSOTPChallengeRequestBuilder setSamlAssertion(String samlAssertion) {
		this.samlAssertion = samlAssertion;
		return this;
	}

	public SMSOTPChallengeRequest build() {
		SMSOTPChallengeRequest sMSOTPChallengeRequest = new SMSOTPChallengeRequest();
		sMSOTPChallengeRequest.setDeviceId(deviceId);
		sMSOTPChallengeRequest.setNetworkId(networkId);
		sMSOTPChallengeRequest.setInitDevice(initDevice);
		sMSOTPChallengeRequest.setTransactionId(transactionId);
		sMSOTPChallengeRequest.setOrganisationId(organisationId);
		sMSOTPChallengeRequest.setBrandSilo(brandSilo);
		sMSOTPChallengeRequest.setRequestingUserId(requestingUserId);
		sMSOTPChallengeRequest.setMessageText(messageText);
		sMSOTPChallengeRequest.setSamlAssertion(samlAssertion);
		return sMSOTPChallengeRequest;
	}
}

