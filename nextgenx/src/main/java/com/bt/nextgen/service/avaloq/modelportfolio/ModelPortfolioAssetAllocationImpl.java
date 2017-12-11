package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioAssetAllocation;

import java.math.BigDecimal;

@ServiceBean(xpath = "asset")
public class ModelPortfolioAssetAllocationImpl implements ModelPortfolioAssetAllocation {
    @ServiceElement(xpath = "asset_key/val")
    private String assetCode;

    @ServiceElement(xpath = "asset_wgt/val")
    private BigDecimal assetAllocation;

    @ServiceElement(xpath = "trade/val")
    private BigDecimal tradePercent;

    @ServiceElement(xpath = "asset_tolrc/val")
    private BigDecimal assetTolerance;

    @Override
    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    @Override
    public BigDecimal getAssetAllocation() {
        return assetAllocation;
    }

    public void setAssetAllocation(BigDecimal assetAllocation) {
        this.assetAllocation = assetAllocation;
    }

    @Override
    public BigDecimal getTradePercent() {
        return tradePercent;
    }

    public void setTradePercent(BigDecimal tradePercent) {
        this.tradePercent = tradePercent;
    }

    public BigDecimal getAssetTolerance() {
        return assetTolerance;
    }

    public void setAssetTolerance(BigDecimal assetTolerance) {
        this.assetTolerance = assetTolerance;
    }
}
