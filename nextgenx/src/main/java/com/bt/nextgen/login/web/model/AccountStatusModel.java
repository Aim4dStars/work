package com.bt.nextgen.login.web.model;

import com.bt.nextgen.clients.web.model.ClientModel;

import java.util.List;

public class AccountStatusModel
{
	private String adviserId;
	private String initiatedDate;
	private String updateRequestDate;
	private String phone;
	private String email;
	private String accountType;
	private String accountSubType;
	private String accountName;
	private String accountDescription;
	private String accountStatusMsg;
	private String applicationReferenceNo;
	private String requestType;
	private String requestStatus;
	private String adviserFirstName;
	private String adviserLastName;
	private String adviserEmail;
	private String adviserPhoneNumber;
	private String noOfApplicants;
	private String productName;
	
	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	private List <ClientModel> clientList;

	public String getAdviserId()
	{
		return adviserId;
	}

	public void setAdviserId(String adviserId)
	{
		this.adviserId = adviserId;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public List <ClientModel> getClientList()
	{
		return clientList;
	}

	public void setClientList(List <ClientModel> clientList)
	{
		this.clientList = clientList;
	}

	public String getAccountType()
	{
		return accountType;
	}

	public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	public String getAccountSubType()
	{
		return accountSubType;
	}

	public void setAccountSubType(String accountSubType)
	{
		this.accountSubType = accountSubType;
	}

	public String getAccountDescription()
	{
		return accountDescription;
	}

	public void setAccountDescription(String accountDescription)
	{
		this.accountDescription = accountDescription;
	}

	public String getInitiatedDate()
	{
		return initiatedDate;
	}

	public void setInitiatedDate(String initiatedDate)
	{
		this.initiatedDate = initiatedDate;
	}

	public String getUpdateRequestDate()
	{
		return updateRequestDate;
	}

	public void setUpdateRequestDate(String updateRequestDate)
	{
		this.updateRequestDate = updateRequestDate;
	}

	public String getAccountStatusMsg()
	{
		return accountStatusMsg;
	}

	public void setAccountStatusMsg(String accountStatusMsg)
	{
		this.accountStatusMsg = accountStatusMsg;
	}

	public String getApplicationReferenceNo()
	{
		return applicationReferenceNo;
	}

	public void setApplicationReferenceNo(String applicationReferenceNo)
	{
		this.applicationReferenceNo = applicationReferenceNo;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getRequestType()
	{
		return requestType;
	}

	public void setRequestType(String requestType)
	{
		this.requestType = requestType;
	}

	public String getRequestStatus()
	{
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus)
	{
		this.requestStatus = requestStatus;
	}

	public String getAdviserFirstName()
	{
		return adviserFirstName;
	}

	public void setAdviserFirstName(String adviserFirstName)
	{
		this.adviserFirstName = adviserFirstName;
	}

	public String getAdviserLastName()
	{
		return adviserLastName;
	}

	public void setAdviserLastName(String adviserLastName)
	{
		this.adviserLastName = adviserLastName;
	}

	public String getAdviserEmail()
	{
		return adviserEmail;
	}

	public void setAdviserEmail(String adviserEmail)
	{
		this.adviserEmail = adviserEmail;
	}

	public String getAdviserPhoneNumber()
	{
		return adviserPhoneNumber;
	}

	public void setAdviserPhoneNumber(String adviserPhoneNumber)
	{
		this.adviserPhoneNumber = adviserPhoneNumber;
	}

	public String getNoOfApplicants()
	{
		return noOfApplicants;
	}

	public void setNoOfApplicants(String noOfApplicants)
	{
		this.noOfApplicants = noOfApplicants;
	}

}
