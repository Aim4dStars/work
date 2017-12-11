package com.bt.nextgen.api.statements.model;

public class DocumentKey {

    private String accountId;

    private String documentId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((documentId == null) ? 0 : documentId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentKey)) {
            return false;
        }
        DocumentKey that = (DocumentKey) o;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) {
            return false;
        }
        return !(documentId != null ? !documentId.equals(that.documentId) : that.documentId != null);

    }
}