package com.bt.nextgen.service.integration.authentication.service;


import com.bt.nextgen.service.integration.authentication.model.Token;
import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authentication.model.TokenResponseStatus;

/**
 * Token management service
 */
public interface TokenIntegrationService
{
	/**
	 * Persist a token to token management store
	 * @param token the token that will be persisted
	 * @return {@link TokenResponseStatus} object
	 */
	TokenResponseStatus saveToken(Token token);

	/**
	 * Retrieve a token from the token management store
	 * @param userId user id to retrieve the token for
	 * @param issuer the issuer of the token
	 * @return {@link TokenResponseStatus} object
	 * TODO: Something doesn't look quite right in the response object when retrieving token
	 */
	TokenResponseStatus getToken(String userId, TokenIssuer issuer);
}
