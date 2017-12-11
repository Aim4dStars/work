package com.bt.nextgen.service.integration.authentication.model;

/**
 * Issuer of the OAuth2 Token
 */
public enum TokenIssuer
{
	BGL;

	private TokenIssuer()
	{
	}

	public String toString()
	{
		return this.name();
	}
}
