package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.ServiceErrors;

public class TermDepositInterfaceModel implements TermDepositInterface
{
	String portfolioId;
	String tdId;
	String tdAccountId;
	String amount;
	String renewModeId;
	ServiceErrors serviceErrors;
	String withdrawalInterestRate;
	String withdrawalPrinicpal;
	String percentTermElapsed;
	
	public String getPortfolioId()
	{
		return portfolioId;
	}
	public void setPortfolioId(String portfolioId)
	{
		this.portfolioId = portfolioId;
	}
	public String getTdId()
	{
		return tdId;
	}	
	public void setTdId(String tdId)
	{
		this.tdId = tdId;
	}	
	public String getTdAccountId()
	{
		return tdAccountId;
	}
	public void setTdAccountId(String tdAccountId)
	{
		this.tdAccountId = tdAccountId;
	}
	public String getAmount()
	{
		return amount;
	}
	public void setAmount(String amount)
	{
		this.amount = amount;
	}
	public String getRenewModeId()
	{
		return renewModeId;
	}
	public void setRenewModeId(String renewModeId)
	{
		this.renewModeId = renewModeId;
	}
	public String getWithdrawalInterestRate() {
		return withdrawalInterestRate;
	}
	public void setWithdrawalInterestRate(String withdrawalInterestRate) {
		this.withdrawalInterestRate = withdrawalInterestRate;
	}
	public String getWithdrawalPrinicpal() {
		return withdrawalPrinicpal;
	}
	public void setWithdrawalPrinicpal(String withdrawalPrinicpal) {
		this.withdrawalPrinicpal = withdrawalPrinicpal;
	}
	public String getPercentTermElapsed() {
		return percentTermElapsed;
	}
	public void setPercentTermElapsed(String percentTermElapsed) {
		this.percentTermElapsed = percentTermElapsed;
	}	

}
