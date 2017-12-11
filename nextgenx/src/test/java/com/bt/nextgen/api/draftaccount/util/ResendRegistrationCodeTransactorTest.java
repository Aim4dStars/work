package com.bt.nextgen.api.draftaccount.util;

import com.bt.nextgen.core.repository.OnboardingCommunication;
import com.bt.nextgen.core.repository.OnboardingCommunicationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ResendRegistrationCodeTransactorTest {

    @InjectMocks
    ResendRegistrationCodeTransactor resendRegistrationCodeTransactor;

    @Mock
    OnboardingCommunicationRepository onboardingCommunicationRepository;

    @Mock
    OnboardingCommunication onboardingCommunication;

    @Test
    public void testSave_shouldCallOnboardingCommunicationRepositorySave() throws Exception {

        resendRegistrationCodeTransactor.save(onboardingCommunication);

        verify(onboardingCommunicationRepository, times(1)).save(onboardingCommunication);
    }
}