package com.bt.nextgen.api.corporateaction.v1.model;


import com.bt.nextgen.service.integration.corporateaction.CorporateActionNotificationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionResponseStatus;


public class CorporateActionNotification {

    private CorporateActionNotificationStatus status;
    private CorporateActionResponseStatus response;
    private Integer responded;
    private Integer expected;
    private Integer roaId;

    public CorporateActionNotification() {
        super();
    }

    public CorporateActionNotification(CorporateActionNotificationStatus status, CorporateActionResponseStatus response, Integer responded,
                                       Integer expected, Integer roaId) {
        this.status = status;
        this.response = response;
        this.responded = responded;
        this.expected = expected;
        this.roaId = roaId;
    }

    public CorporateActionNotificationStatus getStatus() {
        return status;
    }

    public void setStatus(CorporateActionNotificationStatus status) {
        this.status = status;
    }

    public CorporateActionResponseStatus getResponse() {
        return response;
    }

    public void setResponse(CorporateActionResponseStatus response) {
        this.response = response;
    }

    public Integer getResponded() {
        return responded;
    }

    public void setResponded(Integer responded) {
        this.responded = responded;
    }

    public Integer getExpected() {
        return expected;
    }

    public void setExpected(Integer expected) {
        this.expected = expected;
    }

    public Integer getRoaId() {
        return roaId;
    }

    public void setRoaId(Integer roaId) {
        this.roaId = roaId;
    }
}
