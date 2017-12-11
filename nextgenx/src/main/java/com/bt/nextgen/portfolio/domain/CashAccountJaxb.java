package com.bt.nextgen.portfolio.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Deprecated
@XmlAccessorType(XmlAccessType.FIELD)
public class CashAccountJaxb
{
	@XmlElement	
	private String idpsAccountName;
	@XmlElement	
	private String accountType;
	@XmlElement	
	private String productName;
	@XmlElement
	private String accountId;
	@XmlElement
	private String adviserName;

	//Primary contact details
	@XmlElement	
	private String personName;
	@XmlElement	
	private String phoneNumber;
	@XmlElement	
	private String email;
	@XmlElement	
	private String addressLine1;
	@XmlElement	
	private String addressLine2;
	@XmlElement	
	private String portfolio;
	@XmlElement	
	private String lastPortfolioCalculationDate;

	//cash account details
	@XmlElement	
	private String bsb;
	@XmlElement	
	private String billerCode;
	@XmlElement	
	private String crn;
	@XmlElement	
	private String cashAccountNumber;
	@XmlElement	
	private String interestRate;
	@XmlElement	
	private String interestEarned;
	@XmlElement	
	private String financialYearToDate;
	@XmlElement	
	private String availableBalance;
	@XmlElement	
	private String totalBalance;
	@XmlElement	
	private String registeredDate;
	@XmlElement	
	private String unClearedBalance;
	@XmlElement	
	private String dailyLimitForPayAnyone;
	@XmlElement	
	private String dailyLimitForBpay;
	@XmlElement	
	private String dailyLimitForLinked;
	@XmlElement	
	private String remainingPayAnyoneLimit;
	@XmlElement	
	private String remainingBpayLimit;
	@XmlElement	
	private String remainingLinkedLimit;

	public String getRemainingPayAnyoneLimit()
	{
		return remainingPayAnyoneLimit;
	}

	public void setRemainingPayAnyoneLimit(String remainingPayAnyoneLimit)
	{
		this.remainingPayAnyoneLimit = remainingPayAnyoneLimit;
	}

	public String getRemainingBpayLimit()
	{
		return remainingBpayLimit;
	}

	public void setRemainingBpayLimit(String remainingBpayLimit)
	{
		this.remainingBpayLimit = remainingBpayLimit;
	}

	public String getIdpsAccountName()
	{
		return idpsAccountName;
	}

	public void setIdpsAccountName(String idpsAccountName)
	{
		this.idpsAccountName = idpsAccountName;
	}

	public String getAccountType()
	{
		return accountType;
	}

	public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	public String getProductName()
	{
		return productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}

	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public String getAdviserName()
	{
		return adviserName;
	}

	public void setAdviserName(String adviserName)
	{
		this.adviserName = adviserName;
	}

	public String getPersonName()
	{
		return personName;
	}

	public void setPersonName(String personName)
	{
		this.personName = personName;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
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

	public String getPortfolio()
	{
		return portfolio;
	}

	public void setPortfolio(String portfolio)
	{
		this.portfolio = portfolio;
	}

	public String getLastPortfolioCalculationDate()
	{
		return lastPortfolioCalculationDate;
	}

	public void setLastPortfolioCalculationDate(String lastPortfolioCalculationDate)
	{
		this.lastPortfolioCalculationDate = lastPortfolioCalculationDate;
	}

	public String getBsb()
	{
		return bsb;
	}

	public void setBsb(String bsb)
	{
		this.bsb = bsb;
	}

	public String getBillerCode()
	{
		return billerCode;
	}

	public void setBillerCode(String billerCode)
	{
		this.billerCode = billerCode;
	}

	public String getCrn()
	{
		return crn;
	}

	public void setCrn(String crn)
	{
		this.crn = crn;
	}

	public String getCashAccountNumber()
	{
		return cashAccountNumber;
	}

	public void setCashAccountNumber(String cashAccountNumber)
	{
		this.cashAccountNumber = cashAccountNumber;
	}

	public String getInterestRate()
	{
		return interestRate;
	}

	public void setInterestRate(String interestRate)
	{
		this.interestRate = interestRate;
	}

	public String getInterestEarned()
	{
		return interestEarned;
	}

	public void setInterestEarned(String interestEarned)
	{
		this.interestEarned = interestEarned;
	}

	public String getFinancialYearToDate()
	{
		return financialYearToDate;
	}

	public void setFinancialYearToDate(String financialYearToDate)
	{
		this.financialYearToDate = financialYearToDate;
	}

	public String getAvailableBalance()
	{
		return availableBalance;
	}

	public void setAvailableBalance(String availableBalance)
	{
		this.availableBalance = availableBalance;
	}

	public String getTotalBalance()
	{
		return totalBalance;
	}

	public void setTotalBalance(String totalBalance)
	{
		this.totalBalance = totalBalance;
	}

	public String getRegisteredDate()
	{
		return registeredDate;
	}

	public void setRegisteredDate(String registeredDate)
	{
		this.registeredDate = registeredDate;
	}

	public String getUnClearedBalance()
	{
		return unClearedBalance;
	}

	public void setUnClearedBalance(String unClearedBalance)
	{
		this.unClearedBalance = unClearedBalance;
	}

	public String getDailyLimitForPayAnyone() 
	{
		return dailyLimitForPayAnyone;
	}

	public void setDailyLimitForPayAnyone(String dailyLimitForPayAnyone) 
	{
		this.dailyLimitForPayAnyone = dailyLimitForPayAnyone;
	}

	public String getDailyLimitForBpay() 
	{
		return dailyLimitForBpay;
	}

	public void setDailyLimitForBpay(String dailyLimitForBpay) 
	{
		this.dailyLimitForBpay = dailyLimitForBpay;
	}

	public String getDailyLimitForLinked()
	{
		return dailyLimitForLinked;
	}

	public void setDailyLimitForLinked(String dailyLimitForLinked)
	{
		this.dailyLimitForLinked = dailyLimitForLinked;
	}

	public String getRemainingLinkedLimit()
	{
		return remainingLinkedLimit;
	}

	public void setRemainingLinkedLimit(String remainingLinkedLimit)
	{
		this.remainingLinkedLimit = remainingLinkedLimit;
	}
}
