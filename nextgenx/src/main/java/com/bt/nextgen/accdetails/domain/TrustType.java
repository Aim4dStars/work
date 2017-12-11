package com.bt.nextgen.accdetails.domain;

public enum TrustType
{

	REGU_TRUST("Regulated Trust"),
	REGI_MIS("Registered Managed Investment Scheme"),
	GOVT_SUPER_FUND("Government Super Fund"),
	OTHER("Other (Family, Unit, Charitable or Estate)");

	private final String trustTypeValue;

	public String getTrustTypeValue()
	{
		return trustTypeValue;
	}

	TrustType(String trustTypeValue)
	{
		this.trustTypeValue = trustTypeValue;
	}

}
