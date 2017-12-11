package com.bt.nextgen.reports.web.model;

public class IncomeExpenseReportModel implements IncomeExpenseReportInterface
{

	//Dates for Filter
	private String fromIncExpDate;
	private String toIncExpDate;

	//Portfolio Balance Details
	private String openingBalance;
	private String withdrawalBalance;
	private String depositBalance;
	private String interestReceived;
	private String distributionsReceived;
	private String feesAndCharges;
	private String taxes;
	private String afterWithdrawalBalance;
	private String afterDepositBalance;
	private String incomeExpenseDebitBal;
	private String incomeExpenseCreditBal;
	private String incomeExpenseFinalBal;
	private String closingBalance;
	private String totalLoss;
	private String totalGain;

	//Dates for validation 
	private String lastSevenYearDate;
	private String currentDate;
	private String todayDateTime;
	private String currentFinYearDate;


	public String getFromIncExpDate()
	{
		return fromIncExpDate;
	}

	public void setFromIncExpDate(String fromIncExpDate)
	{
		this.fromIncExpDate = fromIncExpDate;
	}

	public String getToIncExpDate()
	{
		return toIncExpDate;
	}

	public void setToIncExpDate(String toIncExpDate)
	{
		this.toIncExpDate = toIncExpDate;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#getOpeningBalance()
	 */
	@Override
	public String getOpeningBalance()
	{
		return openingBalance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#setOpeningBalance(java.lang.String)
	 */
	@Override
	public void setOpeningBalance(String openingBalance)
	{
		this.openingBalance = openingBalance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#getWithdrawalBalance()
	 */
	@Override
	public String getWithdrawalBalance()
	{
		return withdrawalBalance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#setWithdrawalBalance(java.lang.String)
	 */
	@Override
	public void setWithdrawalBalance(String withdrawalBalance)
	{
		this.withdrawalBalance = withdrawalBalance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#getDepositBalance()
	 */
	@Override
	public String getDepositBalance()
	{
		return depositBalance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#setDepositBalance(java.lang.String)
	 */
	@Override
	public void setDepositBalance(String depositBalance)
	{
		this.depositBalance = depositBalance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#getInterestReceived()
	 */
	@Override
	public String getInterestReceived()
	{
		return interestReceived;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#setInterestReceived(java.lang.String)
	 */
	@Override
	public void setInterestReceived(String interestReceived)
	{
		this.interestReceived = interestReceived;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#getDistributionsReceived()
	 */
	@Override
	public String getDistributionsReceived()
	{
		return distributionsReceived;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#setDistributionsReceived(java.lang.String)
	 */
	@Override
	public void setDistributionsReceived(String distributionsReceived)
	{
		this.distributionsReceived = distributionsReceived;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#getFeesAndCharges()
	 */
	@Override
	public String getFeesAndCharges()
	{
		return feesAndCharges;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#setFeesAndCharges(java.lang.String)
	 */
	@Override
	public void setFeesAndCharges(String feesAndCharges)
	{
		this.feesAndCharges = feesAndCharges;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#getTaxes()
	 */
	@Override
	public String getTaxes()
	{
		return taxes;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#setTaxes(java.lang.String)
	 */
	@Override
	public void setTaxes(String taxes)
	{
		this.taxes = taxes;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#getClosingBalance()
	 */
	@Override
	public String getClosingBalance()
	{
		return closingBalance;
	}

	/* (non-Javadoc)
	 * @see com.bt.nextgen.reports.web.model.IncomeExpenseReportInterface#setClosingBalance(java.lang.String)
	 */
	@Override
	public void setClosingBalance(String closingBalance)
	{
		this.closingBalance = closingBalance;
	}


	public String getAfterWithdrawalBalance()
	{
		return afterWithdrawalBalance;
	}

	public void setAfterWithdrawalBalance(String afterWithdrawalBalance)
	{
		this.afterWithdrawalBalance = afterWithdrawalBalance;
	}

	public String getAfterDepositBalance()
	{
		return afterDepositBalance;
	}

	public void setAfterDepositBalance(String afterDepositBalance)
	{
		this.afterDepositBalance = afterDepositBalance;
	}

	public String getIncomeExpenseDebitBal()
	{
		return incomeExpenseDebitBal;
	}

	public void setIncomeExpenseDebitBal(String incomeExpenseDebitBal)
	{
		this.incomeExpenseDebitBal = incomeExpenseDebitBal;
	}

	public String getIncomeExpenseCreditBal()
	{
		return incomeExpenseCreditBal;
	}

	public void setIncomeExpenseCreditBal(String incomeExpenseCreditBal)
	{
		this.incomeExpenseCreditBal = incomeExpenseCreditBal;
	}

	public String getIncomeExpenseFinalBal()
	{
		return incomeExpenseFinalBal;
	}

	public void setIncomeExpenseFinalBal(String incomeExpenseFinalBal)
	{
		this.incomeExpenseFinalBal = incomeExpenseFinalBal;
	}

	public String getLastSevenYearDate()
	{
		return lastSevenYearDate;
	}

	public void setLastSevenYearDate(String lastSevenYearDate)
	{
		this.lastSevenYearDate = lastSevenYearDate;
	}

	public String getCurrentDate()
	{
		return currentDate;
	}

	public void setCurrentDate(String currentDate)
	{
		this.currentDate = currentDate;
	}

	public String getTodayDateTime()
	{
		return todayDateTime;
	}

	public void setTodayDateTime(String todayDateTime)
	{
		this.todayDateTime = todayDateTime;
	}

	public String getCurrentFinYearDate()
	{
		return currentFinYearDate;
	}

	public void setCurrentFinYearDate(String currentFinYearDate)
	{
		this.currentFinYearDate = currentFinYearDate;
	}

	public String getTotalLoss()
	{
		return totalLoss;
	}

	public void setTotalLoss(String totalLoss)
	{
		this.totalLoss = totalLoss;
	}

	public String getTotalGain()
	{
		return totalGain;
	}

	public void setTotalGain(String totalGain)
	{
		this.totalGain = totalGain;
	}

}
