package com.bt.nextgen.login.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "account")
@XmlType(propOrder =
{
	"adviserId", "phone", "email", "initiatedDate","updateRequestDate", "accountType", "accountDescription", "clientMembers"
})
public class AccountStatus
{

	private String adviserId;
	private String initiatedDate;
	private String updateRequestDate;
	private String phone;
	private String email;
	private String accountType;
	private String accountDescription;
	private List <ClientDetail> clientMembers;

	public AccountStatus()
	{}

	@XmlElement(name = "adviserName")
	public String getAdviserId()
	{
		return adviserId;
	}

	public void setAdviserId(String adviserId)
	{
		this.adviserId = adviserId;
	}

	@XmlElement(name = "initiatedDate")
	public String getInitiatedDate()
	{
		return initiatedDate;
	}

	public void setInitiatedDate(String initiatedDate)
	{
		this.initiatedDate = initiatedDate;
	}

	@XmlElement(name = "updateRequestDate")
	public String getUpdateRequestDate()
	{
		return updateRequestDate;
	}

	public void setUpdateRequestDate(String updateRequestDate)
	{
		this.updateRequestDate = updateRequestDate;
	}

	@XmlElement(name = "phone")
	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	@XmlElement(name = "email")
	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	@XmlElement(name = "accountType")
	public String getAccountType()
	{
		return accountType;
	}

	public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	@XmlElement(name = "accountDescription")
	public String getAccountDescription()
	{
		return accountDescription;
	}

	public void setAccountDescription(String accountDescription)
	{
		this.accountDescription = accountDescription;
	}

	@XmlElement(name = "client")
	public List <ClientDetail> getClientMembers()
	{
		return clientMembers;
	}

	public void setClientMembers(List <ClientDetail> clientMembers)
	{
		this.clientMembers = clientMembers;
	}

}
