package com.bt.nextgen.service.avaloq.domain;

import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressUpdate;

public class AddressDetails implements AddressUpdate
{
	private AddressKey addressKey;
	private String addrModificationNumber;
	private String unit;
	private String floor;
	private String streetNumber;
	private String streetName;
	private String streetType;
	private String building;
	private String suburb;
	private String state;
	private String stateOther;
	private String poBox;
	private String city;
	private String stateCode;
	private String postCode;
	private String countryCode;
	private String country;
	private String poBoxPrefix;
	private boolean mailingAddress;
	private String electronicAddress;
	private String addressKindId;
	private String addressMediumId;
	private String addressCatagoryId;

	//CHECKSTYLE:OFF
	// because it detects a load of duplicates due to the domain and dto objects being in the same projects
	public AddressKey getAddressKey()
	{
		return addressKey;
	}

	public void setAddressKey(AddressKey addressKey)
	{
		this.addressKey = addressKey;
	}

	public String getAddrModificationNumber()
	{
		return addrModificationNumber;
	}

	public void setAddrModificationNumber(String addrModificationNumber)
	{
		this.addrModificationNumber = addrModificationNumber;
	}

	public String getUnit()
	{
		return unit;
	}

	public void setUnit(String unit)
	{
		this.unit = unit;
	}

	public String getFloor()
	{
		return floor;
	}

	public void setFloor(String floor)
	{
		this.floor = floor;
	}

	public String getStreetNumber()
	{
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber)
	{
		this.streetNumber = streetNumber;
	}

	public String getStreetName()
	{
		return streetName;
	}

	public void setStreetName(String streetName)
	{
		this.streetName = streetName;
	}

	public String getStreetType()
	{
		return streetType;
	}

	public void setStreetType(String streetType)
	{
		this.streetType = streetType;
	}

	public String getBuilding()
	{
		return building;
	}

	public void setBuilding(String building)
	{
		this.building = building;
	}

	public String getSuburb()
	{
		return suburb;
	}

	public void setSuburb(String suburb)
	{
		this.suburb = suburb;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getPoBox()
	{
		return poBox;
	}

	public void setPoBox(String poBox)
	{
		this.poBox = poBox;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getStateCode()
	{
		return stateCode;
	}

	public void setStateCode(String stateCode)
	{
		this.stateCode = stateCode;
	}

	public String getPostCode()
	{
		return postCode;
	}

	public void setPostCode(String postCode)
	{
		this.postCode = postCode;
	}

	public String getCountryCode()
	{
		return countryCode;
	}

	public void setCountryCode(String countryCode)
	{
		this.countryCode = countryCode;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getPoBoxPrefix()
	{
		return poBoxPrefix;
	}

	public void setPoBoxPrefix(String poBoxPrefix)
	{
		this.poBoxPrefix = poBoxPrefix;
	}

	public boolean isMailingAddress()
	{
		return mailingAddress;
	}

	public void setMailingAddress(boolean mailingAddress)
	{
		this.mailingAddress = mailingAddress;
	}

	public String getElectronicAddress()
	{
		return electronicAddress;
	}

	public void setElectronicAddress(String electronicAddress)
	{
		this.electronicAddress = electronicAddress;
	}

	public String getAddressKindId()
	{
		return addressKindId;
	}

	public void setAddressKindId(String addressKindId)
	{
		this.addressKindId = addressKindId;
	}

	public String getAddressMediumId()
	{
		return addressMediumId;
	}

	public void setAddressMediumId(String addressMediumId)
	{
		this.addressMediumId = addressMediumId;
	}

	public String getStateOther()
	{
		return stateOther;
	}

	public void setStateOther(String stateOther)
	{
		this.stateOther = stateOther;
	}

	public String getAddressCatagoryId()
	{
		return addressCatagoryId;
	}

	public void setAddressCatagoryId(String addressCatagoryId)
	{
		this.addressCatagoryId = addressCatagoryId;
	}
	//CHECKSTYLE:ON
}
