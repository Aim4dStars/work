package com.bt.nextgen.core.web.model;

public class PhoneModel
{
	private String phoneNumber;
	private boolean primary;

	private String type;
	private boolean smsIdentifier;
	private boolean preferedContact;
	
	private String homeWorkPhoneType;

//	private boolean preferredContactMethod;
//
//	
//
//	public boolean isPreferredContactMethod()
//	{
//		return preferredContactMethod;
//	}
//
//	public void setPreferredContactMethod(boolean preferredContactMethod)
//	{
//		this.preferredContactMethod = preferredContactMethod;
//	}


	public String getHomeWorkPhoneType() {
		return homeWorkPhoneType;
	}

	public void setHomeWorkPhoneType(String homeWorkPhoneType) {
		this.homeWorkPhoneType = homeWorkPhoneType;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public boolean isPrimary()
	{
		return primary;
	}

	public void setPrimary(boolean primary)
	{
		this.primary = primary;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public boolean isSmsIdentifier()
	{
		return smsIdentifier;
	}

	public void setSmsIdentifier(boolean smsIdentifier)
	{
		this.smsIdentifier = smsIdentifier;
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
