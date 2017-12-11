package com.bt.nextgen.api.oauth2.model;

/**
 * OAuth2 Token to be used for data requests on protected APIs
 */
public interface OAuth2Token
{
	public String getToken();

	public void setToken(String token);

	public String getRefreshToken();

	public void setRefreshToken(String refreshToken);

	public String getErrorCode();

	public void setErrorCode(String errorCode);

	public String getErrorDescription();

	public void setErrorDescription(String errorDescription);


	/**
	 * Token expiry in seconds
	 * @return number of seconds until token expiry
	 */
	public long getExpiry();

	public void setExpiry(long expiry);

	public String getType();

	public void setType(String type);

	public String getScope();

	public void setScope(String scope);
}