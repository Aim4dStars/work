package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

import java.util.ArrayList;
import java.util.List;

@ServiceBean(xpath = "//RspINS:RetrievePolicyByPolicyNumberResponseMsg|//RspINS:SearchPolicyByPaymentAccountResponseMsg", type = ServiceBeanType.CONCRETE)
public class InsuranceResponseHolder {

    @ServiceElementList(xpath = "ResponseDetails/ResponseDetail/SuccessResponse/Policy", type = PolicyImpl.class)
    private List<Policy> policyResponse = new ArrayList<>();

    @ServiceElement(xpath = "Status")
    private String status;

    @ServiceElement(xpath = "ResponseDetails/ResponseDetail/ErrorResponse/Description")
    private String error;

    public List<Policy> getPolicyResponse() {
        return policyResponse;
    }

    public void setPolicyResponse(List<Policy> policyResponse) {
        this.policyResponse = policyResponse;
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
