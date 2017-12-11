package com.bt.nextgen.accdetails.web.model;

import com.bt.nextgen.core.web.model.AddressModel;

/**
 * Trust Details
 * @author L056616
 *
 */

public class TrustAccountType
{

	private String accountName;
	private String trustType;
	private String abn;
	//Australian Registered Scheme Number
	private String arsn;
	private String registrationState;
	private String tfn;
	private AddressModel address;
	private String idStatus;

	//Trust Type - Government super fund  
	private String legislationName;

	//Trust Type - Regulated
	private String regulatorName;
	private String licensingNumber;

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getTrustType()
	{
		return trustType;
	}

	public void setTrustType(String trustType)
	{
		this.trustType = trustType;
	}

	public String getAbn()
	{
		return abn;
	}

	public void setAbn(String abn)
	{
		this.abn = abn;
	}

	public String getArsn()
	{
		return arsn;
	}

	public void setArsn(String arsn)
	{
		this.arsn = arsn;
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

	public String getLegislationName()
	{
		return legislationName;
	}

	public void setLegislationName(String legislationName)
	{
		this.legislationName = legislationName;
	}

	public String getRegulatorName()
	{
		return regulatorName;
	}

	public void setRegulatorName(String regulatorName)
	{
		this.regulatorName = regulatorName;
	}

	public String getLicensingNumber()
	{
		return licensingNumber;
	}

	public void setLicensingNumber(String licensingNumber)
	{
		this.licensingNumber = licensingNumber;
	}

}
