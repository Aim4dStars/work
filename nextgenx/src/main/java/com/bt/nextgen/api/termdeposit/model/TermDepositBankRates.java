package com.bt.nextgen.api.termdeposit.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.termdeposit.web.model.TermDepositRateModel;

public class TermDepositBankRates 
{
	private String brandLogoUrl;
	private BigDecimal investmentAmount;
	private String maturityDate;
	
	private String brandName;
	private String brandId;
	
	private Map<Term, TermDepositRateModel> termMap = new HashMap<Term, TermDepositRateModel>();

	public String getBrandLogoUrl()
	{
		return brandLogoUrl;
	}

	public void setBrandLogoUrl(String brandLogoUrl)
	{
		this.brandLogoUrl = brandLogoUrl;
	}

	public BigDecimal getInvestmentAmount()
	{
		return investmentAmount;
	}

	public void setInvestmentAmount(BigDecimal investmentAmount)
	{
		this.investmentAmount = investmentAmount;
	}

	public String getMaturityDate()
	{
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate)
	{
		this.maturityDate = maturityDate;
	}

	public String getBrandName() 
	{
		return brandName;
	}

	public void setBrandName(String brandName) 
	{
		this.brandName = brandName;
	}

	public String getBrandId() 
	{
		return brandId;
	}

	public void setBrandId(String brandId) 
	{
		this.brandId = brandId;
	}

	public Map<Term, TermDepositRateModel> getTermMap() 
	{
		return termMap;
	}

	public void setTermMap(Map<Term, TermDepositRateModel> termMap) 
	{
		this.termMap = termMap;
	}
}
