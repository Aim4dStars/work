package com.bt.nextgen.clients.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.bt.nextgen.portfolio.domain.PortfolioDomain;

@XmlAccessorType(XmlAccessType.FIELD)
public class ClientDomain 
{
	@XmlElement
	private String clientId;
	@XmlElement
	private String firstName;
	@XmlElement
	private String lastName;
	@XmlElement
	private String adviserName;
	@XmlElement
	private String clientName;
	@XmlElement
	private String primaryContactName;
	@XmlElement
	private String email;
	@XmlElement
	private String activationStatus;
	@XmlElement(name = "wrapAccounts")
	private List <PortfolioDomain> wrapAccounts = new ArrayList<>();
	@XmlElement
	private String clientIdEncoded;
	@XmlElement
	private String adviserFirstName;
	@XmlElement
	private String adviserLastName;
	@XmlElement
	private String adviserEmail;
	@XmlElement
	private String adviserPhoneNumber;
	@XmlElement
	private String adviserPermission;
	
	public String getPrimaryContactName() {
		return primaryContactName;
	}
	public void setPrimaryContactName(String primaryContactName) {
		this.primaryContactName = primaryContactName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
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
	public String getAdviserName() {
		return adviserName;
	}
	public void setAdviserName(String adviserName) {
		this.adviserName = adviserName;
	}
	public String getActivationStatus() {
		return activationStatus;
	}
	public void setActivationStatus(String activationStatus) {
		this.activationStatus = activationStatus;
	}

	public List<PortfolioDomain> getWrapAccounts() {
		return wrapAccounts;
	}
	public void setWrapAccounts(List<PortfolioDomain> wrapAccounts) {
		this.wrapAccounts = wrapAccounts;
	}
	public String getClientIdEncoded() {
		return clientIdEncoded;
	}
	public void setClientIdEncoded(String clientIdEncoded) {
		this.clientIdEncoded = clientIdEncoded;
	}
	public String getAdviserFirstName() {
		return adviserFirstName;
	}
	public void setAdviserFirstName(String adviserFirstName) {
		this.adviserFirstName = adviserFirstName;
	}
	public String getAdviserLastName() {
		return adviserLastName;
	}
	public void setAdviserLastName(String adviserLastName) {
		this.adviserLastName = adviserLastName;
	}
	public String getAdviserEmail() {
		return adviserEmail;
	}
	public void setAdviserEmail(String adviserEmail) {
		this.adviserEmail = adviserEmail;
	}
	public String getAdviserPhoneNumber() {
		return adviserPhoneNumber;
	}
	public void setAdviserPhoneNumber(String adviserPhoneNumber) {
		this.adviserPhoneNumber = adviserPhoneNumber;
	}
	public String getAdviserPermission() {
		return adviserPermission;
	}
	public void setAdviserPermission(String adviserPermission) {
		this.adviserPermission = adviserPermission;
	}

}
