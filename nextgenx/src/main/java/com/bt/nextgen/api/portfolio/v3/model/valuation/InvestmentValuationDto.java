package com.bt.nextgen.api.portfolio.v3.model.valuation;

import com.bt.nextgen.core.api.model.Dto;
import com.bt.nextgen.core.api.model.NamedDto;

import java.math.BigDecimal;

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
    
    /**
     * Required when Wrap holdings are added to a portfolio.
     * The calculated holding percentages then change.
     */
    void overwriteAccountBalance(BigDecimal accountBalance);

}