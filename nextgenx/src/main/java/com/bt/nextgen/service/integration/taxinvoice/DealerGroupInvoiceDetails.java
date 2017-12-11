package com.bt.nextgen.service.integration.taxinvoice;

import java.util.List;

/**
 * Stores all the DealerGroup Details
 **/
public interface DealerGroupInvoiceDetails {

    /**
     * @return the dealerGroupName
     */
    public String getDealerGroupName();

    /**
     * @param dealerGroupName
     *            the dealerGroupName to set
     */
    public void setDealerGroupName(String dealerGroupName);

    /**
     * @return the dealerGroupABN
     */
    public String getDealerGroupABN();

    /**
     * @param dealerGroupABN
     *            the dealerGroupABN to set
     */
    public void setDealerGroupABN(String dealerGroupABN);

    /**
     * @return the adviserList
     */
    public List<TaxAdviserDetails> getAdviserList();

    /**
     * @param adviserList
     *            the adviserList to set
     */
    public void setAdviserList(List<TaxAdviserDetails> adviserList);

}
