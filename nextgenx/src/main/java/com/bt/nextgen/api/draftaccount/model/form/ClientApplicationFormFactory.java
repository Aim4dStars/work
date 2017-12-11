package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.model.form.v1.ClientApplicationFormFactoryV1;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;

import java.util.Map;

/**
 * Created by m040398 on 15/03/2016.
 */
public final class ClientApplicationFormFactory {

    private ClientApplicationFormFactory() {}

    public static IClientApplicationForm getNewClientApplicationForm(Object formData) {
        if (formData instanceof Map) {
            return new ClientApplicationForm((Map)formData);
        } else if (formData instanceof OnboardingApplicationFormData) {
            return ClientApplicationFormFactoryV1.getNewClientApplicationForm((OnboardingApplicationFormData)formData);
        } else if (formData instanceof DirectClientApplicationFormData) {
            return ClientApplicationFormFactoryV1.getNewDirectClientApplicationForm((DirectClientApplicationFormData) formData);
        }
        else {
            throw new IllegalArgumentException("unknown formData object type: " + formData);
        }
    }
}
