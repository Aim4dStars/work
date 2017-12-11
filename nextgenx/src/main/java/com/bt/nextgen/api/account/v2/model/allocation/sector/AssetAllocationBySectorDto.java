package com.bt.nextgen.api.account.v2.model.allocation.sector;


import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.btfin.panorama.service.integration.asset.AssetType;

import java.util.List;

@Deprecated
public class AssetAllocationBySectorDto extends AggregatedAllocationBySectorDto implements AllocationBySectorDto {

    private String assetId;
    private String assetCode;
    private String assetSector;
    private String assetType;
    private String industrySector;
    private String industrySubSector;
    private String industrySubSectorCode;
    private boolean pending;

    public AssetAllocationBySectorDto(Asset asset, boolean pending, List<AllocationBySectorDto> allocations) {
        super(asset.getAssetName(), allocations);
        this.assetId = asset.getAssetId();
        this.assetCode = asset.getAssetCode();
        this.assetSector = asset.getAssetClass() == null ? AssetClass.CASH.getDescription() : asset.getAssetClass()
                .getDescription();
        if (asset.getIndustrySector() != null) {
            industrySector = asset.getIndustrySector();
        } else {
            industrySector = "Other";
        }

        if (asset.getIndustryType() != null) {
            industrySubSector = asset.getIndustryType();
        } else {
            industrySubSector = "Other";
        }
        industrySubSectorCode = asset.getIndustryTypeCode();
        assetType = asset.getAssetType().name();
        this.pending = pending;
    }

    public AssetAllocationBySectorDto(String name, List<AllocationBySectorDto> allocations) {
        super(name, allocations);
        this.assetId = null;
        this.assetCode = null;
        this.assetSector = AssetClass.CASH.getDescription();
        industrySector = "Other";
        industrySubSector = "Other";
        industrySubSectorCode = null;
        assetType = AssetType.CASH.name();
        pending = false;
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

    public String getIndustrySector() {
        return industrySector;
    }

    public String getIndustrySectorSubSector() {
        return industrySubSector;
    }

    public String getIndustrySectorSubSectorCode() {
        return industrySubSectorCode;
    }

    public String getAssetType() {
        return assetType;
    }

    @Override
    public String getName() {
        String name = super.getName();
        if (name != null && pending) {
            name += " - Pending";
        }
        return name;
    }
}
