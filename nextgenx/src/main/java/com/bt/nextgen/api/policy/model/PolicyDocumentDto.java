package com.bt.nextgen.api.policy.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class PolicyDocumentDto extends BaseDto implements KeyedDto<AccountKey> {

    private AccountKey key;

    private String documentType;

    private String documentId;

    private String mimeType;

    private String policyOrPortfolioId;

    private String effectiveDate;

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getPolicyOrPortfolioId() {
        return policyOrPortfolioId;
    }

    public void setPolicyOrPortfolioId(String policyOrPortfolioId) {
        this.policyOrPortfolioId = policyOrPortfolioId;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }
}
