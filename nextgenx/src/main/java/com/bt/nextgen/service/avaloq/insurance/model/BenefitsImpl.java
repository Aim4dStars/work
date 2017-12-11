package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ServiceBean(xpath = "Benefit")
public class BenefitsImpl {

    @NotNull
    @ServiceElement(xpath = "BenefitType")
    private BenefitType benefitType;

    @ServiceElement(xpath = "CurrentSumInsured", converter = BigDecimalConverter.class)
    private BigDecimal sumInsured;

    @ServiceElement(xpath = "RiskCommenceDate", converter = DateTimeConverter.class)
    private DateTime commencementDate;

    @ServiceElement(xpath = "TPDBenefitDefinition")
    private TPDBenefitDefinitionCode tpdDefinition = TPDBenefitDefinitionCode.NOT_AVAILABLE;

    @ServiceElement(xpath = "ProposedSumInsured", converter = BigDecimalConverter.class)
    private BigDecimal proposedSumInsured;

    @ServiceElement(xpath = "OccupationClass")
    private OccupationClass occupationClass;

    @ServiceElement(xpath = "PremiumType")
    private PremiumType premiumType = PremiumType.NOT_AVAILABLE;

    public BenefitType getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(BenefitType benefitType) {
        this.benefitType = benefitType;
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public DateTime getCommencementDate() {
        return commencementDate;
    }

    public void setCommencementDate(DateTime commencementDate) {
        this.commencementDate = commencementDate;
    }

    public TPDBenefitDefinitionCode getTpdDefinition() {
        return tpdDefinition;
    }

    public void setTpdDefinition(TPDBenefitDefinitionCode tpdDefinition) {
        this.tpdDefinition = tpdDefinition;
    }

    public BigDecimal getProposedSumInsured() {
        return proposedSumInsured;
    }

    public void setProposedSumInsured(BigDecimal proposedSumInsured) {
        this.proposedSumInsured = proposedSumInsured;
    }

    public OccupationClass getOccupationClass() {
        return occupationClass;
    }

    public void setOccupationClass(OccupationClass occupationClass) {
        this.occupationClass = occupationClass;
    }

    public PremiumType getPremiumType() {
        return premiumType;
    }

    public void setPremiumType(PremiumType premiumType) {
        this.premiumType = premiumType;
    }
}
