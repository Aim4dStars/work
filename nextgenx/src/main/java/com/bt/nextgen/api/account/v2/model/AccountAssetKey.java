package com.bt.nextgen.api.account.v2.model;

@Deprecated
public class AccountAssetKey extends AccountKey
{
    private String assetId;

    public AccountAssetKey(String accountId, String assetId)
	{
		super(accountId);
        this.assetId = assetId;
	}

    public String getAssetId()
	{
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
    // disabling warnings on automatically generated code.
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