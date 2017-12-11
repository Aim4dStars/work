package com.bt.nextgen.accdetails.domain;

public enum CompanyRole
{

	DIRECTOR("DIRECTOR"), BENEFICIARY("BENEFICIARY"), SECRETARY("SECRETARY"), SIGNATORY("SIGNATORY"), TRUSTEE("TRUSTEE"), AO(
		"Account Owner"), PC("Primary Contact"), MBR("Member"), BENEF("BENEFICIARY"), SIGN("SIGNATORY");

	private final String companyRoleValue;

	public String getCompanyRoleValue()
	{
		return companyRoleValue;
	}

	CompanyRole(String companyRoleValue)
	{
		this.companyRoleValue = companyRoleValue;
	}

}
