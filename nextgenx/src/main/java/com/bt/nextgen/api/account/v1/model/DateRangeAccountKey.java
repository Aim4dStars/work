package com.bt.nextgen.api.account.v1.model;

import org.joda.time.DateTime;

/**
 * @deprecated Use V2
 */
@Deprecated
public class DateRangeAccountKey extends AccountKey {

    /** The start date. */
    private DateTime startDate;

    /** The end date. */
    private DateTime endDate;

    /**
     * Instantiates a new date range account key.
     *
     * @param accountId
     *            the account id
     * @param startDate
     *            the start date
     * @param endDate
     *            the end date
     */
    public DateRangeAccountKey(String accountId, DateTime startDate, DateTime endDate) {
        super(accountId);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Gets the start date.
     *
     * @return the start date
     */
    public DateTime getStartDate() {
        return startDate;
    }

    /**
     * Gets the end date.
     *
     * @return the end date
     */
    public DateTime getEndDate() {
        return endDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
        DateRangeAccountKey other = (DateRangeAccountKey) obj;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        return true;
    }

}