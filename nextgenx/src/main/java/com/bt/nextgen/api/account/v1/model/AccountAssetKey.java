package com.bt.nextgen.api.account.v1.model;

/**
 * @deprecated Use V2
 */
@Deprecated
public class AccountAssetKey extends AccountKey {

    /** The asset id. */
    private String assetId;

    /**
     * Instantiates a new account asset key.
     *
     * @param accountId
     *            the account id
     * @param assetId
     *            the asset id
     */
    public AccountAssetKey(String accountId, String assetId) {
        super(accountId);
        this.assetId = assetId;
    }

    /**
     * Gets the asset id.
     *
     * @return the asset id
     */
    public String getAssetId() {
        return assetId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((assetId == null) ? 0 : assetId.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccountAssetKey other = (AccountAssetKey) obj;
        if (assetId == null) {
            if (other.assetId != null)
                return false;
        } else if (!assetId.equals(other.assetId))
            return false;
        return true;
    }

}
