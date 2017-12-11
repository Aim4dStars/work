package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.FundEstablishmentDto;
import com.bt.nextgen.api.draftaccount.model.IndividualOrJointApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.TransitionStateDto;
import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IShareholderAndMembersForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.AnswerTypeEnum;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingAccount;
import com.bt.nextgen.core.repository.OnboardingAccountRepository;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.btfin.panorama.service.exception.ServiceException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF;
import static com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm.AccountType.NEW_INDIVIDUAL_SMSF;
import static com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus.*;
import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationDetailsDtoServiceImplTest {

    private static final String ADVISER_ID = "MY_ADVISER_ID";
    public static final String MY_PRODUCT_ID = "MY_PRODUCT_ID";
    public static final String ONBOARDING_KEY = "666";

    @Mock
    private PermittedClientApplicationRepository permittedClientApplicationRepository;

    @Mock
    private ClientApplicationRepository clientApplicationRepositoryWithoutPermissions;

    @Mock
    private OnboardingAccountRepository onboardingAccountRepository;

    @Mock
    BrokerIntegrationService brokerIntegrationService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private PdsService pdsService;

    @Mock
    private ClientApplicationDetailsDtoConverterService clientApplicationDetailsDtoConverterService;


    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AccountsPendingApprovalService accountsPendingApprovalService;

    @Mock
    private AccActivationIntegrationService accActivationIntegrationService;

    @Mock
    private ViewClientApplicationDetailsService viewClientApplicationDetailsService;

    @Mock
    private UserProfileService userProfileService;

    UserProfile activeProfile;



    @Rule
    public ExpectedException exception = ExpectedException.none();

    @InjectMocks
    private ClientApplicationDetailsDtoServiceImpl clientApplicationDetailsDtoService;

    private OnboardingAccount onboardingAccount;
    private String accountNumber;
    private String encodedAccountNumber;

    @Before
    public void setUp() throws Exception {
        encodedAccountNumber = "CA47746B0E93245D12693DD3A6694332307C9798C09DF784";
        accountNumber = "account-number";
        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");

    }

    @Test
    public void findByAccountNumber_ReturnsClientApplicationDetailsWithPdsUrlAndReferenceNumber() {

        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(Long.valueOf(ONBOARDING_KEY));
        when(onBoardingApplication.getKey()).thenReturn(onboardingApplicationKey);
        ClientApplication clientApplication = createClientApplication(1L);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);

        onboardingAccount = new OnboardingAccount(123L, onboardingApplicationKey);
        onboardingAccount.setAccountNumber(accountNumber);
        when(onboardingAccountRepository.findByAccountNumber(any(String.class))).thenReturn(onboardingAccount);

        String expectedPdsUrl = "http://some.url";

        when(pdsService.getUrl(eq(ProductKey.valueOf(MY_PRODUCT_ID)), eq(BrokerKey.valueOf(ADVISER_ID)), any(ServiceErrors.class))).thenReturn(expectedPdsUrl);
        when(viewClientApplicationDetailsService.viewClientApplicationByAccountNumber(eq(accountNumber), any(ServiceErrors.class))).thenReturn(getIndividualOrJointApplicationDetailsDto(AccountStatus.PEND_OPN, true));
        when(clientApplicationRepositoryWithoutPermissions.findByOnboardingApplicationKey(any(OnboardingApplicationKey.class), any(Collection.class))).thenReturn(clientApplication);

        IndividualOrJointApplicationDetailsDto result = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoService.findByAccountNumber(accountNumber, new FailFastErrorsImpl());
        verify(viewClientApplicationDetailsService, times(1)).viewClientApplicationByAccountNumber(eq(accountNumber),any(ServiceErrors.class));
        assertNotNull(result);
        assertThat(result.getReferenceNumber(), is("R000000001"));
        assertThat(EncodedString.toPlainText(result.getOnboardingApplicationKey()), is(ONBOARDING_KEY));
        assertThat(result.getPdsUrl(), is(expectedPdsUrl));

    }

    @Test(expected = ServiceException.class)
    public void findByAccountNumber_throwsExceptionIfAccountStatusIsDiscarded() {
        when(viewClientApplicationDetailsService.viewClientApplicationByAccountNumber(eq(accountNumber), any(ServiceErrors.class))).thenReturn(getIndividualOrJointApplicationDetailsDto(AccountStatus.DISCARD, true));
        clientApplicationDetailsDtoService.findByAccountNumber(accountNumber, new FailFastErrorsImpl());
    }

    @Test
    public void findByClientApplicationId_WhenAccountNumberIsNotNull() {
        long id = 123L;

        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(666);
        when(onBoardingApplication.getKey()).thenReturn(onboardingApplicationKey);
        ClientApplication clientApplication = createClientApplication(id);
        IClientApplicationForm clientApplicationForm = getClientApplicationFormWithMajorShareholderFlag(IClientApplicationForm.AccountType.COMPANY,AnswerTypeEnum.NO);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        when(clientApplication.getClientApplicationForm()).thenReturn(clientApplicationForm);

        when(permittedClientApplicationRepository.find(eq(id))).thenReturn(clientApplication);

        onboardingAccount = new OnboardingAccount(123L, onboardingApplicationKey);
        onboardingAccount.setAccountNumber(accountNumber);
        when(onboardingAccountRepository.findByOnboardingApplicationId(eq(onboardingApplicationKey))).thenReturn(onboardingAccount);
        when(viewClientApplicationDetailsService.viewClientApplicationByAccountNumber(eq(accountNumber), any(ServiceErrors.class))).thenReturn(getIndividualOrJointApplicationDetailsDto(AccountStatus.PEND_OPN, false));;
        IndividualOrJointApplicationDetailsDto result = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoService
            .findByClientApplicationId(id, new FailFastErrorsImpl());
        verify(viewClientApplicationDetailsService, times(0)).viewClientApplicationById(any(Long.class), any(ServiceErrors.class));
        verify(viewClientApplicationDetailsService, times(0)).viewOnlineClientApplicationByAccountNumbers(any(List.class), any(ServiceErrors.class));
        assertNotNull(result);
        assertNull(result.getOnboardingApplicationKey());
        assertThat(result.getReferenceNumber(), is("R000000123"));
    }

    @Test
    public void findByClientApplicationId_WithMajorSharehoderFlag() {
        long id = 123L;

        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(666);
        when(onBoardingApplication.getKey()).thenReturn(onboardingApplicationKey);
        ClientApplication clientApplication = mock(ClientApplication.class);
        IClientApplicationForm clientApplicationForm = getClientApplicationFormWithMajorShareholderFlag(IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF,AnswerTypeEnum.YES);
        when(clientApplication.getClientApplicationForm()).thenReturn(clientApplicationForm);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);

        when(permittedClientApplicationRepository.find(eq(id))).thenReturn(clientApplication);

        onboardingAccount = new OnboardingAccount(123L, onboardingApplicationKey);
        onboardingAccount.setAccountNumber(accountNumber);
        when(onboardingAccountRepository.findByOnboardingApplicationId(eq(onboardingApplicationKey))).thenReturn(onboardingAccount);

        when(viewClientApplicationDetailsService.viewClientApplicationByAccountNumber(eq(accountNumber), any(ServiceErrors.class))).thenReturn(getIndividualOrJointApplicationDetailsDto(AccountStatus.PEND_OPN, false));;

        IndividualOrJointApplicationDetailsDto result = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoService
                .findByClientApplicationId(id, new FailFastErrorsImpl());
        verify(viewClientApplicationDetailsService, times(0)).viewClientApplicationById(any(Long.class), any(ServiceErrors.class));
        assertThat(result.getMajorShareholder(), is("yes"));
        assertNotNull(result);
        assertNull(result.getOnboardingApplicationKey());
    }

    @Test
    public void findByClientApplicationId_WhenAccountNumberIsNull() {
        long id = 123L;

        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(666);
        when(onBoardingApplication.getKey()).thenReturn(onboardingApplicationKey);
        ClientApplication clientApplication = createClientApplication(id);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);

        when(permittedClientApplicationRepository.find(eq(id))).thenReturn(clientApplication);

        IndividualOrJointApplicationDetailsDto individualOrJointApplicationDetailsDto = getIndividualOrJointApplicationDetailsDto(AccountStatus.PEND_OPN, false);

        when(clientApplicationDetailsDtoConverterService.convert(eq(clientApplication), any(ServiceErrors.class)))
            .thenReturn(individualOrJointApplicationDetailsDto);

        IndividualOrJointApplicationDetailsDto result = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoService
            .findByClientApplicationId(id, new FailFastErrorsImpl());
        verify(viewClientApplicationDetailsService, times(0))
            .viewClientApplicationByAccountNumber(any(String.class), any(ServiceErrors.class));
        assertNotNull(result);
        assertNotNull(result.getAdviser());
    }

    @Test
    public void findByClientApplicationId_shouldReturnNullIfTheDraftAccountIsNull() {
        long id = 123L;
        when(permittedClientApplicationRepository.find(eq(id))).thenReturn(null);

        ClientApplicationDetailsDto result = clientApplicationDetailsDtoService.findByClientApplicationId(id, new FailFastErrorsImpl());
        verify(viewClientApplicationDetailsService, times(0))
            .viewClientApplicationByAccountNumber(any(String.class), any(ServiceErrors.class));
        verify(clientApplicationDetailsDtoConverterService, times(0)).convert(any(ClientApplication.class), any(ServiceErrors.class));
        assertNull(result);
    }

    @Test
    public void findByClientApplicationId_shouldReturnNullIfTheOnboardingApplicationIsNull() {
        long id = 123L;
        ClientApplication clientApplication = createClientApplication(id);
        when(clientApplication.getOnboardingApplication()).thenReturn(null);

        when(permittedClientApplicationRepository.find(eq(id))).thenReturn(clientApplication);

        ClientApplicationDetailsDto result = clientApplicationDetailsDtoService.findByClientApplicationId(id, new FailFastErrorsImpl());
        verify(viewClientApplicationDetailsService, times(0))
            .viewClientApplicationByAccountNumber(any(String.class), any(ServiceErrors.class));
        verify(clientApplicationDetailsDtoConverterService, times(0)).convert(any(ClientApplication.class), any(ServiceErrors.class));
        assertNull(result);
    }


    @Test(expected = BadRequestException.class)
    public void findOne_shouldThrowExceptionIfNoAccountsForCurrentUser() {
        when(accountsPendingApprovalService.getUserAccountsPendingApprovals(any(ServiceErrors.class)))
            .thenReturn(new ArrayList<WrapAccount>());
        clientApplicationDetailsDtoService.findOne(new ServiceErrorsImpl());
    }

    @Test
    public void findOne_shouldThrowExceptionIfAccountsExistsForCurrentUser() {
        WrapAccount wrapAccount = mock(WrapAccount.class);
        when(wrapAccount.getAccountNumber()).thenReturn(accountNumber);
        when(accountsPendingApprovalService.getUserAccountsPendingApprovals(any(ServiceErrors.class)))
            .thenReturn(Arrays.asList(wrapAccount));

        Long id = 1l;
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        OnboardingApplicationKey onboardingApplicationKey = OnboardingApplicationKey.valueOf(666);
        when(onBoardingApplication.getKey()).thenReturn(onboardingApplicationKey);
        ClientApplication clientApplication = createClientApplication(id);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);

        when(permittedClientApplicationRepository.find(eq(id))).thenReturn(clientApplication);


        onboardingAccount = new OnboardingAccount(123L, onboardingApplicationKey);
        onboardingAccount.setAccountNumber(accountNumber);
        when(onboardingAccountRepository.findByAccountNumber(any(String.class))).thenReturn(onboardingAccount);
        String expectedPdsUrl = "http://some.url";
        when(pdsService.getUrl(eq(ProductKey.valueOf(MY_PRODUCT_ID)), eq(BrokerKey.valueOf(ADVISER_ID)), any(ServiceErrors.class))).thenReturn(expectedPdsUrl);
        when(viewClientApplicationDetailsService.viewClientApplicationByAccountNumber(eq(accountNumber), any(ServiceErrors.class))).thenReturn(getIndividualOrJointApplicationDetailsDto(AccountStatus.PEND_OPN, true));
        when(clientApplicationRepositoryWithoutPermissions.findByOnboardingApplicationKey(any(OnboardingApplicationKey.class), any(Collection.class))).thenReturn(clientApplication);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        IndividualOrJointApplicationDetailsDto result = (IndividualOrJointApplicationDetailsDto) clientApplicationDetailsDtoService
            .findByAccountNumber(accountNumber, new FailFastErrorsImpl());
        assertNotNull(result);
        assertThat(result.getReferenceNumber(), is("R000000001"));
        assertThat(result.getPdsUrl(), is(expectedPdsUrl));
    }

    @Test
    public void findFundEstablishmentStatus_ByClientApplicationId_WhenStatusIs_AwaitingDocuments() throws java.text.ParseException {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Long cAppNo = 1234L;
        String signedDate = "09/10/2015";
        String submitDate = "10/10/2015";

        setupFundEstablishmentStates(NEW_INDIVIDUAL_SMSF, AWAITING_DOCUMENTS, signedDate, submitDate, null, null, null, null, cAppNo,
            serviceErrors);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        FundEstablishmentDto fundEstablishmentDto = clientApplicationDetailsDtoService
            .findFundEstablishmentStatusByClientApplicationId(cAppNo, encodedAccountNumber, serviceErrors);

        assertThat(serviceErrors.hasErrors(), is(false));
        assertThat(fundEstablishmentDto.getStatus(), is("DocumentVerificationInProgress"));
    }

    @Test
    public void findFundEstablishmentStatus_ByClientApplicationId_WhenStatusIs_HeldABRSubmitted() throws java.text.ParseException {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Long cAppNo = 1234L;
        String signedDate = "09/10/2015";
        String submitDate = "10/10/2015";

        setupFundEstablishmentStates(NEW_INDIVIDUAL_SMSF, HELD_ABR_SUBMITTED, signedDate, submitDate, null, null, null, null, cAppNo,
                serviceErrors);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        FundEstablishmentDto fundEstablishmentDto = clientApplicationDetailsDtoService
            .findFundEstablishmentStatusByClientApplicationId(cAppNo, encodedAccountNumber, serviceErrors);

        assertThat(serviceErrors.hasErrors(), is(false));
        assertThat(fundEstablishmentDto.getStatus(), is("ATOInProgress"));
    }

    @Test
    public void findFundEstablishmentStatus_ByClientApplicationId_WhenStatusIs_ABRSubmitted() throws java.text.ParseException {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Long cAppNo = 1234L;
        String signedDate = "09/10/2015";
        String submitDate = "10/10/2015";

        setupFundEstablishmentStates(NEW_INDIVIDUAL_SMSF, ABR_SUBMITTED, signedDate, submitDate, null, null, null, null, cAppNo,
            serviceErrors);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        FundEstablishmentDto fundEstablishmentDto = clientApplicationDetailsDtoService
            .findFundEstablishmentStatusByClientApplicationId(cAppNo, encodedAccountNumber, serviceErrors);

        assertThat(serviceErrors.hasErrors(), is(false));
        assertThat(fundEstablishmentDto.getStatus(), is("Completed"));
    }

    @Test
    public void findFundEstablishmentStatus_ByClientApplicationId_WhenStatusIs_ASIC_REGISTRATION() throws java.text.ParseException {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Long cAppNo = 1234L;
        String signedDate = "09/10/2015";
        String submitDate = "10/10/2015";
        String companySetupDate = "11/10/2015";

        setupFundEstablishmentStates(NEW_CORPORATE_SMSF, ASIC_REGISTRATION, signedDate, submitDate, null, companySetupDate, null, null,
            cAppNo, serviceErrors);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        FundEstablishmentDto fundEstablishmentDto = clientApplicationDetailsDtoService
            .findFundEstablishmentStatusByClientApplicationId(cAppNo, encodedAccountNumber, serviceErrors);

        assertThat(serviceErrors.hasErrors(), is(false));
        assertThat(fundEstablishmentDto.getStatus(), is("CompanySetupInProgress"));
    }

    @Test
    public void findFundEstablishmentStates_When_ValidationDate_PresentFor_NEW_INDIVIDUAL_SMSF() throws java.text.ParseException {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Long cAppNo = 1234L;
        String signedDate = "09/10/2015";
        String submitDate = "10/10/2015";
        String validateDate = "20/10/2015";

        setupFundEstablishmentStates(NEW_INDIVIDUAL_SMSF, ABR_SUBMITTED, signedDate, submitDate, validateDate, null, null, null, cAppNo,
            serviceErrors);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        FundEstablishmentDto fundEstablishmentDto = clientApplicationDetailsDtoService
            .findFundEstablishmentStatusByClientApplicationId(cAppNo, encodedAccountNumber, serviceErrors);
        assertEquals(fundEstablishmentDto.getFundestablishmentStates().size(), 4);

        TransitionStateDto submit = new TransitionStateDto("Submit application", "10 Oct 2015");
        TransitionStateDto approve = new TransitionStateDto("Approve application", "09 Oct 2015");
        TransitionStateDto validate = new TransitionStateDto("Upload and validate documents", "20 Oct 2015");
        TransitionStateDto submitAto = new TransitionStateDto("Establish SMSF with ATO", "In progress");

        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(0), compareDTOMatcher(submit));
        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(1), compareDTOMatcher(approve));
        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(2), compareDTOMatcher(validate));
        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(3), compareDTOMatcher(submitAto));

    }

    @Test
    public void findFundEstablishmentStates_When_ValidationDate_NotPresentFor_NEW_INDIVIDUAL_SMSF() throws java.text.ParseException {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Long cAppNo = 1234L;
        String signedDate = "09/10/2015";
        String submitDate = "10/10/2015";
        String validateDate = null;

        setupFundEstablishmentStates(NEW_INDIVIDUAL_SMSF, ABR_SUBMITTED, signedDate, submitDate, validateDate, null, null, null, cAppNo,
            serviceErrors);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);

        FundEstablishmentDto fundEstablishmentDto = clientApplicationDetailsDtoService
            .findFundEstablishmentStatusByClientApplicationId(cAppNo, encodedAccountNumber, serviceErrors);
        assertEquals(fundEstablishmentDto.getFundestablishmentStates().size(), 4);

        TransitionStateDto submit = new TransitionStateDto("Submit application", "10 Oct 2015");
        TransitionStateDto approve = new TransitionStateDto("Approve application", "09 Oct 2015");
        TransitionStateDto validate = new TransitionStateDto("Upload and validate documents", "In progress");
        TransitionStateDto submitAto = new TransitionStateDto("Establish SMSF with ATO", "");

        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(0), compareDTOMatcher(submit));
        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(1), compareDTOMatcher(approve));
        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(2), compareDTOMatcher(validate));
        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(3), compareDTOMatcher(submitAto));

    }

    @Test
    public void findFundEstablishmentStates_When_CompanySetupDateIsPresentFor_NEW_CORPORATE_SMSF() throws java.text.ParseException {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Long cAppNo = 1234L;
        String submitDate = "09/10/2015";
        String signedDate = "10/10/2015";
        String validateDate = null;
        String companySetupDate = "11/10/2015";

        setupFundEstablishmentStates(NEW_CORPORATE_SMSF, ASIC_REGISTRATION, signedDate, submitDate, validateDate, companySetupDate, null,
            null, cAppNo, serviceErrors);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        FundEstablishmentDto fundEstablishmentDto = clientApplicationDetailsDtoService
            .findFundEstablishmentStatusByClientApplicationId(cAppNo, encodedAccountNumber, serviceErrors);
        assertEquals(fundEstablishmentDto.getFundestablishmentStates().size(), 5);

        TransitionStateDto submit = new TransitionStateDto("Submit application", "09 Oct 2015");
        TransitionStateDto approve = new TransitionStateDto("Approve application", "10 Oct 2015");
        TransitionStateDto companySetup = new TransitionStateDto("Set up Corporate Trustee", "11 Oct 2015");
        TransitionStateDto validate = new TransitionStateDto("Upload and validate documents", "In progress");
        TransitionStateDto submitAto = new TransitionStateDto("Establish SMSF with ATO", "");

        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(0), compareDTOMatcher(submit));
        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(1), compareDTOMatcher(approve));
        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(2), compareDTOMatcher(companySetup));
        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(3), compareDTOMatcher(validate));
        assertThat(fundEstablishmentDto.getFundestablishmentStates().get(4), compareDTOMatcher(submitAto));
        assertTrue(fundEstablishmentDto.getCompanyRegisteredName().equals("SOME_COMPANY_NAME"));
        assertTrue(fundEstablishmentDto.getCompanyACN().equals("111 111 111"));
    }

    private IClientApplicationForm getClientApplicationFormNewSMSF(IClientApplicationForm.AccountType accountType) {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        IAccountSettingsForm accountSettingsForm = mock(IAccountSettingsForm.class);
        when(form.getAccountType()).thenReturn(accountType);
        when(form.getAccountSettings()).thenReturn(accountSettingsForm);
        when(form.getAccountSettings().getAdviserName()).thenReturn("ADVISER");
        return form;
    }

    private IClientApplicationForm getClientApplicationFormWithMajorShareholderFlag(IClientApplicationForm.AccountType accountType,AnswerTypeEnum flag) {
        IClientApplicationForm form = mock(IClientApplicationForm.class);
        IAccountSettingsForm accountSettingsForm = mock(IAccountSettingsForm.class);
        IShareholderAndMembersForm shareholderAndMembersForm = mock(IShareholderAndMembersForm.class);
        when(shareholderAndMembersForm.getMajorShareholder()).thenReturn(flag.toString());
        when(form.getShareholderAndMembers()).thenReturn(shareholderAndMembersForm);
        when(form.getAccountType()).thenReturn(accountType);
        when(form.getAccountSettings()).thenReturn(accountSettingsForm);
        when(form.getAccountSettings().getAdviserName()).thenReturn("ADVISER");
        return form;
    }

    private void setupFundEstablishmentStates(IClientApplicationForm.AccountType accountType, ApplicationStatus applicationStatus,
                                              String signedDate, String submitDate, String validateDate, String companysetupDate,
                                              String acn, String companyName, Long cAppNo, ServiceErrors serviceErrors)
        throws java.text.ParseException {

        IClientApplicationForm clientApplicationForm = getClientApplicationFormNewSMSF(accountType);
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");

        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppState(applicationStatus);
        applicationDocument.setAppSubmitDate(dateformat.parse(submitDate));
        applicationDocument.setSignedDate(signedDate!=null? dateformat.parse(signedDate): null);
        applicationDocument.setValidatedDate(validateDate!= null? dateformat.parse(validateDate): null);
        applicationDocument.setCompanySetupDate(companysetupDate!= null? dateformat.parse(companysetupDate): null);
        applicationDocument.setCompanyACN(acn!=null? acn: "");
        applicationDocument.setAsicRegisteredName(companyName != null ? companyName : "");

        if(accountType.equals(NEW_CORPORATE_SMSF)){
            applicationDocument.setAsicRegisteredName("SOME_COMPANY_NAME");
            applicationDocument.setCompanyACN("111 111 111");
        }

        ClientApplication clientApplication = mock(ClientApplication.class);

        when(permittedClientApplicationRepository.find(cAppNo)).thenReturn(clientApplication);
        when(clientApplication.getClientApplicationForm()).thenReturn(clientApplicationForm);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn(anyString());


        when(accActivationIntegrationService.loadAccApplicationForPortfolio((List<WrapAccountIdentifier>) anyObject(), any(JobRole.class), any(ClientKey.class), serviceErrors))
            .thenReturn(singletonList(applicationDocument));
    }

    private IndividualOrJointApplicationDetailsDto getIndividualOrJointApplicationDetailsDto(AccountStatus accountStatus, boolean isOnboardingKeyRequired) {
        IndividualOrJointApplicationDetailsDto individualOrJointApplicationDetailsDto = new IndividualOrJointApplicationDetailsDto();
        individualOrJointApplicationDetailsDto.withAccountAvaloqStatus(accountStatus.getStatus());
        BrokerDto brokerDto = new BrokerDto();
        com.bt.nextgen.api.broker.model.BrokerKey brokerKey = new com.bt.nextgen.api.broker.model.BrokerKey(
            EncodedString.fromPlainText("adviser").toString());
        brokerDto.setKey(brokerKey);
        if(isOnboardingKeyRequired) {
            individualOrJointApplicationDetailsDto.withOnboardingApplicationKey(EncodedString.fromPlainText(ONBOARDING_KEY).toString());
        }
        individualOrJointApplicationDetailsDto.withAdviser(brokerDto);
        return individualOrJointApplicationDetailsDto;
    }

    private ClientApplication createClientApplication(long id) {
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getProductId()).thenReturn(MY_PRODUCT_ID);
        when(clientApplication.getAdviserPositionId()).thenReturn(ADVISER_ID);
        when(clientApplication.getId()).thenReturn(id);

        return clientApplication;
    }

    private Matcher<TransitionStateDto> compareDTOMatcher(final TransitionStateDto thatDto) {
        return new BaseMatcher<TransitionStateDto>() {
            @Override
            public boolean matches(final Object item) {
                final TransitionStateDto thisDto = (TransitionStateDto) item;
                return thisDto.equals(thatDto);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Compare Two TransitionStateDto ");
            }
        };
    }

    private UserProfile getProfile(final JobRole role, final String jobId, final String customerId) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));

        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));

        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }
}
