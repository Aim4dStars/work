package com.bt.nextgen.service.integration.authentication.model;


public class OAuth2TokenImpl implements Token
{
	private String userId;

	private TokenIssuer tokenIssuer;

	private String token;

	private String refreshToken;

	private long expiration;

	private String scope;

	private String authorisationEndpoint;

	private String tokenEndpoint;


	@Override
	public String getUserId()
	{
		return userId;
	}

	@Override
	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	@Override
	public TokenIssuer getIssuer()
	{
		return tokenIssuer;
	}

	@Override
	public void setIssuer(TokenIssuer issuer)
	{
		this.tokenIssuer = issuer;
	}

	@Override
	public String getToken()
	{
		return token;
	}

	@Override
	public void setToken(String token)
	{
		this.token = token;
	}

	@Override
	public String getRefreshToken()
	{
		return refreshToken;
	}

	@Override
	public void setRefreshToken(String refreshToken)
	{
		this.refreshToken = refreshToken;
	}

	@Override
	public Long getExpiration()
	{
		return expiration;
	}

	@Override
	public void setExpiration(Long expiration)
	{
		this.expiration = expiration;
	}

	@Override
	public String getScope()
	{
		return scope;
	}

	@Override
	public void setScope(String scope)
	{
		this.scope = scope;
	}

	@Override
	public String getAuthorisationEndpoint()
	{
		return authorisationEndpoint;
	}

	@Override
	public void setAuthorisationEndpoint(String authorisationEndpoint)
	{
		this.authorisationEndpoint = authorisationEndpoint;
	}

	@Override
	public String getTokenEndpoint()
	{
		return tokenEndpoint;
	}

	@Override
	public void setTokenEndpoint(String tokenEndpoint)
	{
		this.tokenEndpoint = tokenEndpoint;
	}
}