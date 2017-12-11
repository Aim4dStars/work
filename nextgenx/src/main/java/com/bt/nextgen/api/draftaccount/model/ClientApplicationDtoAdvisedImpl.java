package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * ClientApplicationDto implementation for ADVISED
 *
 * Created by m040398 on 18/07/2016.
 */
public class ClientApplicationDtoAdvisedImpl extends BaseClientApplicationDto {

    @JsonIgnore
    private OnboardingApplicationFormData formData; // auto-generated from JSON schema: accountApplicationSchema.json

    public ClientApplicationDtoAdvisedImpl(ClientApplicationKey key, OnboardingApplicationFormData formData) {
        super(key);
        this.formData = formData;
    }
    public ClientApplicationDtoAdvisedImpl(OnboardingApplicationFormData formData) {
        this.formData = formData;
    }

    public ClientApplicationDtoAdvisedImpl(ClientApplicationKey key) {
        super(key);
    }

    public Object getFormData() {
        return formData;
    }

    @Override
    public void setFormData(Object formData) {
        this.formData = (OnboardingApplicationFormData) formData;
    }

    @Override
    public boolean isJsonSchemaSupported() {
        return true;
    }

    @Override
    public boolean isDirectApplication() {
        return false;
    }

    @JsonIgnore
    public String getAccountType() {
        return toString(formData.getAccountType());

    }

}
