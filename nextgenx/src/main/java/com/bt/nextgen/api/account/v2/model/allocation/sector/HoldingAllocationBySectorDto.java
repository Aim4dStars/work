package com.bt.nextgen.api.account.v2.model.allocation.sector;

import com.bt.nextgen.api.account.v2.model.allocation.AssetAllocationDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;

import java.math.BigDecimal;

@Deprecated
public class HoldingAllocationBySectorDto extends BaseDto implements AssetAllocationDto, AllocationBySectorDto {

    private final Asset asset;
    private final BigDecimal balance;
    private final BigDecimal units;
    private final BigDecimal percent;
    private final boolean pending;
    private final Boolean isExternal;
    private final String source;

    public HoldingAllocationBySectorDto(Asset asset, String source, BigDecimal balance, BigDecimal units, BigDecimal percent,
            boolean pending, Boolean isExternal) {
        this.asset = asset;
        this.source = source;
        this.balance = balance;
        this.percent = percent;
        this.units = units;
        this.pending = pending;
        this.isExternal = isExternal;
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
        String assetClass = asset.getAssetClass() == null ? AssetClass.CASH.getDescription() : asset.getAssetClass()
                .getDescription();
        return assetClass;
    }


    @Override
    public String getName() {
        String assetName = asset.getAssetName();
        return assetName;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public BigDecimal getInternalBalance() {
        if (isExternal) {
            return BigDecimal.ZERO;
        }
        return balance;
    }

    @Override
    public BigDecimal getExternalBalance() {
        if (isExternal) {
            return balance;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getAllocationPercentage() {
        return percent;
    }

    @Override
    public BigDecimal getUnits() {
        return units;
    }

    @Override
    public Boolean getPending() {
        return pending;
    }

    @Override
    public Boolean getIsExternal() {
        return isExternal;
    }

    public String getSource() {
        return source;
    }

}
