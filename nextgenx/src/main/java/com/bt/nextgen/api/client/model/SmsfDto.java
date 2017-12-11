package com.bt.nextgen.api.client.model;

public class SmsfDto extends RegisteredEntityDto
{

	private CompanyDto company;

	public CompanyDto getCompany()
	{
		return company;
	}

	public void setCompany(CompanyDto company)
	{
		this.company = company;
	}

}
