package com.bt.nextgen.api.account.v2.model;

import org.joda.time.DateTime;

@Deprecated
public class DatedAccountKey extends AccountKey {
    private DateTime effectiveDate;

    public DatedAccountKey(String accountId, DateTime effectiveDate) {
        super(accountId);
        this.effectiveDate = effectiveDate;
    }

    public DateTime getEffectiveDate() {
        return effectiveDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
        return result;
    }

    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        DatedAccountKey other = (DatedAccountKey) obj;
        if (effectiveDate == null) {
            if (other.effectiveDate != null)
                return false;
        } else if (!effectiveDate.equals(other.effectiveDate))
            return false;
        return true;
    }

}