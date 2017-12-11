package com.bt.nextgen.service.gesb.locationmanagement;

import java.io.Serializable;

import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;

/**
 * Created by F030695 on 24/10/2016.
 */
public class PostalAddress implements Address, Serializable {

    private static final String AU = "AU";
    private String buildingName;
    private String unitNumber;
    private String streetNumber;
    private String streetName;
    private String streetType;
    private String city;
    private String state;
    private String postcode;
    private String floor;

    @Override
    public AddressKey getAddressKey() {
        return null;
    }

    @Override
    public String getCareOf() {
        return null;
    }

    @Override
    public String getUnit() {
        return unitNumber;
    }

    @Override
    public String getFloor() {
        return this.floor;
    }


    public void setFloor(String floor) {
        this.floor = floor;
    }

    @Override
    public String getStreetNumber() {
        return streetNumber;
    }

    @Override
    public String getStreetName() {
        return streetName;
    }

    @Override
    public String getStreetType() {
        return streetType;
    }

    @Override
    public String getStreetTypeId() {
        return null;
    }

    @Override
    public String getStreetTypeUserId() {
        return null;
    }

    @Override
    public String getBuilding() {
        return buildingName;
    }

    @Override
    public String getSuburb() {
        return city;
    }

    @Override
    public String getStateAbbr() {
        return state;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public String getPoBox() {
        return null;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public boolean isDomicile() {
        return false;
    }

    @Override
    public String getStateCode() {
        return null;
    }

    @Override
    public String getPostCode() {
        return postcode;
    }

    @Override
    public String getCountryAbbr() {
        //Retrieve Postal Address from svc0454 only returns Australian addresses
        return AU;
    }

    @Override
    public String getCountryCode() {
        //Retrieve Postal Address from svc0454 only returns Australian addresses
        return AU;
    }

    @Override
    public String getCountry() {
        //Retrieve Postal Address from svc0454 only returns Australian addresses
        return AU;
    }

    @Override
    public String getModificationSeq() {
        return null;
    }

    @Override
    public boolean isMailingAddress() {
        return false;
    }

    @Override
    public int getCategoryId() {
        return 0;
    }

    @Override
    public boolean isPreferred() {
        return false;
    }

    @Override
    public String getElectronicAddress() {
        return null;
    }

    @Override
    public AddressMedium getAddressType() {
        return null;
    }

    @Override
    public AddressType getPostAddress() {
        return null;
    }

    @Override
    public String getPoBoxPrefix() {
        return null;
    }

    @Override
    public String getStateOther() {
        return null;
    }

    @Override
    public String getAddressLine1() {
        return null;
    }

    @Override
    public String getAddressLine2() {
        return null;
    }

    @Override
    public String getAddressLine3() {
        return null;
    }

    @Override
    public boolean isInternationalAddress() {
        return false;
    }

    @Override
    public String getOccupierName() {
        return null;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
