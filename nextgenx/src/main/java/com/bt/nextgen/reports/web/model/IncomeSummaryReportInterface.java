package com.bt.nextgen.reports.web.model;

import java.util.List;

public interface IncomeSummaryReportInterface {

	String getFromDate();
	
	void setFromDate(String fromDate);
	
	String getToDate();
	
	void setToDate(String toDate);
	
	String getAccountName();
	
	void setAccountName(String accountName);
	
	String getAccountType();
	
	void setAccountType(String accountType);
	
	String getProductName(); 
	
	void setProductName(String productName);
	
	String getAccountId();
	
	void setAccountId(String accountId);
	
	String getAdviserName();
	
	void setAdviserName(String adviserName);
	
	List<String> getAccountHoldersName();
	
	void setAccountHoldersName(List<String> accountHoldersName);
	
	String getFirstName(); 
	
	void setFirstName(String firstName);
	
	String getLastName();
	
	void setLastName(String lastName);
	
	String getPhoneNumber();
	
	void setPhoneNumber(String phoneNumber);
	
	String getEmail();
	
	void setEmail(String email);
	
	String getAddressLine1();
	
	void setAddressLine1(String addressLine1);
	
	String getAddressLine2(); 
	
	void setAddressLine2(String addressLine2);
	
	String getTotalIncomeAccrued(); 
	
	void setTotalIncomeAccrued(String totalIncomeAccrued);
	
	String getTotalIncomeEarned();
	
	void setTotalIncomeEarned(String totalIncomeEarned);
	
	String getDisclaimer();
	
	void setDisclaimer(String disclaimer); 
	
	String getCreatedDate();
	
	void setCreatedDate(String createdDate);
	
	String getCity();
	
	void setCity(String city);
	
	String getState();
	
	void setState(String state);
	
	String getCountry();
	
	void setCountry(String country);
	
	String getPin(); 
	
	void setPin(String pin);
	
	String getTotalIncomePaid();
	
	void setTotalIncomePaid(String totalIncomePaid);
	
	List<IncomeTransactionModel> getCashTransactions();
	
	void setCashTransactions(List<IncomeTransactionModel> cashTransactions);
	
	String getTotalCashAccountIncome(); 
	
	void setTotalCashAccountIncome(String totalCashAccountIncome);
	
	List<IncomeTransactionModel> getAllTermDepositsTransactions();
	
	void setAllTermDepositsTransactions(List<IncomeTransactionModel> allTermDepositsTransactions);
	
	String getTotalTermDepositInterest();

	void setTotalTermDepositInterest(String totalTermDepositInterest);
	
	String getFmtCurrentDate();
	
	void setFmtCurrentDate(String fmtCurrentDate); 
}


