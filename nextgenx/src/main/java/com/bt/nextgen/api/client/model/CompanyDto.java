package com.bt.nextgen.api.client.model;

import com.bt.nextgen.api.client.model.RegisteredEntityDto;

public class CompanyDto extends RegisteredEntityDto
{
	private String asicName;
	private String acn;
    private String personalInvestmentEntity;

    public String getPersonalInvestmentEntity() {
        return personalInvestmentEntity;
    }

    public void setPersonalInvestmentEntity(String personalInvestmentEntity) {
        this.personalInvestmentEntity = personalInvestmentEntity;
    }

    public String getAsicName()
	{
		return asicName;
	}
	public void setAsicName(String asicName)
	{
		this.asicName = asicName;
	}

	public String getAcn()
	{
		return acn;
	}

	public void setAcn(String acn)
	{
		this.acn = acn;
	}

}
