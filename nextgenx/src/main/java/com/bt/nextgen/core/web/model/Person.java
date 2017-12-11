package com.bt.nextgen.core.web.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.userauthority.web.PermissionsModel;
import com.bt.nextgen.web.controller.cash.util.Attribute;

public abstract class Person implements PersonInterface
{
	private static final long serialVersionUID = 1L;

	private String salutation;
	private String firstName;
	private String lastName;
	private String userName;
	private String gender;
	private int age;
	private String tfn;
	private String registeredDate;
	private String preferedContactMethod;
	private List <PhoneModel> phoneNumbers;
	private List <EmailModel> emailIds;
	private List <AddressModel> addresses;
	private String dateOfBirth;
	private String fax;
	private PermissionsModel permissions;

	//TODO: Added for navigation, need more clarity on this.
	private EncodedString clientId;
	private EncodedString portfolioId;
	private String plainAccountId;
	//Added for First Time User Experience
	private boolean firstTimeUser;
	private boolean ftueMessageStatus;

	//Added for user details service
	private String openDate;
	private String clientType;
	private String fullName;
	private String deathDate;
	private String isAdviser;
	private String isParaPlanner;
	private String isAdviserAdmin;
	private String profession;
	private String oracleUser;
	private String portfolioActiveDate;
	private String blockCodes;

	private boolean beneficiary;
	private boolean member;

	//private String advisor;

	//Added for Account details Section
	//TODO Added to show role(Director,Signatory) for AccountHolders, Need more clarity
	private String companyRole;
	private String idStatus;

	//Added to check the permission on UI
	private String permissionType;
	private String permissionDesc;

	private Object mobileList;

	private String primaryMobileNumber;
	private String primaryEmailId;
	private AddressModel primaryDomiAddress;
	private String middleName;
	private String position;
	private String safiDeviceId;
	private TaxInfo taxInfo;
	private String personId;
	private String custDefinedLogin;

	private boolean isSupportStaffRole;
	private boolean isInvestorRole;
	private boolean isAdviserRole;
	private String gcmId;

	private String positionId;
	private boolean tnCAttached;
	private EncodedString encodedPersonId;
	private boolean avaloqStatusReg; //Its true when person's smartClient Status is Registered.

	
	public Person()
	{
		// TODO: workaround until proper IP Person object is avaialble
		// Default constructor
	}
	
	public String getGcmId()
	{
		return gcmId;
	}

	public void setGcmId(String gcmId)
	{
		this.gcmId = gcmId;
	}

	public boolean isSupportStaffRole()
	{
		return isSupportStaffRole;
	}

	public void setSupportStaffRole(boolean isSupportStaffRole)
	{
		this.isSupportStaffRole = isSupportStaffRole;
	}

	public boolean isInvestorRole()
	{
		return isInvestorRole;
	}

	public void setInvestorRole(boolean isInvestorRole)
	{
		this.isInvestorRole = isInvestorRole;
	}

	public boolean isAdviserRole()
	{
		return isAdviserRole;
	}

	public void setAdviserRole(boolean isAdviserRole)
	{
		this.isAdviserRole = isAdviserRole;
	}

	public String getPermissionDesc()
	{
		return permissionDesc;
	}

	public void setPermissionDesc(String permissionDesc)
	{
		this.permissionDesc = permissionDesc;
	}

	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	public String getPrimaryMobileNumber()
	{
		return primaryMobileNumber;
	}

	public void setPrimaryMobileNumber(String primaryMobileNumber)
	{
		this.primaryMobileNumber = primaryMobileNumber;
	}

	public String getPrimaryEmailId()
	{
		return primaryEmailId;
	}

	public void setPrimaryEmailId(String primaryEmailId)
	{
		this.primaryEmailId = primaryEmailId;
	}

	public AddressModel getPrimaryDomiAddress()
	{
		return primaryDomiAddress;
	}

	public void setPrimaryDomiAddress(AddressModel primaryDomiAddress)
	{
		this.primaryDomiAddress = primaryDomiAddress;
	}

	@Override
	public String getOpenDate()
	{
		return openDate;
	}

	@Override
	public void setOpenDate(String openDate)
	{
		this.openDate = openDate;
	}

	@Override
	public String getClientType()
	{
		return clientType;
	}

	@Override
	public void setClientType(String clientType)
	{
		this.clientType = clientType;
	}

	@Override
	public String getFullName()
	{
		return fullName;
	}

	@Override
	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	@Override
	public String getDeathDate()
	{
		return deathDate;
	}

	@Override
	public void setDeathDate(String deathDate)
	{
		this.deathDate = deathDate;
	}

	public String getIsAdviser()
	{
		return isAdviser;
	}

	public void setIsAdviser(String isAdviser)
	{
		this.isAdviser = isAdviser;
	}

	public String getIsParaPlanner()
	{
		return isParaPlanner;
	}

