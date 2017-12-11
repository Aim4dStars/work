package com.bt.nextgen.service.group.customer.groupesb.address;

import com.bt.nextgen.clients.web.model.State;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.group.customer.groupesb.CustomerMetaData;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;

/**
 * Created by F057654 on 28/07/2015.
 */
public class CustomerAddress implements Address {

    private String addressId;

    private AddressKey addressKey;

    private String careOf;

    private String unitNumber;

    private String floorNumber;

    private String streetNumber;

    private String streetName;

    private String buildingName;

    private String suburb;

    private String poBox;

    private String city;

    private boolean domicile;

    private boolean mail;

    private String postCode;

    private String countryCode;

    private String countryName;

    private String stateCode;

    private String stateName;

    private int categoryId;

    private String electronicAddress;

    private String streetType;

    private String addressKind;

    private String addrCategoryId;

    private String addrMediumId;

    private String boxAddrPrefix;

    private String streetTypeUserId;

    private String countryAbbr;

    private String stateOther;

	private String modificationSeq;
    
    private CustomerMetaData customerMetaData;

    //Field extra in gcm but needs to be maintained as they need to sent back
    private String buildingTypeSuffix;

    private String streetTypeSuffix;

    private String addressLine1;

    private String addressLine2;

    private String addressLine3;

    private boolean internationalAddress;

    private String occupierName;

    public CustomerMetaData getCustomerMetaData() {
		return customerMetaData;
	}

	public void setCustomerMetaData(CustomerMetaData customerMetaData) {
		this.customerMetaData = customerMetaData;
	}
	
    public String getAddressId() {
        return addressId;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public boolean isMail() {
        return mail;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getStateName() {
        return stateName;
    }

    public String getAddressKind() {
        return addressKind;
    }

    public String getAddrCategoryId() {
        return addrCategoryId;
    }

    public String getAddrMediumId() {
        return addrMediumId;
    }

    public String getBoxAddrPrefix() {
        return boxAddrPrefix;
    }

    public String getBuildingTypeSuffix() {
        return buildingTypeSuffix;
    }

    public void setBuildingTypeSuffix(String buildingTypeSuffix) {
        this.buildingTypeSuffix = buildingTypeSuffix;
    }

    public String getStreetTypeSuffix() {
        return streetTypeSuffix;
    }

    public void setStreetTypeSuffix(String streetTypeSuffix) {
        this.streetTypeSuffix = streetTypeSuffix;
    }

    @Override
    public AddressKey getAddressKey()
    {
        if (addressKey == null)
            addressKey = AddressKey.valueOf(addressId);
        return addressKey;
    }

    @Override
    public String getCareOf()
    {
        return careOf;
    }

    @Override
    public String getUnit()
    {
        return unitNumber;
    }

    @Override
    public String getFloor()
    {
        return floorNumber;
    }

    @Override
    public String getStreetNumber()
    {
        return streetNumber;
    }

    @Override
    public String getStreetName()
    {
        return streetName;
    }

    @Override
    public String getStreetType()
    {
        return streetType;
    }

    @Override
    public String getStreetTypeId() {
        return streetType;
    }

    @Override
    public String getStreetTypeUserId()
    {
        return streetTypeUserId;
    }

    @Override
    public String getBuilding()
    {
        return buildingName;
    }

    @Override
    public String getSuburb()
    {
        return suburb;
    }

    @Override
    public String getStateAbbr()
    {
        if(State.forStateValue(stateName) != null){

            return State.forStateValue(stateName).name();
        }
        return null;
    }


    @Override
    public String getState()
    {
        return stateName;
    }

    @Override
    public String getPoBox()
    {
        return poBox;
    }

    @Override
    public String getCity()
    {
        return city;
    }

    @Override
    public boolean isDomicile()
    {
        return domicile;
    }

    @Override
    public String getStateCode()
    {
        return stateCode;
    }

    @Override
    public String getPostCode()
    {
        return postCode;
    }

    @Override
    public String getCountryAbbr()
    {
        return countryAbbr;
    }

    @Override
    public String getCountryCode()
    {
        return countryCode;
    }

    @Override
    public String getCountry()
    {
        return countryName;
    }

    @Override
    public String getModificationSeq()
    {
        return modificationSeq;
    }

    @Override
    public boolean isMailingAddress()
    {
        return mail;
    }

    @Override
    public int getCategoryId()
    {
        return categoryId;
    }

    @Override
    public boolean isPreferred()
    {
        return Constants.PREFERRED_CONTACT.equals(addressKind) ? true : false;
    }

    @Override
    public String getElectronicAddress()
    {
        return electronicAddress;
    }

    @Override
    public AddressMedium getAddressType()
    {
        return AddressMedium.getAddressMedium(addrMediumId);
    }

    @Override
    public AddressType getPostAddress()
    {
        return AddressType.getAddressType(addrCategoryId);
    }

    @Override
    public String getPoBoxPrefix()
    {
        return boxAddrPrefix;
    }

    @Override
    public String getStateOther()
    {
        return stateOther;
    }

    @Override
    public String getAddressLine1() {
        return addressLine1;
    }

    @Override
    public String getAddressLine2() {
        return addressLine2;
    }

    @Override
    public String getAddressLine3() {
        return addressLine3;
    }

    @Override
    public boolean isInternationalAddress() {
        return internationalAddress;
    }

    @Override
    public String getOccupierName() {
        return occupierName;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public void setAddressKey(AddressKey addressKey) {
        this.addressKey = addressKey;
    }

    public void setCareOf(String careOf) {
        this.careOf = careOf;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDomicile(boolean domicile) {
        this.domicile = domicile;
    }

    public void setMail(boolean mail) {
        this.mail = mail;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setElectronicAddress(String electronicAddress) {
        this.electronicAddress = electronicAddress;
    }

    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }

    public void setAddressKind(String addressKind) {
        this.addressKind = addressKind;
    }

    public void setAddrCategoryId(String addrCategoryId) {
        this.addrCategoryId = addrCategoryId;
    }

    public void setAddrMediumId(String addrMediumId) {
        this.addrMediumId = addrMediumId;
    }

    public void setBoxAddrPrefix(String boxAddrPrefix) {
        this.boxAddrPrefix = boxAddrPrefix;
    }

    public void setStreetTypeUserId(String streetTypeUserId) {
        this.streetTypeUserId = streetTypeUserId;
    }

    public void setCountryAbbr(String countryAbbr) {
        this.countryAbbr = countryAbbr;
    }

    public void setStateOther(String stateOther) {
        this.stateOther = stateOther;
    }

    public void setModificationSeq(String modificationSeq) {
        this.modificationSeq = modificationSeq;
    }


    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public void setInternationalAddress(boolean internationalAddress) {
        this.internationalAddress = internationalAddress;
    }
}
