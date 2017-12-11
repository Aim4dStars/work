package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IPensionEligibilityForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.pensioneligibility.PensionEligibility;

/**
 * Created by L070354 on 14/06/2016.
 */
public class PensionEligibilityForm implements IPensionEligibilityForm {

    private final PensionEligibility pensionEligibility;

    public PensionEligibilityForm(PensionEligibility pensionEligibility) {
        this.pensionEligibility = pensionEligibility;
    }

    @Override
    public String getConditionRelease() {
        return this.pensionEligibility != null ? this.pensionEligibility.getConditionRelease() : "";
    }

    @Override
    public String getEligibilityCriteria() {
        return this.pensionEligibility != null ? this.pensionEligibility.getEligibilityCriteria() : "";
    }
}
