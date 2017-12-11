package com.bt.nextgen.api.order.model;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class OrderItemDto extends BaseDto {
    @JsonView(JsonViews.Write.class)
    private String orderId;

    @JsonView(JsonViews.Write.class)
    private String orderType;

    @JsonView(JsonViews.Write.class)
    private BigDecimal amount;

    @JsonView(JsonViews.Write.class)
    private AssetDto asset;

    @JsonView(JsonViews.Write.class)
    private Boolean sellAll;

    @JsonView(JsonViews.Write.class)
    private String assetType;

    @JsonView(JsonViews.Write.class)
    private String distributionMethod;

    @JsonView(JsonViews.Write.class)
    private BigDecimal adminFeeRate;

    @JsonView(JsonViews.Write.class)
    private BigDecimal estimated;

    @JsonView(JsonViews.Write.class)
    private BigInteger units;

    @JsonView(JsonViews.Write.class)
    private BigDecimal price;

    @JsonView(JsonViews.Write.class)
    private String expiry;

    @JsonView(JsonViews.Write.class)
    private String priceType;

    @JsonView(JsonViews.Write.class)
    private List<FundsAllocationDto> fundsAllocation;

    @JsonView(JsonViews.Write.class)
    private List<PreferenceActionDto> preferences;

    @JsonView(JsonViews.Write.class)
    private List<OrderFeeDto> fees;

    @JsonView(JsonViews.Write.class)
    private String bankClearNumber;

    @JsonView(JsonViews.Write.class)
    private String payerAccount;

    @JsonView(JsonViews.Write.class)
    private BigDecimal intRate;

    @JsonView(JsonViews.Write.class)
    private String subAccountId;

    @JsonView(JsonViews.Write.class)
    private String incomePreference;

    private BigDecimal adviserFee;
    private String firstNotification;

    public OrderItemDto() {
        super();
    }

    public OrderItemDto(String orderId, AssetDto asset, String assetType, String orderType, OrderItemSummaryDto summary,
            List<FundsAllocationDto> fundsAllocation) {
        super();
        this.orderId = orderId;
        this.asset = asset;
        this.orderType = orderType;
        this.amount = summary.getAmount();
        this.sellAll = summary.getIsFull();
        this.assetType = assetType;
        this.distributionMethod = summary.getDistributionMethod();
        this.units = summary.getUnits();
        this.price = summary.getPrice();
        this.expiry = summary.getExpiry();
        this.priceType = summary.getPriceType() != null ? summary.getPriceType().getIntlId() : "";
        this.fundsAllocation = Collections.unmodifiableList(fundsAllocation);
    }

    public OrderItemDto(String orderId, AssetDto asset, String assetType, String orderType, OrderItemSummaryDto summary,
            List<FundsAllocationDto> fundsAllocation, String firstNotification) {
        super();
        this.orderId = orderId;
        this.asset = asset;
        this.orderType = orderType;
        this.amount = summary.getAmount();
        this.sellAll = summary.getIsFull();
        this.assetType = assetType;
        this.distributionMethod = summary.getDistributionMethod();
        this.units = summary.getUnits();
        this.price = summary.getPrice();
        this.expiry = summary.getExpiry();
        this.priceType = summary.getPriceType() != null ? summary.getPriceType().getIntlId() : "";
        this.fundsAllocation = Collections.unmodifiableList(fundsAllocation);
        this.firstNotification = firstNotification;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getOrderType() {
        return orderType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public AssetDto getAsset() {
        return asset;
    }

    public Boolean getSellAll() {
        return sellAll;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDistributionMethod() {
        return distributionMethod;
    }

    public List<FundsAllocationDto> getFundsAllocation() {
        return fundsAllocation;
    }

    public BigDecimal getAdminFeeRate() {
        return adminFeeRate;
    }

    public BigDecimal getEstimated() {
        return this.estimated;
    }

    public BigInteger getUnits() {
        return units;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getPriceType() {
        return priceType;
    }

    public BigDecimal getAdviserFee() {
        return adviserFee;
    }

    public String getFirstNotification() {
        return firstNotification;
    }

    public BigDecimal getIntRate() {
        return intRate;
    }

    public String getSubAccountId() {
        return subAccountId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAsset(AssetDto asset) {
        this.asset = asset;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public List<PreferenceActionDto> getPreferences() {
        if (preferences == null) {
            return Collections.emptyList();
        }
        return preferences;
    }

    public void setPreferences(List<PreferenceActionDto> preferences) {
        this.preferences = Collections.unmodifiableList(preferences);
    }

    public String getBankClearNumber() {
        return bankClearNumber;
    }

    public String getPayerAccount() {
        return payerAccount;
    }

    public void setBankClearNumber(String bankClearNumber) {
        this.bankClearNumber = bankClearNumber;
    }

    public void setPayerAccount(String payerAccount) {
        this.payerAccount = payerAccount;
    }

    public List<OrderFeeDto> getFees() {
        if (fees == null)
            return Collections.emptyList();
        return fees;
    }

    public void setFees(List<OrderFeeDto> fees) {
        this.fees = fees;
    }

    public String getIncomePreference() {
        return incomePreference;
    }

    public void setIncomePreference(String incomePreference) {
        this.incomePreference = incomePreference;
    }

}
