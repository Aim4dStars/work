package com.bt.nextgen.core.repository;

import java.util.List;

public interface OnboardingPartyRepository {

    OnboardingParty find(OnboardingParty.OnboardingPartyKey kry);

    OnboardingParty update(OnboardingParty party);

    OnboardingParty save(OnboardingParty party);

    OnboardingParty findByGCMAndApplicationId(String GCMId, Long applicationId);

    List<OnboardingParty> findByGCMId(String gcmId);

    List<OnboardingParty> findOnboardingPartiesByApplicationIds(List<Long> applicationIds);
}
