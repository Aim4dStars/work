package com.bt.nextgen.api.account.v2.model;

import org.joda.time.DateTime;

@Deprecated
public class DatedValuationKey extends DatedAccountKey {
    private Boolean includeExternal;

    public DatedValuationKey(String accountId, DateTime effectiveDate, Boolean includeExternal) {
        super(accountId, effectiveDate);
        if (includeExternal == null) {
            this.includeExternal = Boolean.FALSE;
        } else {
            this.includeExternal = includeExternal;
        }
    }

    public Boolean getIncludeExternal() {
        return includeExternal;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((includeExternal == null) ? 0 : includeExternal.hashCode());
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
        DatedValuationKey other = (DatedValuationKey) obj;
        if (includeExternal == null) {
            if (other.includeExternal != null)
                return false;
        } else if (!includeExternal.equals(other.includeExternal))
            return false;
        return true;
    }

}