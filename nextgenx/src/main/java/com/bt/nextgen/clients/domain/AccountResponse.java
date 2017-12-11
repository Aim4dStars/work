package com.bt.nextgen.clients.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class AccountResponse
{
	private List <AccountJaxb> accounts;

	@XmlElementWrapper
	@XmlElement(name = "account")
	public List <AccountJaxb> getAccounts()
	{
		return accounts;
	}

	public void setAccounts(List <AccountJaxb> accounts)
	{
		this.accounts = accounts;
	}

}
