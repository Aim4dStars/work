package com.bt.nextgen.service.integration.authentication.model;


/**
 * OAuth2 exchange mechanism
 */
public enum TokenType
{
	BEARER(ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.v1_0.TokenType.BEARER);

	private TokenType(ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.v1_0.TokenType value)
	{
		setIntegrationValue(value);
	}

	private ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.v1_0.TokenType integrationValue;


	public ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.v1_0.TokenType getIntegrationValue()
	{
		return integrationValue;
	}

	public void setIntegrationValue(ns.btfin_com.sharedservices.utilities.identitymanagement.authentication.v1_0.TokenType integrationValue)
	{
		this.integrationValue = integrationValue;
	}

	public String toString()
	{
		return this.name();
	}
}
