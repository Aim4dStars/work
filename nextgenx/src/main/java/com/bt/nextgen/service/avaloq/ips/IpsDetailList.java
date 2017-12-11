package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;

import java.util.List;

@ServiceBean(xpath = "/")
public class IpsDetailList extends AvaloqBaseResponseImpl {
    @ServiceElementList(xpath = "//ips_list/ips", type = IpsDetails.class)
    private List<IpsDetails> ipsList;

    public List<IpsDetails> getIpsList() {
        return ipsList;
    }
}
