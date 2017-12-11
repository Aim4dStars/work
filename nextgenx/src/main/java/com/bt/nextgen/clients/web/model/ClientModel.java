package com.bt.nextgen.clients.web.model;

import static com.bt.nextgen.core.util.MoneyUtil.getCentPart;
import static com.bt.nextgen.core.util.MoneyUtil.getDollarPart;
import static com.bt.nextgen.core.web.ApiFormatter.asDecimal;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.clients.util.EncodedStringtoString;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.model.AddressModel;
import com.bt.nextgen.core.web.model.EmailModel;
import com.bt.nextgen.core.web.model.PersonInterface;
import com.bt.nextgen.core.web.model.PhoneModel;
import com.bt.nextgen.portfolio.web.model.PortfolioModel;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ClientModel implements Comparable <ClientModel>, ClientInterface
{
	private String clientId;
	private String salutation;
	private String firstName;
	private String lastName;

	public String getAvaloqUserId() {
		return avaloqUserId;
	}

	public void setAvaloqUserId(String avaloqUserId) {
		this.avaloqUserId = avaloqUserId;
	}

	private String preferedName;
	private boolean gstOption;
	private String avaloqUserId;

	public String getMiddleName()
	{
		return middleName;
	}

	public String getPreferedName()
	{
		return preferedName;
	}

	public void setPreferedName(String preferedName)
	{
		this.preferedName = preferedName;
	}

	public boolean isGstOption()
	{
		return gstOption;
	}

	public void setGstOption(boolean gstOption)
	{
		this.gstOption = gstOption;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	public String getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	private String gender;
	private String age;
	private String userName;
	private String phone;
	private String email;
	private String type;
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
	private BigDecimal totalAmount;
	private String accountHolderType;
	private List <PortfolioModel> wrapAccounts;
	private List <PhoneModel> mobileList;
	private List <EmailModel> emailList;

	//TODO: Added for navigation, need more clarity on this.
	@JsonSerialize(using = EncodedStringtoString.class)
	private EncodedString clientIdEncoded;
	private String streetNumber;
	private String streetName;
	private String adviserFirstName;
	private String adviserLastName;
	private String adviserEmail;
	private String adviserPhoneNumber;
	private String iconStatus;
	private String primaryContactName;
	private int order;
	//Oracle user id
	private String userId;

	private String bpNameAbbr;
	//Adviser permission
	private String adviserPermission;

	private String displayName;

	/**
	 * Adding properties as per the Avaloq response details
	 */
	private List <ClientPortfolioModel> portfolio;
	private String clientName;
	//Added to show report generation dateTime in Client Snapshot PDF. 
	private String currentDate;

	//Added to set Person List in Client model
	private List <PersonInterface> personList;

	private List <AddressModel> addressList;
	private String middleName;
	private String dateOfBirth;

	public List <AddressModel> getAddressList()
	{
		return addressList;
	}

	public void setAddressList(List <AddressModel> addressList)
	{
		this.addressList = addressList;
	}

	public List <PersonInterface> getPersonList()
	{
		return personList;
	}

	public void setPersonList(List <PersonInterface> personList)
	{
		this.personList = personList;
	}

	public List <ClientPortfolioModel> getPortfolio()
	{
		return portfolio;
	}

	public void setPortfolio(List <ClientPortfolioModel> portfolio)
	{
		this.portfolio = portfolio;
	}

	public String getContactName()
	{
		return primaryContactName;
	}

	public void setContactName(String primaryContactName)
	{
		this.primaryContactName = primaryContactName;
	}

	public String getClientName()
	{
		return clientName;
	}

	public void setClientName(String clientName)
	{
		this.clientName = clientName;
	}

	@Override
	public int compareTo(ClientModel clientModel)
	{
		if (clientModel.getClientName() == null)
		{
			return 1;
		}
		return clientName.compareTo(clientModel.getClientName());
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

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getAge()
	{
		return age;
	}

	public void setAge(String age)
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

	public List <PortfolioModel> getWrapAccounts()
	{
		return wrapAccounts;
	}

	public void setWrapAccounts(List <PortfolioModel> wrapAccounts)
	{
		this.wrapAccounts = wrapAccounts;
	}

	public String getActivationStatus()
	{
		return activationStatus;
	}

	public void setActivationStatus(String activationStatus)
	{
		this.activationStatus = activationStatus;
	}

	public String getSalutation()
	{
		return salutation;
	}

	public void setSalutation(String salutation)
	{
		this.salutation = salutation;
	}

	public String getTotalAmount()
	{
		return asDecimal(totalAmount);
	}

	public void setTotalAmount(BigDecimal totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	public String getAccountHolderType()
	{
		return accountHolderType;
	}

	public void setAccountHolderType(String accountHolderType)
	{
		this.accountHolderType = accountHolderType;
	}

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	public EncodedString getClientIdEncoded()
	{
		return clientIdEncoded;
	}

	public void setClientIdEncoded(EncodedString clientIdEncoded)
	{
		this.clientIdEncoded = clientIdEncoded;
	}

	public String getDollarAmount()
	{
		return getDollarPart(totalAmount);
	}

	public String getCentAmount()
	{
		return getCentPart(totalAmount);
	}

	public String getStreetNumber()
	{
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber)
	{
		this.streetNumber = streetNumber;
	}

	public String getStreetName()
	{
		return streetName;
	}

	public void setStreetName(String streetName)
	{
		this.streetName = streetName;
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

	public String getCurrentDate()
	{
		return currentDate;
	}

	public void setCurrentDate(String currentDate)
	{
		this.currentDate = currentDate;
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

	public String getIconStatus()
	{
		return iconStatus;
	}

	public void setIconStatus(String iconStatus)
	{
		this.iconStatus = iconStatus;
	}

	public int getOrder()
	{
		return order;
	}

	public void setOrder(int order)
	{
		this.order = order;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getBpNameAbbr()
	{
		return bpNameAbbr;
	}

	public void setBpNameAbbr(String bpNameAbbr)
	{
		this.bpNameAbbr = bpNameAbbr;
	}

	public String getAdviserPermission()
	{
		return adviserPermission;
	}

	public void setAdviserPermission(String adviserPermission)
	{
		this.adviserPermission = adviserPermission;
	}

	public List <PhoneModel> getMobileList()
	{
		return mobileList;
	}

	public void setMobileList(List <PhoneModel> mobileList)
	{
		this.mobileList = mobileList;
	}

	public String getPrimaryContactName()
	{
		return primaryContactName;
	}

	public void setPrimaryContactName(String primaryContactName)
	{
		this.primaryContactName = primaryContactName;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public List <EmailModel> getEmailList()
	{
		return this.emailList;
	}

	@Override
	public void setEmailList(List <EmailModel> emailList)
	{
		this.emailList = emailList;
	}

	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public void setType(String type)
	{
		this.type = type;

	}

}
