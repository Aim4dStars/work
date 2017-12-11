package com.bt.nextgen.core.web.model;

import java.io.Serializable;

public class AddressModel implements Serializable {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String pin;
    private String type;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getAddressCategory() {
        return addressCategory;
    }

    public void setAddressCategory(String addressCategory) {
        this.addressCategory = addressCategory;
    }

    public String getAddressMedium() {
        return addressMedium;
    }

    public void setAddressMedium(String addressMedium) {
        this.addressMedium = addressMedium;
    }

    public String getAddressKind() {
        return addressKind;
    }

    public void setAddressKind(String addressKind) {
        this.addressKind = addressKind;
    }

    public String getIsMailingAddress() {
        return isMailingAddress;
    }

    public void setIsMailingAddress(String isMailingAddress) {
        this.isMailingAddress = isMailingAddress;
    }

    public String getIsDomicileAddress() {
        return isDomicileAddress;
    }

    public void setIsDomicileAddress(String isDomicileAddress) {
        this.isDomicileAddress = isDomicileAddress;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getBoxPrefix() {
        return boxPrefix;
    }

    public void setBoxPrefix(String boxPrefix) {
        this.boxPrefix = boxPrefix;
    }

    public String getPoBoxNumber() {
        return poBoxNumber;
    }

    public void setPoBoxNumber(String poBoxNumber) {
        this.poBoxNumber = poBoxNumber;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getStreetType() {
        return streetType;
    }

    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

}
