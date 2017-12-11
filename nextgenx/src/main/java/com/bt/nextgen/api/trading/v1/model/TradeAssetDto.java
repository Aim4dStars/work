package com.bt.nextgen.api.trading.v1.model;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;

public class TradeAssetDto extends BaseDto implements KeyedDto<String> {
    private final AssetDto asset;
    private final boolean buyable;
    private final boolean sellable;
    private final BigDecimal balance;
    private final BigDecimal availableBalance;
    private final String assetTypeDescription;
    private final BigDecimal availableQuantity;

    public TradeAssetDto(AssetDto assetDto, boolean buyable, boolean sellable, BigDecimal balance, BigDecimal availableBalance,
            BigDecimal availableQuantity, String assetTypeDescription) {
        this.asset = assetDto;
        this.buyable = buyable;
        this.sellable = sellable;
        this.balance = balance;
        this.availableBalance = availableBalance;
        this.availableQuantity = availableQuantity;
        this.assetTypeDescription = assetTypeDescription;
    }

    /**
     * @return the assetDto
     */
    public AssetDto getAsset() {
        return asset;
    }

    /**
     * @return the buyable
     */
    public boolean getBuyable() {
        return buyable;
    }

    /**
     * @return the sellable
     */
    public boolean getSellable() {
        return sellable;
    }

    @Override
    public String getKey() {
        if (null != asset) {
            return asset.getAssetId();
        }
        return null;
    }

    /**
     * @return the balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * @return the assetTypeDescription
     */
    public String getAssetTypeDescription() {
        return assetTypeDescription;
    }

    /**
     * @return the availableQuantity
     */
    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * @return the availablebalance
     */
    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }
}
