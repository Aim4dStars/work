package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.tracking.model.OnboardingClientStatusDto;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyRepository;
import com.bt.nextgen.core.repository.OnboardingPartyStatus;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class OnboardingClientServiceImplTest {

    @Mock
    private ClientApplicationRepository clientApplicationsRepository;
    @Mock
    private OnboardingPartyRepository onboardingPartyRepository;
    @InjectMocks
    private OnboardingClientServiceImpl clientOnboardingStatusService;

    @Test
    public void shouldReturnStatusFalseIfApplicationIsntSubmitted() {
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplicationsRepository.find(any(Long.class))).thenReturn(clientApplication);

        OnboardingClientStatusDto onboardingClientStatusDto = clientOnboardingStatusService.find(new ClientApplicationKey(123l), new ServiceErrorsImpl());
        assertThat(onboardingClientStatusDto.isClientOnboardingCompleted(), is(false));
    }

    @Test
    public void shouldReturnStatusFalseIfApplicationIsntCreatedInAvaloq() {
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getOnboardingApplication()).thenReturn(mock(OnBoardingApplication.class));
        when(clientApplicationsRepository.find(any(Long.class))).thenReturn(clientApplication);

        OnboardingClientStatusDto onboardingClientStatusDto = clientOnboardingStatusService.find(new ClientApplicationKey(123l), new ServiceErrorsImpl());
        assertThat(onboardingClientStatusDto.isClientOnboardingCompleted(), is(false));
    }

    @Test
    public void shouldReturnStatusFalseIfApplicationIsCreatedInAvaloqButPartyTableNotUpdated() {
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getKey()).thenReturn(OnboardingApplicationKey.valueOf(321l));
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn("someId");

        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        when(clientApplicationsRepository.find(any(Long.class))).thenReturn(clientApplication);

        OnboardingClientStatusDto onboardingClientStatusDto = clientOnboardingStatusService.find(new ClientApplicationKey(123l), new ServiceErrorsImpl());
        assertThat(onboardingClientStatusDto.isClientOnboardingCompleted(), is(false));
    }

    @Test
    public void shouldReturnStatusFalseIfPoaIsNotCompleted() {

        OnboardingParty onboardingParty = mock(OnboardingParty.class);
        when(onboardingParty.getStatus()).thenReturn(null);
        when(onboardingPartyRepository.findOnboardingPartiesByApplicationIds(eq(Arrays.asList(321l)))).thenReturn(Arrays.asList(onboardingParty));

        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getKey()).thenReturn(OnboardingApplicationKey.valueOf(321l));
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn("someId");

        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        when(clientApplicationsRepository.find(any(Long.class))).thenReturn(clientApplication);

        OnboardingClientStatusDto onboardingClientStatusDto = clientOnboardingStatusService.find(new ClientApplicationKey(123l), new ServiceErrorsImpl());
        assertThat(onboardingClientStatusDto.isClientOnboardingCompleted(), is(false));
    }

    @Test
    public void shouldReturnStatusTrueIfPoaCompleted() {
        OnboardingParty onboardingParty = mock(OnboardingParty.class);
        when(onboardingParty.getStatus()).thenReturn(OnboardingPartyStatus.NotificationSentToExistingOnlineUser);
        when(onboardingPartyRepository.findOnboardingPartiesByApplicationIds(eq(Arrays.asList(321l)))).thenReturn(Arrays.asList(onboardingParty));

        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getKey()).thenReturn(OnboardingApplicationKey.valueOf(321l));
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn("someId");

        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        when(clientApplicationsRepository.find(any(Long.class))).thenReturn(clientApplication);

        OnboardingClientStatusDto onboardingClientStatusDto = clientOnboardingStatusService.find(new ClientApplicationKey(123l), new ServiceErrorsImpl());
        assertThat(onboardingClientStatusDto.isClientOnboardingCompleted(), is(true));
    }



}