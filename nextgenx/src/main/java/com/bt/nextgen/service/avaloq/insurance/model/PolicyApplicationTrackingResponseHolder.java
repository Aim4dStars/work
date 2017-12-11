package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

import java.util.List;

@ServiceBean(xpath = "//RspINS:SearchRecentLivesInsuredByAdviserResponseMsg", type = ServiceBeanType.CONCRETE)
public class PolicyApplicationTrackingResponseHolder {

    @ServiceElementList(xpath = "ResponseDetails/ResponseDetail/SuccessResponse/LivesInsured/LifeInsured", type = PolicyApplicationsImpl.class)
    private List<PolicyApplications> policyApplicationsResponse;

    @ServiceElement(xpath = "Status")
    private String status;

    @ServiceElement(xpath = "ResponseDetails/ResponseDetail/ErrorResponse/Description")
    private String error;

    public List<PolicyApplications> getPolicyApplicationsResponse() {
        return policyApplicationsResponse;
    }

    public void setPolicyApplicationsResponse(List<PolicyApplications> policyApplicationsResponse) {
        this.policyApplicationsResponse = policyApplicationsResponse;
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
