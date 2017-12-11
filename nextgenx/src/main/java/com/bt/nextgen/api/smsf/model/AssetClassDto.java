package com.bt.nextgen.api.smsf.model;


import com.bt.nextgen.api.account.v1.model.AccountKey;

import com.bt.nextgen.core.api.model.KeyedDto;

public class AssetClassDto implements KeyedDto<String>
{
    private String assetName;

    private String assetCode;


    // Default constructor
    public AssetClassDto()
    {
    }

    public AssetClassDto(String assetName, String assetCode)
    {
        this.assetName = assetName;
        this.assetCode = assetCode;
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

    @Override
    public String getKey()
    {
        return assetCode;
    }

    public String getType()
    {
        return null;
    }
}
