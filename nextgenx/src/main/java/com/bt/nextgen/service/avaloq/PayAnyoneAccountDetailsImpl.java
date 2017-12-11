package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;

public class PayAnyoneAccountDetailsImpl implements PayAnyoneAccountDetails
{
	private String account;
	private String bsb;

	@Override
	public String getAccount()
	{
		return account;
	}

	@Override
	public void setAccount(String account)
	{
		this.account = account;
	}

	@Override
	public String getBsb()
	{
		return bsb;
	}

	@Override
	public void setBsb(String bsb)
	{
		this.bsb = bsb;

	}


}
