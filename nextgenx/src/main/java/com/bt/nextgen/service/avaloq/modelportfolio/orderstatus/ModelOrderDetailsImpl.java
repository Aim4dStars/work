package com.bt.nextgen.service.avaloq.modelportfolio.orderstatus;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.ModelOrderDetails;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.transactionfee.ExecutionType;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.btfin.panorama.service.integration.order.OrderType;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@ServiceBean(xpath = "doc_head", type = ServiceBeanType.CONCRETE)
public class ModelOrderDetailsImpl implements ModelOrderDetails {

    @ServiceElement(xpath = "asset_code/val")
    private String assetCode;

    @ServiceElement(xpath = "asset_id/val")
    private String assetId;

    @ServiceElement(xpath = "asset_id/annot/displ_text")
    private String assetName;

    @ServiceElement(xpath = "ips_id/val")
    private String ipsId;

    @ServiceElement(xpath = "ips_name/val")
    private String ipsName;

    @ServiceElement(xpath = "ips_key/val")
    private String ipsKey;

    @ServiceElement(xpath = "acc_nr/val")
    private String accountNumber;

    @ServiceElement(xpath = "acc_name/val")
    private String accountName;

    @ServiceElement(xpath = "doc_id/val")
    private String docId;

    @ServiceElement(xpath = "order_type_id/val", staticCodeCategory = "ORDER_TYPE")
    private OrderType orderType;

    @ServiceElement(xpath = "exec_type/val", staticCodeCategory = "EXECUTION_TYPE")
    private ExecutionType execType;

    @ServiceElement(xpath = "expir_type/val", staticCodeCategory = "EXPIRY_METHOD")
    private ExpiryMethod expiryType;

    @ServiceElement(xpath = "orig_qty/val")
    private BigDecimal originalQuantity;

    @ServiceElement(xpath = "fill_qty/val")
    private BigDecimal fillQuantity;

    @ServiceElement(xpath = "remn_qty/val")
    private BigDecimal remainingQuantity;

    @ServiceElement(xpath = "status_id/val", staticCodeCategory = "ORDER_STATUS")
    private OrderStatus status;

    @ServiceElement(xpath = "order_date/val", converter = DateTimeTypeConverter.class)
    private DateTime orderDate;

    @ServiceElement(xpath = "trx_date/val", converter = DateTimeTypeConverter.class)
    private DateTime transactionDate;

    @ServiceElement(xpath = "expir_date/val", converter = DateTimeTypeConverter.class)
    private DateTime expiryDate;

    @ServiceElement(xpath = "net_amt/val")
    private BigDecimal netAmount;

    @ServiceElement(xpath = "price_estim/val")
    private BigDecimal estimatedPrice;

    @ServiceElement(xpath = "brokerage/val")
    private BigDecimal brokerage;

    @ServiceElement(xpath = "adviser/val")
    private String adviserName;

    @ServiceElement(xpath = "dealer_grp/val")
    private String dealerName;

    @Override
    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    @Override
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @Override
    public String getIpsId() {
        return ipsId;
    }

    public void setIpsId(String ipsId) {
        this.ipsId = ipsId;
    }

    public String getIpsName() {
        return ipsName;
    }

    public void setIpsName(String ipsName) {
        this.ipsName = ipsName;
    }

    @Override
    public String getIpsKey() {
        return ipsKey;
    }

    public void setIpsKey(String ipsKey) {
        this.ipsKey = ipsKey;
    }

    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    @Override
    public ExecutionType getExecType() {
        return execType;
    }

    public void setExecType(ExecutionType execType) {
        this.execType = execType;
    }

    @Override
    public ExpiryMethod getExpiryType() {
        return expiryType;
    }

    public void setExpiryType(ExpiryMethod expirType) {
        this.expiryType = expirType;
    }

    @Override
    public BigDecimal getOriginalQuantity() {
        return originalQuantity;
    }

    public void setOriginalQuantity(BigDecimal originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

    @Override
    public BigDecimal getFillQuantity() {
        return fillQuantity;
    }

    public void setFillQuantity(BigDecimal fillQuantity) {
        this.fillQuantity = fillQuantity;
    }

    @Override
    public BigDecimal getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(BigDecimal remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    @Override
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public DateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(DateTime orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public DateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(DateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public DateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(DateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    @Override
    public BigDecimal getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(BigDecimal estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    @Override
    public BigDecimal getBrokerage() {
        return brokerage;
    }

    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }

    @Override
    public String getAdviserName() {
        return adviserName;
    }

    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
    }

    @Override
    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

}