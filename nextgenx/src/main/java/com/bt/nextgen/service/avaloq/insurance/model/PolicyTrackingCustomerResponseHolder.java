package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

import java.util.List;

@ServiceBean(xpath = "//RspINS:SearchPolicyByCustomerNumberResponseMsg", type = ServiceBeanType.CONCRETE)
public class PolicyTrackingCustomerResponseHolder {

    @ServiceElementList(xpath = "ResponseDetails/ResponseDetail/SuccessResponse/Policies/Policy/BasicDetails/BasicPolicyDetails", type = PolicyTrackingImpl.class)
    private List<PolicyTracking> policyTrackingResponse;

    @ServiceElement(xpath = "ResponseDetails/ResponseDetail/SuccessResponse/CustomerDetails/CustomerNumber")
    private String customerNumber;

    @ServiceElement(xpath = "Status")
    private String status;

    @ServiceElement(xpath = "ResponseDetails/ResponseDetail/ErrorResponse/Description")
    private String error;

    public List<PolicyTracking> getPolicyTrackingResponse() {
        return policyTrackingResponse;
    }

    public void setPolicyTrackingResponse(List<PolicyTracking> policyTrackingResponse) {
        this.policyTrackingResponse = policyTrackingResponse;
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

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

}
