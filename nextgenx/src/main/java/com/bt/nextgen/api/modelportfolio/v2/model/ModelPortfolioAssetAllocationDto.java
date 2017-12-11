package com.bt.nextgen.api.modelportfolio.v2.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;

public class ModelPortfolioAssetAllocationDto extends BaseDto {
    @JsonView(JsonViews.Write.class)
    private String assetCode;

    @JsonView(JsonViews.Write.class)
    private BigDecimal assetAllocation;

    @JsonView(JsonViews.Write.class)
    private BigDecimal tradePercent;

    @JsonView(JsonViews.Write.class)
    private BigDecimal toleranceLimit;

    private String assetId;
    private String assetName;
    private String assetClass;
    private String groupClass;
    private String assetType;
    private BigDecimal investmentHoldingLimit;
    private BigDecimal lastEditedAllocation;

    public ModelPortfolioAssetAllocationDto() {
        super();
    }

    public ModelPortfolioAssetAllocationDto(Asset asset, BigDecimal assetAllocation, BigDecimal tradePercent,
            BigDecimal investmentHoldingLimit) {
        super();
        this.assetId = asset.getAssetId();
        this.assetName = asset.getAssetName();
        this.assetCode = asset.getAssetCode();
        this.assetClass = asset.getAssetClass() == null ? Constants.EMPTY_STRING : asset.getAssetClass().getDescription();
        this.groupClass = asset.getModelAssetClass() == null ? null : asset.getModelAssetClass().getDescription();
        this.assetType = asset.getAssetType() == null ? AssetType.OTHER.getGroupDescription() : asset.getAssetType()
                .getGroupDescription();

        this.assetAllocation = assetAllocation;
        this.tradePercent = tradePercent;
        this.investmentHoldingLimit = investmentHoldingLimit;
    }

    public ModelPortfolioAssetAllocationDto(Asset asset, BigDecimal assetAllocation, BigDecimal tradePercent,
            BigDecimal investmentHoldingLimit, BigDecimal toleranceLimit, BigDecimal lastEditedAllocation) {
        this(asset, assetAllocation, tradePercent, investmentHoldingLimit);
        this.toleranceLimit = toleranceLimit;
        this.lastEditedAllocation = lastEditedAllocation;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public BigDecimal getAssetAllocation() {
        return assetAllocation;
    }

    public BigDecimal getTradePercent() {
        return tradePercent;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getGroupClass() {
        return groupClass;
    }

    public BigDecimal getInvestmentHoldingLimit() {
        return investmentHoldingLimit;
    }

    public BigDecimal getToleranceLimit() {
        return toleranceLimit;
    }

    public BigDecimal getLastEditedAllocation() {
        return lastEditedAllocation;
    }

}
