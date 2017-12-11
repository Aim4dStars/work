package com.bt.nextgen.core.web.model;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.portfolio.domain.Portfolio;

import java.util.List;

public class Investor extends Person{
	
	private Advisor advisor;
	private String accountHolderType;
	private String activationStatus;
	private List <Portfolio> portfolios;

	public String getAccountHolderType()
	{
		return accountHolderType;
	}
	public void setAccountHolderType(String accountHolderType) 
	{
		this.accountHolderType = accountHolderType;
	}
	public String getActivationStatus() 
	{
		return activationStatus;
	}
	public void setActivationStatus(String activationStatus) 
	{
		this.activationStatus = activationStatus;
	}
	public List<Portfolio> getWrapAccounts() 
	{
		return portfolios;
	}
	public void setWrapAccounts(List<Portfolio> portfolios) 
	{
		this.portfolios = portfolios;
	}
	public Advisor getAdvisor()
	{
		return advisor;
	}
	public void setAdvisor(Advisor advisor)
	{
		this.advisor = advisor;
	}

	@Override public EncodedString getAdviserId()
	{
		return advisor.getAdviserId();
	}
}
