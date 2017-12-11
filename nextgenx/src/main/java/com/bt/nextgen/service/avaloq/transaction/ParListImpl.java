package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.transaction.ParList;

@ServiceBean(xpath = "par_list")
public class ParListImpl implements ParList {
    @ServiceElement(xpath = "par")
    private String param;

    @Override
    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
