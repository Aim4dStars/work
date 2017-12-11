package com.bt.nextgen.api.policy.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.insurance.model.PremiumType;
import com.bt.nextgen.service.avaloq.insurance.model.TPDBenefitDefinitionCode;

import java.math.BigDecimal;

public class BenefitsTypeDto extends BaseDto {

    private BigDecimal sumInsured;
    private String commencementDate;
    private TPDBenefitDefinitionCode tpdDefinition;
    private String occupationClass;
    private BigDecimal proposedSumInsured;
    private PremiumType premiumStructure;

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public String getCommencementDate() {
        return commencementDate;
    }

    public void setCommencementDate(String commencementDate) {
        this.commencementDate = commencementDate;
    }

    public TPDBenefitDefinitionCode getTpdDefinition() {
        return tpdDefinition;
    }

    public void setTpdDefinition(TPDBenefitDefinitionCode tpdDefinition) {
        this.tpdDefinition = tpdDefinition;
    }

    public String getOccupationClass() {
        return occupationClass;
    }

    public void setOccupationClass(String occupationClass) {
        this.occupationClass = occupationClass;
    }

    public BigDecimal getProposedSumInsured() {
        return proposedSumInsured;
    }

    public void setProposedSumInsured(BigDecimal proposedSumInsured) {
        this.proposedSumInsured = proposedSumInsured;
    }

    public PremiumType getPremiumStructure() {
        return premiumStructure;
    }

    public void setPremiumStructure(PremiumType premiumStructure) {
        this.premiumStructure = premiumStructure;
    }
}
