package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.rollover.SuperfundDetails;
import com.bt.nextgen.service.integration.rollover.SuperfundResponse;

import java.util.List;

@ServiceBean(xpath = "/")
public class SuperfundResponseImpl extends AvaloqBaseResponseImpl implements SuperfundResponse {
    @ServiceElementList(xpath = "//data/d_list/d", type = SuperfundDetailsImpl.class)
    private List<SuperfundDetails> superfunds;

    @Override
    public List<SuperfundDetails> getSuperfunds() {
        return superfunds;
    }
}
