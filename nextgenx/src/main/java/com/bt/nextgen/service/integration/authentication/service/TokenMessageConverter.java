package com.bt.nextgen.service.integration.authentication.service;


import com.bt.nextgen.service.integration.authentication.model.*;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationreply.v2_0.CreateCredentialsResponseMsgType;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationreply.v2_0.RetrieveLoginDetailsResponseMsgType;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationrequest.v2_0.CreateCredentialsRequestMsgType;
import ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.authenticationservice.authenticationrequest.v2_0.RetrieveLoginDetailsRequestMsgType;

public final class TokenMessageConverter
{
	public final static String TOKEN_SAVE_SUCCESS = "SUCCESS";
	public final static String TOKEN_EXISTS = "SUCCESS";

	private TokenMessageConverter()
	{
	}

	public static CreateCredentialsRequestMsgType toCreateCredentialsRequestMsgType(Token token, TokenIssuer issuer, TokenType type, String scope)
	{
		SaveTokenMessageRequestBuilder builder = new SaveTokenMessageRequestBuilder();

		return builder.setUserId(token.getUserId())
				.setToken(token.getToken())
				.setIssuer(issuer)
				.setScope(scope)
				.setTokenType(type)
				.setExpiration(token.getExpiration())
				.setRefreshToken(token.getRefreshToken())
				.setAuthorisationEndpoint(token.getAuthorisationEndpoint())
				.setTokenEndpoint(token.getTokenEndpoint())

				.build();
	}

	public static CreateCredentialsRequestMsgType toCreateCredentialsRequestMsgType(Token token)
	{
		SaveTokenMessageRequestBuilder builder = new SaveTokenMessageRequestBuilder();

		return builder.setUserId(token.getUserId())
				.setToken(token.getToken())
				//.setIssuer(token.getIssuer())
				//.setScope(token.getScope())
				.setExpiration(token.getExpiration())
				.setRefreshToken(token.getRefreshToken())
				.setAuthorisationEndpoint(token.getAuthorisationEndpoint())
				.setTokenEndpoint(token.getTokenEndpoint())
				.build();
	}

	public static TokenResponseStatus toTokenResponseStatus(CreateCredentialsResponseMsgType response)
	{
		TokenResponseStatus status = new TokenResponseStatusImpl(response.getStatus().toString(),
																 response.getResponseDetails().getResponseDetail().toString());

		return status;
	}

	public static RetrieveLoginDetailsRequestMsgType toRetrieveLoginDetailsRequestMsgType(String userId, TokenIssuer issuer)
	{
		RetrieveTokenMessageRequestBuilder builder = new RetrieveTokenMessageRequestBuilder();

		return builder.setUserId(userId)
						.setIssuer(issuer)
						.build();
	}

	public static TokenResponseStatus toTokenResponseStatus(RetrieveLoginDetailsResponseMsgType response)
	{
		TokenResponseStatus status = new TokenResponseStatusImpl(response.getStatus().toString(),
																 response.getResponseDetails().toString());

		return status;
	}
}
