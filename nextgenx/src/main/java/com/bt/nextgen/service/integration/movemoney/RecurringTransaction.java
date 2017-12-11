package com.bt.nextgen.service.integration.movemoney;

import com.btfin.panorama.service.integration.RecurringFrequency;
import org.joda.time.DateTime;


public interface RecurringTransaction {
    /**
     * @return the depositFrequency
     */
    public RecurringFrequency getRecurringFrequency();

    /**
     * @return the startDate
     */
    public DateTime getStartDate();

    /**
     * @return the endDate
     */
    public DateTime getEndDate();

    /**
     * @return the MaxCount
     */
    public Integer getMaxCount();

    public DateTime getNextTransactionDate();

    public void setNextTransactionDate(DateTime nextTransactionDate);
}
