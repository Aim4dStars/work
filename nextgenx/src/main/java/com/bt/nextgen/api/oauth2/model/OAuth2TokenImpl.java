package com.bt.nextgen.api.oauth2.model;


public class OAuth2TokenImpl implements OAuth2Token
{
	private String token;
	private String refreshToken;
	private long expiry;
	private String type;
	private String scope;
	private String errorCode;
	private String errorDescription;

	public OAuth2TokenImpl()
	{

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
	public long getExpiry()
	{
		return expiry;
	}

	@Override
	public void setExpiry(long expiry)
	{
		this.expiry = expiry;
	}

	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public void setType(String type)
	{
		this.type = type;
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
	public String getErrorCode()
	{
		return errorCode;
	}

	@Override
	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	@Override
	public String getErrorDescription()
	{
		return errorDescription;
	}

	@Override
	public void setErrorDescription(String errorDescription)
	{
		this.errorDescription = errorDescription;
	}
}