	public void setIsParaPlanner(String isParaPlanner)
	{
		this.isParaPlanner = isParaPlanner;
	}

	public String getIsAdviserAdmin()
	{
		return isAdviserAdmin;
	}

	public void setIsAdviserAdmin(String isAdviserAdmin)
	{
		this.isAdviserAdmin = isAdviserAdmin;
	}

	public boolean isBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(boolean beneficiary) {
		this.beneficiary = beneficiary;
	}

	public boolean isMember() {
		return member;
	}

	public void setMember(boolean member) {
		this.member = member;
	}

	@Override
	public String getProfession()
	{
		return profession;
	}

	@Override
	public void setProfession(String profession)
	{
		this.profession = profession;
	}

	public String getOracleUser()
	{
		return oracleUser;
	}

	public void setOracleUser(String oracleUser)
	{
		this.oracleUser = oracleUser;
	}

	public String getPortfolioActiveDate()
	{
		return portfolioActiveDate;
	}

	public void setPortfolioActiveDate(String portfolioActiveDate)
	{
		this.portfolioActiveDate = portfolioActiveDate;
	}

	/*public String getAdvisor() 
	{
		return advisor;
	}

	public void setAdvisor(String advisor) 
	{
		this.advisor = advisor;
	}*/

	@Override
	public String getFax()
	{
		return fax;
	}

	@Override
	public void setFax(String fax)
	{
		this.fax = fax;
	}

	@Override
	public String getDateOfBirth()
	{
		return dateOfBirth;
	}

	@Override
	public void setDateOfBirth(String dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	@Override
	public String getSalutation()
	{
		return salutation;
	}

	@Override
	public void setSalutation(String salutation)
	{
		this.salutation = salutation;
	}

	@Override
	public String getFirstName()
	{
		return firstName;
	}

	@Override
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	@Override
	public String getLastName()
	{
		return lastName;
	}

	@Override
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	@Override
	public String getGender()
	{
		return gender;
	}

	@Override
	public void setGender(String gender)
	{
		this.gender = gender;
	}

	@Override
	public int getAge()
	{
		return age;
	}

	@Override
	public void setAge(int age)
	{
		this.age = age;
	}

	@Override
	public String getTfn()
	{
		return tfn;
	}

	@Override
	public void setTfn(String tfn)
	{
		this.tfn = tfn;
	}

	@Override
	public String getRegisteredDate()
	{
		return registeredDate;
	}

	@Override
	public void setRegisteredDate(String registeredDate)
	{
		this.registeredDate = registeredDate;
	}

	@Override
	public String getPreferedContactMethod()
	{
		return preferedContactMethod;
	}

	@Override
	public void setPreferedContactMethod(String preferedContactMethod)
	{
		this.preferedContactMethod = preferedContactMethod;
	}

	@Override
	public List <PhoneModel> getPhoneNumbers()
	{
		return phoneNumbers;
	}

	@Override
	public void setPhoneNumbers(List <PhoneModel> phoneNumbers)
	{
		this.phoneNumbers = phoneNumbers;
	}

	@Override
	public List <EmailModel> getEmailIds()
	{
		return emailIds;
	}

	@Override
	public void setEmailIds(List <EmailModel> emailIds)
	{
		this.emailIds = emailIds;
	}

	@Override
	public List <AddressModel> getAddresses()
	{
		return addresses;
	}

	@Override
	public void setAddresses(List <AddressModel> addresses)
	{
		this.addresses = addresses;
	}

	@Override
	public String toString()
	{
		return "Person [salutation=" + salutation + ", firstName=" + firstName + ", lastName=" + lastName + ", gender=" + gender
			+ ", age=" + age + ", tfn=" + tfn + ", registeredDate=" + registeredDate + ", preferedContactMethod="
			+ preferedContactMethod + ", phoneNumbers=" + phoneNumbers + ", emailIds=" + emailIds + ", addresses=" + addresses
			+ "]";
	}

	@Override
	public EncodedString getClientId()
	{
		return clientId;
	}

	@Override
	public void setClientId(EncodedString clientId)
	{
		this.clientId = clientId;
	}

	@Override
	public EncodedString getPortfolioId()
	{
		return portfolioId;
	}

	@Override
	public void setPortfolioId(EncodedString portfolioId)
	{
		this.portfolioId = portfolioId;
	}

	@Override
	public String getUserName()
	{
		return userName;
	}

	@Override
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	@Override
	public boolean isFirstTimeUser()
	{
		return firstTimeUser;
	}

	@Override
	public void setFirstTimeUser(boolean firstTimeUser)
	{
		this.firstTimeUser = firstTimeUser;
	}

	@Override
	public boolean isFtueMessageStatus()
	{
		return ftueMessageStatus;
	}

	@Override
	public void setFtueMessageStatus(boolean ftueMessageStatus)
	{
		this.ftueMessageStatus = ftueMessageStatus;
	}

	@Override
	public PermissionsModel getPermissions()
	{
		return permissions;
	}

	@Override
	public void setPermissions(PermissionsModel permissions)
	{
		this.permissions = permissions;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person)obj;
		if (clientId == null)
		{
			if (other.clientId != null)
				return false;
		}
		else if (!clientId.equals(other.clientId))
			return false;
		return true;
	}

