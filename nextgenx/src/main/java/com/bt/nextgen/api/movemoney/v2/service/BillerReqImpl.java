package com.bt.nextgen.api.movemoney.v2.service;

import java.math.BigDecimal;

import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountType;
import com.btfin.panorama.service.integration.account.Biller;
import com.bt.nextgen.service.integration.account.BillerRequest;
import com.bt.nextgen.service.integration.account.UpdatePaymentLimitRequest;
import com.btfin.panorama.service.avaloq.pasttransaction.TransactionOrderType;
import com.btfin.panorama.service.avaloq.pasttransaction.TransactionType;

public class BillerReqImpl implements BillerRequest, UpdatePaymentLimitRequest {
    private AccountKey accountkey;
    private BigDecimal identifier;
    private Biller billerDetail;
    private BigDecimal amount;
    private CurrencyType currency;
    private TransactionOrderType businessOrderType;
    private TransactionType businessType;
    private AccountType accountType;

    public TransactionOrderType getBusinessTransactionOrderType() {
        return businessOrderType;
    }

    public void setBusinessTransactionOrderType(TransactionOrderType businessOrderType) {
        this.businessOrderType = businessOrderType;
    }

    public AccountKey getAccountKey() {
        return accountkey;
    }

    public void setAccountKey(AccountKey accountkey) {
        this.accountkey = accountkey;
    }

    @Override
    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getIdentifier() {
        return identifier;
    }

    public void setIdentifier(BigDecimal identifier) {
        this.identifier = identifier;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public CurrencyType getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(CurrencyType currency) {
        this.currency = currency;
    }

    public TransactionType getBusinessTransactionType() {
        return businessType;
    }

    public void setBusinessTransactionType(TransactionType businessType) {
        this.businessType = businessType;
    }

    @Override
    public BigDecimal getModificationIdentifier() {
        return identifier;
    }

    @Override
    public void setModificationIdentifier(BigDecimal identifier) {
        this.identifier = identifier;
    }

    @Override
    public Biller getBillerDetail() {
        return billerDetail;
    }

    @Override
    public void setBillerDetail(Biller billerDetail) {
        this.billerDetail = billerDetail;
    }
}