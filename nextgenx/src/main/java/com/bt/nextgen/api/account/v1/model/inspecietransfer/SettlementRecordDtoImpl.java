package com.bt.nextgen.api.account.v1.model.inspecietransfer;

import java.math.BigDecimal;

import org.joda.time.DateTime;

/**
 * @deprecated Use V2
 */
@Deprecated
public class SettlementRecordDtoImpl implements SettlementRecordDto {
    private String assetId;
    private String assetCode;
    private BigDecimal quantity;

    public SettlementRecordDtoImpl() {
        // Default constructor - being referred in TransferAssetDtoImpl
    }

    public SettlementRecordDtoImpl(String assetId, String assetCode, BigDecimal quantity) {
        this.assetId = assetId;
        this.assetCode = assetCode;
        this.quantity = quantity;
    }

    public SettlementRecordDtoImpl(String assetId, BigDecimal quantity) {
        this.assetId = assetId;
        this.quantity = quantity;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getTransferId() {
        return null;
    }

    @Override
    public BigDecimal getAmount() {
        return null;
    }

    @Override
    public String getTransferStatus() {
        return null;
    }

    @Override
    public DateTime getTransferDate() {
        return null;
    }

}
