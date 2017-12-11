package com.bt.nextgen.api.account.v2.model.allocation;

@Deprecated
public interface AssetAllocationDto extends AllocationDto {

    public String getAssetId();

    public String getAssetCode();

    public String getAssetSector();
}
