package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.util.rebalance.ModelAccountTypeHelper;
import com.bt.nextgen.core.api.model.BaseDto;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderDetails;

import java.math.BigDecimal;

public class RebalanceOrderDetailsDto extends BaseDto {

    private String accountName;
    private String accountNumber;
    private String accountType;
    private String assetName;
    private String assetCode;
    private String assetClass;
    private String assetType;
    private String preference;
    private BigDecimal modelWeight;
    private BigDecimal targetWeight;
    private BigDecimal currentWeight;
    private BigDecimal diffWeight;
    private BigDecimal targetValue;
    private BigDecimal currentValue;
    private BigDecimal diffValue;
    private BigDecimal targetQuantity;
    private BigDecimal currentQuantity;
    private BigDecimal diffQuantity;
    private String orderType;
    private BigDecimal orderAmount;
    private BigDecimal estimatedPrice;
    private BigDecimal orderQuantity;
    private BigDecimal finalWeight;
    private BigDecimal finalValue;
    private BigDecimal finalQuantity;
    private String comments;
    private Boolean isSellAll;
    private Boolean isFullModelRedemption;
    private Boolean isTMPCashAsset;

    public RebalanceOrderDetailsDto(WrapAccount account, Asset asset, BigDecimal estimatedPrice, String comments,
            RebalanceOrderDetails orderDetails, Boolean isFullModelRedemption, Boolean isTMPCashAsset) {
        super();
        setAccountAttributes(account);
        setAssetAttributes(asset);
        this.preference = orderDetails.getPreference();
        this.modelWeight = orderDetails.getModelWeight();
        this.targetWeight = orderDetails.getTargetWeight();
        this.currentWeight = orderDetails.getCurrentWeight();
        this.diffWeight = orderDetails.getDiffWeight() != null ? orderDetails.getDiffWeight().negate() : null;
        this.targetValue = orderDetails.getTargetValue();
        this.currentValue = orderDetails.getCurrentValue();
        this.diffValue = orderDetails.getDiffValue() != null ? orderDetails.getDiffValue().negate() : null;
        this.targetQuantity = orderDetails.getTargetQuantity();
        this.currentQuantity = orderDetails.getCurrentQuantity();
        this.diffQuantity = orderDetails.getDiffQuantity() != null ? orderDetails.getDiffQuantity().negate() : null;
        this.isSellAll = orderDetails.getIsSellAll() != null && orderDetails.getIsSellAll();
        if (isSellAll) {
            this.orderType = "Sell";
            this.orderQuantity = orderDetails.getCurrentQuantity();
        } else {
            this.orderType = orderDetails.getOrderType();
            this.orderQuantity = "Sell".equals(orderType) && orderDetails.getOrderValue() != null ? orderDetails
                    .getOrderQuantity().negate() : orderDetails.getOrderQuantity();
        }
        this.orderAmount = "Sell".equals(orderType) && orderDetails.getOrderValue() != null ? orderDetails
                .getOrderValue().negate() : orderDetails.getOrderValue();
        this.estimatedPrice = estimatedPrice;
        this.finalWeight = orderDetails.getFinalWeight();
        this.finalValue = orderDetails.getFinalValue();
        this.finalQuantity = orderDetails.getFinalQuantity();
        this.comments = comments;
        this.isFullModelRedemption = isFullModelRedemption;
        this.isTMPCashAsset = isTMPCashAsset;
    }

    private void setAccountAttributes(WrapAccount account) {
        if (account != null) {
            this.accountName = account.getAccountName();
            this.accountNumber = account.getAccountNumber();
            this.accountType = ModelAccountTypeHelper.getAccountTypeDescription(account);
        }
    }

    private void setAssetAttributes(Asset asset) {
        if (asset != null) {
            this.assetName = asset.getAssetName();
            this.assetCode = asset.getAssetCode();
            this.assetClass = asset.getAssetClass() != null ? asset.getAssetClass().getDescription() : null;
            this.assetType = asset.getAssetType().getDisplayName();
        }
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getPreference() {
        return preference;
    }

    public BigDecimal getModelWeight() {
        return modelWeight;
    }

    public BigDecimal getTargetWeight() {
        return targetWeight;
    }

    public BigDecimal getCurrentWeight() {
        if (isFullModelRedemption) {
            return null;
        }
        return currentWeight;
    }

    public BigDecimal getDiffWeight() {
        if (isFullModelRedemption) {
            return null;
        }
        return diffWeight;
    }

    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public BigDecimal getDiffValue() {
        return diffValue;
    }

    public BigDecimal getTargetQuantity() {
        return targetQuantity;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public BigDecimal getDiffQuantity() {
        return diffQuantity;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }

    public BigDecimal getOrderQuantity() {
        return orderQuantity;
    }

    public BigDecimal getFinalWeight() {
        return finalWeight;
    }

    public BigDecimal getFinalValue() {
        return finalValue;
    }

    public BigDecimal getFinalQuantity() {
        return finalQuantity;
    }

    public String getComments() {
        return comments;
    }

    public Boolean getIsSellAll() {
        return isSellAll;
    }

    public Boolean getIsFullModelRedemption() {
        return isFullModelRedemption;
    }

    public Boolean isHideOrder() {
        return isFullModelRedemption && isTMPCashAsset;
    }
}