	@Override
	public String getCompanyRole()
	{
		return companyRole;
	}

	@Override
	public void setCompanyRole(String companyRole)
	{
		this.companyRole = companyRole;
	}

	@Override
	public String getIdStatus()
	{
		return idStatus;
	}

	@Override
	public void setIdStatus(String idStatus)
	{
		this.idStatus = idStatus;
	}

	@Override
	public String getPermissionType()
	{
		return permissionType;
	}

	@Override
	public void setPermissionType(String permissionType)
	{
		this.permissionType = permissionType;
	}

	@Override
	public String getPlainAccountId()
	{
		return plainAccountId;
	}

	@Override
	public void setPlainAccountId(String plainAccountId)
	{
		this.plainAccountId = plainAccountId;
	}

	@Override
	public String getBlockCodes()
	{
		return blockCodes;
	}

	@Override
	public void setBlockCodes(String blockCodes)
	{
		this.blockCodes = blockCodes;
	}

	@Override
	public EncodedString getAdviserId()
	{
		return clientId;
	}

	@Override
	public List <PhoneModel> getMobileList()
	{
		return getPhoneModelListByType(Attribute.MOBILE);
	}

	@Override
	public List <PhoneModel> getLandLineList()
	{
		return getPhoneModelListByType(Attribute.LANDLINE);
	}

	private List <PhoneModel> getPhoneModelListByType(String phoneType)
	{
		List <PhoneModel> phoneModelListByType = null;
		if (phoneNumbers != null)
		{
			for (PhoneModel phoneModel : phoneNumbers)
			{
				if (phoneModel.getType().equalsIgnoreCase(phoneType))
				{
					if (phoneModelListByType == null)
					{
						phoneModelListByType = new ArrayList <PhoneModel>();
					}
					phoneModelListByType.add(phoneModel);
				}
			}
		}

		if (phoneModelListByType != null)
		{
			phoneModelListByType = sortedPhoneList(phoneModelListByType);
		}

		return phoneModelListByType;
	}

	private List <PhoneModel> sortedPhoneList(List <PhoneModel> phoneList)
	{
		Collections.sort(phoneList, new Comparator <PhoneModel>()
		{
			@Override
			public int compare(PhoneModel o1, PhoneModel o2)
			{
				boolean b1 = o1.isPrimary();
				boolean b2 = o2.isPrimary();
				if (b1 == b2)
				{
					return 0;
				}
				// either b1 is true or b2
				// if true goes after false switch the -1 and 1
				return (b1 ? -1 : 1);
			}
		});

		return phoneList;

	}

	public String getPosition()
	{
		return position;
	}

	public void setPosition(String position)
	{
		this.position = position;
	}

	public String getSafiDeviceId()
	{
		return this.safiDeviceId;
	}

	public void setSafiDeviceId(String safiDeviceId)
	{
		this.safiDeviceId = safiDeviceId;
	}

	public TaxInfo getTaxInfo()
	{
		return taxInfo;
	}

	public void setTaxInfo(TaxInfo taxInfo)
	{
		this.taxInfo = taxInfo;
	}

	public String getPersonId()
	{
		return personId;
	}

	public void setPersonId(String personId)
	{
		this.personId = personId;
	}

	public String getPositionId()
	{
		return positionId;
	}

	public void setPositionId(String positionId)
	{
		this.positionId = positionId;
	}

	public String getCustDefinedLogin()
	{
		return custDefinedLogin;
	}

	public void setCustDefinedLogin(String custDefinedLogin)
	{
		this.custDefinedLogin = custDefinedLogin;
	}

	public void setMobileList(Object mobileList)
	{
		this.mobileList = mobileList;
	}

	/**
	 * @return the tnCAttached
	 */
	public boolean isTnCAttached() {
		return tnCAttached;
	}

	/**
	 * @param tnCAttached the tnCAttached to set
	 */
	public void setTnCAttached(boolean tnCAttached) {
		this.tnCAttached = tnCAttached;
	}

	public EncodedString getEncodedPersonId()
	{
		return (EncodedString)(personId != null ? EncodedString.fromPlainText(personId) : 		"");
	}

	public void setEncodedPersonId(EncodedString encodedPersonId)
	{
		this.encodedPersonId = encodedPersonId;
	}

	public boolean isAvaloqStatusReg()
	{
		return avaloqStatusReg;
	}

	public void setAvaloqStatusReg(boolean avaloqStatusReg)
	{
		this.avaloqStatusReg = avaloqStatusReg;
	}

}
