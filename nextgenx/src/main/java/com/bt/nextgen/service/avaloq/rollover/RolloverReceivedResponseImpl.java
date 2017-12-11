package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.rollover.RolloverReceived;

import java.util.List;

@ServiceBean(xpath = "/")
public class RolloverReceivedResponseImpl extends AvaloqBaseResponseImpl {

    @ServiceElementList(xpath = "//data/doc_list/doc", type = RolloverReceivedImpl.class)
    private List<RolloverReceived> superfunds;

    public List<RolloverReceived> getReceivedSuperfunds() {
        return superfunds;
    }
}
