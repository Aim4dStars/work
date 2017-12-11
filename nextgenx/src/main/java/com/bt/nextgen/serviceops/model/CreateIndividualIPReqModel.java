/**
 * 
 */
package com.bt.nextgen.serviceops.model;

/**
 * @author L081050
 * 
 */
public class CreateIndividualIPReqModel {

	private static String EMPTY_STRING = "";

	private String silo = EMPTY_STRING;
	private String prefix = EMPTY_STRING;
	private String firstName = EMPTY_STRING;
	private String lastName = EMPTY_STRING;
	private String altName = EMPTY_STRING;
	private String gender = EMPTY_STRING;
	private String birthDate = EMPTY_STRING;
	private String foreignRegistered = EMPTY_STRING;
	private String roleType = EMPTY_STRING;
	private String purposeOfBusinessRelationship = EMPTY_STRING;
	private String sourceOfFunds = EMPTY_STRING;
	private String sourceOfWealth = EMPTY_STRING;
	private String usage = EMPTY_STRING;
	private String addresseeNameText = EMPTY_STRING;
	private String addressType = EMPTY_STRING;
	private String streetNumber = EMPTY_STRING;
	private String streetName = EMPTY_STRING;
	private String streetType = EMPTY_STRING;
	private String city = EMPTY_STRING;
	private String state = EMPTY_STRING;
	private String postCode = EMPTY_STRING;
	private String country = EMPTY_STRING;
	private String registrationIdentifierNumber = EMPTY_STRING;
	private String registrationIdentifierNumberType = EMPTY_STRING;
	private Boolean hasLoansWithOtherBanks;
	private String middleNames = EMPTY_STRING;
	private String preferredName = EMPTY_STRING;
	private String alternateName;
	private String isPreferred;
	private String employmentType = EMPTY_STRING;
	private String occupationCode = EMPTY_STRING;
	private String registrationArrangementsRegistrationNumber = EMPTY_STRING;
	private String registrationArrangementsRegistrationNumberType = EMPTY_STRING;
	private String registrationArrangementsCountry = EMPTY_STRING;
	private String registrationArrangementsState = EMPTY_STRING;
	private String addressLine1 = EMPTY_STRING;
	private String addressLine2 = EMPTY_STRING;
	private String addressLine3 = EMPTY_STRING;
	private String floorNumber = EMPTY_STRING;
	private String unitNumber = EMPTY_STRING;
	private String buildingName = EMPTY_STRING;

	public String getSilo() {
		return silo;
	}

	public void setSilo(String silo) {
		this.silo = null != silo ? silo : EMPTY_STRING;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = null != prefix ? prefix : EMPTY_STRING;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = null != firstName ? firstName : EMPTY_STRING;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = null != lastName ? lastName : EMPTY_STRING;
	}

	public String getAltName() {
		return altName;
	}

	public void setAltName(String altName) {
		this.altName = null != altName ? altName : EMPTY_STRING;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = null != gender ? gender : EMPTY_STRING;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = null != birthDate ? birthDate : EMPTY_STRING;
	}

	public String isForeignRegistered() {
		return foreignRegistered;
	}

	public void setForeignRegistered(String isForeignRegistered) {
		this.foreignRegistered = null != isForeignRegistered ? isForeignRegistered : EMPTY_STRING;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = null != roleType ? roleType : EMPTY_STRING;
	}

	public String getPurposeOfBusinessRelationship() {
		return purposeOfBusinessRelationship;
	}

	public void setPurposeOfBusinessRelationship(String purposeOfBusinessRelationship) {
		this.purposeOfBusinessRelationship = null != purposeOfBusinessRelationship ? purposeOfBusinessRelationship : EMPTY_STRING;
	}

	public String getSourceOfFunds() {
		return sourceOfFunds;
	}

	public void setSourceOfFunds(String sourceOfFunds) {
		this.sourceOfFunds = null != sourceOfFunds ? sourceOfFunds : EMPTY_STRING;
	}

	public String getSourceOfWealth() {
		return sourceOfWealth;
	}

	public void setSourceOfWealth(String sourceOfWealth) {
		this.sourceOfWealth = null != sourceOfWealth ? sourceOfWealth : EMPTY_STRING;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = null != usage ? usage : EMPTY_STRING;
	}

	public String getAddresseeNameText() {
		return addresseeNameText;
	}

	public void setAddresseeNameText(String addresseeNameText) {
		this.addresseeNameText = null != addresseeNameText ? addresseeNameText : EMPTY_STRING;
	}

