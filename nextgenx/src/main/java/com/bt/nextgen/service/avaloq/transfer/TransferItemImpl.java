package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.transfer.TransferItem;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@ServiceBean(xpath = "xfer")
public class TransferItemImpl implements TransferItem {

    @ServiceElement(xpath = "settle_doc_id/val")
    private String settlementId;

    @ServiceElement(xpath = "settle_ui_wfs_id/val", staticCodeCategory = "ORDER_STATUS")
    private OrderStatus transferStatus;

    @ServiceElement(xpath = "settle_asset_id/val")
    private String assetId;

    @ServiceElement(xpath = "settle_qty/val")
    private BigDecimal quantity;

    @ServiceElement(xpath = "settle_amount/val")
    private BigDecimal amount;

    @ServiceElement(xpath = "settle_last_trx_timestp/val", converter = DateTimeTypeConverter.class)
    private DateTime transactionDateTime;

    public TransferItemImpl() {
    }

    public String getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(String settlementId) {
        this.settlementId = settlementId;
    }

    public OrderStatus getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(OrderStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public DateTime getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(DateTime transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }
}
