package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@Deprecated
public class InvestmentAssetDto extends BaseDto {
    private String assetId;
    private String assetCode;
    private String assetType;
    private String assetName;
    private DateTime effectiveDate;
    private BigDecimal quantity;
    private BigDecimal averageCost;
    private BigDecimal marketValue;
    private BigDecimal dollarGain;
    private BigDecimal allocationPercent;
    private BigDecimal unitPrice;
    private BigDecimal availableQuantity;
    private String status;
    private Boolean hasPending;
    private Boolean prepaymentAsset;
    private Boolean incomeOnly;
    private String isin;

    public InvestmentAssetDto(AccountHolding accountHolding, BigDecimal mpBalance) {
        super();
        this.assetId = accountHolding.getAsset().getAssetId();
        this.assetType = accountHolding.getAsset().getAssetType().name();
        this.assetName = accountHolding.getHoldingKey().getName();
        this.assetCode = accountHolding.getAsset().getAssetCode();
        this.effectiveDate = accountHolding.getUnitPriceDate();
        this.quantity = accountHolding.getUnits();
        this.averageCost = accountHolding.getCost();
        this.marketValue = accountHolding.getMarketValue();
        this.dollarGain = this.marketValue.subtract(this.averageCost);
        this.allocationPercent = PortfolioUtils.getValuationAsPercent(this.marketValue, mpBalance);
        this.unitPrice = accountHolding.getUnitPrice();
        this.availableQuantity = accountHolding.getAvailableUnits();
        this.status = accountHolding.getAsset().getStatus() != null ? accountHolding.getAsset().getStatus().getDisplayName() : "";
        this.hasPending = accountHolding.getHasPending();
        this.incomeOnly = accountHolding.getIncomeOnly();
        this.prepaymentAsset = Boolean.FALSE;
        if (accountHolding.getReferenceAsset() != null) {
            this.prepaymentAsset = Boolean.TRUE;
        }
        this.isin = accountHolding.getAsset().getIsin();
    }


    public String getAssetId() {
        return assetId;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public DateTime getEffectiveDate() {
        return effectiveDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public BigDecimal getDollarGain() {
        return dollarGain;
    }

    public BigDecimal getPercentGain() {
        return PortfolioUtils.getValuationAsPercent(dollarGain, averageCost);
    }

    public BigDecimal getAllocationPercent() {
        return allocationPercent;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Boolean getHasPending() {
        return hasPending;
    }

    public Boolean isPrepaymentAsset() {
        return prepaymentAsset;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public String getStatus() {
        return status;
    }

    public String getIsin() {
        return isin;
    }   
    
    public Boolean getIncomeOnly() {
        return incomeOnly;
    }

}
