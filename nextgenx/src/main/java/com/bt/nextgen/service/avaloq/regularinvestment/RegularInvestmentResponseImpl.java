package com.bt.nextgen.service.avaloq.regularinvestment;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentTransaction;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentResponse;

import java.util.List;

@ServiceBean(xpath = "/")
public class RegularInvestmentResponseImpl extends AvaloqBaseResponseImpl implements RegularInvestmentResponse {
    @ServiceElementList(xpath = "//data/doc_list/doc", type = RegularInvestmentTransactionImpl.class)
    private List<RegularInvestmentTransaction> rips;

    @Override
    public List<RegularInvestmentTransaction> getRegularInvestments() {
        return rips;
    }
}
