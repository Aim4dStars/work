package com.bt.nextgen.api.modelportfolio.v2.model.orderstatus;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.ModelOrderDetails;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class ModelOrderDetailsDto extends BaseDto {

    private String assetCode;
    private String assetName;
    private String ipsKey;
    private String accountNumber;
    private String accountName;
    private String docId;
    private String orderType;
    private String execType;
    private String expiryType;
    private BigDecimal originalQuantity;
    private BigDecimal fillQuantity;
    private BigDecimal remainingQuantity;
    private String status;
    private DateTime orderDate;
    private DateTime transactionDate;
    private DateTime expiryDate;
    private BigDecimal netAmount;
    private BigDecimal estimatedPrice;
    private BigDecimal brokerage;
    private String adviserName;
    private String dealerName;
    private String modelName;
    private String modelId;

    public ModelOrderDetailsDto(ModelOrderDetails model) {
        super();
        this.assetCode = model.getAssetCode();
        this.ipsKey = model.getIpsKey();
        this.accountNumber = model.getAccountNumber();
        this.accountName = model.getAccountName();
        this.docId = model.getDocId();
        this.orderType = model.getOrderType().getDisplayName();
        this.execType = model.getExecType() != null ? model.getExecType().getIntlId().toUpperCase() : "";
        this.expiryType = model.getExpiryType() != null ? model.getExpiryType().name() : "";
        this.originalQuantity = model.getOriginalQuantity();
        this.fillQuantity = model.getFillQuantity();
        this.status = model.getStatus() == null ? "" : model.getStatus().getDisplayName();
        this.orderDate = model.getOrderDate();
        this.transactionDate = model.getTransactionDate();
        this.expiryDate = model.getExpiryDate();
        this.netAmount = model.getNetAmount();
        this.estimatedPrice = model.getEstimatedPrice();
        this.brokerage = model.getBrokerage();
        this.remainingQuantity = model.getRemainingQuantity();
        this.adviserName = model.getAdviserName();
        this.dealerName = model.getDealerName();
        this.modelName = model.getIpsName();
        this.modelId = model.getIpsId();
    }

    public ModelOrderDetailsDto(ModelOrderDetails model, Asset asset) {
        this(model);
        if (asset != null) {            
            this.assetName = asset.getAssetName();
        }
    }

    public String getIpsKey() {
        return ipsKey;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getDocId() {
        return docId;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getExecType() {
        return execType;
    }

    public String getExpiryType() {
        return expiryType;
    }

    public BigDecimal getOriginalQuantity() {
        return originalQuantity;
    }

    public BigDecimal getFillQuantity() {
        return fillQuantity;
    }

    public String getStatus() {
        return status;
    }

    public DateTime getOrderDate() {
        return orderDate;
    }

    public DateTime getTransactionDate() {
        return transactionDate;
    }

    public DateTime getExpiryDate() {
        return expiryDate;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }

    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public String getDealerName() {
        return dealerName;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelId() {
        return modelId;
    }
}
