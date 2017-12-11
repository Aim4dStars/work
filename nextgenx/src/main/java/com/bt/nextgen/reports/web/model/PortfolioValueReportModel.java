package com.bt.nextgen.reports.web.model;

import com.bt.nextgen.clients.domain.Client;
import com.bt.nextgen.portfolio.domain.Portfolio;
import com.bt.nextgen.termdeposit.domain.TermDepositAccount;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PortfolioValueReportModel
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
	private String availableBalance;
	private String totalBalance;
	private String registeredDate;
	private List <Client> accountHolders;
	private String generateDate;

	private String fmtRegisteredDate;
	private String fmtCurrentDate;
	private String currentDate;
	private List <TermDepositAccount> listOfTermDepositAccounts;
	private String totalSumTermDepsoit;

	public PortfolioValueReportModel()
	{

	}

	public PortfolioValueReportModel(Portfolio portfolio, String wrapAccountId, String generatedDate, Client client)
	{
		this.idpsAccountName = portfolio.getAccountName();
		this.adviserName = client.getAdviserName();
		this.accountType = portfolio.getAccountType();
		this.personName = client.getUserName();
		this.phoneNumber = client.getPhone();
		this.email = client.getEmail();
		this.addressLine1 = client.getAddressLine1();
		this.addressLine2 = client.getAddressLine2();
		this.wrapAccountId = wrapAccountId;
		this.productName = portfolio.getProductName();

		this.portfolio = "1212121212";

		this.lastPortfolioCalculationDate = "21 march 13";

		//cash account details
		this.bsb = portfolio.getCashAccount().getBsb();
		this.billerCode = portfolio.getCashAccount().getBillerCode();
		this.crn = portfolio.getCashAccount().getCrn();
		this.cashAccountNumber = portfolio.getCashAccount().getAccountNumber();
		this.interestRate = portfolio.getCashAccount().getInterestRate();
		this.interestEarned = portfolio.getCashAccount().getInterestEarned().toString();
		this.financialYearToDate = "21 march 13";
		this.availableBalance = portfolio.getCashAccount().getAvailableBalance().toString();
		this.totalBalance = portfolio.getCashAccount().getTotalBalance().toString();
		this.registeredDate = portfolio.getRegisterDate().toString();
		this.generateDate = generatedDate;
		//	this.accountHolders = client.getAccountHolders();

		// Raj: for future date validation
		SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
		fmtRegisteredDate = new SimpleDateFormat(YYYY_MM_DD).format(portfolio.getRegisterDate());
		Date now = new Date();
		fmtCurrentDate = sdf.format(now);

		/*SimpleDateFormat sdf1 = new SimpleDateFormat("d MMM yyyy");
		Date date1 = sdf1.parse(generatedDate);*/
		this.generateDate = generatedDate;
		Date date = new Date();
		SimpleDateFormat sdfj = new SimpleDateFormat("d MMM yyyy h:mm a");
		this.currentDate = (sdfj.format(date)).toString();
		//	this.accountHolders = client.getAccountHolders();	
		this.listOfTermDepositAccounts = portfolio.getTermDepositAccounts();

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

	public List <Client> getAccountHolders()
	{
		return accountHolders;
	}

	public void setAccountHolders(List <Client> accountHolders)
	{
		this.accountHolders = accountHolders;
	}

	public String getGenerateDate()
	{
		return generateDate;
	}

	public void setGenerateDate(String generateDate)
	{
		this.generateDate = generateDate;
	}

	public String getWrapAccountId()
	{
		return wrapAccountId;
	}

	public void setWrapAccountId(String wrapAccountId)
	{
		this.wrapAccountId = wrapAccountId;
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

	public String getCurrentDate()
	{
		return currentDate;
	}

	public void setCurrentDate(String currentDate)
	{
		this.currentDate = currentDate;
	}

	public List <TermDepositAccount> getListOfTermDepositAccounts()
	{
		return listOfTermDepositAccounts;
	}

	public void setListOfTermDepositAccounts(List <TermDepositAccount> listOfTermDepositAccounts)
	{
		this.listOfTermDepositAccounts = listOfTermDepositAccounts;
	}

	public String getTotalSumTermDepsoit()
	{
		return totalSumTermDepsoit;
	}

	public void setTotalSumTermDepsoit(String totalSumTermDepsoit)
	{
		this.totalSumTermDepsoit = totalSumTermDepsoit;
	}

}
