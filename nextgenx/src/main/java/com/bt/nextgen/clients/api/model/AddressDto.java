package com.bt.nextgen.clients.api.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class AddressDto extends BaseDto implements KeyedDto <AddressKey>
{
	private AddressKey key;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String country;
	private String pin;
	private String addressType;
	private boolean validationRequired;
    private String matchConfidence;

	// Avaloq Fields
	private String fullAddress;
	private String addressCategory;
	private String addressMedium;
	private String addressKind;
	private String isMailingAddress;
	private String isDomicileAddress;
	private String streetNumber;
	private String street;
	private String postcode;
	private String boxPrefix;
	private String poBoxNumber;
	private String suburb;
	private String profession;
	private String buildingName;
	private String streetType;
	private String unitNumber;
	private String floorNumber;
	private String officeAddress;
	private String businessAddress;
	private String trustAddress;
	private String smsfAddress;
	private String errorMessage;
    private String propertyName;
    private String streetName;

    public AddressKey getKey()
	{
		return key;
	}

	public void setKey(AddressKey key)
	{
		this.key = key;
	}

	public String getTrustAddress()
	{
		return trustAddress;
	}

	public void setTrustAddress(String trustAddress)
	{
		this.trustAddress = trustAddress;
	}

	public String getSmsfAddress()
	{
		return smsfAddress;
	}

	public void setSmsfAddress(String smsfAddress)
	{
		this.smsfAddress = smsfAddress;
	}

	public String getOfficeAddress()
	{
		return officeAddress;
	}

	public void setOfficeAddress(String officeAddress)
	{
		this.officeAddress = officeAddress;
	}

	public String getBusinessAddress()
	{
		return businessAddress;
	}

	public void setBusinessAddress(String businessAddress)
	{
		this.businessAddress = businessAddress;
	}

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

	public String getAddressType()
	{
		return addressType;
	}

	public void setAddressType(String addressType)
	{
		this.addressType = addressType;
	}

	public String getFullAddress()
	{
		return fullAddress;
	}

	public void setFullAddress(String fullAddress)
	{
		this.fullAddress = fullAddress;
	}

	public String getAddressCategory()
	{
		return addressCategory;
	}

	public void setAddressCategory(String addressCategory)
	{
		this.addressCategory = addressCategory;
	}

	public String getAddressMedium()
	{
		return addressMedium;
	}

	public void setAddressMedium(String addressMedium)
	{
		this.addressMedium = addressMedium;
	}

	public String getAddressKind()
	{
		return addressKind;
	}

	public void setAddressKind(String addressKind)
	{
		this.addressKind = addressKind;
	}

	public String getIsMailingAddress()
	{
		return isMailingAddress;
	}

	public void setIsMailingAddress(String isMailingAddress)
	{
		this.isMailingAddress = isMailingAddress;
	}

	public String getIsDomicileAddress()
	{
		return isDomicileAddress;
	}

	public void setIsDomicileAddress(String isDomicileAddress)
	{
		this.isDomicileAddress = isDomicileAddress;
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

	public String getPostcode()
	{
		return postcode;
	}

	public void setPostcode(String postcode)
	{
		this.postcode = postcode;
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

	public String getSuburb()
	{
		return suburb;
	}

	public void setSuburb(String suburb)
	{
		this.suburb = suburb;
	}

	public String getProfession()
	{
		return profession;
	}

	public void setProfession(String profession)
	{
		this.profession = profession;
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

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public boolean isValidationRequired()
	{
		return validationRequired;
	}

	public void setValidationRequired(boolean validationRequired)
	{
		this.validationRequired = validationRequired;
	}

	@Override
	public String toString()
	{

		return super.toString();
	}
    public String getMatchConfidence() {
        return matchConfidence;
    }

    public void setMatchConfidence(String matchConfidence) {
        this.matchConfidence = matchConfidence;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }


    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetName() {
        return streetName;
    }
}
