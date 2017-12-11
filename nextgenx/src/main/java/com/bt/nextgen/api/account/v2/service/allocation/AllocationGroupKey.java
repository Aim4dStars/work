package com.bt.nextgen.api.account.v2.service.allocation;

@Deprecated
 // IDE generated equals method
@SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142",
            "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck", })
class AllocationGroupKey {
    private String assetKey;
    private Boolean isExternal;
    private Boolean isPending;

    public AllocationGroupKey(String assetKey, Boolean isExternal, Boolean isPending) {
        super();
        this.assetKey = assetKey;
        this.isExternal = isExternal;
        this.isPending = isPending;
    }

    public String getAssetKey() {
        return assetKey;
    }

    public Boolean getIsExternal() {
        return isExternal;
    }

    public Boolean getIsPending() {
        return isPending;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assetKey == null) ? 0 : assetKey.hashCode());
        result = prime * result + ((isExternal == null) ? 0 : isExternal.hashCode());
        result = prime * result + ((isPending == null) ? 0 : isPending.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AllocationGroupKey other = (AllocationGroupKey) obj;
        if (assetKey == null) {
            if (other.assetKey != null)
                return false;
        } else if (!assetKey.equals(other.assetKey))
            return false;
        if (isExternal == null) {
            if (other.isExternal != null)
                return false;
        } else if (!isExternal.equals(other.isExternal))
            return false;
        if (isPending == null) {
            if (other.isPending != null)
                return false;
        } else if (!isPending.equals(other.isPending))
            return false;
        return true;
    }
}
