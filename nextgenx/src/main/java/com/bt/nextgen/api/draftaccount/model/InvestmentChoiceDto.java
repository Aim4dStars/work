package com.bt.nextgen.api.draftaccount.model;


import java.math.BigDecimal;

public class InvestmentChoiceDto {
    private String managedPortfolio;
    private BigDecimal initialInvestmentAmount;

    public String getManagedPortfolio() {
        return managedPortfolio;
    }

    public void setManagedPortfolio(String managedPortfolio) {
        this.managedPortfolio = managedPortfolio;
    }

    public BigDecimal getInitialInvestmentAmount() {
        return initialInvestmentAmount;
    }

    public void setInitialInvestmentAmount(BigDecimal initialInvestmentAmount) {
        this.initialInvestmentAmount = initialInvestmentAmount;
    }
}
