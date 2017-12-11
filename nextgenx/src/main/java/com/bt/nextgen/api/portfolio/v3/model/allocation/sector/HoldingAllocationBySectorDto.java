package com.bt.nextgen.api.portfolio.v3.model.allocation.sector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import ch.lambdaj.Lambda;

import com.bt.nextgen.api.portfolio.v3.model.allocation.AssetAllocationDto;
import com.bt.nextgen.api.portfolio.v3.service.allocation.HoldingSource;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class HoldingAllocationBySectorDto extends BaseDto implements AssetAllocationDto, AllocationBySectorDto {

    private final Asset asset;
    private BigDecimal portfolioBalance;
    private List<HoldingAllocationBySourceDto> children = new ArrayList<>();

    public HoldingAllocationBySectorDto(List<HoldingSource> holdings, BigDecimal portfolioBalance, boolean isIncome) {
        for(HoldingSource holding: holdings){
            this.children.add(new HoldingAllocationBySourceDto(holding, portfolioBalance, isIncome));
        }
        this.asset = holdings.get(0).getSource();
        this.portfolioBalance = portfolioBalance;
    }

    public String getAssetCode() {
        return asset.getAssetCode();
    }

    public String getAssetType() {
        if (asset.getAssetType() != null) {
            return asset.getAssetType().name();
        }
        return null;
    }

    public String getAssetId() {
        return asset.getAssetId();
    }

    public String getAssetSector() {
        String assetClass = asset.getAssetClass() == null ? AssetClass.CASH.getDescription()
                : asset.getAssetClass().getDescription();
        return assetClass;
    }

    @JsonIgnore
    public List<HoldingAllocationBySourceDto> getChildren() {
        return children;
    }

    @Override
    public String getName() {
        String assetName = asset.getAssetName();
        return assetName;
    }

    @Override
    public BigDecimal getBalance() {
        return Lambda.sumFrom(this.children).getBalance();
    }

    @Override
    public BigDecimal getInternalBalance() {
        if (this.children.get(0).isExternal()) {
            return BigDecimal.ZERO;
        }
        return getBalance();
    }

    @Override
    public BigDecimal getExternalBalance() {
        if (this.children.get(0).isExternal()) {
            return getBalance();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getAllocationPercentage() {
        BigDecimal marketValue = Lambda.sumFrom(this.children).getBalance();
        BigDecimal percent = BigDecimal.ZERO;
        if (!(BigDecimal.ZERO.compareTo(marketValue) == 0)) {
            percent = marketValue.divide(this.portfolioBalance, 8, RoundingMode.HALF_UP);
        }
        return percent;
    }

    @Override
    public BigDecimal getUnits() {
        BigDecimal units = null;
        if (!(asset.getAssetType() == AssetType.CASH || asset.getAssetType() == AssetType.TERM_DEPOSIT)) {
            units = Lambda.sumFrom(this.children).getUnits(); 
        }
        return units;
    }

    @Override
    public Boolean getPending() {
        return this.children.get(0).isPending();
    }

    @Override
    public Boolean getIsExternal() {
        return this.children.get(0).isExternal();
    }

    public String getSource() {
        return this.children.get(0).getSource();
    }

}
