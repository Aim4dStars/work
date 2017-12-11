package com.bt.nextgen.api.trading.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;

public class TradeAssetCountDto extends BaseDto {

    private String assetType;
    private int count;

    public TradeAssetCountDto() {
    }

    public TradeAssetCountDto(String assetType, int count) {
        this.assetType = assetType;
        this.count = count;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
