package com.bt.nextgen.api.transactionhistory.model;

import java.math.BigDecimal;
import java.util.Map;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;

public class TransactionHistoryDto extends BaseDto {
    private String orderId;
    private DateTime tradeDate;
    private DateTime settlementDate;
    private String investmentCode;
    private String investmentName;
    private String contType;
    private String assetCode;
    private String assetName;
    private String description;
    private String transactionType;
    private String status;
    private String assetType;
    private BigDecimal quantity;
    private BigDecimal netAmount;
    private Boolean isLinked;
    private Boolean isLink;
    private String origin;

    /**
     * Instantiates a new transaction history dto.
     */
    public TransactionHistoryDto() {
        // Empty constructor
    }

    public TransactionHistoryDto(TransactionHistory transaction, Map<String, String> assetDetails, String description,
            Map<String, Object> amountDetails) {
        this.orderId = transaction.getDocId();
        this.tradeDate = transaction.getEffectiveDate();
        this.settlementDate = transaction.getValDate();
        this.investmentCode = assetDetails.get("investmentCode");
        this.investmentName = assetDetails.get("investmentName");
        this.contType = assetDetails.get("investmentType");
        this.assetCode = assetDetails.get("assetCode");
        this.assetName = assetDetails.get("assetName");
        this.description = description;
        this.transactionType = (String) amountDetails.get("transactionType");
        this.status = transaction.getStatus();
        this.assetType = transaction.getAsset() == null ? null : transaction.getAsset().getAssetType().name();
        this.quantity = (BigDecimal) amountDetails.get("quantity");
        this.netAmount = (BigDecimal) amountDetails.get("netAmount");
        this.origin = transaction.getOrigin() == null ? null : transaction.getOrigin().getName();
    }

    public String getOrderId() {
        return orderId;
    }

    public DateTime getTradeDate() {
        return tradeDate;
    }

    public DateTime getSettlementDate() {
        return settlementDate;
    }

    public String getInvestmentCode() {
        return investmentCode;
    }

    public String getInvestmentName() {
        return investmentName;
    }

    public String getContType() {
        return contType;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public String getStatus() {
        return status;
    }

    public String getAssetType() {
        return assetType;
    }

    public Boolean getIsLinked() {
        return isLinked;
    }

    public void setIsLinked(Boolean isLinked) {
        this.isLinked = isLinked;
    }

    public Boolean getIsLink() {
        return isLink;
    }

    public void setIsLink(Boolean isLink) {
        this.isLink = isLink;
    }

    public String getOrigin() {
        return origin;
    }
}
