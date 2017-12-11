package com.bt.nextgen.api.client.model;

import java.util.List;

public class ExistingClientSearchDto extends ClientIdentificationDto
{
	private String fullName;
	private String displayName;
    private boolean idVerified;
    private String investorType;

	private List <AddressDto> addresses;

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

    public List <AddressDto> getAddresses()
	{
		return addresses;
	}

	public void setAddresses(List <AddressDto> addresses)
	{
		this.addresses = addresses;
	}

    public void setIdVerified(boolean idvVerified){
        this.idVerified = idvVerified;
    }

    public boolean isIdVerified() {
        return idVerified;
    }

    public String getInvestorType()
    {
        return investorType;
    }

    public void setInvestorType(String investorType)
    {
        this.investorType = investorType;
    }
}
