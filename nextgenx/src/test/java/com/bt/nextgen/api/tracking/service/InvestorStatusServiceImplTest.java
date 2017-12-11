package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyRepository;
import com.bt.nextgen.core.repository.OnboardingPartyStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.btfin.panorama.core.security.integration.domain.Individual;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InvestorStatusServiceImplTest {

    @InjectMocks
    private InvestorStatusServiceImpl partyStatusService;
    @Mock
    private OnboardingPartyRepository partyRepository;
    @Mock
    private AssociatedPerson associatedPerson;
    @Mock
    private EmailStatusService emailStatusService;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private String gcmId = "gcm-Id";
    private Long applicationId = 1L;

    private Individual clientDetail;

    @Before
    public void setUp() throws Exception {
        clientDetail = Mockito.mock(Individual.class);
        when(clientDetail.getGcmId()).thenReturn(gcmId);
    }

    @Test
    public void shouldThrowNotFoundExceptionIfOnboardingPartyDoesNotExist() throws Exception {
        when(partyRepository.findByGCMAndApplicationId(gcmId, applicationId)).thenReturn(null);
        exception.expect(NotFoundException.class);
        exception.expectMessage("Error while fetching investor's status from OnboardingCommunication for the onboardingApplication 1");
        partyStatusService.getInvestorStatus(applicationId, associatedPerson, null);
    }

    @Test
    public void shouldReturnProcessingIfPartyStatusIsNull() throws Exception {
        OnboardingParty onboardingParty = new OnboardingParty(1, applicationId, gcmId);
        ApplicationClientStatus investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.PROCESSING));
    }

    @Test
    public void shouldReturnProcessingIfPartyStatusIsFailure() throws Exception {
        OnboardingParty onboardingParty = new OnboardingParty(1, applicationId, gcmId);

        onboardingParty.setStatus(OnboardingPartyStatus.CustomerNotificationFailed);
        ApplicationClientStatus investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.PROCESSING));

        onboardingParty.setStatus(OnboardingPartyStatus.DeviceRegistrationFailed);
        investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.PROCESSING));

        onboardingParty.setStatus(OnboardingPartyStatus.OnlineAccessRegistrationFailed);
        investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.PROCESSING));

        onboardingParty.setStatus(OnboardingPartyStatus.ServerFailure);
        investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.PROCESSING));
    }

    @Test
    public void shouldReturnRegisteredIfPartyStatusIsExistingUserAndInvestorIsNotApprover() throws Exception {

        OnboardingParty onboardingParty = new OnboardingParty(1, applicationId, gcmId);
        onboardingParty.setStatus(OnboardingPartyStatus.ExistingPanoramaOnlineUser);

        when(associatedPerson.isHasToAcceptTnC()).thenReturn(false);
        when(associatedPerson.isHasApprovedTnC()).thenReturn(false);
        when(associatedPerson.isRegisteredOnline()).thenReturn(true);
        ApplicationClientStatus investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.REGISTERED));
    }

    @Test
    public void shouldReturnAwaitingApprovalIfPartyStatusIsExistingUserAndApproverHasNotApproved() throws Exception {
        OnboardingParty onboardingParty = new OnboardingParty(1, applicationId, gcmId);
        onboardingParty.setStatus(OnboardingPartyStatus.ExistingPanoramaOnlineUser);

        when(associatedPerson.isHasToAcceptTnC()).thenReturn(true);
        when(associatedPerson.isHasApprovedTnC()).thenReturn(false);
        when(associatedPerson.isRegisteredOnline()).thenReturn(true);
        ApplicationClientStatus investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.AWAITING_APPROVAL));
    }

    @Test
    public void shouldReturnApprovedIfPartyStatusIsExistingUserAndApproverHasApproved() throws Exception {
        OnboardingParty onboardingParty = new OnboardingParty(1, applicationId, gcmId);
        onboardingParty.setStatus(OnboardingPartyStatus.ExistingPanoramaOnlineUser);

        when(associatedPerson.isHasToAcceptTnC()).thenReturn(true);
        when(associatedPerson.isHasApprovedTnC()).thenReturn(true);
        when(associatedPerson.isRegisteredOnline()).thenReturn(true);
        ApplicationClientStatus investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.APPROVED));
    }

    @Test
    public void shouldReturnNotRegisteredIfPartyStatusIsExistingUserAndApproverHasNotRegistered() throws Exception {
        OnboardingParty onboardingParty = new OnboardingParty(1, applicationId, gcmId);
        onboardingParty.setStatus(OnboardingPartyStatus.ExistingPanoramaOnlineUser);

        when(associatedPerson.isHasToAcceptTnC()).thenReturn(true);
        when(associatedPerson.isHasApprovedTnC()).thenReturn(true);
        when(associatedPerson.isRegisteredOnline()).thenReturn(false);
        ApplicationClientStatus investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.NOT_REGISTERED));
    }

    @Test
    public void shouldReturnApprovedIfPartyStatusIsCustomerNotificationSentToExistOnlAndApproverHasApproved() throws Exception {
        OnboardingParty onboardingParty = new OnboardingParty(1, applicationId, gcmId);
        onboardingParty.setStatus(OnboardingPartyStatus.NotificationSentToExistingOnlineUser);

        when(associatedPerson.isHasToAcceptTnC()).thenReturn(true);
        when(associatedPerson.isHasApprovedTnC()).thenReturn(true);
        when(associatedPerson.isRegisteredOnline()).thenReturn(true); // This will be always true for westpac online customer
        ApplicationClientStatus investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.APPROVED));
    }

    @Test
    public void shouldReturnRegisteredIfPartyStatusIsCustomerNotificationSentToExistOnlForNonApprover() throws Exception {
        OnboardingParty onboardingParty = new OnboardingParty(1, applicationId, gcmId);
        onboardingParty.setStatus(OnboardingPartyStatus.NotificationSentToExistingOnlineUser);

        when(associatedPerson.isHasToAcceptTnC()).thenReturn(false);
        when(associatedPerson.isHasApprovedTnC()).thenReturn(false);
        when(associatedPerson.isRegisteredOnline()).thenReturn(true); // This will be always true for westpac online customer
        ApplicationClientStatus investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.REGISTERED));
    }

    @Test
    public void shouldReturnAwaitingApprovalIfPartyStatusIsCustomerNotificationSentToExistOnlAndApproverHasntApproved() throws Exception {
        OnboardingParty onboardingParty = new OnboardingParty(1, applicationId, gcmId);
        onboardingParty.setStatus(OnboardingPartyStatus.NotificationSentToExistingOnlineUser);

        when(associatedPerson.isHasToAcceptTnC()).thenReturn(true);
        when(associatedPerson.isHasApprovedTnC()).thenReturn(false);
        when(associatedPerson.isRegisteredOnline()).thenReturn(true); // This will be always true for westpac online customer
        ApplicationClientStatus investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.AWAITING_APPROVAL));
    }

    @Test
    public void shouldCallEmailStatusServiceIfStatusIsNotificationSent() throws Exception {
        OnboardingParty onboardingParty = new OnboardingParty(1, applicationId, gcmId);
        onboardingParty.setStatus(OnboardingPartyStatus.NotificationSent);
        when(emailStatusService.getApplicationClientStatus(applicationId, associatedPerson, gcmId)).thenReturn(ApplicationClientStatus.APPROVED);
        ApplicationClientStatus investorStatus = partyStatusService.getInvestorStatus(applicationId, associatedPerson, onboardingParty);
        assertThat(investorStatus, is(ApplicationClientStatus.APPROVED));
        verify(emailStatusService,times(1)).getApplicationClientStatus(applicationId, associatedPerson, gcmId);
    }

    @Test
    public void hasSendEmailFailedShouldThrowExceptionIfIfCannotFindGCMIdOftheInvestor() throws Exception {
        when(clientDetail.getGcmId()).thenThrow(NullPointerException.class);
        exception.expect(NotFoundException.class);
        exception.expectMessage("Error while fetching investor's status from OnboardingCommunication for the onboardingApplication "+applicationId);
        partyStatusService.getInvestorStatus(applicationId, associatedPerson, null);
    }
}
