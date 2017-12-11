package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.rollover.RolloverHistory;
import com.bt.nextgen.service.integration.rollover.RolloverHistoryResponse;

import java.util.List;

@ServiceBean(xpath = "/")
public class RolloverHistoryResponseImpl extends AvaloqBaseResponseImpl implements RolloverHistoryResponse {

    @ServiceElementList(xpath = "//data/dtm_list/dtm", type = RolloverHistoryImpl.class)
    private List<RolloverHistory> rolloverHistory;

    @Override
    public List<RolloverHistory> getRolloverHistory() {
        return rolloverHistory;
    }
}
