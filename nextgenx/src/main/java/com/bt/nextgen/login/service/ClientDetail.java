package com.bt.nextgen.login.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "client")
public class ClientDetail
{
	private String salutation;
	private String firstName;
	private String lastName;
	private String gender;
	private String age;
	private String userName;
	private String phone;
	private String email;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String country;
	private String pin;
	private String adviserName;
	private String status;
	private String accountHolderType;

	public ClientDetail()
	{}

	@XmlElement(name = "firstName")
	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	@XmlElement(name = "lastName")
	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	@XmlElement(name = "gender")
	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	@XmlElement(name = "age")
	public String getAge()
	{
		return age;
	}

	public void setAge(String age)
	{
		this.age = age;
	}

	@XmlElement(name = "userName")
	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
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

	@XmlElement(name = "addressLine1")
	public String getAddressLine1()
	{
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1)
	{
		this.addressLine1 = addressLine1;
	}

	@XmlElement(name = "addressLine2")
	public String getAddressLine2()
	{
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2)
	{
		this.addressLine2 = addressLine2;
	}

	@XmlElement(name = "city")
	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	@XmlElement(name = "state")
	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	@XmlElement(name = "country")
	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	@XmlElement(name = "pin")
	public String getPin()
	{
		return pin;
	}

	public void setPin(String pin)
	{
		this.pin = pin;
	}

	@XmlElement(name = "adviserName")
	public String getAdviserName()
	{
		return adviserName;
	}

	public void setAdviserName(String adviserName)
	{
		this.adviserName = adviserName;
	}

	@XmlElement(name = "status")
	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	@XmlElement(name = "salutation")
	public String getSalutation()
	{
		return salutation;
	}

	public void setSalutation(String salutation)
	{
		this.salutation = salutation;
	}

	@XmlElement(name = "accountHolderType")
	public String getAccountHolderType()
	{
		return accountHolderType;
	}

	public void setAccountHolderType(String accountHolderType)
	{
		this.accountHolderType = accountHolderType;
	}
}
