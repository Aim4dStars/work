package com.bt.nextgen.core.security.api.model;

public class PermissionKey
{
	private String clientId;
	private String portfolioId;
	private String aclId;
	private String operation;

	public PermissionKey(String clientId, String portfolioId)
	{
		this(clientId, portfolioId, null, null);
	}

	public PermissionKey(String clientId, String portfolioId, String aclId)
	{
		this(clientId, portfolioId, aclId, null);
	}

	public PermissionKey(String clientId, String portfolioId, String aclId, String operation)
	{
		super();
		this.clientId = clientId;
		this.portfolioId = portfolioId;
		this.aclId = aclId;
		this.operation = operation;
	}

	public String getClientId()
	{
		return clientId;
	}

	public String getPortfolioId()
	{
		return portfolioId;
	}

	public String getAclId()
	{
		return aclId;
	}

	public String getOperation()
	{
		return operation;
	}

}
