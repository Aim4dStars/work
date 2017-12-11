package com.bt.nextgen.api.oauth2.model;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class BglClientConfiguration implements OAuth2ClientConfiguration
{
	private String clientKey = "btTest";

	private String clientSecret = "6980acbd-a842-431a-b45e-5666084e7ddb";

	private int port = 9080;

	private String callbackDomain = "www.bt.com.au";

	private String callbackPath = "http://localhost:9080/ng/oauth/bgl/authcode";

	private String tokenServerUrl = "https://api-staging.bgl360.com.au/oauth/token";

	private String authorisationServerUrl = "https://api-staging.bgl360.com.au/oauth/authorize";

	private String scope = "audit";


	@Inject
	public BglClientConfiguration(@Value("${bgl.oauth2.client.key}") String clientKey,
								  @Value("${bgl.oauth2.client.secret}") String clientSecret,
								  @Value("${bgl.oauth2.port}") int port,
								  @Value("${bgl.oauth2.callback.domain}") String callbackDomain,
								  @Value("${bgl.oauth2.callback.path}") String callbackPath,
								  @Value("${bgl.oauth2.token.server.url}") String tokenServerUrl,
								  @Value("${bgl.oauth2.authorisation.server.url}") String authorisationServerUrl,
								  @Value("${bgl.oauth2.scope}") String scope)
	{
		this.clientKey = clientKey;
		this.clientSecret = clientSecret;
		this.port = port;
		this.callbackDomain = callbackDomain;
		this.callbackPath = callbackPath;
		this.tokenServerUrl = tokenServerUrl;
		this.authorisationServerUrl = authorisationServerUrl;
		this.scope = scope;
	}


	@Override
	public String getApiKey()
	{
		return clientKey;
	}

	@Override
	public String getApiSecret()
	{
		return clientSecret;
	}

	@Override
	public int getPort()
	{
		return port;
	}

	@Override
	public String getCallbackDomain()
	{
		return callbackDomain;
	}

	@Override
	public String getCallbackPath()
	{
		return callbackPath;
	}

	@Override
	public String getTokenServerUrl()
	{
		return tokenServerUrl;
	}

	@Override
	public String getAuthorisationServerUrl()
	{
		return authorisationServerUrl;
	}

	@Override
	public String getScope()
	{
		return scope;
	}
}
