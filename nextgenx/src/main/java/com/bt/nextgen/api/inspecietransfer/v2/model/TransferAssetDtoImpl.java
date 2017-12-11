package com.bt.nextgen.api.inspecietransfer.v2.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * @deprecated Use V3
 */
@Deprecated
public class TransferAssetDtoImpl extends SettlementRecordDtoImpl implements SettlementRecordDto {

    private String transferId;
    private String assetName;
    private BigDecimal amount;
    private String transferStatus;
    private DateTime transferDate;

    public TransferAssetDtoImpl() {
        super();
    }

    public TransferAssetDtoImpl(String assetId, String assetCode, BigDecimal quantity, String transferStatus) {
        super(assetId, assetCode, quantity);
        this.transferStatus = transferStatus;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public DateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(DateTime transferDate) {
        this.transferDate = transferDate;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

}
