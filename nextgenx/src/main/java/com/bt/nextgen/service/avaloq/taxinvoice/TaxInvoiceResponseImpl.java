package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.taxinvoice.DealerGroupInvoiceDetails;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceResponse;

import java.util.List;

/**
 * 
 * @author L070815
 * 
 *         xpath implementation of the tax Invoice response
 * 
 */

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class TaxInvoiceResponseImpl extends AvaloqBaseResponseImpl implements TaxInvoiceResponse {

    @ServiceElementList(xpath = "//data/dg_list/dg", type = DealerGroupInvoiceDetailsImpl.class)
    private List<DealerGroupInvoiceDetails> dealerGroupDetailslist;

    @Override
    public List<DealerGroupInvoiceDetails> getDealerGroupDetails() {
        return dealerGroupDetailslist;
    }

    @Override
    public void setDealerGroupDetails(List<DealerGroupInvoiceDetails> dealerGroupDetailslist) {
        this.dealerGroupDetailslist = dealerGroupDetailslist;

    }

}
