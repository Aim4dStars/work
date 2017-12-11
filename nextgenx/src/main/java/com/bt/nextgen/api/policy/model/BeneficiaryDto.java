package com.bt.nextgen.api.policy.model;

import java.math.BigDecimal;

public class BeneficiaryDto{

    private String givenName;
    private String lastName;
    private BigDecimal beneficiaryContribution;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public BigDecimal getBeneficiaryContribution() {
        return beneficiaryContribution;
    }

    public void setBeneficiaryContribution(BigDecimal beneficiaryContribution) {
        this.beneficiaryContribution = beneficiaryContribution;
    }
}
