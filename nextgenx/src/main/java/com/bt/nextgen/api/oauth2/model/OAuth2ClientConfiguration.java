package com.bt.nextgen.api.oauth2.model;


public interface OAuth2ClientConfiguration
{
	public String getApiKey();

	public String getApiSecret();

	public int getPort();

	public String getCallbackDomain();

	public String getCallbackPath();

	public String getTokenServerUrl();

	public String getAuthorisationServerUrl();

	public String getScope();
}
