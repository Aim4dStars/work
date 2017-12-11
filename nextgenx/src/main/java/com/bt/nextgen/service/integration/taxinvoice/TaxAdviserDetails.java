package com.bt.nextgen.service.integration.taxinvoice;

import com.bt.nextgen.service.integration.AdviserIdentifier;

import java.math.BigDecimal;
import java.util.List;

/**
 * Stores all the Adviser Details
 **/
public interface TaxAdviserDetails {

    /**
     * @return the adviserId
     */
    public AdviserIdentifier getAdviserIdentifier();

    /**
     * @return the adviserName
     */
    public String getAdviserName();

    /**
     * @return the adviserPhoneNumber
     */
    public String getAdviserPhoneNumber();

    /**
     * @return the taxInvoiceData
     */
    public List<TaxInvoiceData> getTaxInvoice();

    /**
     * @return the totalfeeExcludingGST
     */
    public BigDecimal getTotalfeeExcludingGST();

    /**
     * @param totalfeeExcludingGST
     *            the totalfeeExcludingGST to set
     */
    public void setTotalfeeExcludingGST(BigDecimal totalfeeExcludingGST);

    /**
     * @return the totalGST
     */
    public BigDecimal getTotalGST();

    /**
     * @param totalGST
     *            the totalGST to set
     */
    public void setTotalGST(BigDecimal totalGST);

    /**
     * @return the totalfeeIncludingGST
     */
    public BigDecimal getTotalfeeIncludingGST();

    /**
     * @param totalfeeIncludingGST
     *            the totalfeeIncludingGST to set
     */
    public void setTotalfeeIncludingGST(BigDecimal totalfeeIncludingGST);

}