package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus.*;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.*;
import static com.bt.nextgen.draftaccount.repository.ClientApplicationStatus.*;
import static com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountStatusServiceTest {

    @InjectMocks
    private AccountStatusServiceImpl accountStatusService;

    private ClientApplication application;

    @Mock
    private OnBoardingApplication onboardingApplication;

    private Map<String, ApplicationDocument> applicationDocumentMap;

    @Before
    public void setUp() throws Exception {
        application = new ClientApplication();
        applicationDocumentMap = new HashMap<>();
        application.setOnboardingApplication(onboardingApplication);
    }

    @Test
    public void shouldReturnProcessingStatusIfAvaloqStatusIsServerFailure() throws Exception {

        when(onboardingApplication.getStatus()).thenReturn(OnboardingApplicationStatus.ServerFailure);

        OnboardingApplicationStatus applicationStatus = accountStatusService.getApplicationStatus(application, applicationDocumentMap);
        assertThat(applicationStatus, is(OnboardingApplicationStatus.processing));
    }

    @Test
    public void shouldReturnProcessingStatusIfAvaloqStatusIsApplicationCreationInProgress() throws Exception {

        when(onboardingApplication.getStatus())
                .thenReturn(OnboardingApplicationStatus.ApplicationCreationInProgress);

        OnboardingApplicationStatus applicationStatus = accountStatusService.getApplicationStatus(application, applicationDocumentMap);
        assertThat(applicationStatus, is(OnboardingApplicationStatus.processing));
    }

    @Test
    public void shouldReturnFailedStatusIfAvaloqStatusIsApplicationCreationFailed() throws Exception {
        when(onboardingApplication.getStatus())
                .thenReturn(OnboardingApplicationStatus.ApplicationCreationFailed);

        OnboardingApplicationStatus applicationStatus = accountStatusService.getApplicationStatus(application, applicationDocumentMap);
        assertThat(applicationStatus, is(OnboardingApplicationStatus.failed));
    }

    @Test
    public void shouldReturnFailedStatusIfAvaloqStatusIsPartyCreationFailed() throws Exception {
        when(onboardingApplication.getStatus())
                .thenReturn(OnboardingApplicationStatus.PartyCreationFailed);

        OnboardingApplicationStatus applicationStatus = accountStatusService.getApplicationStatus(application, applicationDocumentMap);
        assertThat(applicationStatus, is(OnboardingApplicationStatus.failed));
    }

    @Test
    public void shouldReturnSameStatusIfApplicatoinStatusIsNotNullAndIsNotProcessingOrFailed() throws Exception {
        OnboardingApplicationStatus status = OnboardingApplicationStatus.processing;
        when(onboardingApplication.getStatus()).thenReturn(status);
        OnboardingApplicationStatus applicationStatus = accountStatusService.getApplicationStatus(application, applicationDocumentMap);
        assertThat(applicationStatus, is(status));
    }

    @Test
    public void shouldReturnClientApplicationStatusIfNoOnboardingApplicationIsAssociated() throws Exception {
        application.setOnboardingApplication(null);
        OnboardingApplicationStatus applicationStatus = accountStatusService.getApplicationStatus(application, applicationDocumentMap);
        assertThat(applicationStatus, is(OnboardingApplicationStatus.draft));
    }

    @Test
    public void shouldReturnProcessingStatusIfListOfInvestorIsEmpty() throws Exception {
        OnboardingApplicationStatus accountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(new ArrayList<TrackingDto.Investor>());
        assertThat(accountStatus, is(OnboardingApplicationStatus.processing));
    }

    @Test
    public void shouldReturnCorporateFEStatus_ifAccountTypeIsNewCorporateSmsf_andApplicationDocStatusIsAsicSubmission()
            throws Exception {
        ClientApplication newCorpSMSFApplication = getClientApplication(NEW_CORPORATE_SMSF);
        applicationDocumentMap = getApplicationDocumentMap(newCorpSMSFApplication, ASIC_SUBMISSION);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.awaitingApproval,
                newCorpSMSFApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfcorporateinProgress));
    }

    @Test
    public void shouldReturnCorporateFEStatus_ifAccountTypeIsNewCorporateSmsf_andApplicationDocStatusIsAsicRegistration()
            throws Exception {
        ClientApplication newCorpSMSFApplication = getClientApplication(NEW_CORPORATE_SMSF);
        applicationDocumentMap = getApplicationDocumentMap(newCorpSMSFApplication, ASIC_REGISTRATION);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.awaitingApproval,
                newCorpSMSFApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfcorporateinProgress));
    }

    @Test
    public void shouldReturnProcessingStatus_ifAccountTypeIsNewCorporateSmsf_andApplicationDocStatusIsDocumentsReceived()
            throws Exception {
        ClientApplication newCorpSMSFApplication = getClientApplication(NEW_CORPORATE_SMSF);
        applicationDocumentMap = getApplicationDocumentMap(newCorpSMSFApplication, DOCUMENTS_RECIEVED);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.awaitingApproval,
                newCorpSMSFApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.processing));
    }

    @Test
    public void shouldReturnAwaitingApprovalStatus_ifAccountTypeIsNewCorporateSmsf_andApplicationDocStatusIsPendAccept()
            throws Exception {
        ClientApplication newCorpSMSFApplication = getClientApplication(NEW_CORPORATE_SMSF);
        applicationDocumentMap = getApplicationDocumentMap(newCorpSMSFApplication, PEND_ACCEPT);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.awaitingApproval,
                newCorpSMSFApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.awaitingApproval));
    }

    @Test
    public void shouldReturnProcessingStatus_ifAccountTypeIsNewCorporateSmsf_andInvestorStausIsProcessing() throws Exception {
        ClientApplication newCorpSMSFApplication = getClientApplication(NEW_CORPORATE_SMSF);
        applicationDocumentMap = getApplicationDocumentMap(newCorpSMSFApplication, DOCUMENTS_RECIEVED);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.processing,
                newCorpSMSFApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.processing));
    }

    @Test
    public void shouldReturnActiveStatus_ifAccountTypeIsNewCorporateSmsf_andApplicationDocStatusIsDone() throws Exception {
        ClientApplication newCorpSMSFApplication = getClientApplication(NEW_CORPORATE_SMSF);
        applicationDocumentMap = getApplicationDocumentMap(newCorpSMSFApplication, DONE);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.active,
                newCorpSMSFApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.active));
    }

    @Test
    public void shouldReturnFEInProgressStatus_ifAccountTypeIsNewCorporateSmsf_andApplicationDocStatusIsAbrVerification()
            throws Exception {
        ClientApplication newCorpSMSFApplication = getClientApplication(NEW_CORPORATE_SMSF);
        applicationDocumentMap = getApplicationDocumentMap(newCorpSMSFApplication, ABR_VERIFICATION);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(OnboardingApplicationStatus.active,
                newCorpSMSFApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfinProgress));
    }

    private ClientApplication getClientApplication(final IClientApplicationForm.AccountType accountType) {
        ClientApplication application1 = mock(ClientApplication.class);
        when(application1.getClientApplicationForm()).thenReturn(mock(IClientApplicationForm.class));
        when(application1.getClientApplicationForm().getAccountType()).thenReturn(accountType);
        return application1;
    }

    private ClientApplication getClientApplication(final IClientApplicationForm.AccountType accountType, ClientApplicationStatus status, Boolean offline) {
        ClientApplication application = mock(ClientApplication.class);
        when(application.getClientApplicationForm()).thenReturn(mock(IClientApplicationForm.class));
        when(application.getClientApplicationForm().getAccountType()).thenReturn(accountType);
        when(application.getStatus()).thenReturn(status);
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.isOffline()).thenReturn(offline);
        application.setOnboardingApplication(onBoardingApplication);

        return application;
    }
    private ClientApplication getClientApplicationWithStatus(final IClientApplicationForm.AccountType accountType, ClientApplicationStatus status) {
        ClientApplication application = mock(ClientApplication.class);
        when(application.getClientApplicationForm()).thenReturn(mock(IClientApplicationForm.class));
        when(application.getClientApplicationForm().getAccountType()).thenReturn(accountType);
        when(application.getStatus()).thenReturn(status);
        return application;
    }

    private Map<String, ApplicationDocument> getApplicationDocumentMap(final ClientApplication clientApplication,
            final ApplicationStatus status) {
        OnBoardingApplication onBoardingApplication = new OnBoardingApplication(null, "ABS_ORDER_ID");
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        ApplicationDocument applicationDocument = mock(ApplicationDocument.class);
        when(applicationDocument.getAppState()).thenReturn(status);

        Map<String, ApplicationDocument> applicationDocumentMap = new HashMap<String, ApplicationDocument>();
        applicationDocumentMap.put("ABS_ORDER_ID", applicationDocument);

        return applicationDocumentMap;
    }

    private Map<String, ApplicationDocument> getApplicationDocumentMap(final ClientApplication clientApplication,
                                                                       final ApplicationStatus status, Boolean offline) {

        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn("ABS_ORDER_ID");
        when(onBoardingApplication.isOffline()).thenReturn(false);
        clientApplication.setOnboardingApplication(onBoardingApplication);

        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        when(clientApplication.getOnboardingApplication().isOffline()).thenReturn(offline);
        ApplicationDocument applicationDocument = mock(ApplicationDocument.class);
        when(applicationDocument.getAppState()).thenReturn(status);

        Map<String, ApplicationDocument> applicationDocumentMap = new HashMap<String, ApplicationDocument>();
        applicationDocumentMap.put("ABS_ORDER_ID", applicationDocument);

        return applicationDocumentMap;
    }

    @Test
    public void shouldReturn_Active_IfAllInvestorsApproved_and_avaloqStateis_AWAITING_DOCUMENTS_for_NEWSMSF() throws Exception {
        TrackingDto.Investor mockedInvestor = createMockedInvestorWithStatus(APPROVED);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestor);

        ClientApplication newSMSFApplication = getClientApplication(NEW_INDIVIDUAL_SMSF);
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(newSMSFApplication,
                DONE);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, newSMSFApplication,
                applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.active));
    }

    private OnboardingApplicationStatus createApplicationsWithStatus(ApplicationClientStatus inv1Status,ApplicationClientStatus inv2Status, ClientApplicationStatus clientApplicationStatus, ApplicationStatus avaloqStatus, IClientApplicationForm.AccountType accountType, Boolean offline ){
        TrackingDto.Investor mockedInvestor1 = createMockedInvestorWithStatus(inv1Status);
        TrackingDto.Investor mockedInvestor2 = createMockedInvestorWithStatus(inv2Status);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestor1, mockedInvestor2);

        ClientApplication clientApplication = getClientApplication(accountType, clientApplicationStatus, offline);
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(clientApplication,avaloqStatus, offline);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        investorAccountStatus = getOfflineUploadInvestorStatus(clientApplication, investorAccountStatus);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, clientApplication,
                applicationDocumentMap);
        return accountStatus;
    }

    @Test
    public void shouldReturn_SMSFINPROGRESS_When_Doc_Uploaded_and_avaloqStateis_AwaitingDocuments_for_NEW_INDIVIDUAL_SMSF_OFFLINE_BothInvestorNotRegistered() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(NOT_REGISTERED, NOT_REGISTERED, docuploaded, AWAITING_DOCUMENTS, NEW_INDIVIDUAL_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfinProgress));
    }

    @Test
    public void shouldReturn_SMSFINPROGRESS_When_Doc_Uploaded_and_avaloqStateis_AwaitingDocuments_for_NEW_INDIVIDUAL_SMSF_OFFLINE_OneInvestorRegistered() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(NOT_REGISTERED, REGISTERED, docuploaded, AWAITING_DOCUMENTS, NEW_INDIVIDUAL_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfinProgress));
    }

    @Test
    public void shouldReturn_SMSFINPROGRESS_When_Doc_Uploaded_and_avaloqStateis_AwaitingDocuments_for_NEW_INDIVIDUAL_SMSF_OFFLINE_BothInvestorRegistered() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(REGISTERED, REGISTERED, docuploaded, AWAITING_DOCUMENTS, NEW_INDIVIDUAL_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfinProgress));
    }

    @Test
    public void shouldReturn_SMSFINPROGRESS_When_Doc_Uploaded_and_avaloqStateis_AwaitingDocuments_for_NEW_INDIVIDUAL_SMSF_OFFLINE_BothInvestorApproved() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(APPROVED, APPROVED, docuploaded, AWAITING_DOCUMENTS, NEW_INDIVIDUAL_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfinProgress));
    }

    @Test
     public void shouldReturn_FILEUPLOADED_When_Doc_Uploaded_and_avaloqStateis_PendingAcceptance_for_NEW_INDIVIDUAL_SMSF_OFFLINE() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(PROCESSING, PROCESSING, docuploaded, PEND_ACCEPT, NEW_INDIVIDUAL_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.offlineDocUpload));
    }

    @Test
    public void shouldReturn_AwaitingApprovalOffline_When_Processing_and_avaloqStateis_PendingAcceptance_for_NEW_INDIVIDUAL_SMSF_OFFLINE() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(PROCESSING, PROCESSING, processing, PEND_ACCEPT, NEW_INDIVIDUAL_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.awaitingApprovalOffline));
    }

    @Test
    public void shouldReturn_AwaitingApprovalOnline_When_avaloqStateis_AwaitingDocuments_for_NEW_INDIVIDUAL_SMSF_ONLINE() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(AWAITING_APPROVAL, AWAITING_APPROVAL, processing, PEND_ACCEPT, NEW_INDIVIDUAL_SMSF, false );
        assertThat(accountStatus, is(OnboardingApplicationStatus.awaitingApproval));
    }

    @Test
    public void shouldReturn_SMSFINPROGRESS_When_Doc_Uploaded_and_avaloqState_is_DocumentsRecieved_for_NEW_CORPORATE_SMSF_OFFLINE() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(AWAITING_APPROVAL, AWAITING_APPROVAL, docuploaded, DOCUMENTS_RECIEVED, NEW_CORPORATE_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfinProgress));
    }

    @Test
    public void shouldReturn_SMSFINPROGRESS_When_Doc_Uploaded_and_avaloqState_is_DocumentsRecieved_for_NEW_CORPORATE_SMSF_OFFLINE_BothInvestorNotRegistered() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(NOT_REGISTERED, NOT_REGISTERED, docuploaded, DOCUMENTS_RECIEVED, NEW_CORPORATE_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfinProgress));
    }

    @Test
    public void shouldReturn_SMSFINPROGRESS_When_Doc_Uploaded_and_avaloqState_is_DocumentsRecieved_for_NEW_CORPORATE_SMSF_OFFLINE_OneInvestorNotRegistered() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(REGISTERED, NOT_REGISTERED, docuploaded, DOCUMENTS_RECIEVED, NEW_CORPORATE_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfinProgress));
    }


    @Test
    public void shouldReturn_AWAITING_OFFLINE_When_avaloqState_is_PendingAcceptance_for_NEW_CORPORATE_SMSF_OFFLINE() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(PROCESSING, PROCESSING, processing, PEND_ACCEPT, NEW_CORPORATE_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.awaitingApprovalOffline));
    }

    @Test
    public void shouldReturn_SMSFCORP_INPROGRESS_When_avaloqState_is_ASICREGISTRATION_for_NEW_CORPORATE_SMSF_OFFLINE() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(PROCESSING, PROCESSING, processing, ASIC_REGISTRATION, NEW_CORPORATE_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfcorporateinProgress));
    }

    @Test
    public void shouldReturn_SMSFCORP_INPROGRESS_When_avaloqState_is_ASICSUBMISSION_for_NEW_CORPORATE_SMSF_OFFLINE() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(PROCESSING, PROCESSING, processing, ASIC_SUBMISSION, NEW_CORPORATE_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfcorporateinProgress));
    }

    @Test
    public void shouldReturn_ACTIVE_When_avaloqState_is_DONE_for_NEW_CORPORATE_SMSF_OFFLINE() throws Exception {
        OnboardingApplicationStatus accountStatus = createApplicationsWithStatus(NOT_REGISTERED, NOT_REGISTERED, active, DONE, NEW_CORPORATE_SMSF, true );
        assertThat(accountStatus, is(OnboardingApplicationStatus.active));
    }

    @Test
    public void shouldReturn_AwaitingApprovalOfflineIfPendAcceptOfflineApplication() throws Exception {
        TrackingDto.Investor mockedInvestor = createMockedInvestorWithStatus(PROCESSING);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestor);

        ClientApplication clientApplication = getClientApplication(SUPER_ACCUMULATION); // any
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(clientApplication, PEND_ACCEPT);
        clientApplication.getOnboardingApplication().setStatus(OnboardingApplicationStatus.ApplicationCreationInProgress);
        clientApplication.getOnboardingApplication().setOffline(true);
        clientApplication.setClientApplicationsStatus(processing);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, clientApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.awaitingApprovalOffline));
    }

    @Test
    public void shouldReturn_DocUploaded_IfPendAcceptOfflineApplication() throws Exception {
        TrackingDto.Investor mockedInvestor = createMockedInvestorWithStatus(PROCESSING);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestor);

        ClientApplication clientApplication = getClientApplicationWithStatus(INDIVIDUAL, docuploaded); // any
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(clientApplication, PEND_ACCEPT);
        clientApplication.getOnboardingApplication().setStatus(OnboardingApplicationStatus.ApplicationCreationInProgress);
        clientApplication.getOnboardingApplication().setOffline(true);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, clientApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.offlineDocUpload));
    }

    @Test
    public void shouldReturn_ACTIVE_IfOfflineApplication_isActive() throws Exception {
        TrackingDto.Investor mockedInvestor = createMockedInvestorWithStatus(PROCESSING);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestor);

        ClientApplication clientApplication = getClientApplicationWithStatus(INDIVIDUAL, docuploaded); // any
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(clientApplication, DONE);
        clientApplication.getOnboardingApplication().setOffline(true);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, clientApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.active));
    }

    @Test
    public void shouldReturn_AwaitingApprovalOnlineIfPendAcceptOnlineApplication() throws Exception {
        TrackingDto.Investor mockedInvestor = createMockedInvestorWithStatus(AWAITING_APPROVAL);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestor);

        ClientApplication clientApplication = getClientApplication(SUPER_ACCUMULATION); // any
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(clientApplication, PEND_ACCEPT);
        clientApplication.getOnboardingApplication().setStatus(OnboardingApplicationStatus.ApplicationCreationInProgress);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, clientApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.awaitingApproval));
    }

    @Test
    public void shouldReturn_ActiveIfDoneOfflineApplication() throws Exception {
        TrackingDto.Investor mockedInvestor = createMockedInvestorWithStatus(AWAITING_APPROVAL);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestor);

        ClientApplication clientApplication = getClientApplication(SUPER_ACCUMULATION); // any
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(clientApplication, DONE);
        clientApplication.getOnboardingApplication().setStatus(OnboardingApplicationStatus.ApplicationCreationInProgress);
        clientApplication.getOnboardingApplication().setOffline(true);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, clientApplication, applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.active));
        verify(clientApplication,times(1)).markActive();
    }

    @Test
    public void shouldReturn_SMSFINProgress_IfAllInvestorsApproved_and_avaloqStateis_notDone_for_NEWSMSF() throws Exception {
        TrackingDto.Investor mockedInvestor = createMockedInvestorWithStatus(APPROVED);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestor);

        ClientApplication newSMSFApplication = getClientApplication(NEW_INDIVIDUAL_SMSF);
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(newSMSFApplication,
                HELD_ABR_SUBMITTED);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, newSMSFApplication,
                applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfinProgress));
    }

    @Test
    public void shouldReturn_SMSFCorporateInProgress_IfInvestorsProcessing_and_avaloqStateIs_ASIC_REGISTRATION_for_NEWCORPORATESMSF()
            throws Exception {
        TrackingDto.Investor unapprovedInvestorOne = createMockedInvestorWithStatus(PROCESSING);
        TrackingDto.Investor unapprovedInvestorTwo = createMockedInvestorWithStatus(PROCESSING);
        List<TrackingDto.Investor> investors = Arrays.asList(unapprovedInvestorOne, unapprovedInvestorTwo);

        ClientApplication newCorporateSMSFApplication = getClientApplication(
                NEW_CORPORATE_SMSF);
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(newCorporateSMSFApplication,
                ASIC_REGISTRATION);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, newCorporateSMSFApplication,
                applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfcorporateinProgress));
    }

    @Test
    public void shouldReturn_SMSFCorporateInProgress_IfInvestorsProcessing_and_avaloqStateis_ASIC_SUBMISSION_for_NEWCORPORATESMSF()
            throws Exception {
        TrackingDto.Investor unapprovedInvestorOne = createMockedInvestorWithStatus(PROCESSING);
        TrackingDto.Investor unapprovedInvestorTwo = createMockedInvestorWithStatus(PROCESSING);
        List<TrackingDto.Investor> investors = Arrays.asList(unapprovedInvestorOne, unapprovedInvestorTwo);

        ClientApplication newCorporateSMSFApplication = getClientApplication(
                NEW_CORPORATE_SMSF);
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(newCorporateSMSFApplication,
                ASIC_SUBMISSION);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, newCorporateSMSFApplication,
                applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfcorporateinProgress));
    }

    @Test
    public void shouldReturn_AwaitingApproval_IfInvestorsAwaitingApproval_and_avaloqStateis_PEND_ACCEPT_for_NEWCORPORATESMSF()
            throws Exception {
        TrackingDto.Investor unapprovedInvestorOne = createMockedInvestorWithStatus(AWAITING_APPROVAL);
        TrackingDto.Investor unapprovedInvestorTwo = createMockedInvestorWithStatus(AWAITING_APPROVAL);
        List<TrackingDto.Investor> investors = Arrays.asList(unapprovedInvestorOne, unapprovedInvestorTwo);

        ClientApplication newCorporateSMSFApplication = getClientApplication(
                NEW_CORPORATE_SMSF);
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(newCorporateSMSFApplication,
                PEND_ACCEPT);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, newCorporateSMSFApplication,
                applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.awaitingApproval));
    }

    @Test
    public void shouldReturn_Processing_IfDocumentIsNull_for_NEWCORPORATESMSF() throws Exception {
        TrackingDto.Investor unapprovedInvestorOne = createMockedInvestorWithStatus(PROCESSING);
        TrackingDto.Investor unapprovedInvestorTwo = createMockedInvestorWithStatus(PROCESSING);
        List<TrackingDto.Investor> investors = Arrays.asList(unapprovedInvestorOne, unapprovedInvestorTwo);

        ClientApplication newCorporateSMSFApplication = getClientApplication(
                NEW_CORPORATE_SMSF);

        OnBoardingApplication onBoardingApplication = new OnBoardingApplication(null, "AVALOQ_ORDER_ID");
        when(newCorporateSMSFApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        ApplicationDocument applicationDocument = mock(ApplicationDocument.class);
        when(applicationDocument.getAppState()).thenReturn(null);
        Map<String, ApplicationDocument> applicationDocumentMap = new HashMap<String, ApplicationDocument>();

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, newCorporateSMSFApplication,
                applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.processing));
    }

    @Test
    public void shouldReturn_SMSFInProgress_IfInvestorsApproved_and_avaloqStateIs_HELD_ABR_VERIFICATION_for_NEWCORPORATESMSF()
            throws Exception {
        TrackingDto.Investor unapprovedInvestorOne = createMockedInvestorWithStatus(APPROVED);
        TrackingDto.Investor unapprovedInvestorTwo = createMockedInvestorWithStatus(APPROVED);
        List<TrackingDto.Investor> investors = Arrays.asList(unapprovedInvestorOne, unapprovedInvestorTwo);

        ClientApplication newCorporateSMSFApplication = getClientApplication(
                NEW_CORPORATE_SMSF);
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(newCorporateSMSFApplication,
                HELD_ABR_VERIFICATION);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, newCorporateSMSFApplication,
                applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfinProgress));
    }

    @Test
    public void shouldReturn_Active_IfAllInvestorsApproved_and_avaloqStateis_notDone_for_anynonNewindividualSMSFAccount()
            throws Exception {
        TrackingDto.Investor mockedInvestor = createMockedInvestorWithStatus(APPROVED);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestor);

        ClientApplication individualSMSFApplication = getClientApplication(INDIVIDUAL_SMSF);
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(individualSMSFApplication,
                HELD_ABR_SUBMITTED);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, individualSMSFApplication,
                applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.active));
    }

    @Test
    public void shouldReturnFailedEmailIfAnyApproverHasFailedEmailStatus() throws Exception {
        TrackingDto.Investor mockedApprover1 = createMockedInvestorWithStatus(FAILED_EMAIL);
        TrackingDto.Investor mockedApprover2 = createMockedInvestorWithStatus(APPROVED);
        OnboardingApplicationStatus accountStatus = accountStatusService
                .getAccountStatusByInvestorsStatuses(Arrays.asList(mockedApprover1, mockedApprover2));
        assertThat(accountStatus, is(OnboardingApplicationStatus.failedEmail));
    }

    @Test
    public void shouldReturnAwaitingApprovalIfStatusIsAnyOfApprovedOrNotRegisteredOrAwaitingApproval() throws Exception {
        TrackingDto.Investor investorWithApprovedStatus = createMockedInvestorWithStatus(APPROVED);
        TrackingDto.Investor investorWithNotRegStatus = createMockedInvestorWithStatus(NOT_REGISTERED);
        TrackingDto.Investor investorWithAwaitingApprovalStatus = createMockedInvestorWithStatus(
                AWAITING_APPROVAL);
        List<TrackingDto.Investor> investors = Arrays.asList(investorWithApprovedStatus, investorWithNotRegStatus,
                investorWithAwaitingApprovalStatus);

        ClientApplication newSMSFApplication = getClientApplication(NEW_INDIVIDUAL_SMSF);
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(newSMSFApplication,
                AWAITING_DOCUMENTS);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, newSMSFApplication,
                applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.awaitingApproval));
    }

    @Test
    public void shouldReturnProcessingIfStatusOfApproverIsTechnicalErrorForNewIndividualSMSF() throws Exception {
        TrackingDto.Investor mockedInvestorWithStatus = createMockedInvestorWithStatus(TECHNICAL_ERROR);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestorWithStatus);

        ClientApplication newSMSFApplication = getClientApplication(NEW_INDIVIDUAL_SMSF);
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(newSMSFApplication,
                AWAITING_DOCUMENTS);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, newSMSFApplication,
                applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.processing));
    }

    @Test
    public void shouldReturnProcessingIfStatusOfApproverIsTechnicalError() throws Exception {
        TrackingDto.Investor mockedInvestorWithStatus = createMockedInvestorWithStatus(TECHNICAL_ERROR);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestorWithStatus);
        OnboardingApplicationStatus accountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        assertThat(accountStatus, is(OnboardingApplicationStatus.processing));
    }

    @Test
    public void shouldReturnProcessingIfAnyOfApproverStatusIsProcessing() throws Exception {
        TrackingDto.Investor investorWithApprovedStatus = createMockedInvestorWithStatus(APPROVED);
        TrackingDto.Investor investorWithProcessingStatus = createMockedInvestorWithStatus(PROCESSING);
        List<TrackingDto.Investor> investors = Arrays.asList(investorWithApprovedStatus, investorWithProcessingStatus);
        OnboardingApplicationStatus accountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        assertThat(accountStatus, is(OnboardingApplicationStatus.processing));
    }

    @Test
    public void shouldReturnSMSFFundEstablishmentInProgressIfAllApproverStatusIsApproved() throws Exception {
        TrackingDto.Investor firstInvestorWithApprovedStatus = createMockedInvestorWithStatus(APPROVED);
        TrackingDto.Investor secondInvestorWithApprovedStatus = createMockedInvestorWithStatus(APPROVED);
        List<TrackingDto.Investor> investors = Arrays.asList(firstInvestorWithApprovedStatus, secondInvestorWithApprovedStatus);

        ClientApplication newSMSFApplication = getClientApplication(NEW_INDIVIDUAL_SMSF);
        Map<String, ApplicationDocument> applicationDocumentMap = getApplicationDocumentMap(newSMSFApplication,
                HELD_READY_FOR_ABR);

        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, newSMSFApplication,
                applicationDocumentMap);
        assertThat(accountStatus, is(OnboardingApplicationStatus.smsfinProgress));
    }

    @Test
    public void shouldReturnProcessingByDefault() throws Exception {
        TrackingDto.Investor investorWithApprovedStatus = createMockedInvestorWithStatus(APPROVED);
        TrackingDto.Investor investorWithNotRegStatus = createMockedInvestorWithStatus(REGISTERED);
        TrackingDto.Investor investorWithAwaitingApprovalStatus = createMockedInvestorWithStatus(
                AWAITING_APPROVAL);
        List<TrackingDto.Investor> investors = Arrays.asList(investorWithApprovedStatus, investorWithNotRegStatus,
                investorWithAwaitingApprovalStatus);
        OnboardingApplicationStatus accountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        assertThat(accountStatus, is(OnboardingApplicationStatus.processing));
    }

    @Test
    public void shouldReturn_Processing_whenOfflineApplication_hasNoApplicationDocument() throws Exception {
        TrackingDto.Investor mockedInvestor = createMockedInvestorWithStatus(PROCESSING);
        List<TrackingDto.Investor> investors = Arrays.asList(mockedInvestor);

        ClientApplication clientApplication = getClientApplication(INDIVIDUAL);
        onboardingApplication = getOnboardingApplication(null);
        when(clientApplication.getOnboardingApplication()).thenReturn(onboardingApplication);
        OnboardingApplicationStatus investorAccountStatus = accountStatusService.getAccountStatusByInvestorsStatuses(investors);
        OnboardingApplicationStatus accountStatus = accountStatusService.getStatusForAccountType(investorAccountStatus, clientApplication, new HashMap<String, ApplicationDocument>());
        assertThat(accountStatus, is(OnboardingApplicationStatus.processing));
    }

    private OnBoardingApplication getOnboardingApplication(String avaloqOrderId) {
        OnBoardingApplication onBoardingApplication = Mockito.mock(OnBoardingApplication.class);
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn(avaloqOrderId);
        return onBoardingApplication;
    }


    private OnboardingApplicationStatus getOfflineUploadInvestorStatus(ClientApplication application, OnboardingApplicationStatus applicationStatus) {
        if (application.getOnboardingApplication().isOffline() && application.getStatus().equals(docuploaded)) {
            return OnboardingApplicationStatus.active;
        }
        return applicationStatus;
    }


    private TrackingDto.Investor createMockedInvestorWithStatus(ApplicationClientStatus status) {
        TrackingDto.Investor mockedInvestor = Mockito.mock(TrackingDto.Investor.class);
        when(mockedInvestor.getStatus()).thenReturn(status);
        return mockedInvestor;
    }


}
