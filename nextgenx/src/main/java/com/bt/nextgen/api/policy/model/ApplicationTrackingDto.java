package com.bt.nextgen.api.policy.model;

import java.math.BigDecimal;
import java.util.List;

public class ApplicationTrackingDto extends PolicyTrackingIdentifier{

    private String applicationReceivedDate;
    private String insuredPersonGivenName;
    private String insuredPersonLastName;
    private BigDecimal totalPremium;
    private String applicationStatus;
    private List<PolicyDetailsDto> policyDetails;

    public String getApplicationReceivedDate() {
        return applicationReceivedDate;
    }

    public void setApplicationReceivedDate(String applicationReceivedDate) {
        this.applicationReceivedDate = applicationReceivedDate;
    }

    public String getInsuredPersonGivenName() {
        return insuredPersonGivenName;
    }

    public void setInsuredPersonGivenName(String insuredPersonGivenName) {
        this.insuredPersonGivenName = insuredPersonGivenName;
    }

    public String getInsuredPersonLastName() {
        return insuredPersonLastName;
    }

    public void setInsuredPersonLastName(String insuredPersonLastName) {
        this.insuredPersonLastName = insuredPersonLastName;
    }

    public BigDecimal getTotalPremium() {
        return totalPremium;
    }

    public void setTotalPremium(BigDecimal totalPremium) {
        this.totalPremium = totalPremium;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public List<PolicyDetailsDto> getPolicyDetails() {
        return policyDetails;
    }

    public void setPolicyDetails(List<PolicyDetailsDto> policyDetails) {
        this.policyDetails = policyDetails;
    }
}
