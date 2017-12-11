package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

@ServiceBean(xpath = "BenefitOption")
public class BenefitOptionsImpl {

    @ServiceElement(xpath = "BenefitOptionType")
    private BenefitOptionType benefitOptions;

    @ServiceElement(xpath = "BenefitOptionStatus")
    private BenefitOptionStatus benefitOptionStatus;

    public BenefitOptionType getBenefitOptions() {
        return benefitOptions;
    }

    public void setBenefitOptions(BenefitOptionType benefitOptions) {
        this.benefitOptions = benefitOptions;
    }

    public BenefitOptionStatus getBenefitOptionStatus() {
        return benefitOptionStatus;
    }

    public void setBenefitOptionStatus(BenefitOptionStatus benefitOptionStatus) {
        this.benefitOptionStatus = benefitOptionStatus;
    }
}
