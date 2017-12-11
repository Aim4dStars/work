package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.taxinvoice.DealerGroupInvoiceDetails;
import com.bt.nextgen.service.integration.taxinvoice.TaxAdviserDetails;

import java.util.List;

@ServiceBean(xpath = "dg", type = ServiceBeanType.CONCRETE)
public class DealerGroupInvoiceDetailsImpl implements DealerGroupInvoiceDetails {
    public static final String PATH_DEALER_GROUP_NAME = "dg_head_list/dg_head/dg_oe_owner/val";
    public static final String PATH_DEALER_GROUP_ABN = "dg_head_list/dg_head/dg_oe_owner_abn/val";

    @ServiceElement(xpath = PATH_DEALER_GROUP_NAME)
    private String dealerGroupName;

    @ServiceElement(xpath = PATH_DEALER_GROUP_ABN)
    private String dealerGroupABN;

    @ServiceElementList(xpath = "avsr_list/avsr", type = TaxAdviserDetailsImpl.class)
    private List<TaxAdviserDetails> adviserList;

    @Override
    public String getDealerGroupName() {
        return dealerGroupName;
    }

    @Override
    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;

    }

    @Override
    public String getDealerGroupABN() {
        // TODO Auto-generated method stub
        return dealerGroupABN;
    }

    @Override
    public void setDealerGroupABN(String dealerGroupABN) {
        this.dealerGroupABN = dealerGroupABN;

    }

    @Override
    public List<TaxAdviserDetails> getAdviserList() {
        return adviserList;
    }

    @Override
    public void setAdviserList(List<TaxAdviserDetails> adviserList) {
        this.adviserList = adviserList;

    }

}
