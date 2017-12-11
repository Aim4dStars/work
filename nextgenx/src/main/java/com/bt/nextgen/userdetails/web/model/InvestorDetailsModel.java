package com.bt.nextgen.userdetails.web.model;

import java.util.List;

import com.bt.nextgen.core.web.model.Address;
import com.bt.nextgen.core.web.model.AddressModel;
import com.bt.nextgen.core.web.model.EmailModel;
import com.bt.nextgen.core.web.model.PhoneModel;


public class InvestorDetailsModel
{
	private String registeredDate;
	private String userName;
	private String userId;
	private String countryOfResidence;
	private String taxOption;
	private String exemption;
	private String tfn;
	
	private String preferedContactMethod;
	private List <PhoneModel> phoneNumbers;
	private List <EmailModel> emailIds;
	private List<AddressModel> addresses;
	private List <PhoneModel> landLineList;
	private List <PhoneModel> mobileList;
	private PhoneModel smsMobile;
	private EmailModel primaryEmail;
	private PhoneModel primaryPhone;
	private boolean preferedContact;
	private Address residentialAddress;
	private Address intResidentialAddress;
	private Address postalAddress;
	private Address intPostalAddress;
	private boolean usePostal;
	private String smsMobileNumber;
	private String primaryEmailId;
	private Address residentialAddressFromParty;
	private Address postalAddressFromParty;
	private boolean skipResidentialAddressValidation;
	private boolean skipPostalAddressValidation;
	
	public String getRegisteredDate() 
	{
		return registeredDate;
	}
	public void setRegisteredDate(String registeredDate) 
	{
		this.registeredDate = registeredDate;
	}
	public String getUserName() 
	{
		return userName;
	}
	public void setUserName(String userName) 
	{
		this.userName = userName;
	}
	public String getUserId() 
	{
		return userId;
	}
	public void setUserId(String userId) 
	{
		this.userId = userId;
	}
	public String getCountryOfResidence() 
	{
		return countryOfResidence;
	}
	public void setCountryOfResidence(String countryOfResidence) 
	{
		this.countryOfResidence = countryOfResidence;
	}
	public String getTaxOption() 
	{
		return taxOption;
	}
	public void setTaxOption(String taxOption) 
	{
		this.taxOption = taxOption;
	}
	public String getExemption() 
	{
		return exemption;
	}
	public void setExemption(String exemption) 
	{
		this.exemption = exemption;
	}
	public String getTfn() 
	{
		return tfn;
	}
	public void setTfn(String tfn) 
	{
		this.tfn = tfn;
	}
	public String getPreferedContactMethod() 
	{
		return preferedContactMethod;
	}
	public void setPreferedContactMethod(String preferedContactMethod) 
	{
		this.preferedContactMethod = preferedContactMethod;
	}
	public List<PhoneModel> getPhoneNumbers() 
	{
		return phoneNumbers;
	}
	public void setPhoneNumbers(List<PhoneModel> phoneNumbers) 
	{
		this.phoneNumbers = phoneNumbers;
	}
	public List<EmailModel> getEmailIds() 
	{
		return emailIds;
	}
	public void setEmailIds(List<EmailModel> emailIds) 
	{
		this.emailIds = emailIds;
	}
	public List<AddressModel> getAddresses() 
	{
		return addresses;
	}
	public void setAddresses(List<AddressModel> addresses) 
	{
		this.addresses = addresses;
	}
	public List<PhoneModel> getLandLineList()
	{
		return landLineList;
	}
	public void setLandLineList(List<PhoneModel> landLineList)
	{
		this.landLineList = landLineList;
	}
	public List<PhoneModel> getMobileList()
	{
		return mobileList;
	}
	public void setMobileList(List<PhoneModel> mobileList)
	{
		this.mobileList = mobileList;
	}
	public PhoneModel getSmsMobile()
	{
		return smsMobile;
	}
	public void setSmsMobile(PhoneModel smsMobile)
	{
		this.smsMobile = smsMobile;
	}
	public PhoneModel getPrimaryPhone()
	{
		return primaryPhone;
	}
	public void setPrimaryPhone(PhoneModel primaryPhone)
	{
		this.primaryPhone = primaryPhone;
	}
	public EmailModel getPrimaryEmail()
	{
		return primaryEmail;
	}
	public void setPrimaryEmail(EmailModel primaryEmail)
	{
		this.primaryEmail = primaryEmail;
	}
	public boolean isPreferedContact()
	{
		return preferedContact;
	}
	public void setPreferedContact(boolean preferedContact)
	{
		this.preferedContact = preferedContact;
	}
	public boolean isUsePostal()
	{
		return usePostal;
	}
	public void setUsePostal(boolean usePostal)
	{
		this.usePostal = usePostal;
	}
	public String getSmsMobileNumber()
	{
		return smsMobileNumber;
	}
	public void setSmsMobileNumber(String smsMobileNumber)
	{
		this.smsMobileNumber = smsMobileNumber;
	}
	public String getPrimaryEmailId()
	{
		return primaryEmailId;
	}
	public void setPrimaryEmailId(String primaryEmailId)
	{
		this.primaryEmailId = primaryEmailId;
	}
	public Address getResidentialAddressFromParty()
	{
		return residentialAddressFromParty;
	}
	public void setResidentialAddressFromParty(Address residentialAddressFromParty)
	{
		this.residentialAddressFromParty = residentialAddressFromParty;
	}
	public Address getPostalAddressFromParty()
	{
		return postalAddressFromParty;
	}
	public void setPostalAddressFromParty(Address postalAddressFromParty)
	{
		this.postalAddressFromParty = postalAddressFromParty;
	}
	public Address getResidentialAddress()
	{
		return residentialAddress;
	}
	public void setResidentialAddress(Address residentialAddress)
	{
		this.residentialAddress = residentialAddress;
	}
	public Address getPostalAddress()
	{
		return postalAddress;
	}
	public void setPostalAddress(Address postalAddress)
	{
		this.postalAddress = postalAddress;
	}
	public boolean isSkipResidentialAddressValidation()
	{
		return skipResidentialAddressValidation;
	}
	public void setSkipResidentialAddressValidation(boolean skipResidentialAddressValidation)
	{
		this.skipResidentialAddressValidation = skipResidentialAddressValidation;
	}
	public boolean isSkipPostalAddressValidation()
	{
		return skipPostalAddressValidation;
	}
	public void setSkipPostalAddressValidation(boolean skipPostalAddressValidation)
	{
		this.skipPostalAddressValidation = skipPostalAddressValidation;
	}

	public Address getIntResidentialAddress()
	{
		return intResidentialAddress;
	}

	public void setIntResidentialAddress(Address intResidentialAddress)
	{
		this.intResidentialAddress = intResidentialAddress;
	}

	public Address getIntPostalAddress()
	{
		return intPostalAddress;
	}

	public void setIntPostalAddress(Address intPostalAddress)
	{
		this.intPostalAddress = intPostalAddress;
	}
}
