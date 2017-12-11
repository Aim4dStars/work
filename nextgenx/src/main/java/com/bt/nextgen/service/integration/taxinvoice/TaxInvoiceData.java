package com.bt.nextgen.service.integration.taxinvoice;

import com.bt.nextgen.service.integration.CurrencyType;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Stores all the Tax Invoice Details
 **/
public interface TaxInvoiceData {
    /**
     * @return the WrapAccountIdentifier
     */
    public WrapAccountIdentifier getWrapAccountIdentifier();

    /**
     * @return the feeDate
     */
    public DateTime getFeeDate();

    /**
     * @return the descriptionOfSupply
     */
    public String getDescriptionOfSupply();

    /**
     * @return the feeExcludingGST
     */
    public BigDecimal getFeeExcludingGST();

    /**
     * @return the gST
     */
    public BigDecimal getGST();

    /**
     * @return the feeIncludingGST
     */
    public BigDecimal getFeeIncludingGST();

    /**
     * @return the currency
     */
    public CurrencyType getCurrency();

    /**
     * @return the reversalFlag
     */
    public boolean getReversalFlag();

}