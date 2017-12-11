package com.bt.nextgen.core.repository;

import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;

public interface OnboardingApplicationRepository {

    OnBoardingApplication find(OnboardingApplicationKey key);

    OnBoardingApplication save(OnBoardingApplication onBoardingApplication);

    OnBoardingApplication update(OnBoardingApplication onBoardingApplication);
}
