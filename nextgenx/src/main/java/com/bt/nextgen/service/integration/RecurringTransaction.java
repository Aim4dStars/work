package com.bt.nextgen.service.integration;

import java.util.Date;

import com.btfin.panorama.service.integration.RecurringFrequency;
import org.joda.time.DateTime;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface RecurringTransaction {

    /**
     * @return the depositFrequency
     */
    public RecurringFrequency getRecurringFrequency();

    /**
     * @param recurringFrequency
     *            the recurringFrequency to set
     */
    public void setRecurringFrequency(RecurringFrequency recurringFrequency);

    /**
     * @return the startDate
     */
    public Date getStartDate();

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(Date startDate);

    /**
     * @return the endDate
     */
    public Date getEndDate();

    /**
     * @param endDate
     *            the endDate to set
     */
    public void setEndDate(Date endDate);

    /**
     * @return the MaxCount
     */
    public Integer getMaxCount();

    /**
     * @param maxCount
     *            the maxCount to set
     */
    public void setMaxCount(Integer maxCount);

    public DateTime getNextTransactionDate();

    public void setNextTransactionDate(DateTime txnDate);
}
