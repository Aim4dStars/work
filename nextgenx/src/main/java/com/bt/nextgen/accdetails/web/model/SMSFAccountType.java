package com.bt.nextgen.accdetails.web.model;

import com.bt.nextgen.core.web.model.AddressModel;

/**
 * SMSF Account Details
 * @author L056616
 *
 */
public class SMSFAccountType
{
	private String accountName;
	private String abn;
	private String registrationState;
	private String tfn;
	private AddressModel address;
	private String idStatus;

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getAbn()
	{
		return abn;
	}

	public void setAbn(String abn)
	{
		this.abn = abn;
	}

	public String getRegistrationState()
	{
		return registrationState;
	}

	public void setRegistrationState(String registrationState)
	{
		this.registrationState = registrationState;
	}

	public String getTfn()
	{
		return tfn;
	}

	public void setTfn(String tfn)
	{
		this.tfn = tfn;
	}

	public AddressModel getAddress()
	{
		return address;
	}

	public void setAddress(AddressModel address)
	{
		this.address = address;
	}

	public String getIdStatus()
	{
		return idStatus;
	}

	public void setIdStatus(String idStatus)
	{
		this.idStatus = idStatus;
	}

}
