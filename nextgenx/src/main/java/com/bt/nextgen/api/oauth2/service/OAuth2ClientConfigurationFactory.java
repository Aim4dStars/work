package com.bt.nextgen.api.oauth2.service;

import com.bt.nextgen.api.oauth2.model.BglClientConfiguration;
import com.bt.nextgen.api.oauth2.model.OAuth2ClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OAuth2ClientConfigurationFactory
{
	private static final String BGL_ID = "bgl360";

	@Autowired
	private BglClientConfiguration bglConfiguration;

	public OAuth2ClientConfiguration getClientConfiguration(String application)
	{
		if (BGL_ID.equalsIgnoreCase(application))
		{
			return bglConfiguration;
		}

		return null;
	}
}