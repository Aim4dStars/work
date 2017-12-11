package com.bt.nextgen.api.income.v1.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

@Deprecated
public class FeeRebateIncomeDto extends IncomeDto {

    public FeeRebateIncomeDto(String name, String code, DateTime paymentDate, BigDecimal amount) {
        super(name, code, paymentDate, amount);
    }

    public FeeRebateIncomeDto(DistributionIncomeDto distributionIncomeDto) {
        super(distributionIncomeDto.getName(), distributionIncomeDto.getCode(), distributionIncomeDto.getPaymentDate(),
                distributionIncomeDto.getAmount());
    }

}
