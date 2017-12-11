package com.bt.nextgen.api.income.v2.model;

import java.math.BigDecimal;
import java.util.List;

public interface IncomeDto {

    public String getName();

    public String getCode();

    public List<IncomeDto> getChildren();

    public BigDecimal getAmount();
}
