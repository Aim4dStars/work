package com.bt.nextgen.api.smsf.model;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * AssetDto to hold assets for SMSF
 *
 */
public class AssetDto extends BaseDto  {

    private String assetId;

    private String assetName;

    private String assetCode;

    private String assetClass;

    private String assetClassId;

    private String assetType;

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    public String getAssetClassId() {
        return assetClassId;
    }

    public void setAssetClassId(String assetClassId) {
        this.assetClassId = assetClassId;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }
}

