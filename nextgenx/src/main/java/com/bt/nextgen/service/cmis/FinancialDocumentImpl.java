package com.bt.nextgen.service.cmis;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocument;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentKey;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import com.bt.nextgen.service.integration.user.UserKey;
import org.joda.time.DateTime;

import java.math.BigInteger;

public class FinancialDocumentImpl implements FinancialDocument {
    private AccountKey accountKey;
    private FinancialDocumentKey documentKey;
    private FinancialDocumentType documentType;
    private DateTime periodEndDate;
    private DateTime periodStartDate;
    private BigInteger size;
    private DateTime generationDate;
    private String extensionType;
    private BrokerKey dealerGroupKey;
    private UserKey customerKey;
    private String gcmId;
    private String documentName;

    @Override
    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    @Override
    public FinancialDocumentKey getDocumentKey() {
        return documentKey;
    }

    public void setDocumentKey(FinancialDocumentKey documentKey) {
        this.documentKey = documentKey;
    }

    @Override
    public FinancialDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(FinancialDocumentType documentType) {
        this.documentType = documentType;
    }

    @Override
    public DateTime getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(DateTime periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    @Override
    public DateTime getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(DateTime periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    @Override
    public DateTime getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(DateTime generationDate) {
        this.generationDate = generationDate;
    }

    public BigInteger getSize() {
        return size;
    }

    public void setSize(BigInteger size) {
        this.size = size;
    }

    @Override
    public String getExtensionType() {
        return extensionType;
    }

    public void setExtensionType(String extensionType) {
        this.extensionType = extensionType;
    }

    @Override
    public BrokerKey getDealerGroupKey() {
        return dealerGroupKey;
    }

    public void setDealerGroupKey(BrokerKey dealerGroupKey) {
        this.dealerGroupKey = dealerGroupKey;
    }

    @Override
    public UserKey getCustomerKey() {
        return customerKey;
    }

    public void setCustomerKey(UserKey customerKey) {
        this.customerKey = customerKey;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    @Override
    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    @Override
    public String getDocumentTitleCode() {
        return documentType.getCode();
    }
}
