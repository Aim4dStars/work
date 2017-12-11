package com.bt.nextgen.core.web.model;

import java.io.Serializable;
import java.util.List;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.userauthority.web.PermissionsModel;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Barker (m035652)
 * Date: 9/08/13
 * Time: 9:06 AM
 * To change this template use File | Settings | File Templates.
 */
public interface PersonInterface extends Serializable
{

	String getOpenDate();

	void setOpenDate(String openDate);

	String getClientType();

	void setClientType(String clientType);

	String getFullName();

	void setFullName(String fullName);

	String getDeathDate();

	void setDeathDate(String deathDate);

	String getProfession();

	void setProfession(String profession);

	String getFax();

	void setFax(String fax);

	String getDateOfBirth();

	void setDateOfBirth(String dateOfBirth);

	String getSalutation();

	void setSalutation(String salutation);

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	String getGender();

	void setGender(String gender);

	int getAge();

	void setAge(int age);

	String getTfn();

	void setTfn(String tfn);

	String getRegisteredDate();

	void setRegisteredDate(String registeredDate);

	String getPreferedContactMethod();

	void setPreferedContactMethod(String preferedContactMethod);

	List <PhoneModel> getPhoneNumbers();

	void setPhoneNumbers(List <PhoneModel> phoneNumbers);

	List <EmailModel> getEmailIds();

	void setEmailIds(List <EmailModel> emailIds);

	List <AddressModel> getAddresses();

	void setAddresses(List <AddressModel> addresses);

	EncodedString getClientId();

	void setClientId(EncodedString clientId);

	EncodedString getPortfolioId();

	void setPortfolioId(EncodedString portfolioId);

	String getUserName();

	void setUserName(String userName);

	boolean isFirstTimeUser();

	void setFirstTimeUser(boolean firstTimeUser);

	boolean isFtueMessageStatus();

	void setFtueMessageStatus(boolean ftueMessageStatus);

	PermissionsModel getPermissions();

	void setPermissions(PermissionsModel permissions);

	String getCompanyRole();

	void setCompanyRole(String companyRole);

	String getIdStatus();

	void setIdStatus(String idStatus);

	String getPermissionType();

	void setPermissionType(String permissionType);

	String getPlainAccountId();

	void setPlainAccountId(String plainAccountId);

	String getBlockCodes();

	void setBlockCodes(String blockCodes);

	EncodedString getAdviserId();

	List <PhoneModel> getMobileList();

	List <PhoneModel> getLandLineList();

	String getOracleUser();

	String getPrimaryMobileNumber();

	void setPrimaryMobileNumber(String primaryMobileNumber);

	String getPrimaryEmailId();

	void setPrimaryEmailId(String primaryEmailId);

	AddressModel getPrimaryDomiAddress();

	void setPrimaryDomiAddress(AddressModel primaryDomiAddress);

	String getMiddleName();

	void setMiddleName(String middleName);

	String getPosition();
	
	String getPermissionDesc();

	void setPermissionDesc(String permissionDesc);

	String getSafiDeviceId();

	void setSafiDeviceId(String safiDeviceId);

	boolean isSupportStaffRole();

	void setSupportStaffRole(boolean isSupportStaffRole);

	boolean isInvestorRole();

	void setInvestorRole(boolean isInvestorRole);

	boolean isAdviserRole();

	void setAdviserRole(boolean isAdviserRole);

	TaxInfo getTaxInfo();

	void setTaxInfo(TaxInfo taxInfo);

	String getPersonId();
	
	void setPersonId(String personId);
	
	String getGcmId();
	
	void setGcmId(String gcmId);
	
	String getPositionId();
	
	void setPositionId(String positionId);
	
	boolean isTnCAttached();
	
	void setTnCAttached(boolean tnCAttached);
	
	String getCustDefinedLogin();

	void setCustDefinedLogin(String custDefinedLogin);
	
	EncodedString getEncodedPersonId();
	
	void setAvaloqStatusReg(boolean avaloqStatusReg);

	boolean isAvaloqStatusReg();

}