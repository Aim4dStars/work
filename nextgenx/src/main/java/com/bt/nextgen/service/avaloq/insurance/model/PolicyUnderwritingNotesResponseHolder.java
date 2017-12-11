package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

@ServiceBean(xpath = "//RspINS:RetrieveUnderwritingByPolicyNumberResponseMsg", type = ServiceBeanType.CONCRETE)
public class PolicyUnderwritingNotesResponseHolder {

    @ServiceElement(xpath = "ResponseDetails/ResponseDetail/SuccessResponse",
            type = PolicyUnderwritingImpl.class)
    private PolicyUnderwriting underWritingNotesResponse;

    @ServiceElement(xpath = "Status")
    private String status;

    @ServiceElement(xpath = "ResponseDetails/ResponseDetail/ErrorResponse/Description")
    private String error;

    public PolicyUnderwriting getUnderWritingNotesResponse() {
        return underWritingNotesResponse;
    }

    public void setUnderWritingNotesResponse(PolicyUnderwriting underWritingNotesResponse) {
        this.underWritingNotesResponse = underWritingNotesResponse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
