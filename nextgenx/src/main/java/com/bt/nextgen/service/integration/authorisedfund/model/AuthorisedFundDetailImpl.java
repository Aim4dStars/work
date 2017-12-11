package com.bt.nextgen.service.integration.authorisedfund.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

@ServiceBean(xpath = "InvestmentTrustDetail", type = ServiceBeanType.CONCRETE)
public class AuthorisedFundDetailImpl implements AuthorisedFundDetail {

    @ServiceElement(xpath = "OrganisationName")
    private String organisationName;

    @ServiceElement(xpath = "ABN")
    private String abn;

    @ServiceElementList(xpath = "TrustDetails", type = TrustDetailsImpl.class)
    private TrustDetails trustDetails;

    @Override
    public String getOrganisationName() {
        return organisationName;
    }

    @Override
    public void setOrganisationName(String organisationName) {
        this.organisationName=organisationName;
    }

    @Override
    public String getAbn() {
        return abn;
    }

    @Override
    public void setAbn(String abn) {
        this.abn=abn;
    }

    @Override
    public TrustDetails getTrustDetails() {
        return trustDetails;
    }

    @Override
    public void setTrustDetails(TrustDetails trustDetails) {
        this.trustDetails=trustDetails;
    }
}
