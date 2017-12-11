package com.bt.nextgen.service.avaloq.dealergroupparams;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "/")
public class DealerGroupParamsResponseImpl implements DealerGroupParamsResponse {   
    
    @ServiceElementList(xpath = "//data/report/cua_list/cua/cua_head_list/cua_head", type = DealerGroupParamsImpl.class)
    private List<DealerGroupParams> customerAccountObjects;

    public List<DealerGroupParams> getCustomerAccountObjects() {
        if (customerAccountObjects == null) {
            return Collections.emptyList();
        }
        return customerAccountObjects;
    }

    public void setCustomerAccountObjects(List<DealerGroupParams> customerAccoountObjects) {  		
        this.customerAccountObjects = customerAccoountObjects;
    }                

}
