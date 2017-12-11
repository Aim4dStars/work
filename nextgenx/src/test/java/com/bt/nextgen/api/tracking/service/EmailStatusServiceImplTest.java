package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.core.repository.OnboardingCommunication;
import com.bt.nextgen.core.repository.OnboardingCommunicationRepository;
import com.bt.nextgen.core.repository.OnboardingCommunicationStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmailStatusServiceImplTest {

    @InjectMocks
    private EmailStatusServiceImpl emailStatusService;

    @Mock
    private OnboardingCommunicationRepository communicationRepository;

    @Rule
    public ExpectedException expectException = ExpectedException.none();

    final private static Long applicationId = 1L;
    final private static String GCM_ID = "TEST_GCM";
    private InvestorDetail clientDetail;

    @Before
    public void setUp() throws Exception {
        clientDetail = Mockito.mock(InvestorDetail.class);
        when(clientDetail.getGcmId()).thenReturn(GCM_ID);
    }

    @Test
    public void getApplicationClientStatusShouldReturnProcessingIfNoRecordInCommunication() throws Exception {
        List<OnboardingCommunication> communications = new ArrayList<>();
        when(communicationRepository.findByApplicationIdAndGCMId(1L,GCM_ID)).thenReturn(communications);

        AssociatedPerson person = Mockito.mock(AssociatedPerson.class);
        when(person.isRegisteredOnline()).thenReturn(false);
        ApplicationClientStatus status = emailStatusService.getApplicationClientStatus(applicationId, person, GCM_ID);
        assertThat(status, is(ApplicationClientStatus.PROCESSING));
    }

    @Test
    public void getApplicationClientStatusShouldReturnNotRegisteredForEmailSuccessAndNotRegisteredInvestor() throws Exception {
        OnboardingCommunication communication1 = createCommunicationWithStatus(OnboardingCommunicationStatus.INITIATED);
        OnboardingCommunication communication2 = createCommunicationWithStatus(OnboardingCommunicationStatus.HARD_BOUNCE);
        List<OnboardingCommunication> communications = Arrays.asList(communication1, communication2);
        when(communicationRepository.findByApplicationIdAndGCMId(1L,GCM_ID)).thenReturn(communications);

        AssociatedPerson person = Mockito.mock(AssociatedPerson.class);
        when(person.isRegisteredOnline()).thenReturn(false);
        ApplicationClientStatus status = emailStatusService.getApplicationClientStatus(applicationId, person, GCM_ID);
        assertThat(status, is(ApplicationClientStatus.NOT_REGISTERED));
    }

    @Test
    public void getApplicationClientStatusShouldReturnEmailFailedForEmailFailedAndNotRegisteredInvestor() throws Exception {
        OnboardingCommunication communication2 = createCommunicationWithStatus(OnboardingCommunicationStatus.HARD_BOUNCE);
        List<OnboardingCommunication> communications = Arrays.asList(communication2);
        when(communicationRepository.findByApplicationIdAndGCMId(1L,GCM_ID)).thenReturn(communications);

        AssociatedPerson person = Mockito.mock(AssociatedPerson.class);
        when(person.isRegisteredOnline()).thenReturn(false);
        ApplicationClientStatus status = emailStatusService.getApplicationClientStatus(applicationId, person, GCM_ID);
        assertThat(status, is(ApplicationClientStatus.FAILED_EMAIL));
    }

    @Test
    public void getApplicationClientStatusShouldReturnApprovedForEmailFailedAndRegisteredInvestorWhoApprovedApplication() throws Exception {
        OnboardingCommunication communication2 = createCommunicationWithStatus(OnboardingCommunicationStatus.HARD_BOUNCE);
        List<OnboardingCommunication> communications = Arrays.asList(communication2);
        when(communicationRepository.findByApplicationIdAndGCMId(1L,GCM_ID)).thenReturn(communications);

        AssociatedPerson person = Mockito.mock(AssociatedPerson.class);
        when(person.isRegisteredOnline()).thenReturn(true);
        when(person.isHasToAcceptTnC()).thenReturn(true);
        when(person.isHasApprovedTnC()).thenReturn(true);
        ApplicationClientStatus status = emailStatusService.getApplicationClientStatus(applicationId, person, GCM_ID);
        assertThat(status, is(ApplicationClientStatus.APPROVED));
    }

    @Test
    public void getApplicationClientStatusShouldReturnAwaitingApprovalForFailingStatusAndRegisteredInvestorWhoNotApprovedApplication() throws Exception {
        OnboardingCommunication communication2 = createCommunicationWithStatus(OnboardingCommunicationStatus.HARD_BOUNCE);
        List<OnboardingCommunication> communications = Arrays.asList(communication2);
        when(communicationRepository.findByApplicationIdAndGCMId(1L,GCM_ID)).thenReturn(communications);

        AssociatedPerson person = Mockito.mock(AssociatedPerson.class);
        when(person.isRegisteredOnline()).thenReturn(true);
        when(person.isHasToAcceptTnC()).thenReturn(true);
        when(person.isHasApprovedTnC()).thenReturn(false);
        ApplicationClientStatus status = emailStatusService.getApplicationClientStatus(applicationId, person, GCM_ID);
        assertThat(status, is(ApplicationClientStatus.AWAITING_APPROVAL));
    }


    @Test
    public void getApplicationClientStatusShouldReturnRegisteredForFailingStatusAndRegisteredInvestorWhoIsNotApprover() throws Exception {
        OnboardingCommunication communication2 = createCommunicationWithStatus(OnboardingCommunicationStatus.HARD_BOUNCE);
        List<OnboardingCommunication> communications = Arrays.asList(communication2);
        when(communicationRepository.findByApplicationIdAndGCMId(1L,GCM_ID)).thenReturn(communications);

        AssociatedPerson person = Mockito.mock(AssociatedPerson.class);
        when(person.isRegisteredOnline()).thenReturn(true);
        when(person.isHasToAcceptTnC()).thenReturn(false);
        when(person.isHasApprovedTnC()).thenReturn(true);
        ApplicationClientStatus status = emailStatusService.getApplicationClientStatus(applicationId, person, GCM_ID);
        assertThat(status, is(ApplicationClientStatus.REGISTERED));
    }

    @Test
    public void shouldReturnTrueWhenCommunicationSuccessfulForTheParty(){
        final String gcmId = "TestGcm";
        OnboardingCommunication communication = createCommunicationWithStatus(OnboardingCommunicationStatus.SOFT_BOUNCE);
        when(communicationRepository.findCommunicationsByGcmId(eq(gcmId))).thenReturn(Arrays.asList(communication));
        assertThat(emailStatusService.isCommunicationSuccessfulForTheParty(gcmId), is(true));

    }

    @Test
    public void shouldReturnFalseWhenCommunicationIsNotSuccessfulForTheParty_HardBounce(){
        final String gcmId = "TestGcm";
        OnboardingCommunication communication = createCommunicationWithStatus(OnboardingCommunicationStatus.HARD_BOUNCE);
        when(communicationRepository.findCommunicationsByGcmId(eq(gcmId))).thenReturn(Arrays.asList(communication));
        assertThat(emailStatusService.isCommunicationSuccessfulForTheParty(gcmId), is(false));
    }

    @Test
    public void shouldReturnFalseWhenCommunicationIsNotSuccessfulForTheParty_FormatError(){
        final String gcmId = "TestGcm";
        OnboardingCommunication communication = createCommunicationWithStatus(OnboardingCommunicationStatus.FORMAT_ERROR);
        when(communicationRepository.findCommunicationsByGcmId(eq(gcmId))).thenReturn(Arrays.asList(communication));
        assertThat(emailStatusService.isCommunicationSuccessfulForTheParty(gcmId), is(false));
    }

    @Test
    public void shouldReturnFalseWhenCommunicationIsNotSuccessfulForTheParty_Error(){
        final String gcmId = "TestGcm";
        OnboardingCommunication communication = createCommunicationWithStatus(OnboardingCommunicationStatus.ERROR);
        when(communicationRepository.findCommunicationsByGcmId(eq(gcmId))).thenReturn(Arrays.asList(communication));
        assertThat(emailStatusService.isCommunicationSuccessfulForTheParty(gcmId), is(false));
    }

    private OnboardingCommunication createCommunicationWithStatus(String emailStatus) {
        OnboardingCommunication communication = new OnboardingCommunication();
        communication.setStatus(emailStatus);
        return communication;
    }
}
