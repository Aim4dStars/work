package com.bt.nextgen.clients.web.model;

import com.btfin.panorama.core.security.encryption.EncodedString;

public class ClientDetails implements Comparable<ClientDetails>
{
	private String clientId;
	private String salutation;
	private String firstName;
	private String lastName;
	private String gender;
	private String age;
	private String userName;
	private String phone;
	private String email;
	//TODO to be removed. use street number
	private String addressLine1;
	//TODO to be removed. use street name
	private String addressLine2;
	private String city;
	private String state;
	private String country;
	//TODO need to renamed to postal code
	private String pin;
	private String adviserName;
	private String activationStatus;
	private String totalAmount;
	private String accountHolderType;
	private String dollarAmount;
	private String centAmount;
	//TODO: Added for navigation, need more clarity on this.
	private EncodedString clientIdEncoded;
	private String streetNumber;
	private String streetName;
	private String adviserFirstName;
	private String adviserLastName;
	private String clientName;


	public String getClientName()
	{
		return clientName;
	}

	public void setClientName(String clientName)
	{
		this.clientName = clientName;
	}

	public String getAdviserLastName()
	{
		return adviserLastName;
	}

	public void setAdviserLastName(String adviserLastName)
	{
		this.adviserLastName = adviserLastName;
	}

	public String getAdviserFirstName()
	{
		return adviserFirstName;
	}

	public void setAdviserFirstName(String adviserFirstName)
	{
		this.adviserFirstName = adviserFirstName;
	}

	public String getStreetName()
	{
		return streetName;
	}

	public void setStreetName(String streetName)
	{
		this.streetName = streetName;
	}

	public String getStreetNumber()
	{
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber)
	{
		this.streetNumber = streetNumber;
	}

	public EncodedString getClientIdEncoded()
	{
		return clientIdEncoded;
	}

	public void setClientIdEncoded(EncodedString clientIdEncoded)
	{
		this.clientIdEncoded = clientIdEncoded;
	}

	public String getCentAmount()
	{
		return centAmount;
	}

	public void setCentAmount(String centAmount)
	{
		this.centAmount = centAmount;
	}

	public String getDollarAmount()
	{
		return dollarAmount;
	}

	public void setDollarAmount(String dollarAmount)
	{
		this.dollarAmount = dollarAmount;
	}

	public String getAccountHolderType()
	{
		return accountHolderType;
	}

	public void setAccountHolderType(String accountHolderType)
	{
		this.accountHolderType = accountHolderType;
	}

	public String getTotalAmount()
	{
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	public String getActivationStatus()
	{
		return activationStatus;
	}

	public void setActivationStatus(String activationStatus)
	{
		this.activationStatus = activationStatus;
	}

	public String getAdviserName()
	{
		return adviserName;
	}

	public void setAdviserName(String adviserName)
	{
		this.adviserName = adviserName;
	}

	public String getPin()
	{
		return pin;
	}

	public void setPin(String pin)
	{
		this.pin = pin;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getAddressLine2()
	{
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2)
	{
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine1()
	{
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1)
	{
		this.addressLine1 = addressLine1;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getAge()
	{
		return age;
	}

	public void setAge(String age)
	{
		this.age = age;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getSalutation()
	{
		return salutation;
	}

	public void setSalutation(String salutation)
	{
		this.salutation = salutation;
	}

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	@Override public int compareTo(ClientDetails client)
	{
		int result;
		result = clientName.compareTo(client.getClientName());
		if (result == 0)
		{
			return clientName.compareTo(client.getClientName());
		}
		return result;
	}

}
