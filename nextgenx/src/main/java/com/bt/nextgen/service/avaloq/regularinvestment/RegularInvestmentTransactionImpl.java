package com.bt.nextgen.service.avaloq.regularinvestment;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.regularinvestment.RIPRecurringFrequency;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentTransaction;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

@ServiceBean(xpath = "doc", type = ServiceBeanType.CONCRETE)
public class RegularInvestmentTransactionImpl implements RegularInvestmentTransaction {
    private static final String XML_HEADER = "doc_head_list/doc_head/";

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "doc_id/val")
    private String orderGroupId;

    @ServiceElement(xpath = XML_HEADER + "timestamp/val", converter = DateTimeTypeConverter.class)
    private DateTime lastUpdateDate;

    @ServiceElement(xpath = XML_HEADER + "ins_by_sec_user/val")
    private String ownerName;

    @ServiceElement(xpath = XML_HEADER + "order_type/val", staticCodeCategory = "ORDER_TYPE")
    private String orderType;

    @ServiceElement(xpath = XML_HEADER + "order_date/val", converter = DateTimeTypeConverter.class)
    private DateTime transactionDate;

    @ServiceElement(xpath = XML_HEADER + "bp/val")
    private String accountKey;

    @ServiceElement(xpath = XML_HEADER + "description/val")
    private String description;

    @ServiceElement(xpath = XML_HEADER + "owner/val")
    private String owner;

    @ServiceElement(xpath = XML_HEADER + "period/val", staticCodeCategory = "TRX_BDL_PERIOD")
    private RIPRecurringFrequency ripFrequency;

    @ServiceElement(xpath = XML_HEADER + "tot_qty/val")
    private BigDecimal ripAmount;

    @ServiceElement(xpath = XML_HEADER + "first_exec/val", converter = DateTimeTypeConverter.class)
    private DateTime ripFirstExecDate;

    @ServiceElement(xpath = XML_HEADER + "this_exec/val", converter = DateTimeTypeConverter.class)
    private DateTime ripCurrExecDate;

    @ServiceElement(xpath = XML_HEADER + "next_exec_val/val", converter = DateTimeTypeConverter.class)
    private DateTime ripNextExecDate;

    @ServiceElement(xpath = XML_HEADER + "last_exec/val", converter = DateTimeTypeConverter.class)
    private DateTime ripLastExecDate;

    @ServiceElement(xpath = XML_HEADER + "ui_wf_status/val", staticCodeCategory = "ORDER_STATUS")
    private RIPStatus ripStatus;

    @ServiceElement(xpath = XML_HEADER + "ref_doc_id/val")
    private String refDocId;

    @ServiceElement(xpath = XML_HEADER + "ref_doc_start_date/val", converter = DateTimeTypeConverter.class)
    private DateTime ddFirstExecDate;

    @ServiceElement(xpath = XML_HEADER + "ref_doc_next_date/val", converter = DateTimeTypeConverter.class)
    private DateTime ddNextExecDate;

    @ServiceElement(xpath = XML_HEADER + "ref_doc_last_date/val", converter = DateTimeTypeConverter.class)
    private DateTime ddLastExecDate;

    @ServiceElement(xpath = XML_HEADER + "ref_doc_period_id/val", staticCodeCategory = "DD_PERIOD")
    private RecurringFrequency ddFrequency;

    @ServiceElement(xpath = XML_HEADER + "ref_doc_amount/val")
    private BigDecimal ddAmount;

    @ServiceElement(xpath = XML_HEADER + "ref_doc_acc_nr/val")
    private String payerAccountId;

    @ServiceElement(xpath = XML_HEADER + "ref_doc_acc_name/val")
    private String payerAccountName;

    @ServiceElement(xpath = XML_HEADER + "ref_doc_acc_bsb/val")
    private String payerBSB;

    @ServiceElement(xpath = XML_HEADER + "bp_nr/val")
    private String ripCashAccountId;

    @ServiceElement(xpath = XML_HEADER + "this_exec_status_id/val", staticCodeCategory = "CURR_EXEC_STATUS")
    private RIPStatus currExecStatus;

    @Override
    public String getOrderGroupId() {

        return orderGroupId;
    }

    @Override
    public DateTime getLastUpdateDate() {

        return lastUpdateDate;
    }

    @Override
    public String getOwnerName() {

        return ownerName;
    }

    @Override
    public String getOrderType() {

        return orderType;
    }

    @Override
    public DateTime getTransactionDate() {

        return transactionDate;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public RIPRecurringFrequency getRipFrequency() {
        return ripFrequency;
    }

    @Override
    public BigDecimal getRipAmount() {

        return ripAmount;
    }

    @Override
    public DateTime getRipFirstExecDate() {

        return ripFirstExecDate;
    }

    @Override
    public DateTime getRipCurrExecDate() {

        return ripCurrExecDate;
    }

    @Override
    public DateTime getRipNextExecDate() {

        return ripNextExecDate;
    }

    @Override
    public DateTime getRipLastExecDate() {

        return ripLastExecDate;
    }

    @Override
    public RIPStatus getRipStatus() {

        return ripStatus;
    }

    @Override
    public String getRefDocId() {

        return refDocId;
    }

    @Override
    public DateTime getDDFirstExecDate() {

        return ddFirstExecDate;
    }

    @Override
    public DateTime getDDNextExecDate() {

        return ddNextExecDate;
    }

    @Override
    public DateTime getDDLastExecDate() {

        return ddLastExecDate;
    }

    @Override
    public RecurringFrequency getDDFrequency() {

        return ddFrequency;
    }

    @Override
    public BigDecimal getDDAmount() {

        return ddAmount;
    }

    @Override
    public String getPayerAccountId() {

        return payerAccountId;
    }

    @Override
    public String getPayerAccountName() {

        return payerAccountName;
    }

    public void setOrderGroupId(String orderGroupId) {
        this.orderGroupId = orderGroupId;
    }

    public void setLastUpdateDate(DateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setTransactionDate(DateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRipFrequency(RIPRecurringFrequency ripFrequency) {
        this.ripFrequency = ripFrequency;
    }

    public void setRipAmount(BigDecimal ripAmount) {
        this.ripAmount = ripAmount;
    }

    public void setRipFirstExecDate(DateTime ripFirstExecDate) {
        this.ripFirstExecDate = ripFirstExecDate;
    }

    public void setRipCurrExecDate(DateTime ripCurrExecDate) {
        this.ripCurrExecDate = ripCurrExecDate;
    }

    public void setRipNextExecDate(DateTime ripNextExecDate) {
        this.ripNextExecDate = ripNextExecDate;
    }

    public void setRipLastExecDate(DateTime ripLastExecDate) {
        this.ripLastExecDate = ripLastExecDate;
    }

    public void setRipStatus(RIPStatus ripStatus) {
        this.ripStatus = ripStatus;
    }

    public void setRefDocId(String refDocId) {
        this.refDocId = refDocId;
    }

    public void setDdFirstExecDate(DateTime ddFirstExecDate) {
        this.ddFirstExecDate = ddFirstExecDate;
    }

    public void setDdNextExecDate(DateTime ddNextExecDate) {
        this.ddNextExecDate = ddNextExecDate;
    }

    public void setDdLastExecDate(DateTime ddLastExecDate) {
        this.ddLastExecDate = ddLastExecDate;
    }

    public void setDdFrequency(RecurringFrequency ddFrequency) {
        this.ddFrequency = ddFrequency;
    }

    public void setDdAmount(BigDecimal ddAmount) {
        this.ddAmount = ddAmount;
    }

    public void setPayerAccountId(String payerAccountId) {
        this.payerAccountId = payerAccountId;
    }

    public void setPayerAccountName(String payerAccountName) {
        this.payerAccountName = payerAccountName;
    }

    @Override
    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String getPayerBSB() {
        return payerBSB;
    }

    public void setPayerBSB(String payerBSB) {
        this.payerBSB = payerBSB;
    }

    @Override
    public String getRipCashAccountId() {
        return ripCashAccountId;
    }

    public void setRipCashAccountId(String ripCashAccountId) {
        this.ripCashAccountId = ripCashAccountId;
    }

    @Override
    public RIPStatus getCurrExecStatus() {
        return currExecStatus;
    }

    public void setCurrExecStatus(RIPStatus currExecStatus) {
        this.currExecStatus = currExecStatus;
    }

}
