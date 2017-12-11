package com.bt.nextgen.reports.web.model;

import com.bt.nextgen.clients.domain.Client;
import com.bt.nextgen.portfolio.domain.Portfolio;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IncomeSummaryReportModel implements IncomeSummaryReportInterface
{

	// header details
	private String fromDate;
	private String toDate;

	// Wrap Acoount Details
	private String accountName;
	private String accountType;
	private String productName;
	private String accountId;
	private String adviserName;
	private List<String> accountHoldersName;

	// Primary contact details
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String email;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String country;
	private String pin;
	

	// Income Details
	private String totalIncomePaid;
	private String totalIncomeAccrued;
	private String totalIncomeEarned;
	
	//Cash Management Account Transactions Details
	private List <IncomeTransactionModel> cashTransactions;
	private String totalCashAccountIncome;
	
	// Adding propoerty for US-568
	private List<IncomeTransactionModel> allTermDepositsTransactions;
	private String totalTermDepositInterest;

	private String disclaimer;
	private String createdDate;
	
	private static final String YYYY_MM_DD = "yyyy-MM-dd";
	private String fmtCurrentDate;


	public IncomeSummaryReportModel(Portfolio portfolio, String toDate,
			String fromDate, Client client) 
	{
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.accountName = portfolio.getAccountName();
		this.accountType = portfolio.getAccountType();
		this.accountId = portfolio.getAccountId();
		this.adviserName = portfolio.getPrimaryHolder().getAdviserName();
		for (Client accountHolders : portfolio.getAccountHolders())
		{
			
			accountHoldersName.add(accountHolders.getFirstName().concat(
					accountHolders.getLastName()));
		}
		this.firstName = portfolio.getPrimaryHolder().getFirstName();
		this.lastName = portfolio.getPrimaryHolder().getLastName();
		this.phoneNumber = portfolio.getPrimaryHolder().getPhone();
		this.email = portfolio.getPrimaryHolder().getEmail();
		this.addressLine1 = portfolio.getPrimaryHolder().getAddressLine1();
		this.addressLine2 = portfolio.getPrimaryHolder().getAddressLine2();

	}
	

	public String getFromDate() 
	{
		return fromDate;
	}

	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
	}

	public String getToDate() 
	{
		return toDate;
	}

	public void setToDate(String toDate)
	{
		this.toDate = toDate;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
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

	public List<String> getAccountHoldersName()
	{
		return accountHoldersName;
	}

	public void setAccountHoldersName(List<String> accountHoldersName)
	{
		this.accountHoldersName = accountHoldersName;
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

	

	public String getTotalIncomeAccrued() 
	{
		return totalIncomeAccrued;
	}

	public void setTotalIncomeAccrued(String totalIncomeAccrued)
	{
		this.totalIncomeAccrued = totalIncomeAccrued;
	}

	public String getTotalIncomeEarned()
	{
		return totalIncomeEarned;
	}

	public void setTotalIncomeEarned(String totalIncomeEarned)
	{
		this.totalIncomeEarned = totalIncomeEarned;
	}

	public String getDisclaimer()
	{
		return disclaimer;
	}

	public void setDisclaimer(String disclaimer) 
	{
		this.disclaimer = disclaimer;
	}

	public String getCreatedDate()
	{
		return createdDate;
	}

	public void setCreatedDate(String createdDate)
	{
		this.createdDate = createdDate;
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

	public String getTotalIncomePaid()
	{
		return totalIncomePaid;
	}

	public void setTotalIncomePaid(String totalIncomePaid)
	{
		this.totalIncomePaid = totalIncomePaid;
	}

	public List<IncomeTransactionModel> getCashTransactions()
	{
		return cashTransactions;
	}

	public void setCashTransactions(List<IncomeTransactionModel> cashTransactions)
	{
		this.cashTransactions = cashTransactions;
	}

	public String getTotalCashAccountIncome() 
	{
		return totalCashAccountIncome;
	}

	public void setTotalCashAccountIncome(String totalCashAccountIncome)
	{
		this.totalCashAccountIncome = totalCashAccountIncome;
	}

	public List<IncomeTransactionModel> getAllTermDepositsTransactions()
	{
		return allTermDepositsTransactions;
	}

	public void setAllTermDepositsTransactions(
			List<IncomeTransactionModel> allTermDepositsTransactions)
	{
		this.allTermDepositsTransactions = allTermDepositsTransactions;
	}

	public String getTotalTermDepositInterest()
	{
		return totalTermDepositInterest;
	}

	public void setTotalTermDepositInterest(String totalTermDepositInterest)
	{
		this.totalTermDepositInterest = totalTermDepositInterest;
	}
	
	public String getFmtCurrentDate()
	{
		return fmtCurrentDate;
	}

	public void setFmtCurrentDate(String fmtCurrentDate) 
	{
		this.fmtCurrentDate = fmtCurrentDate;
	}

	public IncomeSummaryReportModel()
	{
		// Raj: for future date validation
		SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
		fmtCurrentDate = sdf.format(new Date());
	}
}
