package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.order.PriceType;
import org.apache.commons.lang3.tuple.Pair;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OrderItemImpl implements OrderItem {
    @NotNull
    private String orderId;

    @NotNull
    private String orderType;

    @NotNull
    private BigDecimal amount;

    private AssetType assetType;

    @NotNull
    private Boolean isFull;

    @NotNull
    private String assetId;

    private SubAccountKey subAccountKey;

    private List<Pair<String, BigDecimal>> fundsSource;

    private String distributionMethod;

    private BigInteger units;

    private BigDecimal price;

    private String expiry;

    private PriceType priceType;

    private String firstNotification;

    private String bankClearNumber;

    private String payerAccount;

    private List<ModelPreferenceAction> preferences;

    private Map<FeesType, List<FeesComponents>> fees;

    private IncomePreference incomePreference;

    public OrderItemImpl() {
        super();
    }

    public OrderItemImpl(String orderId, String orderType, AssetType assetType, String assetId, OrderItemSummaryImpl summary,
            List<Pair<String, BigDecimal>> fundsSource) {
        super();
        this.orderId = orderId;
        this.orderType = orderType;
        this.assetType = assetType;
        this.amount = summary.getAmount();
        this.assetId = assetId;
        this.isFull = summary.getIsFull();
        this.distributionMethod = summary.getDistributionMethod();
        this.fundsSource = Collections.unmodifiableList(fundsSource);
        this.units = summary.getUnits();
        this.price = summary.getPrice();
        this.expiry = summary.getExpiry();
        this.priceType = summary.getPriceType();
    }

    public OrderItemImpl(SubAccountKey subAccountKey, String orderId, String orderType, AssetType assetType, String assetId,
            OrderItemSummaryImpl summary, List<Pair<String, BigDecimal>> fundsSource) {
        super();
        this.subAccountKey = subAccountKey;
        this.orderId = orderId;
        this.orderType = orderType;
        this.assetType = assetType;
        this.amount = summary.getAmount();
        this.assetId = assetId;
        this.isFull = summary.getIsFull();
        this.distributionMethod = summary.getDistributionMethod();
        this.fundsSource = Collections.unmodifiableList(fundsSource);
        this.units = summary.getUnits();
        this.price = summary.getPrice();
        this.expiry = summary.getExpiry();
        this.priceType = summary.getPriceType();
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getOrderType() {
        return orderType;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public AssetType getAssetType() {
        return assetType;
    }

    @Override
    public List<Pair<String, BigDecimal>> getFundsSource() {
        return fundsSource;
    }

    @Override
    public SubAccountKey getSubAccountKey() {
        return subAccountKey;
    }

    @Override
    public String getAssetId() {
        return assetId;
    }

    @Override
    public Boolean getIsFull() {
        return isFull;
    }

    @Override
    public String getDistributionMethod() {
        return distributionMethod;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setSubAccountKey(SubAccountKey subAccountKey) {
        this.subAccountKey = subAccountKey;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public void setFundsSource(List<Pair<String, BigDecimal>> fundsSource) {
        this.fundsSource = fundsSource;
    }

    @Override
    public BigInteger getUnits() {
        return units;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String getExpiry() {
        return expiry;
    }

    @Override
    public PriceType getPriceType() {
        return priceType;
    }

    @Override
    public String getFirstNotification() {
        return firstNotification;
    }

    public void setFirstNotification(String firstNotification) {
        this.firstNotification = firstNotification;
    }

    public List<ModelPreferenceAction> getPreferences() {
        if (preferences == null)
            return Collections.emptyList();
        return preferences;
    }

    public void setPreferences(List<ModelPreferenceAction> preferences) {
        this.preferences = preferences;
    }

    public Map<FeesType, List<FeesComponents>> getFees() {
        if (fees == null)
            return Collections.emptyMap();
        return fees;
    }

    public void setFees(Map<FeesType, List<FeesComponents>> fees) {
        this.fees = fees;
    }

    public String getBankClearNumber() {
        return bankClearNumber;
    }

    public void setBankClearNumber(String bankClearNumber) {
        this.bankClearNumber = bankClearNumber;
    }

    @Override
    public String getPayerAccount() {
        return payerAccount;
    }

    public void setPayerAccount(String payerAccount) {
        this.payerAccount = payerAccount;
    }

    @Override
    public IncomePreference getIncomePreference() {
        return incomePreference;
    }

    public void setIncomePreference(IncomePreference incomePreference) {
        this.incomePreference = incomePreference;
    }
}
