package com.bt.nextgen.service.integration.externalasset.model;

import com.bt.nextgen.service.integration.asset.AssetIdentifier;
import com.bt.nextgen.service.integration.asset.AssetKey;


public interface OnPlatformExternalAsset extends AssetIdentifier
{
    public AssetKey getAssetKey();

    public void setAssetKey(AssetKey assetKey);

    public String getAssetCode();

    public void setAssetCode(String assetCode);
}