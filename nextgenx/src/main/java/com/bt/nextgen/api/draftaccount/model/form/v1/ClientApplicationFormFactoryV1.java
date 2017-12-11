package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;

/**
 * Created by m040398 on 24/03/2016.
 */
public final class ClientApplicationFormFactoryV1 {
    private ClientApplicationFormFactoryV1() {}

    public static IClientApplicationForm getNewClientApplicationForm(OnboardingApplicationFormData data) {
        return new ClientApplicationForm(data);
    }

    public static IClientApplicationForm getNewDirectClientApplicationForm(DirectClientApplicationFormData data) {
        return  new DirectClientApplicationForm(data);
    }
}
