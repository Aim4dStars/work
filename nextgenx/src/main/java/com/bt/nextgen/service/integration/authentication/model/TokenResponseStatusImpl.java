package com.bt.nextgen.service.integration.authentication.model;


public class TokenResponseStatusImpl implements TokenResponseStatus
{
	private String status;

	private String message;


	public TokenResponseStatusImpl(String status, String message)
	{
		this.status = status;
		this.message = message;
	}


	@Override
	public String getStatus()
	{
		return status;
	}

	@Override
	public String getMessage()
	{
		return message;
	}
}
