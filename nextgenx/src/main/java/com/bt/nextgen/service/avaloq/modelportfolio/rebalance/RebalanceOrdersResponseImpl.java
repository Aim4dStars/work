package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderGroup;

import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "/")
public class RebalanceOrdersResponseImpl extends AvaloqBaseResponseImpl {

    @ServiceElementList(xpath = "//data/rebal_det_list/rebal_det/rebal_det_head_list/rebal_det_head", type = RebalanceOrderGroupImpl.class)
    private List<RebalanceOrderGroup> rebalanceOrders;

    public List<RebalanceOrderGroup> getRebalanceOrders() {
        if (rebalanceOrders == null) {
            return Collections.emptyList();
        }
        return rebalanceOrders;
    }

}
