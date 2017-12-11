package com.bt.nextgen.reports.web.model;

public interface IncomeExpenseReportInterface
{

	String getFromIncExpDate();

	void setFromIncExpDate(String fromDate);

	String getToIncExpDate();

	void setToIncExpDate(String toDate);

	String getOpeningBalance();

	void setOpeningBalance(String openingBalance);

	String getWithdrawalBalance();

	void setWithdrawalBalance(String withdrawalBalance);

	String getDepositBalance();

	void setDepositBalance(String depositBalance);

	String getInterestReceived();

	void setInterestReceived(String interestReceived);

	String getDistributionsReceived();

	void setDistributionsReceived(String distributionsReceived);

	String getFeesAndCharges();

	void setFeesAndCharges(String feesAndCharges);

	String getTaxes();

	void setTaxes(String taxes);

	String getClosingBalance();

	void setClosingBalance(String closingBalance);

	String getAfterWithdrawalBalance();

	void setAfterWithdrawalBalance(String afterWithdrawalBalance);
	
	String getAfterDepositBalance();

	void setAfterDepositBalance(String afterDepositBalance);

	String getIncomeExpenseDebitBal();

	void setIncomeExpenseDebitBal(String incomeExpenseDebitBal);

	String getIncomeExpenseCreditBal();

	void setIncomeExpenseCreditBal(String incomeExpenseCreditBal);
	
	String getIncomeExpenseFinalBal();

	void setIncomeExpenseFinalBal(String incomeExpenseFinalBal);

	String getTotalLoss();

	void setTotalLoss(String totalLoss);

	String getTotalGain();

	void setTotalGain(String totalGain);
}