package com.bt.nextgen.accdetails.web.model;

import com.bt.nextgen.core.web.model.AddressModel;

/**
 * Company Details
 * @author L056616
 *
 */

public class CompanyAccountType
{
	private String accountName;
	private String asic;
	private String acn;
	private String abn;
	private String tfn;
	private AddressModel registeredAddress;
	private AddressModel principalAddress;
	private String idStatus;

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getAsic()
	{
		return asic;
	}

	public void setAsic(String asic)
	{
		this.asic = asic;
	}

	public String getAcn()
	{
		return acn;
	}

	public void setAcn(String acn)
	{
		this.acn = acn;
	}

	public String getAbn()
	{
		return abn;
	}

	public void setAbn(String abn)
	{
		this.abn = abn;
	}

	public String getTfn()
	{
		return tfn;
	}

	public void setTfn(String tfn)
	{
		this.tfn = tfn;
	}

	public AddressModel getRegisteredAddress()
	{
		return registeredAddress;
	}

	public void setRegisteredAddress(AddressModel registeredAddress)
	{
		this.registeredAddress = registeredAddress;
	}

	public AddressModel getPrincipalAddress()
	{
		return principalAddress;
	}

	public void setPrincipalAddress(AddressModel principalAddress)
	{
		this.principalAddress = principalAddress;
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
