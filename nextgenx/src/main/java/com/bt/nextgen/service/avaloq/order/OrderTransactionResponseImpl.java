package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.order.OrderTransaction;
import com.bt.nextgen.service.integration.order.OrderTransactionResponse;

import java.util.List;

@ServiceBean(xpath = "/")
public class OrderTransactionResponseImpl implements OrderTransactionResponse {

    @ServiceElementList(xpath = "//data/doc_list/doc", type = OrderTransactionImpl.class)
    private List<OrderTransaction> orderTransactions;

    @Override
    public List<OrderTransaction> getOrderTransactions() {
        return orderTransactions;
    }

}
