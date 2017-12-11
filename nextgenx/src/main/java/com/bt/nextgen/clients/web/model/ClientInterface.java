package com.bt.nextgen.clients.web.model;

import java.math.BigDecimal;
import java.util.List;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.model.EmailModel;
import com.bt.nextgen.core.web.model.PhoneModel;
import com.bt.nextgen.portfolio.web.model.PortfolioModel;

public interface ClientInterface

{
	List <ClientPortfolioModel> getPortfolio();

	void setPortfolio(List <ClientPortfolioModel> portfolio);

	String getClientName();

	void setClientName(String clientName);

	String getFirstName();

	void setFirstName(String firstName);

	String getLastName();

	void setLastName(String lastName);

	String getGender();

	void setGender(String gender);

	String getAge();

	void setAge(String age);

	String getUserName();

	void setUserName(String userName);

	String getPhone();

	void setPhone(String phone);

	String getEmail();

	void setEmail(String email);

	String getAddressLine1();

	void setAddressLine1(String addressLine1);

	String getAddressLine2();

	void setAddressLine2(String addressLine2);

	String getCity();

	void setCity(String city);

	String getState();

	void setState(String state);

	String getCountry();

	void setCountry(String country);

	String getPin();

	void setPin(String pin);

	String getAdviserName();

	void setAdviserName(String adviserName);

	List <PortfolioModel> getWrapAccounts();

	void setWrapAccounts(List <PortfolioModel> wrapAccounts);

	String getActivationStatus();

	void setActivationStatus(String activationStatus);

	String getSalutation();

	void setSalutation(String salutation);

	String getTotalAmount();

	void setTotalAmount(BigDecimal totalAmount);

	String getAccountHolderType();

	void setAccountHolderType(String accountHolderType);

	String getClientId();

	void setClientId(String clientId);

	EncodedString getClientIdEncoded();

	void setClientIdEncoded(EncodedString clientIdEncoded);

	String getDollarAmount();

	String getCentAmount();

	String getStreetNumber();

	void setStreetNumber(String streetNumber);

	String getStreetName();

	void setStreetName(String streetName);

	String getAdviserFirstName();

	void setAdviserFirstName(String adviserFirstName);

	String getAdviserLastName();

	void setAdviserLastName(String adviserLastName);

	String getCurrentDate();

	void setCurrentDate(String currentDate);

	String getAdviserEmail();

	void setAdviserEmail(String adviserEmail);

	String getAdviserPhoneNumber();

	void setAdviserPhoneNumber(String adviserPhoneNumber);

	String getIconStatus();

	void setIconStatus(String iconStatus);

	int getOrder();

	void setOrder(int order);

	String getUserId();

	void setUserId(String userId);

	String getBpNameAbbr();

	void setBpNameAbbr(String bpNameAbbr);

	List <PhoneModel> getMobileList();

	void setMobileList(List <PhoneModel> mobileList);

	List <EmailModel> getEmailList();

	void setEmailList(List <EmailModel> emailList);

	String getType();

	void setType(String type);

}
