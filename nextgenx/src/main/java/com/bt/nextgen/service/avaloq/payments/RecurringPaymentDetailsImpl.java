package com.bt.nextgen.service.avaloq.payments;

import java.util.Date;

import org.joda.time.DateTime;

import com.btfin.panorama.service.integration.RecurringFrequency;
import com.bt.nextgen.service.integration.payments.RecurringPaymentDetails;

@Deprecated
public class RecurringPaymentDetailsImpl extends PaymentDetailsImpl implements RecurringPaymentDetails {

    RecurringFrequency recurringFrequency;
    Date startDate;
    Date endDate;
    Integer maxCount;
    private DateTime nextTransactionDate;

    /**
     * @return the recurringFrequency
     */
    public RecurringFrequency getRecurringFrequency() {
        return recurringFrequency;
    }

    /**
     * @param recurringFrequency
     *            the recurringFrequency to set
     */
    public void setRecurringFrequency(RecurringFrequency recurringFrequency) {
        this.recurringFrequency = recurringFrequency;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     *            the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the maxCount
     */
    public Integer getMaxCount() {
        return maxCount;
    }

    /**
     * @param maxCount
     *            the maxCount to set
     */
    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public DateTime getNextTransactionDate() {
        return nextTransactionDate;
    }

    @Override
    public void setNextTransactionDate(DateTime txnDate) {
        this.nextTransactionDate = txnDate;
    }

}
