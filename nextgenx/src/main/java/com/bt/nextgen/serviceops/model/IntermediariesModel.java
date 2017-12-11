package com.bt.nextgen.serviceops.model;

import com.bt.nextgen.core.web.model.AddressModel;

public class IntermediariesModel 
{
	public String getAvaloqUserId() {
		return avaloqUserId;
	}

	public void setAvaloqUserId(String avaloqUserId) {
		this.avaloqUserId = avaloqUserId;
	}

	private String firstName;
	private String lastName;
	private String userId;
	private String dealerGroup;
	private String primaryMobileNumber;
	private String email;
	private AddressModel addresses;
	private String city;
	private String state;
	private String phone;
	String clientId;
	private String role;
	private String avaloqUserId;
	private String companyName;

	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDealerGroup() {
		return dealerGroup;
	}
	public void setDealerGroup(String dealerGroup) {
		this.dealerGroup = dealerGroup;
	}
	public String getPrimaryMobileNumber() {
		return primaryMobileNumber;
	}
	public void setPrimaryMobileNumber(String primaryMobileNumber) {
		this.primaryMobileNumber = primaryMobileNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public AddressModel getAddresses() {
		return addresses;
	}
	public void setAddresses(AddressModel addresses) {
		this.addresses = addresses;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getRole()
	{
		return role;
	}
	public void setRole(String role)
	{
		this.role = role;
	}
	
}