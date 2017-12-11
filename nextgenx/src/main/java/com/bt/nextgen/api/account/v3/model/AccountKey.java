package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.config.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;

public class AccountKey {

    @JsonView(JsonViews.Write.class)
    private String accountId;

    public AccountKey() {
        super();
    }

    public AccountKey(String accountId) {
        super();
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
        return result;
    }

    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccountKey other = (AccountKey) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        return true;
    }

}
