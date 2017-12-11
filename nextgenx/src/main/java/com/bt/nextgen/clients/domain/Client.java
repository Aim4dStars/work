package com.bt.nextgen.clients.domain;

import com.bt.nextgen.portfolio.domain.Portfolio;

import java.util.List;

//TODO -this client domain will depricate as  client details can have multiple email,phone.
//this domain will be created in clientadmin theme will update attributes
@Deprecated
public class Client
{
	private String clientId;
	private String salutation;
	private String firstName;
	private String lastName;
	private String gender;
	private int age;
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
	private String accountHolderType;
	private String activationStatus;
	private List <Portfolio> portfolios;

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

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
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

	public String getAddressLine1()
	{
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1)
	{
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2()
	{
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2)
	{
		this.addressLine2 = addressLine2;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getPin()
	{
		return pin;
	}

	public void setPin(String pin)
	{
		this.pin = pin;
	}

	public String getAdviserName()
	{
		return adviserName;
	}

	public void setAdviserName(String adviserName)
	{
		this.adviserName = adviserName;
	}

	public List <Portfolio> getWrapAccounts()
	{
		return portfolios;
	}

	public void setWrapAccounts(List <Portfolio> portfolios)
	{
		this.portfolios = portfolios;
	}

	public String getAccountHolderType()
	{
		return accountHolderType;
	}

	public void setAccountHolderType(String accountHolderType)
	{
		this.accountHolderType = accountHolderType;
	}

	public String getSalutation()
	{
		return salutation;
	}

	public void setSalutation(String salutation)
	{
		this.salutation = salutation;
	}

	public String getActivationStatus()
	{
		return activationStatus;
	}

	public void setActivationStatus(String activationStatus)
	{
		this.activationStatus = activationStatus;
	}

	@Override
	public String toString()
	{
		return "Client [salutation=" + salutation + ", firstName=" + firstName + ", lastName=" + lastName + ", gender=" + gender
			+ ", age=" + age + ", userName=" + userName + ", phone=" + phone + ", email=" + email + ", addressLine1="
			+ addressLine1 + ", addressLine2=" + addressLine2 + ", city=" + city + ", state=" + state + ", country=" + country
			+ ", pin=" + pin + ", adviserName=" + adviserName + ", accountHolderType=" + accountHolderType
			+ ", activationStatus=" + activationStatus + ", portfolios=" + portfolios + "]";
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
