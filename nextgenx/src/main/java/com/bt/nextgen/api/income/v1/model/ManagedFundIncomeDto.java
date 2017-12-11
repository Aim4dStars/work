package com.bt.nextgen.api.income.v1.model;

import com.bt.nextgen.service.integration.income.DistributionIncome;

@Deprecated
public class ManagedFundIncomeDto extends DistributionIncomeDto
{
    public ManagedFundIncomeDto(String name, String code, DistributionIncome income)
	{
        super(name, code, income);
    }
}
