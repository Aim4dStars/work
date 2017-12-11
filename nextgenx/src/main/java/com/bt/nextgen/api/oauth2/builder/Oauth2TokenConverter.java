package com.bt.nextgen.api.oauth2.builder;


import com.bt.nextgen.api.oauth2.model.OAuth2Token;
import com.bt.nextgen.api.oauth2.model.OAuth2TokenImpl;
import com.google.api.client.auth.oauth2.Credential;


public final class Oauth2TokenConverter
{
	public static OAuth2Token toOauth2Token(Credential credential)
	{
		OAuth2Token token = new OAuth2TokenImpl();
		token.setToken(credential.getAccessToken());
		token.setRefreshToken(credential.getRefreshToken());
		token.setExpiry(credential.getExpiresInSeconds());

		return token;
	}

	public static OAuth2Token toOauth2Token(String errorCode, String description)
	{
		OAuth2Token token = new OAuth2TokenImpl();
		token.setErrorCode(errorCode);

		return token;
	}
}