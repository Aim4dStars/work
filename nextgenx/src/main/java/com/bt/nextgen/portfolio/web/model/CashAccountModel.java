package com.bt.nextgen.portfolio.web.model;

import com.bt.nextgen.clients.domain.Client;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.Format;
import com.bt.nextgen.portfolio.domain.Portfolio;
import com.bt.nextgen.termdeposit.domain.TermDepositAccount;

import javax.validation.constraints.Null;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Deprecated
public class CashAccountModel
{

	private static final String YYYY_MM_DD = "yyyy-MM-dd";
	private String idpsAccountName;
	private String accountType;
	private String productName;
	private String accountId;
	private String adviserName;
	private String wrapAccountId;

	//Primary contact details
	private String personName;
	private String phoneNumber;
	private String email;
	private String addressLine1;
	private String addressLine2;
	private String portfolio;
	private String lastPortfolioCalculationDate;

	//cash account details
	private String bsb;
	private String billerCode;
	private String crn;
	private String cashAccountNumber;
	private String interestRate;
	private String interestEarned;
	private String financialYearToDate;
	private String availableBalance = "$0.00";
	private String unClearedBalance;
	private String totalBalance;
	@Null
	private String registeredDate;
	@Null
	private String generatedDate;
	@Null
	private String currentDate;

	private String fmtRegisteredDate;
	private String fmtCurrentDate;

	private String dailyLimitForPayAnyone;
	private String dailyLimitForBpay;
	private String dailyLimitForLinked;
	private String remainingPayAnyoneLimit;
	private String remainingBpayLimit;
	private String remainingLinkedLimit;

	private List <TermDepositAccount> listOfTermDepositAccounts;
	private int noOfProductHolder;

	private String previousMonth;
	private String previousQuater;
	private String maccId;
	private String investmentId;

	public String getMaccId()
	{
		return maccId;
	}

	public void setMaccId(String maccId)
	{
		this.maccId = maccId;
	}

	//TODO: sneed to decide accountHolders belong to CashAccountModel or PortfolioModel as spoke with Rajeev
	private List<String> accountHolders = new ArrayList<>();

	public void setAccountHolders(List <String> accountHolders)
	{
		this.accountHolders = accountHolders;
	}

	public List <String> getAccountHolders()
	{
		return accountHolders;
	}

	public CashAccountModel(Portfolio portfolio, String wrapAccountId, Client client, int size)
	{
		this.idpsAccountName = portfolio.getAccountName();
		this.accountId = portfolio.getAccountId();
		this.wrapAccountId = wrapAccountId;
		this.adviserName = client.getAdviserName();
		this.accountType = portfolio.getAccountType();
		this.productName = portfolio.getProductName();
		this.personName = client.getUserName();
		this.phoneNumber = client.getPhone();
		this.email = client.getEmail();
		this.addressLine1 = client.getAddressLine1();
		this.addressLine2 = client.getAddressLine2();

		this.portfolio = "1212121212";

		this.lastPortfolioCalculationDate = ApiFormatter.asShortDate(portfolio.getLastPortfolioCalDate());
		//cash account details
		this.bsb = portfolio.getCashAccount().getBsb();
		this.billerCode = portfolio.getCashAccount().getBillerCode();
		this.crn = portfolio.getCashAccount().getCrn();
		this.cashAccountNumber = portfolio.getCashAccount().getAccountNumber();
		this.interestRate = portfolio.getCashAccount().getInterestRate();
		this.interestEarned = Format.asCurrency(portfolio.getCashAccount().getInterestEarned());
		//this.financialYearToDate = ClientUtil.getFinnancialYearToDate();
		this.availableBalance = portfolio.getCashAccount().getAvailableBalance().toString();
		this.totalBalance = portfolio.getCashAccount().getTotalBalance().toString();
		this.registeredDate = portfolio.getRegisterDate().toString();
		Date now = new Date();
		this.generatedDate = ApiFormatter.asShortDate(now);
		//	this.accountHolders = client.getAccountHolders();
		this.currentDate = new SimpleDateFormat("d MMM yyyy").format(now);

		// Raj: for future date validation
		SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
		fmtRegisteredDate = sdf.format(portfolio.getRegisterDate());
		fmtCurrentDate = sdf.format(now);
		this.listOfTermDepositAccounts = portfolio.getTermDepositAccounts();
		this.noOfProductHolder = size;
		//this.previousMonth= ClientUtil.getPreviousMonthToDate();
		//this.previousQuater = ClientUtil.getPreviousQuater();
	}

