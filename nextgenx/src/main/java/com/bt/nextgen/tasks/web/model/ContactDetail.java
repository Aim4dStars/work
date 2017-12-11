package com.bt.nextgen.tasks.web.model;

import java.util.List;

import com.btfin.panorama.core.security.encryption.EncodedString;

public class ContactDetail
{
	public enum Icon
	{
		ChangeRequested,
		NotRegistered,
		Registered,
		Approved;
	}

	private String salutation;
	private String firstName;
	private String lastName;
	private String status;
	private String contactNumber;
	private String email;
	private boolean isPrimaryContact;
	private String iconStatus;
	private String description;
	private boolean hasOnlineAccess;
	private String clientId;
	private List<String> availableAccess;
	private String dob;
	private String termsconditionAcceptedDate;
	private String encodedClientId;
	private String encodedPortfolioId;
	private String activeAppLegalClientId;
	private boolean approver;


	public boolean isApprover()
	{
		return approver;
	}

	public void setApprover(boolean approver)
	{
		this.approver = approver;
	}

	public String getActiveAppLegalClientId()
	{
		return activeAppLegalClientId;
	}

	public void setActiveAppLegalClientId(String activeAppLegalClientId)
	{
		this.activeAppLegalClientId = activeAppLegalClientId;
	}
		
	public String getEncodedClientId()
	{
		return EncodedString.fromPlainText(clientId).toString();
	}

	public String getEncodedPortfolioId()
	{
		return encodedPortfolioId;
	}

	public void setEncodedPortfolioId(String encodedPortfolioId)
	{
		this.encodedPortfolioId = encodedPortfolioId;
	}
	
	public String getTermsconditionAcceptedDate() {
		return termsconditionAcceptedDate;
	}

	public void setTermsconditionAcceptedDate(String termsconditionAcceptedDate) {
		this.termsconditionAcceptedDate = termsconditionAcceptedDate;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public List<String> getAvailableAccess() {
		return availableAccess;
	}

	public void setAvailableAccess(List<String> availableAccess) {
		this.availableAccess = availableAccess;
	}

	public ContactDetail()
	{
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getContactNumber()
	{
		return contactNumber;
	}

	public void setContactNumber(String contactNumber)
	{
		this.contactNumber = contactNumber;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public boolean isPrimaryContact()
	{
		return isPrimaryContact;
	}

	public void setPrimaryContact(boolean primaryContact)
	{
		isPrimaryContact = primaryContact;
	}

	public String getSalutation()
	{
		return salutation;
	}

	public void setSalutation(String salutation)
	{
		this.salutation = salutation;
	}

	public String getIconStatus()
	{
		if(status != null)
		{
			if(status.equalsIgnoreCase("Registered"))
			{
				iconStatus = Icon.Registered.name();
			}
			else if(status.equalsIgnoreCase("Not Registered"))
			{
				iconStatus = Icon.NotRegistered.name();
			}
			else if(status.equalsIgnoreCase("Change Requested"))
			{
				iconStatus = Icon.ChangeRequested.name();
			}
			else if(status.equalsIgnoreCase("Approved"))
			{
				iconStatus = Icon.Approved.name();
			}
		}
		return iconStatus;
	}

	public void setIconStatus(String iconStatus)
	{
		this.iconStatus = iconStatus;
	}

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
	}

	public boolean isHasOnlineAccess() 
	{
		return hasOnlineAccess;
	}

	public void setHasOnlineAccess(boolean hasOnlineAccess) 
	{
		this.hasOnlineAccess = hasOnlineAccess;
	}

	public String getClientId() 
	{
		return clientId;
	}

	public void setClientId(String clientId) 
	{
		this.clientId = clientId;
	}
		
}
