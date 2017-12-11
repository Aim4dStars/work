package com.bt.nextgen.service.integration.authorisedfund.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

import java.util.ArrayList;
import java.util.List;

@ServiceBean(xpath = "//RspNS:RetrieveAuthorisedTrustsResponseMsg", type = ServiceBeanType.CONCRETE)
public class AuthorisedFundDetailHolder
{
    @ServiceElementList(xpath = "ResponseDetails/ResponseDetail/SuccessResponse/InvestmentTrustDetail", type = AuthorisedFundDetailImpl.class)
    private List<AuthorisedFundDetail> authorisedFundDetailList = new ArrayList<>();

    @ServiceElement(xpath="Status")
    private String status;

    @ServiceElement(xpath="ResponseDetails/ResponseDetail/ErrorResponse/SubCode")
    private String statusSubcode;


    public List<AuthorisedFundDetail> getAuthorisedFundDetailsList()
    {
        return authorisedFundDetailList;
    }

    public String getStatus()
    {
        return status;
    }

    public String getStatusSubcode()
    {
        return statusSubcode;
    }
}