package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.transfer.TransferOrder;

import java.util.List;

@ServiceBean(xpath = "/")
public class TransferResponseImpl extends AvaloqBaseResponseImpl {
    @ServiceElementList(xpath = "//data/doc_list/doc", type = TransferOrderImpl.class)
    private List<TransferOrder> transferOrders;

    public List<TransferOrder> getTransferOrders() {
        return transferOrders;
    }
}
