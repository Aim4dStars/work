package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;

import java.util.List;

/**
 * Created by L067218 on 27/01/2017.
 */
@ServiceBean(xpath = "/")
public class SavedPaymentsHolderImpl extends AvaloqBaseResponseImpl implements SavedPaymentsHolder {

    @ServiceElementList(xpath = "//data/doc_list/doc", type = SavedPaymentImpl.class)
    private List <SavedPayment> payments;

    @Override
    public List<SavedPayment> getPensionPayments() {
        return payments;
    }
}



