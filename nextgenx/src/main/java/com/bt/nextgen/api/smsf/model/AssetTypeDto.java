package com.bt.nextgen.api.smsf.model;

/**
 * Asset Type dto
 */
public class AssetTypeDto
{
    private String assetName;

    private String assetCode;

    private int order;


    // Default constructor
    public AssetTypeDto()
    {
    }

    public AssetTypeDto(String assetName, String assetCode, int order)
    {
        this.assetName = assetName;
        this.assetCode = assetCode;
        this.order = order;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}