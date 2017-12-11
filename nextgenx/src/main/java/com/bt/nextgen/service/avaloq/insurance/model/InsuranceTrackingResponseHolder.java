package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

import java.util.List;

@ServiceBean(xpath = "//RspINA:SearchAccessibleAccountsResponseMsg|//RspINS:SearchPolicyByAdviserResponseMsg", type = ServiceBeanType.CONCRETE)
public class InsuranceTrackingResponseHolder {

    @ServiceElementList(xpath = "ResponseDetails/ResponseDetail/SuccessResponse/AccessibleAccounts/AccessibleAccount|" +
            "ResponseDetails/ResponseDetail/SuccessResponse/PolicyBasic[contains(./PaymentMethod/WorkingCashAccount/FirstAgentIdentification/NameOfInstitution,'PANORAMA')]", type = PolicyTrackingImpl.class)
    private List<PolicyTracking> policyTrackingResponse;

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
}
