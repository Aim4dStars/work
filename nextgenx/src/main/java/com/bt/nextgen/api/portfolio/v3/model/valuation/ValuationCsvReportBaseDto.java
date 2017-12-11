package com.bt.nextgen.api.portfolio.v3.model.valuation;

import java.math.BigDecimal;

public class ValuationCsvReportBaseDto {

    protected final String holdingType;
    protected final String assetName;
    protected final BigDecimal balance;
    protected final BigDecimal portfolioPercent;
    protected final BigDecimal interestRate;

    public ValuationCsvReportBaseDto(String holdingType, String assetName, BigDecimal balance, BigDecimal portfolioPercent,
            BigDecimal interestRate) {
        this.holdingType = holdingType;
        this.assetName = assetName;
        this.balance = balance;
        this.portfolioPercent = portfolioPercent;
        this.interestRate = interestRate;
    }

    public String getHoldingType() {
        return holdingType;
    }

    public String getAssetName() {
        return assetName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getPortfolioPercent() {
        return portfolioPercent;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

}