	public String getRemainingPayAnyoneLimit()
	{
		return remainingPayAnyoneLimit;
	}

	public void setDailyLimitForLinked(String dailyLimitForLinked)
	{
		this.dailyLimitForLinked = dailyLimitForLinked;
	}

	public void setRemainingLinkedLimit(String remainingLinkedLimit)
	{
		this.remainingLinkedLimit = remainingLinkedLimit;
	}

	public String getDailyLimitForLinked()
	{
		return dailyLimitForLinked;
	}

	public String getRemainingLinkedLimit()
	{
		return remainingLinkedLimit;
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

	public CashAccountModel()
	{

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

	public String getWrapAccountId()
	{
		return wrapAccountId;
	}

	public void setWrapAccountId(String wrapAccountId)
	{
		this.wrapAccountId = wrapAccountId;
	}

	public String getGeneratedDate()
	{
		return generatedDate;
	}

	public void setGeneratedDate(String generatedDate)
	{
		this.generatedDate = generatedDate;
	}

	@Override
	public String toString()
	{
		return "CashAccountModel [idpsAccountName=" + idpsAccountName + ", accountType=" + accountType + ", productName="
			+ productName + ", accountId=" + accountId + ", adviserName=" + adviserName + ", personName=" + personName
			+ ", phoneNumber=" + phoneNumber + ", email=" + email + ", addressLine1=" + addressLine1 + ", addressLine2="
			+ addressLine2 + ", portfolio=" + portfolio + ", lastPortfolioCalculationDate=" + lastPortfolioCalculationDate
			+ ", bsb=" + bsb + ", billerCode=" + billerCode + ", crn=" + crn + ", cashAccountNumber=" + cashAccountNumber
			+ ", interestRate=" + interestRate + ", interestEarned=" + interestEarned + ", financialYearToDate="
			+ financialYearToDate + ", availableBalance=" + availableBalance + ", totalBalance=" + totalBalance
			+ ", registeredDate=" + registeredDate + "]";
	}

	public String getUnClearedBalance()
	{
		return unClearedBalance;
	}

	public void setUnClearedBalance(String unClearedBalance)
	{
		this.unClearedBalance = unClearedBalance;
	}

	public String getCurrentDate()
	{
		return currentDate;
	}

	public void setCurrentDate(String currentDate)
	{
		this.currentDate = currentDate;
	}

	public String getFmtRegisteredDate()
	{
		return fmtRegisteredDate;
	}

	public void setFmtRegisteredDate(String fmtRegisteredDate)
	{
		this.fmtRegisteredDate = fmtRegisteredDate;
	}

	public String getFmtCurrentDate()
	{
		return fmtCurrentDate;
	}

	public void setFmtCurrentDate(String fmtCurrentDate)
	{
		this.fmtCurrentDate = fmtCurrentDate;
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

	public List <TermDepositAccount> getListOfTermDepositAccounts()
	{
		return listOfTermDepositAccounts;
	}

	public void setListOfTermDepositAccounts(List <TermDepositAccount> listOfTermDepositAccounts)
	{
		this.listOfTermDepositAccounts = listOfTermDepositAccounts;
	}

	public int getNoOfProductHolder()
	{
		return noOfProductHolder;
	}

	public void setNoOfProductHolder(int noOfProductHolder)
	{
		this.noOfProductHolder = noOfProductHolder;
	}

	public String getPreviousMonth()
	{
		return previousMonth;
	}

	public void setPreviousMonth(String previousMonth)
	{
		this.previousMonth = previousMonth;
	}

	public String getPreviousQuater()
	{
		return previousQuater;
	}

	public void setPreviousQuater(String previousQuater)
	{
		this.previousQuater = previousQuater;
	}

	public String getInvestmentId()
	{
		return investmentId;
	}

	public void setInvestmentId(String investmentId)
	{
		this.investmentId = investmentId;
	}

}
