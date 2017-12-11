package com.bt.nextgen.api.income.v2.model;

import com.bt.nextgen.service.integration.income.FeeRebateIncome;
import com.bt.nextgen.service.integration.income.Income;

public class FeeRebateIncomeDto extends AbstractIncomeDto {

    public FeeRebateIncomeDto(String name, String code, Income income) {
        super(name, code, ((FeeRebateIncome) income).getPaymentDate(), ((FeeRebateIncome) income).getAmount());
    }

    public FeeRebateIncomeDto(DistributionIncomeDto distributionIncomeDto) {
        super(distributionIncomeDto.getName(), distributionIncomeDto.getCode(), distributionIncomeDto.getPaymentDate(),
                distributionIncomeDto.getAmount());
    }

}
