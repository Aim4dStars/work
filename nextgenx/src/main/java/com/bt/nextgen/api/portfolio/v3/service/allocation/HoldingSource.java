package com.bt.nextgen.api.portfolio.v3.service.allocation;

import java.math.BigDecimal;

import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;

public class HoldingSource {
    private Asset asset;
    private Asset source;
    private BigDecimal units;
    private BigDecimal marketValue;
    private BigDecimal balance;
    private BigDecimal income;
    private String externalSource;
    private boolean pending;
    private boolean external;
    private boolean incomeOnly;

    public HoldingSource(Asset asset, AccountHolding holding, SubAccountValuation valuation) {
        super();
        this.asset = asset;
        this.balance = holding.getBalance();
        this.marketValue = holding.getMarketValue();
        if (valuation instanceof ManagedPortfolioAccountValuation) {
            this.source = ((ManagedPortfolioAccountValuation) valuation).getAsset();
        } else {
            this.source = holding.getAsset();
        }
        this.units = holding.getUnits();
        this.external = holding.getExternal();
        this.externalSource = holding.getSource();
        this.pending = holding.getReferenceAsset() != null;
        this.income = holding.getAccruedIncome();
        this.incomeOnly = holding.getIncomeOnly();
    }

    public HoldingSource(Asset cashAsset, BigDecimal cashHolding) {
        super();
        this.asset = cashAsset;
        this.source = cashAsset;
        this.balance = cashHolding;
        this.marketValue = cashHolding;
        this.units = cashHolding;
        this.income = BigDecimal.ZERO;
        this.pending = false;
    }

    public Asset getAsset() {
        return asset;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getUnits() {
        return units;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public Asset getSource() {
        return source;
    }

    public boolean isPending() {
        return pending;
    }

    public boolean isExternal() {
        return external;
    }

    public boolean isIncomeOnly() {
        return incomeOnly;
    }

    public String getExternalSource() {
        return externalSource;
    }
}
