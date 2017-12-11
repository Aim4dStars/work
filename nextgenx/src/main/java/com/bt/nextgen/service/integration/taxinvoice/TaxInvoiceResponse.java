package com.bt.nextgen.service.integration.taxinvoice;

import java.util.List;

public interface TaxInvoiceResponse {

    /**
     * @return the DealerGroupDetails
     */
    List<DealerGroupInvoiceDetails> getDealerGroupDetails();

    /**
     * @param dealerGroupDetails
     *            the DealerGroupDetails to set
     */
    void setDealerGroupDetails(List<DealerGroupInvoiceDetails> dealerGroupDetails);

}
