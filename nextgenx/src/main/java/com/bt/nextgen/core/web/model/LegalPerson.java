package com.bt.nextgen.core.web.model;

public class LegalPerson extends Person
{
	private String abn;

	//SMSF,Trust
	private String registrationState;

	//CompanyType
	private String asic;
	private String acn;

	//TrustType
	private String arsn; //Will be ARSN for REG_MIS and legislationName  for REG_TRUST 
	private String trustType;
	//Trust Type - Government super fund  
	private String legislationName;
	//Trust Type - Regulated
	private String regulatorName;
	private boolean isCompany;
	private Advisor advisor;

	public Advisor getAdvisor()
	{
		return advisor;
	}

	public void setAdvisor(Advisor advisor)
	{
		this.advisor = advisor;
	}

	public String getAbn()
	{
		return abn;
	}

	public void setAbn(String abn)
	{
		this.abn = abn;
	}

	public String getRegistrationState()
	{
		return registrationState;
	}

	public void setRegistrationState(String registrationState)
	{
		this.registrationState = registrationState;
	}

	public String getAsic()
	{
		return asic;
	}

	public void setAsic(String asic)
	{
		this.asic = asic;
	}

	public String getAcn()
	{
		return acn;
	}

	public void setAcn(String acn)
	{
		this.acn = acn;
	}

	public String getArsn()
	{
		return arsn;
	}

	public void setArsn(String arsn)
	{
		this.arsn = arsn;
	}

	public String getTrustType()
	{
		return trustType;
	}

	public void setTrustType(String trustType)
	{
		this.trustType = trustType;
	}

	public String getLegislationName()
	{
		return legislationName;
	}

	public void setLegislationName(String legislationName)
	{
		this.legislationName = legislationName;
	}

	public String getRegulatorName()
	{
		return regulatorName;
	}

	public void setRegulatorName(String regulatorName)
	{
		this.regulatorName = regulatorName;
	}

	public boolean getIsCompany()
	{
		return isCompany;
	}

	public void setIsCompany(boolean isCompany)
	{
		this.isCompany = isCompany;
	}

}
