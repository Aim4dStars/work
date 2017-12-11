package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyRepository;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.DateTimeServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OnboardingPartyServiceImplTest {

    private static final Integer DUMMY_SEQ_NUM = 1;
    private static final String DUMMY_GCM_ID = "123";
    private static final long DUMMY_ONBOARDING_APP_ID = 123l;

    @Mock
    OnboardingPartyRepository mockedRepository;

    @Mock
    private DateTimeServiceImpl dateTimeService;

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    OnboardingPartyServiceImpl service = new OnboardingPartyServiceImpl();

    @Test
    public void shouldSaveWhenExistingPartyExists() {
        when(dateTimeService.getCurrentDateTime()).thenCallRealMethod();

        IPersonDetailsForm mockedForm = mock(IPersonDetailsForm.class);
        when(mockedForm.getCorrelationSequenceNumber()).thenReturn(DUMMY_SEQ_NUM);
        when(mockedForm.getPanoramaNumber()).thenReturn(DUMMY_GCM_ID);
        List<IPersonDetailsForm> personDetailsForms = new ArrayList<>();
        personDetailsForms.add(mockedForm);

        IClientApplicationForm mockedApplicationForm = mock(IClientApplicationForm.class);
        when(mockedApplicationForm.getExistingPersonDetails()).thenReturn(personDetailsForms);

        service.createOnboardingPartyForExistingUsers(mockedApplicationForm, DUMMY_ONBOARDING_APP_ID);
        verify(mockedRepository, times(1)).save(any(OnboardingParty.class));
    }

    @Test
    public void shouldNotCallSaveWhenNoExistingPartyExists() {
        List<IPersonDetailsForm> personDetailsForms = new ArrayList<>();

        IClientApplicationForm mockedApplicationForm = mock(IClientApplicationForm.class);
        when(mockedApplicationForm.getExistingPersonDetails()).thenReturn(personDetailsForms);

        service.createOnboardingPartyForExistingUsers(mockedApplicationForm, DUMMY_ONBOARDING_APP_ID);
        verify(mockedRepository, never()).save(any(OnboardingParty.class));
    }


}