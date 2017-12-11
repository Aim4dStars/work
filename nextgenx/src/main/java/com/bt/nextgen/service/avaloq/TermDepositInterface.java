package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.ServiceErrors;

public interface TermDepositInterface
{
	public String getPortfolioId();
	public void setPortfolioId(String portfolioId);
	public String getTdId();
	public void setTdId(String tdId);
	public String getTdAccountId();
	public void setTdAccountId(String tdAccountId);
	public String getAmount();
	public void setAmount(String amount);
	public String getRenewModeId();
	public void setRenewModeId(String renewModeId);
	public String getWithdrawalInterestRate();
	public void setWithdrawalInterestRate(String withdrawalInterestRate);
	public String getWithdrawalPrinicpal();
	public void setWithdrawalPrinicpal(String withdrawalPrinicpal);
	public String getPercentTermElapsed();
	public void setPercentTermElapsed(String percentTermElapsed);
}
