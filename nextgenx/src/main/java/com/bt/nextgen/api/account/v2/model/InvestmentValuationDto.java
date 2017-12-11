package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.NamedDto;

import java.math.BigDecimal;

@Deprecated
public interface InvestmentValuationDto extends Dto, NamedDto {

    BigDecimal getBalance();

    BigDecimal getAvailableBalance();

    BigDecimal getIncome();

    Boolean getIncomeOnly();

    BigDecimal getPortfolioPercent();

    String getSubAccountId();

    Boolean getExternalAsset();

    String getSource();

    String getCategoryName();

}