package com.bt.nextgen.service.integration.authentication.service;


import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authentication.model.TokenType;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationrequest.v2_0.CreateCredentialsRequestMsgType;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationrequest.v2_0.ObjectFactory;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.v1_0.LoginIDIssuerType;
import org.apache.commons.lang.StringUtils;

public class SaveTokenMessageRequestBuilder
{
	private ObjectFactory of = new ObjectFactory();
	private CreateCredentialsRequestMsgType credentialsRequestMsg = new CreateCredentialsRequestMsgType();


	public SaveTokenMessageRequestBuilder setUserId(String userId)
	{
		credentialsRequestMsg.setLoginID(userId);
		return this;
	}

	public SaveTokenMessageRequestBuilder setIssuer(TokenIssuer issuer)
	{
		credentialsRequestMsg.setLoginIDIssuer(LoginIDIssuerType.BGL);
		return this;
	}

	public SaveTokenMessageRequestBuilder setTokenType(TokenType tokenType)
	{
		credentialsRequestMsg.setTokenType(tokenType.getIntegrationValue());
		return this;
	}

	public SaveTokenMessageRequestBuilder setToken(String token)
	{
		credentialsRequestMsg.setAccessToken(token);
		return this;
	}

	public SaveTokenMessageRequestBuilder setRefreshToken(String refreshToken)
	{
		credentialsRequestMsg.setRefreshToken(refreshToken);
		return this;
	}

	public SaveTokenMessageRequestBuilder setExpiration(Long expiration)
	{
		credentialsRequestMsg.setExpirationInSeconds(expiration);
		return this;
	}

	public SaveTokenMessageRequestBuilder setScope(String scope)
	{
		credentialsRequestMsg.setScope(scope);
		return this;
	}

	public SaveTokenMessageRequestBuilder setAuthorisationEndpoint(String endpoint)
	{
		credentialsRequestMsg.setAuthorisationEndpoint(endpoint);
		return this;
	}

	public SaveTokenMessageRequestBuilder setTokenEndpoint(String endpoint)
	{
		credentialsRequestMsg.setTokenEndpoint(endpoint);
		return this;
	}

	public SaveTokenMessageRequestBuilder setxTokenEndpoint(TokenType type)
	{
		credentialsRequestMsg.setTokenType(type.getIntegrationValue());
		return this;
	}

	public CreateCredentialsRequestMsgType build() throws IllegalArgumentException
	{
		if (StringUtils.isBlank(credentialsRequestMsg.getLoginID()) ||
			credentialsRequestMsg.getLoginIDIssuer() == null ||
			StringUtils.isBlank(credentialsRequestMsg.getAccessToken()) ||
			StringUtils.isBlank(credentialsRequestMsg.getRefreshToken()) ||
			credentialsRequestMsg.getAccessToken().equals(Long.valueOf(0)))
		{
			throw new IllegalArgumentException("One or more values are not present");
		}

		return credentialsRequestMsg;
	}
}
