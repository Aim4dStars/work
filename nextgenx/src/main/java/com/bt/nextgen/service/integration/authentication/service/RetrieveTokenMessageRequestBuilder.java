package com.bt.nextgen.service.integration.authentication.service;


import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationrequest.v2_0.ObjectFactory;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationrequest.v2_0.RetrieveLoginDetailsRequestMsgType;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.v1_0.LoginIDIssuerType;
import org.apache.commons.lang.StringUtils;

public class RetrieveTokenMessageRequestBuilder
{
	private ObjectFactory of = new ObjectFactory();
	private RetrieveLoginDetailsRequestMsgType retrieveTokenMsg = new RetrieveLoginDetailsRequestMsgType();

	public RetrieveTokenMessageRequestBuilder setUserId(String userId)
	{
		retrieveTokenMsg.setLoginID(userId);
		return this;
	}

	public RetrieveTokenMessageRequestBuilder setIssuer(TokenIssuer issuer)
	{
		retrieveTokenMsg.setLoginIDIssuer(LoginIDIssuerType.BGL);
		return this;
	}

	public RetrieveLoginDetailsRequestMsgType build() throws IllegalArgumentException
	{
		if (StringUtils.isBlank(retrieveTokenMsg.getLoginID()) ||
			retrieveTokenMsg.getLoginIDIssuer() == null)
		{
			throw new IllegalArgumentException("One or more values are not present");
		}

		return retrieveTokenMsg;
	}
}
