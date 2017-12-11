package com.bt.nextgen.api.inspecietransfer.v3.model;

import com.bt.nextgen.service.integration.asset.Asset;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class TransferItemDto {

    private String assetCode;
    private String assetName;
    private BigDecimal quantity;
    private String transferStatus;
    private DateTime transferDate;

    public TransferItemDto() {
        // Default Constructor
    }

    public TransferItemDto(Asset asset, BigDecimal quantity, String transferStatus, DateTime transferDate) {
        if (asset != null) {
            this.assetCode = asset.getAssetCode();
            this.assetName = asset.getAssetName();
        }
        this.quantity = quantity;
        this.transferStatus = transferStatus;
        this.transferDate = transferDate;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public DateTime getTransferDate() {
        return transferDate;
    }

}
