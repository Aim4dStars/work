/**
 * 
 */
package com.bt.nextgen.serviceops.model;

import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationNumberType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.ContactMethodUsage;

/**
 * @author L081050
 */
public class MaintainIdvDetailReqModel {
	private static String BLANK_STRING = "";
	private String idvType = BLANK_STRING;
	private String iparId = BLANK_STRING;

	private String iparCisKey = BLANK_STRING;
	private String iparType = BLANK_STRING;
	private String iparCountry = BLANK_STRING;
	private String iparPostCode = BLANK_STRING;
	private String iparState = BLANK_STRING;
	private String iparCity = BLANK_STRING;
	private String iparAddressLine1 = BLANK_STRING;
	private String isForeignRegistered = BLANK_STRING;
	private String isIssuedAtState = BLANK_STRING;
	private String isIssuedAtCountry = BLANK_STRING;
	private String isRegulatedBy = BLANK_STRING;
	private String isOtherName = BLANK_STRING;
	private String involvedPartyNameType = BLANK_STRING;
	private String hfcpFullName = BLANK_STRING;
	private String postalAddressType = BLANK_STRING;
	private String requestedAction = BLANK_STRING;
	private String registrationNumber = BLANK_STRING;
	private String registrationNumberType = RegistrationNumberType.ABN.name();
	private String customerNumber = BLANK_STRING;
	// private String evRoleType;
	private String evBusinessEntityName = BLANK_STRING;
	private String evRecepientEmail1 = BLANK_STRING;
	private String evRecepientEmail2 = BLANK_STRING;
	private String isSoleTrader = BLANK_STRING;
	private String middleName = BLANK_STRING;
	private String fullName = BLANK_STRING;
	private String addressline2 = BLANK_STRING;
	private String cisKey = BLANK_STRING;

	private String silo = BLANK_STRING;

	private String personType = BLANK_STRING;

	private String firstName = BLANK_STRING;

	private String lastName = BLANK_STRING;

	private String agentCisKey = BLANK_STRING;

	private String agentName = BLANK_STRING;

	private String employerName = BLANK_STRING;

	private String dateOfBirth = BLANK_STRING;

	private String extIdvDate = BLANK_STRING;

	private String addressline1 = BLANK_STRING;

	private String city = BLANK_STRING;

	private String state = BLANK_STRING;

	private String pincode = BLANK_STRING;

	private String country = BLANK_STRING;

	private String documentType = BLANK_STRING;

	private String usage = ContactMethodUsage.RESIDENTIAL_ADDRESS.value();

	private String optAttrName = BLANK_STRING;

	private String optAttrVal = BLANK_STRING;

	public String getIdvType() {
		return idvType;
	}

	public void setIdvType(String idvType) {
		this.idvType = null != idvType ? idvType : BLANK_STRING;
	}

	public String getIparId() {
		return iparId;
	}

	public void setIparId(String iparId) {
		this.iparId = null != iparId ? iparId : BLANK_STRING;
	}

	public String getIparCisKey() {
		return iparCisKey;
	}

	public void setIparCisKey(String iparCisKey) {
		this.iparCisKey = null != iparCisKey ? iparCisKey : BLANK_STRING;
	}

	public String getIparType() {
		return iparType;
	}

	public void setIparType(String iparType) {
		this.iparType = null != iparType ? iparType : BLANK_STRING;
	}

	public String getIparCountry() {
		return iparCountry;
	}

	public void setIparCountry(String iparCountry) {
		this.iparCountry = null != iparCountry ? iparCountry : BLANK_STRING;
	}

	public String getIparPostCode() {
		return iparPostCode;
	}

	public void setIparPostCode(String iparPostCode) {
		this.iparPostCode = null != iparPostCode ? iparPostCode : BLANK_STRING;
	}

	public String getIparState() {
		return iparState;
	}

	public void setIparState(String iparState) {
		this.iparState = null != iparState ? iparState : BLANK_STRING;
	}

	public String getIparCity() {
		return iparCity;
	}

	public void setIparCity(String iparCity) {
		this.iparCity = null != iparCity ? iparCity : BLANK_STRING;
	}

	public String getIparAddressLine1() {
		return iparAddressLine1;
	}

	public void setIparAddressLine1(String iparAddressLine1) {
		this.iparAddressLine1 = null != iparAddressLine1 ? iparAddressLine1 : BLANK_STRING;
	}

	public String getIsForeignRegistered() {
		return isForeignRegistered;
	}

	public void setIsForeignRegistered(String isForeignRegistered) {
		this.isForeignRegistered = null != isForeignRegistered ? isForeignRegistered : BLANK_STRING;
	}

	public String getIsIssuedAtState() {
		return isIssuedAtState;
	}

	public void setIsIssuedAtState(String isIssuedAtState) {
		this.isIssuedAtState = null != isIssuedAtState ? isIssuedAtState : BLANK_STRING;
	}

	public String getIsIssuedAtCountry() {
		return isIssuedAtCountry;
	}

	public void setIsIssuedAtCountry(String isIssuedAtCountry) {
		this.isIssuedAtCountry = null != isIssuedAtCountry ? isIssuedAtCountry : BLANK_STRING;
	}

	public String getIsRegulatedBy() {
		return isRegulatedBy;
	}

	public void setIsRegulatedBy(String isRegulatedBy) {
		this.isRegulatedBy = null != isRegulatedBy ? isRegulatedBy : BLANK_STRING;
	}

