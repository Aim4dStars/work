package com.bt.nextgen.service.integration.taxinvoice;

import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.joda.time.DateTime;

/**
 * Holds all the details to request TaxInvoice
 **/
public interface TaxInvoiceRequest {
    /**
     * @return the wrapAccountIdentifier
     */
    public WrapAccountIdentifier getWrapAccountIdentifier();

    /**
     * @param wrapAccountIdentifier
     *            the wrapAccountIdentifier to set
     */
    public void setWrapAccountIdentifier(WrapAccountIdentifier wrapAccountIdentifier);

    /**
     * @return the startDate
     */
    public DateTime getStartDate();

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(DateTime startDate);

    /**
     * @return the endDate
     */
    public DateTime getEndDate();

    /**
     * @param endDate
     *            the endDate to set
     */
    public void setEndDate(DateTime endDate);

}
