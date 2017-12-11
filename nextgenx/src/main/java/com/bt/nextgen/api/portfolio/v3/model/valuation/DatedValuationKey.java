package com.bt.nextgen.api.portfolio.v3.model.valuation;

import com.bt.nextgen.api.portfolio.v3.model.DatedAccountKey;
import org.joda.time.DateTime;

public class DatedValuationKey extends DatedAccountKey {
    private Boolean includeExternal;
    private final Boolean isCache;

    public DatedValuationKey(String accountId, DateTime effectiveDate, Boolean includeExternal) {
        super(accountId, effectiveDate);
        if (includeExternal == null) {
            this.includeExternal = Boolean.FALSE;
        } else {
            this.includeExternal = includeExternal;
        }
        this.isCache = Boolean.FALSE;
    }

    public DatedValuationKey(String accountId, DateTime effectiveDate, Boolean includeExternal, Boolean isCache) {
        super(accountId, effectiveDate);
        if (includeExternal == null) {
            this.includeExternal = Boolean.FALSE;
        } else {
            this.includeExternal = includeExternal;
        }
        this.isCache = isCache;
    }

    public Boolean getIncludeExternal() {
        return includeExternal;
    }


    public Boolean getIsCache() {
        return isCache;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((includeExternal == null) ? 0 : includeExternal.hashCode());
        result = prime * result + ((isCache == null) ? 0 : isCache.hashCode());
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
        DatedValuationKey other = (DatedValuationKey) obj;
        if (includeExternal == null) {
            if (other.includeExternal != null)
                return false;
        } else if (!includeExternal.equals(other.includeExternal))
            return false;
        if (isCache == null) {
            if (other.isCache != null)
                return false;
        } else if (!isCache.equals(other.isCache))
            return false;
        return true;
    }

}