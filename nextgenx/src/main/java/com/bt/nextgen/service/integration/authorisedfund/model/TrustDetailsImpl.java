package com.bt.nextgen.service.integration.authorisedfund.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

@ServiceBean(xpath = "TrustDetails", type = ServiceBeanType.CONCRETE)
public class TrustDetailsImpl implements TrustDetails {

    @ServiceElement(xpath = "TrustName")
    private String trustName;

    @ServiceElement(xpath = "TrustID")
    private String trustId;

    @ServiceElement(xpath = "TrustIDIssuer")
    private String trustIdIssuer;

    @Override
    public String getTrustName() {
        return trustName;
    }

    @Override
    public void setTrustName(String trustName) {
        this.trustName=trustName;
    }

    @Override
    public String getTrustId() {
        return trustId;
    }

    @Override
    public void setTrustId(String trustId) {
        this.trustId=trustId;
    }

    @Override
    public String getTrustIdIssuer() {
        return trustIdIssuer;
    }

    @Override
    public void setTrustIdIssuer(String trustIdIssuer) {
        this.trustIdIssuer=trustIdIssuer;
    }
}
