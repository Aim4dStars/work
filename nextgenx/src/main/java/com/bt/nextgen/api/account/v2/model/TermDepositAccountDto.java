package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

@Deprecated
public class TermDepositAccountDto extends BaseDto implements KeyedDto <AccountKey>
{

	private String tdAccountId;
	private String renewModeId;
	private AccountKey key;
	private String investmentAmount;
	private String termDuration;

	public TermDepositAccountDto(AccountKey key, String tdAccountId, String renewModeId)
	{
		super();
		this.tdAccountId = tdAccountId;
		this.renewModeId = renewModeId;
		this.key = key;
	}

	public String getTdAccountId()
	{
		return tdAccountId;
	}

	public void setTdAccountId(String tdAccountId)
	{
		this.tdAccountId = tdAccountId;
	}

	public String getRenewModeId()
	{
		return renewModeId;
	}

	public void setRenewModeId(String renewModeId)
	{
		this.renewModeId = renewModeId;
	}

	public AccountKey getKey()
	{
		return key;
	}

	public void setKey(AccountKey key)
	{
		this.key = key;
	}

	public String getTermDuration()
	{
		return termDuration;
	}

	public void setTermDuration(String termDuration)
	{
		this.termDuration = termDuration;
	}

	public String getInvestmentAmount()
	{
		return investmentAmount;
	}

	public void setInvestmentAmount(String investmentAmount)
	{
		this.investmentAmount = investmentAmount;
	}

}
