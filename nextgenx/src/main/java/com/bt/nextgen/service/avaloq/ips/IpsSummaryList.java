package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.ips.IpsSummaryDetails;

import java.util.List;

@ServiceBean(xpath = "/")
public class IpsSummaryList extends AvaloqBaseResponseImpl {

    @ServiceElementList(xpath = "//data/ips_list/ips", type = IpsSummaryDetailsImpl.class)
    private List<IpsSummaryDetails> summaryDetailsList;

    public List<IpsSummaryDetails> getSummaryDetailsList() {
        return summaryDetailsList;
    }

    public void setSummaryDetailsList(List<IpsSummaryDetails> summaryDetailsList) {
        this.summaryDetailsList = summaryDetailsList;
    }
}
