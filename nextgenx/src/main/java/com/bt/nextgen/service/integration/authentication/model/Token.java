package com.bt.nextgen.service.integration.authentication.model;

public interface Token
{
	String getUserId();

	void setUserId(String userId);

	TokenIssuer getIssuer();

	void setIssuer(TokenIssuer issuer);

	String getToken();

	void setToken(String token);

	String getRefreshToken();

	void setRefreshToken(String refreshToken);

	Long getExpiration();

	void setExpiration(Long expiration);

	String getScope();

	void setScope(String scope);

	String getAuthorisationEndpoint();

	void setAuthorisationEndpoint(String authorisationEndpoint);

	String getTokenEndpoint();

	void setTokenEndpoint(String tokenEndpoint);
}