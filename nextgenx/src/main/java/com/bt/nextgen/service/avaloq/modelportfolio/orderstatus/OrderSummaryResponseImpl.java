package com.bt.nextgen.service.avaloq.modelportfolio.orderstatus;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.ModelOrderDetails;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.ModelOrderSummaryResponse;

import java.util.List;

@ServiceBean(xpath = "/")
public class OrderSummaryResponseImpl extends AvaloqBaseResponseImpl implements ModelOrderSummaryResponse {

    @ServiceElementList(xpath = "//data/doc_list/doc/doc_head_list/doc_head", type = ModelOrderDetailsImpl.class)
    private List<ModelOrderDetails> orderDetails;

    @Override
    public List<ModelOrderDetails> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<ModelOrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
