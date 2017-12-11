package com.bt.nextgen.api.account.v2.model.allocation.exposure;

import com.bt.nextgen.api.account.v2.model.allocation.AssetAllocationDto;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.portfolio.valuation.CashHolding;

import java.util.List;

@Deprecated
public class AssetAllocationByExposureDto extends AggregateAllocationByExposureDto implements AssetAllocationDto {

    private String assetId;
    private String assetCode;
    private String assetSector;

    /**
     * @param holding
     * @param contents
     */
    public AssetAllocationByExposureDto(Asset asset, List<AllocationByExposureDto> contents) {
        super(asset.getAssetName(), contents);
        this.assetId = asset.getAssetId();
        this.assetCode = asset.getAssetCode();

        this.assetSector = asset.getAssetClass() == null ? AssetClass.CASH.getDescription() : asset.getAssetClass()
                .getDescription();
    }

    public AssetAllocationByExposureDto(String assetName, List<AllocationByExposureDto> contents) {
        super(assetName, contents);
        this.assetId = null;
        this.assetCode = null;
        this.assetSector = AssetClass.CASH.getDescription();
    }

    /**
     * @param holding
     * @param contents
     */
    public AssetAllocationByExposureDto(CashHolding holding, List<AllocationByExposureDto> contents) {
        super(holding.getAccountName(), contents);
    }

    public String getAssetId() {
        return assetId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetSector() {
        return assetSector;
    }
}
