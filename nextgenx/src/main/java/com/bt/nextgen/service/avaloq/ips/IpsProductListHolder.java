package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;

import java.util.List;

@ServiceBean(xpath = "/")
public class IpsProductListHolder extends AvaloqBaseResponseImpl {
    @ServiceElementList(xpath = "//prod_list/prod/prod_head_list/prod_head", type = IpsProductImpl.class)
    private List<IpsProductImpl> ipsProductList;

    public List<IpsProductImpl> getIpsList() {
        return ipsProductList;
    }

    public void setIpsList(List<IpsProductImpl> ipsProductList) {
        this.ipsProductList = ipsProductList;
    }
}
