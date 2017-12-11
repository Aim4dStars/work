package com.bt.nextgen.api.policy.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class PolicySummaryDto extends PolicyTrackingDto {

    private String encodedAccountId;
    private String accountName;
    private String accountNumber;
    private String accountType;
    private String accountSubType;
    private String pensionType;
    private String policyNumber;
    private String policyStatus;
    private String policyType;
    private BigDecimal premium;
    private String paymentFrequency;
    private BigDecimal renewalCommission;
    private DateTime renewalCalenderDay;
    private DateTime commencementDate;
    private String fundingAccount;
    private String csvCommencementDate;
    private String fnumber;
    private String productName;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountSubType() {
        return accountSubType;
    }

    public void setAccountSubType(String accountSubType) {
        this.accountSubType = accountSubType;
    }

    public String getPensionType() {
        return pensionType;
    }

    public void setPensionType(String pensionType) {
        this.pensionType = pensionType;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

    public void setPaymentFrequency(String paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
    }

    public BigDecimal getRenewalCommission() {
        return renewalCommission;
    }

    public void setRenewalCommission(BigDecimal renewalCommission) {
        this.renewalCommission = renewalCommission;
    }

    public DateTime getRenewalCalenderDay() {
        return renewalCalenderDay;
    }

    public void setRenewalCalenderDay(DateTime renewalCalenderDay) {
        this.renewalCalenderDay = renewalCalenderDay;
    }

    public DateTime getCommencementDate() {
        return commencementDate;
    }

    public void setCommencementDate(DateTime commencementDate) {
        this.commencementDate = commencementDate;
    }

    public String getEncodedAccountId() {
        return encodedAccountId;
    }

    public void setEncodedAccountId(String encodedAccountId) {
        this.encodedAccountId = encodedAccountId;
    }

    public String getFundingAccount() {
        return fundingAccount;
    }

    public void setFundingAccount(String fundingAccount) {
        this.fundingAccount = fundingAccount;
    }

    public String getCsvCommencementDate() {
        return csvCommencementDate;
    }

    public void setCsvCommencementDate(String csvCommencementDate) {
        this.csvCommencementDate = csvCommencementDate;
    }

    public String getFnumber() {
        return fnumber;
    }

    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
