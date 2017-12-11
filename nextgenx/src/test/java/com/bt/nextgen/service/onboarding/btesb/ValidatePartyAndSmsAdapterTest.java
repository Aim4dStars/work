package com.bt.nextgen.service.onboarding.btesb;

import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartyRegistrationResponseMsgType;

import org.junit.Assert;
import org.junit.Test;

import com.bt.nextgen.clients.util.JaxbUtil;

public class ValidatePartyAndSmsAdapterTest {

	private static String ONE_TIME_PASSWORD = "d802-3b4b-e1";
	
	@Test
	public void testValidatePartyConstructor(){
		ValidatePartyRegistrationResponseMsgType response = JaxbUtil.unmarshall("/webservices/response/ValidatePartyRegistrationResponseMsgInvalidDetailsResponse.xml", ValidatePartyRegistrationResponseMsgType.class);
		ValidatePartyAndSmsAdapter adapter = new ValidatePartyAndSmsAdapter(response);
		Assert.assertFalse(adapter.getServiceErrors().hasErrors());
		Assert.assertTrue(ONE_TIME_PASSWORD.equals(adapter.getInvalidPartyDetails().getCredentialDetails().getOneTimePassword()));
	}
}
