package com.bt.nextgen.service.avaloq.payments;

import com.bt.nextgen.service.integration.payments.LinkedAccountDetails;

@Deprecated
public class LinkedAccountDetailsImpl implements LinkedAccountDetails
{
	private String account;
	private String bsb;
	private String payeeName;
	private String nickName;
	private boolean isPrimary;

	
	@Override
	public boolean isPrimary()
	{
		return isPrimary;
	}
	
	@Override
	public void setPrimary(boolean isPrimary)
	{
		this.isPrimary = isPrimary;
	}

	@Override
	public String getAccountNumber()
	{
		return account;
	}

	@Override
	public String getName()
	{
		return payeeName;
	}

	@Override
	public String getNickName()
	{
		return nickName;
	}

	@Override
	public String getBsb()
	{
		return bsb;
	}

	
}
