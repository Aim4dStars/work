package com.bt.nextgen.service.avaloq.holdingbreach;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachAsset;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

@ServiceBean(xpath = "asset", type = ServiceBeanType.CONCRETE)
public class HoldingBreachAssetImpl implements HoldingBreachAsset {
    private static final String XML_HEADER = "asset_head_list/asset_head/";

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "asset/annot/ctx/id")
    private String assetId;

    @ServiceElement(xpath = XML_HEADER + "mv/val")
    private BigDecimal marketValue;

    @ServiceElement(xpath = XML_HEADER + "viol_val/val")
    private BigDecimal portfolioPercent;

    @ServiceElement(xpath = XML_HEADER + "val_to/val")
    private BigDecimal holdingLimitPercent;

    @ServiceElement(xpath = XML_HEADER + "breach/val")
    private BigDecimal breachAmount;

    @Override
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @Override
    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(BigDecimal marketValue) {
        this.marketValue = marketValue;
    }

    @Override
    public BigDecimal getPortfolioPercent() {
        return portfolioPercent;
    }

    public void setPortfolioPercent(BigDecimal portfolioPercent) {
        this.portfolioPercent = portfolioPercent;
    }

    @Override
    public BigDecimal getHoldingLimitPercent() {
        return holdingLimitPercent;
    }

    public void setHoldingLimitPercent(BigDecimal holdingLimitPercent) {
        this.holdingLimitPercent = holdingLimitPercent;
    }

    @Override
    public BigDecimal getBreachAmount() {
        return breachAmount;
    }

    public void setBreachAmount(BigDecimal breachAmount) {
        this.breachAmount = breachAmount;
    }
}
