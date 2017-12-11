package com.bt.nextgen.api.draftaccount.util;

import com.bt.nextgen.core.repository.OnboardingCommunication;
import com.bt.nextgen.core.repository.OnboardingCommunicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ResendRegistrationCodeTransactor {

    @Autowired
    private OnboardingCommunicationRepository onboardingCommunicationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OnboardingCommunication save(OnboardingCommunication communication) {
       return onboardingCommunicationRepository.save(communication);
    }
}
