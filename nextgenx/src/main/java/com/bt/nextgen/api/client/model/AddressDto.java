package com.bt.nextgen.api.client.model;

import com.bt.nextgen.core.api.model.BaseDto;

public class AddressDto extends BaseDto
{
	private String careOf;
	private String unitNumber;
	private String floor;
	private String streetNumber;
	private String streetName;
	private String streetType;
	private String streetTypeUserId;
	private String building;
	private String suburb;
	private String state;
	private String stateAbbr;
	private String poBox;
	private String city;
	private String stateCode;
	private String postcode;
	private String countryCode;
	private String country;
	private String countryAbbr;
	private String modificationSeq;
	private String poBoxPrefix;
	private boolean domicile;
	private boolean mailingAddress;
	private String addressType;
	private AddressKey addressKey;
    private boolean gcmAddress;
	private boolean internationalAddress;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private boolean standardAddressFormat;
    private String gcmStreetType;
	private String addressIdentifier;

    public AddressKey getAddressKey()
	{
		return addressKey;
	}

	public void setAddressKey(AddressKey addressKey)
	{
		this.addressKey = addressKey;
	}

	public String getCareOf()
	{
		return careOf;
	}

	public void setCareOf(String careOf)
	{
		this.careOf = careOf;
	}

	public String getUnitNumber()
	{
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber)
	{
		this.unitNumber = unitNumber;
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

	public String getStreetTypeUserId()
	{
		return streetTypeUserId;
	}

	public void setStreetTypeUserId(String streetTypeUserId)
	{
		this.streetTypeUserId = streetTypeUserId;
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

	public String getPostcode()
	{
		return postcode;
	}

	public void setPostcode(String postcode)
	{
		this.postcode = postcode;
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

	public String getModificationSeq()
	{
		return modificationSeq;
	}

	public void setModificationSeq(String modificationSeq)
	{
		this.modificationSeq = modificationSeq;
	}

	public String getPoBoxPrefix()
	{
		return poBoxPrefix;
	}

	public void setPoBoxPrefix(String poBoxPrefix)
	{
		this.poBoxPrefix = poBoxPrefix;
	}

	public boolean isDomicile()
	{
		return domicile;
	}

	public void setDomicile(boolean domicile)
	{
		this.domicile = domicile;
	}

	public boolean isMailingAddress()
	{
		return mailingAddress;
	}

	public void setMailingAddress(boolean mailingAddress)
	{
		this.mailingAddress = mailingAddress;
	}

	public String getAddressType()
	{
		return addressType;
	}

	public void setAddressType(String addressType)
	{
		this.addressType = addressType;
	}

	public String getStateAbbr()
	{
		return stateAbbr;
	}

	public void setStateAbbr(String stateAbbr)
	{
		this.stateAbbr = stateAbbr;
	}

	public String getCountryAbbr()
	{
		return countryAbbr;
	}

	public void setCountryAbbr(String countryAbbr)
	{
		this.countryAbbr = countryAbbr;
	}


    public boolean isGcmAddress() {
        return gcmAddress;
    }

    public void setGcmAddress(boolean isGcmAddress) {
        this.gcmAddress = isGcmAddress;
    }


	public boolean isInternationalAddress() {
		return internationalAddress;
	}

	public void setInternationalAddress(boolean internationalAddress) {
		this.internationalAddress = internationalAddress;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	/**
	 * This is used for GCM address retrieval
	 * @return
	 */
	public boolean isStandardAddressFormat() {
		return standardAddressFormat;
	}

	/**
	 * This is set when retrieving an address from GCM
	 * @param standardAddressFormat
	 */
	public void setStandardAddressFormat(boolean standardAddressFormat) {
		this.standardAddressFormat = standardAddressFormat;
	}

    public void setGcmStreetType(String gcmStreetType) {
        this.gcmStreetType = gcmStreetType;
    }

    public String getGcmStreetType() {
        return gcmStreetType;
    }

	public String getAddressIdentifier() {
		return addressIdentifier;
	}

	public void setAddressIdentifier(String addressIdentifier) {
		this.addressIdentifier = addressIdentifier;
	}

}
