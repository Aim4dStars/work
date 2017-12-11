package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;

public class AccountRebalanceKey extends ModelPortfolioKey {
    private String accountId;

    public AccountRebalanceKey() {
        super();
    }

    public AccountRebalanceKey(String modelId, String accountId) {
        super(modelId);
        this.accountId= accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
        return result;
    }

    @Override
    // IDE generated method
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccountRebalanceKey other = (AccountRebalanceKey) obj;
        if (accountId == null) {
            if (other.accountId != null)
                return false;
        } else if (!accountId.equals(other.accountId))
            return false;
        return true;
    }

}
