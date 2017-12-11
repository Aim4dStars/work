package com.bt.nextgen.service.avaloq.pension;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

/**
 * Created by m040398 on 15/08/2016.
 */
@ServiceBean(xpath = "doc_head")
public class PensionEligibilityImpl implements PensionEligibility {

    @ServiceElement(xpath = "pens_elglbty_opt_id/val",  staticCodeCategory = "PENSION_ELIGIBILITY_CRITERIA")
    private EligibilityCriteria eligibilitytCriteria;

    @ServiceElement(xpath = "pens_cond_of_rel_id/val",  staticCodeCategory = "PENSION_CONDITION_RELEASE")
    private ConditionOfRelease conditionOfRelease;

    @Override
    public EligibilityCriteria getEligibilityCriteria() {
        return eligibilitytCriteria;
    }

    @Override
    public ConditionOfRelease getConditionOfRelease() {
        return conditionOfRelease;
    }

    public void setEligibilitytCriteria(EligibilityCriteria eligibilitytCriteria) {
        this.eligibilitytCriteria = eligibilitytCriteria;
    }

    public void setConditionOfRelease(ConditionOfRelease conditionOfRelease) {
        this.conditionOfRelease = conditionOfRelease;
    }
}
