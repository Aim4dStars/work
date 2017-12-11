package com.bt.nextgen.api.draftaccount.service;


import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;

public interface OnboardingPartyService {

    public void createOnboardingPartyForExistingUsers(IClientApplicationForm clientApplicationForm, Long onboardingApplicationId);
}
