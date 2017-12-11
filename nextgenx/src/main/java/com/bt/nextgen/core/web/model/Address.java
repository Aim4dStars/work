package com.bt.nextgen.core.web.model;

import java.io.Serializable;

/**
 * Model represents the party service fields.
 *
 */
public class Address implements Serializable
{
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String country;
	private String pin;
	private String type;
	private String streetNumber;
	private String street;
	private String boxPrefix;
	private String poBoxNumber;
	private String buildingName;
	private String streetType;
	private String unitNumber;
	private String floorNumber;
	private String partyResponse;
	private String streetName;
	private String propertyName;
    private String matchConfidence;

	public String getAddressLine1()
	{
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1)
	{
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2()
	{
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2)
	{
		this.addressLine2 = addressLine2;
	}
	public String getCity()
	{
		return city;
	}
	public void setCity(String city)
	{
		this.city = city;
	}
	public String getState()
	{
		return state;
	}
	public void setState(String state)
	{
		this.state = state;
	}
	public String getCountry()
	{
		return country;
	}
	public void setCountry(String country)
	{
		this.country = country;
	}
	public String getPin()
	{
		return pin;
	}
	public void setPin(String pin)
	{
		this.pin = pin;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public String getStreetNumber()
	{
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber)
	{
		this.streetNumber = streetNumber;
	}
	public String getStreet()
	{
		return street;
	}
	public void setStreet(String street)
	{
		this.street = street;
	}
	public String getBoxPrefix()
	{
		return boxPrefix;
	}
	public void setBoxPrefix(String boxPrefix)
	{
		this.boxPrefix = boxPrefix;
	}
	public String getPoBoxNumber()
	{
		return poBoxNumber;
	}
	public void setPoBoxNumber(String poBoxNumber)
	{
		this.poBoxNumber = poBoxNumber;
	}
	public String getBuildingName()
	{
		return buildingName;
	}
	public void setBuildingName(String buildingName)
	{
		this.buildingName = buildingName;
	}
	public String getStreetType()
	{
		return streetType;
	}
	public void setStreetType(String streetType)
	{
		this.streetType = streetType;
	}
	public String getUnitNumber()
	{
		return unitNumber;
	}
	public void setUnitNumber(String unitNumber)
	{
		this.unitNumber = unitNumber;
	}
	public String getFloorNumber()
	{
		return floorNumber;
	}
	public void setFloorNumber(String floorNumber)
	{
		this.floorNumber = floorNumber;
	}
	public String getPartyResponse()
	{
		return partyResponse;
	}
	public void setPartyResponse(String partyResponse)
	{
		this.partyResponse = partyResponse;
	}
	public String getStreetName()
	{
		return streetName;
	}
	public void setStreetName(String streetName)
	{
		this.streetName = streetName;
	}
	public String getPropertyName()
	{
		return propertyName;
	}
	public void setPropertyName(String propertyName)
	{
		this.propertyName = propertyName;
	}
    public String getMatchConfidence() {
        return matchConfidence;
    }
    public void setMatchConfidence(String matchConfidence) {
        this.matchConfidence = matchConfidence;
    }
}
