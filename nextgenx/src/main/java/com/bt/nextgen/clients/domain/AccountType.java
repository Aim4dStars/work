package com.bt.nextgen.clients.domain;

public enum AccountType
{
	SMSF("SMSF"), JOINT("Joint"), TRUST("Trust"), COMPANY("Company"), INDIVIDUAL("Individual"), SUPER_FUND("Superfund"),
	NATURAL("natural"),
	//TODO Need to set the actual accountTypes after getting clarity
	SMSF_INDIVIDUAL("SMSFIndividual"),SMSF_CORPORATE("SMSFCorporate"),
	TRUST_INDIVIDUAL("TRUSTIndividual") ,TRUST_CORPORATE("TRUSTCorporate");;
	String name;

	public String getName()
	{
		return name;
	}

	AccountType(String name)
	{
		this.name = name;
	}
}
