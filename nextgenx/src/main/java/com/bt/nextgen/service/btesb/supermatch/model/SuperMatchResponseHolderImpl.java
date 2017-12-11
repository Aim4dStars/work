package com.bt.nextgen.service.btesb.supermatch.model;


import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.btesb.base.model.EsbError;
import com.bt.nextgen.service.integration.supermatch.SuperMatchDetails;
import com.bt.nextgen.service.integration.supermatch.SuperMatchResponseHolder;

import java.util.List;

@ServiceBean(xpath = "//RspSCNS:RetrieveDetailsResponseMsg|//RspSCNS:UpsertStatusSummaryResponseMsg|//RspSCNS:UpdateRolloverStatusResponseMsg|//RspSCNS:MaintainECOCustomerResponseMsg", type = ServiceBeanType.CONCRETE)
public class SuperMatchResponseHolderImpl implements SuperMatchResponseHolder {

    @ServiceElementList(xpath = "ResponseDetails/ResponseDetail/SuccessResponse/SuperannuationMatch", type = SuperMatchDetailsImpl.class)
    private List<SuperMatchDetails> superMatchDetails;

    @ServiceElement(xpath = "Status")
    private String status;

    @ServiceElement(xpath = "ResponseDetails/ResponseDetail/ErrorResponse|ErrorResponse")
    private EsbError error;

    @Override
    public List<SuperMatchDetails> getSuperMatchDetails() {
        return superMatchDetails;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public EsbError getError() {
        return error;
    }
}
