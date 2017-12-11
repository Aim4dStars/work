package com.bt.nextgen.api.portfolio.v3.model.allocation;


public interface AssetAllocationDto extends AllocationDto {

    public String getAssetId();

    public String getAssetCode();

    public String getAssetSector();
}
