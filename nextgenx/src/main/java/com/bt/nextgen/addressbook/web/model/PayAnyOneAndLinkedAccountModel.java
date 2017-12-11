package com.bt.nextgen.addressbook.web.model;


public class PayAnyOneAndLinkedAccountModel
{
	//variable used in Payment and Deposit 
	private String accountNumber;
	private String bsb;
	private String accountNickName;

	private String accountType;
	private String accountName;

	public PayAnyOneAndLinkedAccountModel()
	{

	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public String getBsb()
	{
		return bsb;
	}

	public void setBsb(String bsb)
	{
		this.bsb = bsb;
	}

	public String getAccountNickName()
	{
		return accountNickName;
	}

	public void setAccountNickName(String accountNickName)
	{
		this.accountNickName = accountNickName;
	}

	public String getAccountType()
	{
		return accountType;
	}

	public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

}
