package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;

@Deprecated
public class BankAccountDto extends BaseDto
{
	private String bsb;
	private String accountNumber;
	private String name;
	private String nickName;

	public String getBsb()
	{
		return bsb;
	}

	public void setBsb(String bsb)
	{
		this.bsb = bsb;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getNickName()
	{
		return nickName;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}
}
