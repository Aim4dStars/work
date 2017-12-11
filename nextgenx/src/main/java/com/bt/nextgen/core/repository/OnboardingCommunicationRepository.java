package com.bt.nextgen.core.repository;

import java.util.List;

public interface OnboardingCommunicationRepository {

    OnboardingCommunication save(OnboardingCommunication communication);

    OnboardingCommunication find(String communicationId);

    void flush();

    List<OnboardingCommunication> findByApplicationIdAndGCMId(Long onboardingApplicationId, String gcmPan);

	List <OnboardingCommunication> findCommunicationsByGcmId(String gcmId);
}
