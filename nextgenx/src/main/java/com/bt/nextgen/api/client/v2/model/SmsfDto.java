package com.bt.nextgen.api.client.v2.model;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class SmsfDto extends RegisteredEntityDto
{

	private CompanyDto company;

	public CompanyDto getCompany() {
		return company;
	}

	public void setCompany(CompanyDto company) {
		this.company = company;
	}

}
