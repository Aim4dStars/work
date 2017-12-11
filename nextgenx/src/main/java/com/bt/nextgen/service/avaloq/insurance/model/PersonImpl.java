package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

import java.math.BigDecimal;

@ServiceBean(xpath = "Owner|Beneficiary")
public class PersonImpl {

    @ServiceElement(xpath = "PartyDetails/GivenName|Beneficiary/PartyDetails/GivenName")
    private String givenName;

    @ServiceElement(xpath = "PartyDetails/LastName|Beneficiary/PartyDetails/LastName")
    private String lastName;

    @ServiceElement(xpath = "BeneficiaryPercent")
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