	public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = null != addressType ? addressType : EMPTY_STRING;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = null != streetNumber ? streetNumber : EMPTY_STRING;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = null != streetName ? streetName : EMPTY_STRING;
	}

	public String getStreetType() {
		return streetType;
	}

	public void setStreetType(String streetType) {
		this.streetType = null != streetType ? streetType : EMPTY_STRING;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = null != city ? city : EMPTY_STRING;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = null != state ? state : EMPTY_STRING;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = null != postCode ? postCode : EMPTY_STRING;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = null != country ? country : EMPTY_STRING;
	}

	public String getRegistrationIdentifierNumber() {
		return registrationIdentifierNumber;
	}

	public void setRegistrationIdentifierNumber(String registrationIdentifierNumber) {
		this.registrationIdentifierNumber = null != registrationIdentifierNumber ? registrationIdentifierNumber : EMPTY_STRING;
	}

	public String getRegistrationIdentifierNumberType() {
		return registrationIdentifierNumberType;
	}

	public void setRegistrationIdentifierNumberType(String registrationIdentifierNumberType) {
		this.registrationIdentifierNumberType = null != registrationIdentifierNumberType ? registrationIdentifierNumberType : EMPTY_STRING;
	}

	public String getIsForeignRegistered() {
		return foreignRegistered;
	}

	public void setIsForeignRegistered(String isForeignRegistered) {
		this.foreignRegistered = null != isForeignRegistered ? isForeignRegistered : EMPTY_STRING;
	}

	public Boolean getHasLoansWithOtherBanks() {
		return hasLoansWithOtherBanks;
	}

	public void setHasLoansWithOtherBanks(Boolean hasLoansWithOtherBanks) {
		this.hasLoansWithOtherBanks = hasLoansWithOtherBanks;
	}

	public String getMiddleNames() {
		return middleNames;
	}

	public void setMiddleNames(String middleNames) {
		this.middleNames = null != middleNames ? middleNames : EMPTY_STRING;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = null != preferredName ? preferredName : EMPTY_STRING;
	}

	public String getAlternateName() {
		return alternateName;
	}

	public void setAlternateName(String alternateName) {
		this.alternateName = alternateName;
	}

	public String getIsPreferred() {
		return isPreferred;
	}

	public void setIsPreferred(String isPreferred) {
		this.isPreferred = isPreferred;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = null != employmentType ? employmentType : EMPTY_STRING;
	}

	public String getOccupationCode() {
		return occupationCode;
	}

	public void setOccupationCode(String occupationCode) {
		this.occupationCode = null != occupationCode ? occupationCode : EMPTY_STRING;
	}

	public String getRegistrationArrangementsRegistrationNumber() {
		return registrationArrangementsRegistrationNumber;
	}

	public void setRegistrationArrangementsRegistrationNumber(String registrationArrangementsRegistrationNumber) {
		this.registrationArrangementsRegistrationNumber = null != registrationArrangementsRegistrationNumber ? registrationArrangementsRegistrationNumber
				: EMPTY_STRING;
	}

	public String getRegistrationArrangementsRegistrationNumberType() {
		return registrationArrangementsRegistrationNumberType;
	}

	public void setRegistrationArrangementsRegistrationNumberType(String registrationArrangementsRegistrationNumberType) {
		this.registrationArrangementsRegistrationNumberType = null != registrationArrangementsRegistrationNumberType ? registrationArrangementsRegistrationNumberType
				: EMPTY_STRING;
	}

	public String getRegistrationArrangementsCountry() {
		return registrationArrangementsCountry;
	}

	public void setRegistrationArrangementsCountry(String registrationArrangementsCountry) {
		this.registrationArrangementsCountry = null != registrationArrangementsCountry ? registrationArrangementsCountry : EMPTY_STRING;
	}

	public String getRegistrationArrangementsState() {
		return registrationArrangementsState;
	}

	public void setRegistrationArrangementsState(String registrationArrangementsState) {
		this.registrationArrangementsState = null != registrationArrangementsState ? registrationArrangementsState : EMPTY_STRING;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = null != addressLine1 ? addressLine1 : EMPTY_STRING;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = null != addressLine2 ? addressLine2 : EMPTY_STRING;
	}

	public String getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(String floorNumber) {
		this.floorNumber = null != floorNumber ? floorNumber : EMPTY_STRING;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = null != unitNumber ? unitNumber : EMPTY_STRING;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = null != buildingName ? buildingName : EMPTY_STRING;
	}

	/**
	 * @return the addressLine3
	 */
	public String getAddressLine3() {
		return addressLine3;
	}

	/**
	 * @param addressLine3
	 *            the addressLine3 to set
	 */
	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = null != addressLine3 ? addressLine3 : EMPTY_STRING;
	}
}