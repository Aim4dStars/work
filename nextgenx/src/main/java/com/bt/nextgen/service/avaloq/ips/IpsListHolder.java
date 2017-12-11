package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;

import java.util.List;

@ServiceBean(xpath = "/")
public class IpsListHolder extends AvaloqBaseResponseImpl {
    @ServiceElementList(xpath = "//ips_list/ips", type = InvestmentPolicyStatementImpl.class)
    private List<InvestmentPolicyStatementInterface> ipsList;

    public List<InvestmentPolicyStatementInterface> getIpsList() {
        return ipsList;
    }
}
