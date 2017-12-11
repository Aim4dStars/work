package com.bt.nextgen.core.web.model;

public class TaxInfo 
{
	private String countryForTax;
	private String taxOption;
	private String taxExemption;

	public String getCountryForTax() 
	{
		return countryForTax;
	}
	public void setCountryForTax(String countryForTax) 
	{
		this.countryForTax = countryForTax;
	}
	public String getTaxOption() 
	{
		return taxOption;
	}
	public void setTaxOption(String taxOption) 
	{
		this.taxOption = taxOption;
	}
	public String getTaxExemption() 
	{
		return taxExemption;
	}
	public void setTaxExemption(String taxExemption) 
	{
		this.taxExemption = taxExemption;
	}
}
