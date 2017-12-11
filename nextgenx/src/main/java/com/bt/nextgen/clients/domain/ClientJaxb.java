package com.bt.nextgen.clients.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class ClientJaxb
{
	private String clientId;
	private String firstName;
	private String lastName;

	private List <AccountJaxb> accounts;

	@XmlElementWrapper
	@XmlElement(name = "account")
	public List <AccountJaxb> getAccounts()
	{
		return accounts;
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

	public void setAccounts(List <AccountJaxb> accounts)
	{
		this.accounts = accounts;
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
