package com.bt.nextgen.service.avaloq.matchtfn.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

/**
 * Created by L070354 on 13/07/2017.
 */

@ServiceBean(xpath = "//person_tfn_exist", type = ServiceBeanType.CONCRETE)
public class MatchTFNImpl  implements  MatchTFN{

    @ServiceElement(xpath = "val")
    private Boolean doTFNMatch;

    @Override
    public Boolean doesTFNMatch() {
        return doTFNMatch;
    }
}
