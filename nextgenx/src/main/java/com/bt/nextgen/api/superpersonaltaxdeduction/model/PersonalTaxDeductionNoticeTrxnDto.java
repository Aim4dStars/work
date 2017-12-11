package com.bt.nextgen.api.superpersonaltaxdeduction.model;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

/**
 * Created by L067218 on 11/11/2016.
 */
public class PersonalTaxDeductionNoticeTrxnDto extends BaseDto implements KeyedDto<AccountKey> {
    /**
     * Account key for the notice.
     */
    @JsonIgnore
    private AccountKey key;

    /**
     * Id of the original notice.
     * This value is {@code null} when creating a new notice, {@code non-null} for a notice variation.
     */
    private String docId;

    /**
     * Result status when saving a notice.
     */
    private String transactionStatus;

    /**
     * Start date of the financial year.
     */
    private String date;

    /**
     * Amount to put in the notice.
     */
    private BigDecimal amount;

    /**
     * Amount in the original notice.
     */
    @JsonIgnore
    private BigDecimal originalNoticeAmount;

    /**
     * Total amount of contributions.
     */
    @JsonIgnore
    private BigDecimal totalContributions;


    @Override
    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getOriginalNoticeAmount() {
        return originalNoticeAmount;
    }

    public void setOriginalNoticeAmount(BigDecimal originalNoticeAmount) {
        this.originalNoticeAmount = originalNoticeAmount;
    }

    public BigDecimal getTotalContributions() {
        return totalContributions;
    }

    public void setTotalContributions(BigDecimal totalContributions) {
        this.totalContributions = totalContributions;
    }
}