	public String getIsOtherName() {
		return isOtherName;
	}

	public void setIsOtherName(String isOtherName) {
		this.isOtherName = null != isOtherName ? isOtherName : BLANK_STRING;
	}

	public String getInvolvedPartyNameType() {
		return involvedPartyNameType;
	}

	public void setInvolvedPartyNameType(String involvedPartyNameType) {
		this.involvedPartyNameType = null != involvedPartyNameType ? involvedPartyNameType : BLANK_STRING;
	}

	public String getHfcpFullName() {
		return hfcpFullName;
	}

	public void setHfcpFullName(String hfcpFullName) {
		this.hfcpFullName = null != hfcpFullName ? hfcpFullName : BLANK_STRING;
	}

	public String getPostalAddressType() {
		return postalAddressType;
	}

	public void setPostalAddressType(String postalAddressType) {
		this.postalAddressType = null != postalAddressType ? postalAddressType : BLANK_STRING;
	}

	public String getRequestedAction() {
		return requestedAction;
	}

	public void setRequestedAction(String requestedAction) {
		this.requestedAction = null != requestedAction ? requestedAction : BLANK_STRING;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = null != registrationNumber ? registrationNumber : BLANK_STRING;
	}

	public String getRegistrationNumberType() {
		return registrationNumberType;
	}

	public void setRegistrationNumberType(String registrationNumberType) {
		this.registrationNumberType = null != registrationNumberType ? registrationNumberType : RegistrationNumberType.ABN.name();
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = null != customerNumber ? customerNumber : BLANK_STRING;
	}

	/*
	 * public String getEvRoleType() { return evRoleType; }
	 * 
	 * public void setEvRoleType(String evRoleType) { this.evRoleType =
	 * evRoleType; }
	 */

	public String getEvBusinessEntityName() {
		return evBusinessEntityName;
	}

	public void setEvBusinessEntityName(String evBusinessEntityName) {
		this.evBusinessEntityName = null != evBusinessEntityName ? evBusinessEntityName : BLANK_STRING;
	}

	public String getEvRecepientEmail1() {
		return evRecepientEmail1;
	}

	public void setEvRecepientEmail1(String evRecepientEmail1) {
		this.evRecepientEmail1 = null != evRecepientEmail1 ? evRecepientEmail1 : BLANK_STRING;
	}

	public String getEvRecepientEmail2() {
		return evRecepientEmail2;
	}

	public void setEvRecepientEmail2(String evRecepientEmail2) {
		this.evRecepientEmail2 = null != evRecepientEmail2 ? evRecepientEmail2 : BLANK_STRING;
	}

	public String getIsSoleTrader() {
		return isSoleTrader;
	}

	public void setIsSoleTrader(String isSoleTrader) {
		this.isSoleTrader = null != isSoleTrader ? isSoleTrader : BLANK_STRING;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = null != middleName ? middleName : BLANK_STRING;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = null != fullName ? fullName : BLANK_STRING;
	}

	public String getAddressline2() {
		return addressline2;
	}

	public void setAddressline2(String addressline2) {
		this.addressline2 = null != addressline2 ? addressline2 : BLANK_STRING;
	}

	public String getOptAttrName() {
		return optAttrName;
	}

	public void setOptAttrName(String optAttrName) {
		this.optAttrName = null != optAttrName ? optAttrName : BLANK_STRING;
	}

	public String getOptAttrVal() {
		return optAttrVal;
	}

	public void setOptAttrVal(String optAttrVal) {
		this.optAttrVal = null != optAttrVal ? optAttrVal : BLANK_STRING;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = null != usage ? usage : ContactMethodUsage.RESIDENTIAL_ADDRESS.value();
	}

	public String getSilo() {
		return silo;
	}

	public void setSilo(String silo) {
		this.silo = null != silo ? silo : BLANK_STRING;
	}

	public String getPersonType() {
		return personType;
	}

	public void setPersonType(String personType) {
		this.personType = null != personType ? personType : BLANK_STRING;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = null != firstName ? firstName : BLANK_STRING;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = null != lastName ? lastName : BLANK_STRING;
	}

	public String getAgentCisKey() {
		return agentCisKey;
	}

	public void setAgentCisKey(String agentCisKey) {
		this.agentCisKey = null != agentCisKey ? agentCisKey : BLANK_STRING;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = null != agentName ? agentName : BLANK_STRING;
	}

	public String getEmployerName() {
		return employerName;
	}

	public void setEmployerName(String employerName) {
		this.employerName = null != employerName ? employerName : BLANK_STRING;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = null != dateOfBirth ? dateOfBirth : BLANK_STRING;
	}

	public String getExtIdvDate() {
		return extIdvDate;
	}

	public void setExtIdvDate(String extIdvDate) {
		this.extIdvDate = null != extIdvDate ? extIdvDate : BLANK_STRING;
	}

	public String getAddressline1() {
		return addressline1;
	}

	public void setAddressline1(String addressline1) {
		this.addressline1 = null != addressline1 ? addressline1 : BLANK_STRING;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = null != city ? city : BLANK_STRING;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = null != state ? state : BLANK_STRING;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = null != pincode ? pincode : BLANK_STRING;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = null != country ? country : BLANK_STRING;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = null != documentType ? documentType : BLANK_STRING;
	}

	public String getCisKey() {
		return cisKey;
	}

	public void setCisKey(String cisKey) {
		this.cisKey = null != cisKey ? cisKey : BLANK_STRING;
	}
}
