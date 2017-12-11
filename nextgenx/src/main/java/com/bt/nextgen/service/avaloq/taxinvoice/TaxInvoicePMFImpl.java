package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.taxinvoice.InvstMngrInvoiceDetails;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoicePMF;

import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class TaxInvoicePMFImpl implements TaxInvoicePMF {

    @ServiceElementList(xpath = "//data/invst_mngr_list/invst_mngr", type = InvstMngrInvoiceDetailsImpl.class)
    private List<InvstMngrInvoiceDetails> invstMngrInvoiceDetailsList;

    @Override
    public List<InvstMngrInvoiceDetails> getInvstMngrInvoiceDetailsList() {
        if (invstMngrInvoiceDetailsList == null) {
            return Collections.emptyList();
        }
        return invstMngrInvoiceDetailsList;
    }

    public void setInvstMngrInvoiceDetailsList(List<InvstMngrInvoiceDetails> invstMngrInvoiceDetailsList) {
        this.invstMngrInvoiceDetailsList = invstMngrInvoiceDetailsList;
    }

}
