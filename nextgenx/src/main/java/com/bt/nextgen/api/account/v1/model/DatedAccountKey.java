package com.bt.nextgen.api.account.v1.model;

import org.joda.time.DateTime;

/**
 * @deprecated Use V2
 */
@Deprecated
public class DatedAccountKey extends AccountKey {

    /** The effective date. */
    private DateTime effectiveDate;

    /**
     * Instantiates a new dated account key.
     *
     * @param accountId
     *            the account id
     * @param effectiveDate
     *            the effective date
     */
    public DatedAccountKey(String accountId, DateTime effectiveDate) {
        super(accountId);
        this.effectiveDate = effectiveDate;
    }

    /**
     * Gets the effective date.
     *
     * @return the effective date
     */
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

    @Override
    @SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
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