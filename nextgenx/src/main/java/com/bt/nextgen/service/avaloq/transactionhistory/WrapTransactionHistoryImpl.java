package com.bt.nextgen.service.avaloq.transactionhistory;

import com.btfin.panorama.service.integration.asset.AssetType;

import java.math.BigDecimal;

/**
 *  Domain class for Wrap transactions
 */
public class WrapTransactionHistoryImpl extends TransactionHistoryImpl{

    private AssetType assetType;
    private String assetCode;
    private String assetName;
    private BigDecimal quantity;

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
