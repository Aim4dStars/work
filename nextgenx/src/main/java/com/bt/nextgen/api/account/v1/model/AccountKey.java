package com.bt.nextgen.api.account.v1.model;

/**
 * @deprecated Use V2
 */
@Deprecated
public class AccountKey {

    /** The account id. */
    private String accountId;

    /**
     * Instantiates a new account key.
     */
    public AccountKey() {
    }

    /**
     * Instantiates a new account key.
     *
     * @param accountId
     *            the account id
     */
    public AccountKey(String accountId) {
        super();
        this.accountId = accountId;
    }

    /**
     * Gets the account id.
     *
     * @return the account id
     */
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

    @Override
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
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
