package com.bt.nextgen.api.policy.model;

import java.math.BigDecimal;

public class PolicyDetailsDto {

    private String policyNumber;
    private String policyType;
    private BigDecimal totalPremium;
    private String policyStatus;
    private String insuranceAdviserId;
    private String applicationReceivedDate;
    private String encodedAccountId;

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public BigDecimal getTotalPremium() {
        return totalPremium;
    }

    public void setTotalPremium(BigDecimal totalPremium) {
        this.totalPremium = totalPremium;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }

    public String getInsuranceAdviserId() {
        return insuranceAdviserId;
    }

    public void setInsuranceAdviserId(String insuranceAdviserId) {
        this.insuranceAdviserId = insuranceAdviserId;
    }

    public String getApplicationReceivedDate() {
        return applicationReceivedDate;
    }

    public void setApplicationReceivedDate(String applicationReceivedDate) {
        this.applicationReceivedDate = applicationReceivedDate;
    }

    public String getEncodedAccountId() {
        return encodedAccountId;
    }

    public void setEncodedAccountId(String encodedAccountId) {
        this.encodedAccountId = encodedAccountId;
    }
}
