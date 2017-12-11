package com.bt.nextgen.core.web.model;

import java.io.Serializable;

public class EmailModel implements Serializable
{
	private String email;
	private boolean primary;

	private boolean preferedContact;

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public boolean isPrimary()
	{
		return primary;
	}

	public void setPrimary(boolean primary)
	{
		this.primary = primary;
	}

	public boolean isPreferedContact()
	{
		return preferedContact;
	}

	public void setPreferedContact(boolean preferedContact)
	{
		this.preferedContact = preferedContact;
	}

}
