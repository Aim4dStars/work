package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.rollover.ContributionReceived;

import java.util.List;

@ServiceBean(xpath = "/")
public class ContributionReceivedResponseImpl extends AvaloqBaseResponseImpl {

    @ServiceElementList(xpath = "//data/doc_list/doc", type = ContributionReceivedImpl.class)
    private List<ContributionReceived> contributions;

    public List<ContributionReceived> getContributionReceived() {
        return contributions;
    }
}
