package com.bt.nextgen.api.profile.v1.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.api.profile.model.InvestorTncDetails;
import com.bt.nextgen.api.profile.v1.model.HomePageEnum;
import com.bt.nextgen.api.profile.v1.model.ProfileDetailsDto;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.avaloq.accountactivation.ApprovalType;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetails;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepository;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

import static com.bt.nextgen.service.avaloq.account.AccountStatus.ACTIVE;
import static com.bt.nextgen.service.avaloq.account.AccountStatus.CLOSE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvestorHomepageEvaluatorTest {

    private static final String ACCOUNT_ID_1 = "123";
    private static final String ACCOUNT_ID_2 = "456";

    @InjectMocks
    @Qualifier("investorHomepageEvaluatorV1")
    private InvestorHomepageEvaluatorImpl investorHomepageEvaluator;

    @Mock
    private UserProfileService profileService;

    @Mock
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Mock
    private AccActivationIntegrationService activationService;

    @Mock
    private UserRoleTermsAndConditionsRepository userRoleTncRepository;

    @Mock
    private BrokerHelperService brokerHelperService;

    private ProfileDetailsDto profileDetailsDto;

    @Before
    public void setUp() {
        profileDetailsDto = new ProfileDetailsDto();
        mockProfileForInvestor();
        mockAccountService();
        when(brokerHelperService.isDirectInvestor(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(false);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
    }

    private void mockAccountService() {
        Map<AccountKey, WrapAccount> mockAccountListMap = Mockito.mock(HashMap.class);
        when(mockAccountListMap.isEmpty()).thenReturn(false);
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(mockAccountListMap);
    }

    private void mockProfileForInvestor() {
        when(profileService.isExistingAvaloqUser()).thenReturn(true);
        when(profileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        UserProfile activeProfile = mock(UserProfile.class);
        when(activeProfile.getClientKey()).thenReturn(ClientKey.valueOf("123456"));
        when(activeProfile.getBankReferenceId()).thenReturn(ACCOUNT_ID_1);
        when(activeProfile.getProfileId()).thenReturn(ACCOUNT_ID_2);
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
    }

    /**
     * Multiple account tests
     */
    @Test
    public void findOne_shouldSetInvestorHomePageToClientOverview_WhenMultipleAccounts() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE),
            getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_2, "123456", true, false, ApprovalType.ONLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN),
            getAccount(ACCOUNT_ID_2, AccountStatus.PEND_OPN));

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.CLIENT_ACCOUNT_OVERVIEW.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToAccountApproval_WhenMultipleAccount_OneAppIsWithdrawn_OtherPendingApproval_Approver() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DISCARDED, AccountStatus.DISCARD, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE),
            getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_2, "123456", true, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(true, false);
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.PEND_OPN);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.DISCARD),
            getAccount(ACCOUNT_ID_2, AccountStatus.PEND_OPN));
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(getUserRoleTnC("0"));

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.ACCOUNT_APPROVAL.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_2));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToNonApproverTnCs_WhenMultipleAccount_OneAppIsWithdrawn_OtherPendingApproval_NonApprover() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", false, false, ApprovalType.ONLINE),
            getApplicationDocument(ApplicationStatus.DISCARDED, AccountStatus.DISCARD, ACCOUNT_ID_2, "123456", true, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(false, false);
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.PEND_OPN);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN),
            getAccount(ACCOUNT_ID_2, AccountStatus.DISCARD));

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.NON_APPROVER_TNCS.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToWithdrawn_WhenAllApplicationsAreDiscarded() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DISCARDED, AccountStatus.DISCARD, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE),
            getApplicationDocument(ApplicationStatus.DISCARDED, AccountStatus.DISCARD, ACCOUNT_ID_2, "123456", true, false, ApprovalType.ONLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.DISCARD),
            getAccount(ACCOUNT_ID_2, AccountStatus.DISCARD));

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.WITHDRAWN.toString()));
    }

    @Test
    public void findOne_shouldSetInvestorHomepageToInvestorExperience_whenMultipleAccount_withOneActive_andOneAsicRegistered()
        throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", true, true, ApprovalType.ONLINE),
            getApplicationDocument(ApplicationStatus.DISCARDED, AccountStatus.COMPANY_SETUP_IN_PROGRESS, ACCOUNT_ID_2, "123456", true, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(true, false);
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, ACTIVE);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, ACTIVE),
            getAccount(ACCOUNT_ID_2, AccountStatus.COMPANY_SETUP_IN_PROGRESS));

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomepageToHoldingPage_whenMultipleAccount_withOnePendingOthersApproval_andOneAsicSubmitted()
        throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", true, true, ApprovalType.ONLINE),
            getApplicationDocument(ApplicationStatus.ASIC_SUBMISSION, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS, ACCOUNT_ID_2, "123456", true, true, ApprovalType.ONLINE));
        mockInvestorTncDetails(true, true);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN),
            getAccount(ACCOUNT_ID_2, AccountStatus.COMPANY_SETUP_IN_PROGRESS));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.PEND_OPN);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.HOLDING.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomepageToHoldingPage_withOneFundEstablishmentInProgress()
        throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.ABR_SUBMITTED, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS, ACCOUNT_ID_1, "123456", true, true, ApprovalType.ONLINE));
        mockInvestorTncDetails(true, true);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.HOLDING.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    /**
     * Single account tests
     */
    @Test
    public void findOne_shouldSetInvestorHomePageToWithdrawn_WhenTheOneAndOnlyApplicationIsDiscarded() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DISCARDED, AccountStatus.DISCARD, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.DISCARD));

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.WITHDRAWN.toString()));
    }


    @Test
    public void findOne_shouldSetInvestorHomePageToAccountApproval_WhenSingleAccount_Approver_NotAcceptedTnCs() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN));
        mockInvestorTncDetails(true, false);
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(getUserRoleTnC("0"));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.PEND_OPN);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.ACCOUNT_APPROVAL.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToAccountDetails_WhenSingleAccount_MigratedUser_NotAcceptedTnCs() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, AccountStatus.ACTIVE, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.ACTIVE));
        mockInvestorTncDetails(false, false);
        when(profileService.isEmulating()).thenReturn(false);
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(getUserRoleTnC("0"));
        mockLoadMigratedWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.ACTIVE);
        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.NON_APPROVER_TNCS.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToAccountDetails_WhenSingleAccount_MigratedUser_Emulating_NotAcceptedTnCs() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, AccountStatus.ACTIVE, ACCOUNT_ID_1, "123456", true, true, ApprovalType.ONLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.ACTIVE));
        mockInvestorTncDetails(false, false);
        when(profileService.isEmulating()).thenReturn(true);
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(getUserRoleTnC("0"));
        mockLoadMigratedWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.ACTIVE);
        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToAccountDetails_WhenSingleAccount_MigratedUser_NonEmulating_NotAcceptedTnCs() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, AccountStatus.ACTIVE, ACCOUNT_ID_1, "123456", true, true, ApprovalType.ONLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.ACTIVE));
        mockInvestorTncDetails(false, false);
        when(profileService.isEmulating()).thenReturn(false);
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(getUserRoleTnC("0"));
        mockLoadMigratedWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.ACTIVE);
        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.NON_APPROVER_TNCS.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }
    @Test
    public void findOne_shouldSetInvestorHomePageToPlatformTandCs_WhenSingleAccount_MigratedUser_AcceptedTnCs() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, AccountStatus.ACTIVE, ACCOUNT_ID_1, "123456", true, false, ApprovalType.OFFLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.ACTIVE));
        mockInvestorTncDetails(true, true);
        when(profileService.isEmulating()).thenReturn(false);
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(getUserRoleTnC("1"));
        mockLoadMigratedWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.ACTIVE);
        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToAccountDetails_WhenSingleAccount_MigratedUser_NonEmulated_AcceptedTnCs() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, AccountStatus.ACTIVE, ACCOUNT_ID_1, "123456", true, false, ApprovalType.OFFLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.ACTIVE));
        mockInvestorTncDetails(true, true);
        when(profileService.isEmulating()).thenReturn(true);
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(getUserRoleTnC("1"));
        mockLoadMigratedWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.ACTIVE);
        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToAccountDetails_WhenSingleAccount_MigratedUser_Emulated_AcceptedTnCs() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, AccountStatus.ACTIVE, ACCOUNT_ID_1, "123456", true, false, ApprovalType.OFFLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.ACTIVE));
        mockInvestorTncDetails(true, true);
        when(profileService.isEmulating()).thenReturn(true);
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(getUserRoleTnC("1"));
        mockLoadMigratedWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.ACTIVE);
        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToNonApproverTnC_WhenSingleAccount_NonApprover_NotAcceptedTnCs() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", false, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(false, false);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.PEND_OPN);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.NON_APPROVER_TNCS.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToHoldingPage_WhenSingleAccount_NonApprover_AcceptedTnCs_NotActive() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", false, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(false, true);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN));
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(getUserRoleTnC("1"));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.PEND_OPN);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.HOLDING.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToHoldingPage_WhenSingleAccount_Approver_AcceptedTnCs_NotActive() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", true, true, ApprovalType.ONLINE));
        mockInvestorTncDetails(true, true);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.PEND_OPN);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.HOLDING.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToPvPage_WhenSingleAccount_NonApprover_AcceptedTnCs_ActiveAccount() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, ACTIVE, ACCOUNT_ID_1, "123456", false, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(false, true);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, ACTIVE));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, ACTIVE);

        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(getUserRoleTnC("1"));
        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToNonApproverTnCPage_WhenSingleAccount_NonApprover_NotAcceptedTnCs_ActiveAccount() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, ACTIVE, ACCOUNT_ID_1, "123456", false, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(false, false);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, ACTIVE));
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(null);
        when(profileService.isEmulating()).thenReturn(false);
        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.NON_APPROVER_TNCS.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToPvPage_WhenSingleAccount_Approver_ActiveAccount() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, ACTIVE, ACCOUNT_ID_1, "123456", true, true, ApprovalType.ONLINE));
        mockInvestorTncDetails(true, true);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, ACTIVE));

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToErrorPage_whenNotAbleToDetermineInvestorHomePage() {
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(new HashMap<AccountKey, WrapAccount>());

        investorHomepageEvaluator
            .setInvestorHomepageDetails(profileDetailsDto, new HashMap<AccountKey, WrapAccount>(), new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.ERROR_PAGE.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void find_shouldSetInvestorHomePageToPvPage_whenAccountStatusIsUpdatedToClosed() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.CLOSE);
        mockInvestorTncDetails(true, false);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, ACTIVE));

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void find_shouldSetInvestorHomePageToPvPage_whenAccountStatusIsUpdatedToActive() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(true, false);
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, ACTIVE);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN));

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldReturnDirectOnlyPage_whenDirectInvestorWithOneAccount() throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, ACTIVE, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, ACTIVE));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, ACTIVE);
        mockInvestorTncDetails(true, false);

        when(brokerHelperService.isDirectInvestor(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(true);
        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldReturnAccountDetailsPage_whenNonASIMInvestorWithOneClosedAccountOnly() throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, ACTIVE, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, CLOSE));
        mockInvestorTncDetails(true, false);

        when(brokerHelperService.isDirectInvestor(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(true);
        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.ACCOUNT_DETAILS.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldReturnDirectOnlyPage_whenInvestorHasSuperAccount() throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, AccountStatus.ACTIVE, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE));
        WrapAccountDetail accountDetail = getWrapAccountDetail(ACCOUNT_ID_1, ACTIVE);
        when(accountDetail.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(accountDetail);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetail);

        mockInvestorTncDetails(true, false);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldReturnPortfolioValuationPage_whenInvestorHasASIMAccount() throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, AccountStatus.ACTIVE, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE));
        WrapAccountDetail accountDetail = getWrapAccountDetail(ACCOUNT_ID_1, ACTIVE);
        when(accountDetail.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(accountDetail);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountDetail);
        when(brokerHelperService.getUserExperience(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(UserExperience.ASIM);

        mockInvestorTncDetails(true, false);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.PORTFOLIO_VALUATION.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findone_shouldSetInvestorHomePageToClientOverview_WhenMultipleAccounts_withSingleAppdocument() throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, ACTIVE, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE));
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN),
            getAccount(ACCOUNT_ID_2, AccountStatus.PEND_OPN));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, ACTIVE);
        mockInvestorTncDetails(true, false);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.CLIENT_ACCOUNT_OVERVIEW.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findone_shouldSetInvestorHomePageToClientOverview_WhenMultipleAccounts_withNoAppdocument() throws Exception {
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN),
            getAccount(ACCOUNT_ID_2, AccountStatus.PEND_OPN));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, ACTIVE);
        mockInvestorTncDetails(true, false);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.CLIENT_ACCOUNT_OVERVIEW.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldReturnDirectOnlyPage_whenDirectInvestorWithOneAccount_withoutAppDocument() throws Exception {
        when(activationService.loadAccApplicationForPortfolio(anyList(), any(JobRole.class),any(ClientKey.class),any(ServiceErrors.class))).thenReturn(null);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, ACTIVE));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, ACTIVE);
        mockInvestorTncDetails(true, false);

        when(brokerHelperService.isDirectInvestor(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn(true);
        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToNonApproverTnC_WhenSingleAccount_OfflineApproval_Approver_AcceptedTnCs() {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, ACTIVE, ACCOUNT_ID_1, "123456", true, true, ApprovalType.OFFLINE));
        mockInvestorTncDetails(false, false);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, ACTIVE));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, ACTIVE);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.NON_APPROVER_TNCS.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToNonApproverTncs_whenAddedToAccountDirectorList_afterAccountIsActive() {
        mockLoadApplicationDocumentsWithNoFilter(getApplicationDocument(ApplicationStatus.DONE, ACTIVE, ACCOUNT_ID_1, "97892", false, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(false, false);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, ACTIVE));
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(null);
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, ACTIVE);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.NON_APPROVER_TNCS.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToAccountApproval_withPendingOnlineAccount_withPendingOfflineAccount() {
        mockLoadApplicationDocumentsWithNoFilter(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", true, false, ApprovalType.ONLINE),
            getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_2, "123456", false, false, ApprovalType.OFFLINE));
        mockInvestorTncDetails(false, false);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN), getAccount(ACCOUNT_ID_2, AccountStatus.PEND_OPN));
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(null);
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.PEND_OPN);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.ACCOUNT_APPROVAL.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToAccountApproval_withMultiplePendingOfflineAccount_withPendingOnlineAccount() {
        mockLoadApplicationDocumentsWithNoFilter(getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_1, "123456", true, false, ApprovalType.OFFLINE),
            getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_2, "123456", true, false, ApprovalType.ONLINE),
            getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, "789", "123456", true, false, ApprovalType.OFFLINE));
        mockInvestorTncDetails(false, false);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.PEND_OPN), getAccount(ACCOUNT_ID_2, AccountStatus.PEND_OPN),
            getAccount("789", AccountStatus.PEND_OPN));
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(null);
        mockLoadWrapAccountDetail(ACCOUNT_ID_2, AccountStatus.PEND_OPN);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.ACCOUNT_APPROVAL.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_2));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToClientOverview_withFEOfflineAccount_withPendingOnlineAccount() {
        mockLoadApplicationDocumentsWithNoFilter(getApplicationDocument(ApplicationStatus.READY_FOR_ABR, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS, ACCOUNT_ID_1, "123456", true, false, ApprovalType.OFFLINE),
            getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_2, "123456", true, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(false, false);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS), getAccount(ACCOUNT_ID_2, AccountStatus.PEND_OPN));
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(null);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.CLIENT_ACCOUNT_OVERVIEW.toString()));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomePageToHoldingPage_withFEOfflineAccount_withPendingOfflineAccount() {
        mockLoadApplicationDocumentsWithNoFilter(getApplicationDocument(ApplicationStatus.READY_FOR_ABR, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS, ACCOUNT_ID_1, "123456", true, true, ApprovalType.OFFLINE),
            getApplicationDocument(ApplicationStatus.PEND_ACCEPT, AccountStatus.PEND_OPN, ACCOUNT_ID_2, "123456", true, false, ApprovalType.OFFLINE));
        mockInvestorTncDetails(false, false);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS),
            getAccount(ACCOUNT_ID_2, AccountStatus.PEND_OPN));
        when(userRoleTncRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(null);
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.HOLDING.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomepageToInvestorExperience_whenMultipleAccount_withOneActive_andOneDiscarded()
            throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DISCARDED, AccountStatus.DISCARD, ACCOUNT_ID_1, "123456", true, true, ApprovalType.ONLINE),
                getApplicationDocument(ApplicationStatus.DONE, ACTIVE, ACCOUNT_ID_2, "123456", true, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(true, false);
        mockLoadWrapAccountDetail(ACCOUNT_ID_2, ACTIVE);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.DISCARD),
                getAccount(ACCOUNT_ID_2, ACTIVE));

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_2));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomepageToInvestorExperience_whenMultipleAccounts_withAccountIdInProfile()
            throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.DONE, AccountStatus.ACTIVE, ACCOUNT_ID_1, "123456", true, true, ApprovalType.ONLINE),
                getApplicationDocument(ApplicationStatus.DONE, ACTIVE, ACCOUNT_ID_2, "123456", true, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(true, false);
        mockLoadWrapAccountDetail(ACCOUNT_ID_2, ACTIVE);
        profileDetailsDto.setAccountId(EncodedString.fromPlainText(ACCOUNT_ID_2).toString());
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.DISCARD),
                getAccount(ACCOUNT_ID_2, ACTIVE));

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.INVESTOR_EXPERIENCE.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_2));
        assertThat(profileDetailsDto.isCanNavigate(), is(true));
    }

    @Test
    public void findOne_shouldSetInvestorHomepageToNonApproverTncs_withOneFundEstablishmentInProgress()
        throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.ABR_SUBMITTED, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS, ACCOUNT_ID_1, "123456", false, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(false, false);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.FUND_ESTABLISHMENT_IN_PROGRESS);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.NON_APPROVER_TNCS.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    @Test
    public void findOne_shouldSetInvestorHomepageToNonApproverTncs_withOneFundEstablishmentPending()
        throws Exception {
        mockLoadApplicationDocuments(getApplicationDocument(ApplicationStatus.ABR_SUBMITTED, AccountStatus.FUND_ESTABLISHMENT_PENDING, ACCOUNT_ID_1, "123456", false, false, ApprovalType.ONLINE));
        mockInvestorTncDetails(false, false);
        Map<AccountKey, WrapAccount> accountMap = mockLoadWrapAccountMap(getAccount(ACCOUNT_ID_1, AccountStatus.FUND_ESTABLISHMENT_PENDING));
        mockLoadWrapAccountDetail(ACCOUNT_ID_1, AccountStatus.FUND_ESTABLISHMENT_PENDING);

        investorHomepageEvaluator.setInvestorHomepageDetails(profileDetailsDto, accountMap, new ServiceErrorsImpl());
        assertThat(profileDetailsDto.getHomePage(), is(HomePageEnum.NON_APPROVER_TNCS.toString()));
        assertThat(EncodedString.toPlainText(profileDetailsDto.getAccountId()), is(ACCOUNT_ID_1));
        assertThat(profileDetailsDto.isCanNavigate(), is(false));
    }

    private ApplicationDocument getApplicationDocument(ApplicationStatus applicationStatus, AccountStatus accountStatus, String bpId, String clientKey, boolean isHasToAccept,
                                                       boolean hasAccepted, ApprovalType approvalType) {
        ApplicationDocument document = mock(ApplicationDocument.class);
        when(document.getAppState()).thenReturn(applicationStatus);
        when(document.getBpid()).thenReturn(AccountKey.valueOf(bpId));
        when(document.getAppSubmitDate()).thenReturn(new Date());
        when(document.getApprovalType()).thenReturn(approvalType);
        when(document.getAccountStatus()).thenReturn(accountStatus);

        List<LinkedPortfolioDetails> portfolios = new ArrayList<>();
        LinkedPortfolioDetails portfolio = mock(LinkedPortfolioDetails.class);
        when(portfolio.getPortfolioId()).thenReturn(bpId);
        portfolios.add(portfolio);
        when(document.getPortfolio()).thenReturn(portfolios);

        List<AssociatedPerson> persons = new ArrayList<>();
        AssociatedPerson person = mock(AssociatedPerson.class);
        when(person.getClientKey()).thenReturn(ClientKey.valueOf(clientKey));
        when(person.isHasToAcceptTnC()).thenReturn(isHasToAccept);
        when(person.isHasApprovedTnC()).thenReturn(hasAccepted);
        if (hasAccepted) {
            when(person.getTncSignDate()).thenReturn(new Date());
        }

        persons.add(person);
        when(document.getPersonDetails()).thenReturn(persons);

        return document;
    }

    private WrapAccount getAccount(final String key, final AccountStatus accountStatus) {
        WrapAccount account = mock(WrapAccount.class);
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf(key));
        when(account.getAccountStatus()).thenReturn(accountStatus);
        return account;
    }

    private WrapAccountDetail getWrapAccountDetail(final String key, final AccountStatus accountStatus) {
        WrapAccountDetail account = mock(WrapAccountDetail.class);
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf(key));
        when(account.getAccountStatus()).thenReturn(accountStatus);
        return account;
    }

    private WrapAccountDetail getMigratedWrapAccountDetail(final String key, final AccountStatus accountStatus) {
        WrapAccountDetail account = mock(WrapAccountDetail.class);
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf(key));
        when(account.getAccountStatus()).thenReturn(accountStatus);
        when(account.getMigrationKey()).thenReturn("WRAP123456789");
        when(account.getMigrationDate()).thenReturn(new DateTime());
        return account;
    }


    private Map<AccountKey, WrapAccount> mockLoadWrapAccountMap(WrapAccount... accounts) {
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        for (WrapAccount account : accounts) {
            accountMap.put(account.getAccountKey(), account);
        }
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        return accountMap;
    }

    private void mockLoadApplicationDocuments(ApplicationDocument... applicationDocuments) {
        List<ApplicationDocument> appDocs = new ArrayList<>();
        for (ApplicationDocument doc : applicationDocuments) {
            appDocs.add(doc);
        }
        when(activationService.loadAccApplicationForPortfolio(anyList(), any(JobRole.class),any(ClientKey.class),any(ServiceErrors.class))).thenReturn(appDocs);
    }

    private void mockLoadApplicationDocumentsWithNoFilter(ApplicationDocument... applicationDocuments) {
        List<ApplicationDocument> appDocs = new ArrayList<>();
        for (ApplicationDocument doc : applicationDocuments) {
            appDocs.add(doc);
        }
        when(activationService.loadAccApplicationForPortfolioWithNoFilter(anyList(), any(ServiceErrors.class))).thenReturn(appDocs);
    }

    private void mockInvestorTncDetails(boolean isApprover, boolean hasAccepted) {
        InvestorTncDetails investorTncDetails = mock(InvestorTncDetails.class);
        when(investorTncDetails.isApprover()).thenReturn(isApprover);
        when(investorTncDetails.isAcceptedTnc()).thenReturn(hasAccepted);

        if (hasAccepted) {
            when(investorTncDetails.getTncSignDate()).thenReturn(new Date());
        }
    }

    private void mockLoadWrapAccountDetail(String bpId, AccountStatus accountStatus) {
        WrapAccountDetail wrapAccountDetail = getWrapAccountDetail(bpId, accountStatus);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(wrapAccountDetail);
    }

    private void mockLoadMigratedWrapAccountDetail(String bpId, AccountStatus accountStatus) {
        WrapAccountDetail wrapAccountDetail = getMigratedWrapAccountDetail(bpId, accountStatus);
        when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(wrapAccountDetail);
    }

    private UserRoleTermsAndConditions getUserRoleTnC(String accepted) {
        UserRoleTermsAndConditions userRoleTnC = new UserRoleTermsAndConditions();
        userRoleTnC.setTncAccepted(accepted);

        if (accepted.equalsIgnoreCase("1")) {
            userRoleTnC.setTncAcceptedOn(new Date());
        }

        return userRoleTnC;
    }
}