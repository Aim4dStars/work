package com.bt.nextgen.service.safi.document;

import org.apache.commons.lang.StringUtils;

import au.com.rsa.ps.smsotp.SMSOTPAuthenticationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMSOTPAuthenticationRequestBuilder 
{
    private Logger logger = LoggerFactory.getLogger(SMSOTPAuthenticationRequestBuilder.class);
	private String deviceId;
	private String smsOTP;
	private String transactionId;
	private String samlAssertion;

	public SMSOTPAuthenticationRequestBuilder setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}

	public SMSOTPAuthenticationRequestBuilder setSmsOTP(String smsOTP) {
		this.smsOTP = smsOTP;
		return this;
	}

	public SMSOTPAuthenticationRequestBuilder setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}

	public SMSOTPAuthenticationRequestBuilder setSamlAssertion(String samlAssertion) {
		this.samlAssertion = samlAssertion;
		return this;
	}

	public SMSOTPAuthenticationRequest build() {
		
		if (StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(smsOTP) || StringUtils.isEmpty(transactionId) || StringUtils.isEmpty(samlAssertion))
		{
            logger.warn("Device ID:{} smsOTP:{} transactionId:{} samlAssertion:{}", deviceId,smsOTP,transactionId,samlAssertion);
			throw new IllegalArgumentException("One or more parameters have not been defined");
		}
		
		SMSOTPAuthenticationRequest sMSOTPAuthenticationRequest = new SMSOTPAuthenticationRequest();
		sMSOTPAuthenticationRequest.setDeviceId(deviceId);
		sMSOTPAuthenticationRequest.setSmsOTP(smsOTP);
		sMSOTPAuthenticationRequest.setTransactionId(transactionId);
		sMSOTPAuthenticationRequest.setSamlAssertion(samlAssertion);
		return sMSOTPAuthenticationRequest;
	}
}
