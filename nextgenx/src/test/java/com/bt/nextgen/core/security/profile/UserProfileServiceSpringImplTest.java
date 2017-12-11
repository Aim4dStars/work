package com.bt.nextgen.core.security.profile;

import com.bt.nextgen.core.repository.User;
import com.bt.nextgen.core.repository.UserRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.bt.nextgen.service.avaloq.accountactivation.AssociatedPersonImpl;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetails;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetailsImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.client.GenericClientInvestorImpl;
import com.bt.nextgen.service.avaloq.client.TwoFASecuredClient;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.group.customer.CredentialRequest;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepository;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.integration.userprofile.ProfileIntegrationService;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.SafiDeviceIdentifier;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.bt.nextgen.core.security.profile.UserProfileServiceSpringImpl.BRAND_SILO;
import static com.btfin.panorama.core.security.Roles.ROLE_ADVISER;
import static com.btfin.panorama.core.security.Roles.ROLE_INVESTOR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileServiceSpringImplTest {
    @Spy
    @InjectMocks
    UserProfileServiceSpringImpl userProfileServiceSpring;

    
    private final static String portfolioId = "1205";
    private List<ApplicationDocument> applications;
    @Mock
    private CustomerLoginManagementIntegrationService customerLoginService;

    @Mock
    private UserInformationIntegrationService userInformationIntegrationService;

    @Mock
    private ProfileIntegrationService profileIntegrationService;

    @Mock
    private com.bt.nextgen.service.integration.client.ClientIntegrationService clientIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccActivationIntegrationService activationService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private Broker broker;

    @Mock
    private UserRoleTermsAndConditionsRepository userRoleTermsAndConditionsRepository;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private HttpSession httpSession;

    @Before
    public void setup() {
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        when(profileIntegrationService.loadAvailableJobProfiles(any(ServiceErrors.class))).thenReturn(
                generateSingleJobProfileList(false));

        when(userInformationIntegrationService.loadUserInformation(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(
                createUserInfo(generateJobProfile()));
        when(clientIntegrationService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(
                (getIndividualDetails()));
        userProfileServiceSpring.getActiveProfile();

        UserRoleTermsAndConditions userRoleTnc = new UserRoleTermsAndConditions();
        userRoleTnc.setTncAccepted("Y");
        userRoleTnc.setVersion(1);
        userRoleTnc.setModifyDatetime(new Date());
        userRoleTnc.setUserRoleTermsAndConditionsKey(new UserRoleTermsAndConditionsKey("12345", "65432"));
        when(userRoleTermsAndConditionsRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(userRoleTnc);

    }

    @Ignore
    public void testGetUserName() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        String userName = userProfileServiceSpring.getUserName();
        // assertThat(userName, Is.is(Attribute.EMPTY_STRING));

        TestingAuthenticationToken authentication = new TestingAuthenticationToken("adviser", "adviser",
                Roles.ROLE_ADVISER.name());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String stringToken = SamlUtil.loadSaml(ROLE_ADVISER);
        SamlToken token = new SamlToken(stringToken);
        Profile profile = new Profile(token);

        authentication.setDetails(profile);
        // userName = userProfileServiceSpring.getUserName(serviceErrors);
        // assertThat(userName, Is.is("Martin Taylor"));

        Field field = SamlToken.class.getDeclaredField("custDefinedLogin");
        field.setAccessible(true);
        /*
         * TODO Bad test consider revision field.set(token, ""); userName = userProfileServiceSpring.getUserName(serviceErrors);
         * assertThat(userName, Is.is(Attribute.EMPTY_STRING));
         */
        CustomerCredentialInformation customerCredential = mock(CustomerCredentialInformation.class);
        when(customerCredential.getUsername()).thenReturn("Martin Taylor");
        when(customerLoginService.getCustomerInformation(any(CredentialRequest.class), any(ServiceErrors.class))).thenReturn(
                customerCredential);

        userName = userProfileServiceSpring.getUserName();
        assertThat(userName, is("Martin Taylor"));

        field.set(token, "NOT_FOUND");
        userName = userProfileServiceSpring.getUserName();
        assertThat(userName, is("Martin Taylor"));
    }

    private JobProfile generateJobProfile() {
        JobProfileImpl tmpProfile = new JobProfileImpl();
        tmpProfile.setJob(JobKey.valueOf("fakeJobKey1"));
        tmpProfile.setProfileId("fakeProfileId1");
        tmpProfile.setJobRole(JobRole.ADVISER);
        tmpProfile.setCloseDate(null);
        tmpProfile.setPersonJobId("fakePersonJobId1");
        tmpProfile.setUserExperience(UserExperience.ADVISED);
        // tmpProfile.setJobProfileName("fakeBasicProfilename");
        return tmpProfile;
    }

    private JobProfile generateInvestorJobProfile() {
        JobProfileImpl tmpProfile = new JobProfileImpl();
        tmpProfile.setJob(JobKey.valueOf("fakeJobKey2"));
        tmpProfile.setProfileId("fakeProfileId2");
        tmpProfile.setJobRole(JobRole.INVESTOR);
        tmpProfile.setCloseDate(null);
        tmpProfile.setPersonJobId("fakePersonJobId2");
        tmpProfile.setUserExperience(UserExperience.DIRECT);
        // tmpProfile.setJobProfileName("fakeBasicProfilename");
        return tmpProfile;
    }

    private JobProfile generateJobProfileForMatchingID() {
        JobProfileImpl tmpProfile = new JobProfileImpl();
        tmpProfile.setJob(JobKey.valueOf("fakeJobKey1"));
        tmpProfile.setProfileId("ProfileId2");
        // tmpProfile.setJobProfileName("fakeBasicProfilename");
        return tmpProfile;
    }

    private List<JobProfile> generateSingleJobProfileListForMatchingID() {
        List<JobProfile> fakeJobProfileList = new ArrayList<JobProfile>();
        fakeJobProfileList.add(generateJobProfileForMatchingID());
        return fakeJobProfileList;
    }

    private List<JobProfile> generateSingleJobProfileList(boolean isInvestor) {
        List<JobProfile> fakeJobProfileList = new ArrayList<JobProfile>();
        fakeJobProfileList.add(isInvestor ? generateInvestorJobProfile() : generateJobProfile());
        return fakeJobProfileList;
    }

    private List<JobProfile> generateMultiJobProfileList() {
        List<JobProfile> fakeJobProfileList = new ArrayList<JobProfile>();
        return fakeJobProfileList;
    }

    private List<FunctionalRole> getFunctionalRoles() {
        return new ArrayList<FunctionalRole>();
    }

    private UserInformation createUserInfo(JobProfile profile) {
        UserInformationTestImpl userInfo = new UserInformationTestImpl(profile, ClientKey.valueOf("fakeClientKey1"),
                getFunctionalRoles());

        return userInfo;
    }

    public void createLoggedInProfile(String username, String siteGroup, Roles role, boolean wplIntegrated) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(username, siteGroup, role.name());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String stringToken = wplIntegrated ? SamlUtil.loadWplSaml() : SamlUtil.loadSaml(role);
        SamlToken token = new SamlToken(stringToken);
        Profile profile = new Profile(token);
        authentication.setDetails(profile);
    }

    @Test
    public void testGetAvailableProfile() {
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);

        when(profileIntegrationService.loadAvailableJobProfiles(any(ServiceErrors.class))).thenReturn(
                generateSingleJobProfileList(false));

        when(userInformationIntegrationService.loadUserInformation(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(
                createUserInfo(generateJobProfile()));

        List<JobProfile> jobProfiles = userProfileServiceSpring.getAvailableProfiles();
        assertThat(jobProfiles, not(nullValue()));
        assertThat(jobProfiles.size(), is(1));
        assertThat(jobProfiles.get(0).getProfileId(), is("fakeProfileId1"));

    }

    @Test
    public void testGetDealerGroupBrandSiloForAdvisor(){
        when(brokerHelperService.getBrandSiloForIntermediary(any(UserProfile.class),any(FailFastErrorsImpl.class))).thenReturn("WPAC");
        assertThat(userProfileServiceSpring.getDealerGroupBrandSilo(),is("WPAC"));
    }

    @Test
    public void testGetBrandsiloForInvestor(){
        when(brokerHelperService.getBrandSiloForInvestor(any(FailFastErrorsImpl.class))).thenReturn("WPAC");
        assertThat(userProfileServiceSpring.getBrandSiloForInvestor(),is("WPAC"));
    }

    @Test
    public void testGetBrandSiloForIntermediary(){
        when(brokerHelperService.getBrandSiloForIntermediary(any(UserProfile.class),any(FailFastErrorsImpl.class))).thenReturn("WPAC");
        assertThat(userProfileServiceSpring.getBrandSiloForIntermediary(),is("WPAC"));
    }

    @Test
    public void testGetDealerGroupBrandSiloForInvestor(){
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        when(profileIntegrationService.loadAvailableJobProfiles(any(ServiceErrors.class))).thenReturn(
                generateSingleJobProfileList(true));

        when(userInformationIntegrationService.loadUserInformation(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(
                createUserInfo(generateJobProfile()));
        when(brokerHelperService.getBrandSiloForInvestor(any(FailFastErrorsImpl.class))).thenReturn("WPAC");
        assertThat(userProfileServiceSpring.getDealerGroupBrandSilo(),is("WPAC"));
    }

    @Test
    public void testGetActiveProfile() {
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        when(profileIntegrationService.loadAvailableJobProfiles(any(ServiceErrors.class))).thenReturn(
                generateSingleJobProfileList(false));

        when(userInformationIntegrationService.loadUserInformation(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(
                createUserInfo(generateJobProfile()));
        UserProfile userProfile = userProfileServiceSpring.getActiveProfile();
        assertThat(userProfile, not(nullValue()));
        assertThat(userProfile.getProfileId(), is("fakeProfileId1"));
        assertThat(userProfile.getJob().getId(), is("fakeJobKey1"));
        assertThat(userProfile.getClientKey().getId(), is("fakeClientKey1"));
    }

    @Test
    public void testGetCredentialId() {
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        String credentialId = userProfileServiceSpring.getCredentialId(new ServiceErrorsImpl());
        assertThat(credentialId, not(nullValue()));
    }

    @Test
    public void testGetProfile() {
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        when(profileIntegrationService.loadAvailableJobProfiles(any(ServiceErrors.class))).thenReturn(
                generateSingleJobProfileList(false));

        when(userInformationIntegrationService.loadUserInformation(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(
                createUserInfo(generateJobProfile()));
        userProfileServiceSpring.getActiveProfile();
        JobProfileIdentifier jobProfile = userProfileServiceSpring.getJobProfile();
        assertThat(jobProfile, not(nullValue()));
        assertThat(jobProfile.getProfileId(), is("fakeProfileId1"));
    }

    @Test
    public void testGetSafiDeviceId() {
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        when(clientIntegrationService.loadGenericClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(
                getGenericClientDetails());
        SafiDeviceIdentifier safiDeviceId = userProfileServiceSpring.getSafiDeviceIdentifier();
        assertThat(safiDeviceId, not(nullValue()));
        assertThat(safiDeviceId.getSafiDeviceId(), is("10101010"));
    }

    @Test
    public void testIsSafiDeviceActive() {
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        when(clientIntegrationService.loadGenericClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(
                getGenericClientDetails());
        boolean isSafiDeviceActive = userProfileServiceSpring.isSafiDeviceActive();
        assertThat(isSafiDeviceActive, is(true));
    }

    @Test
    public void testSwitchActiveProfileWithId() {
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        userProfileServiceSpring.getEffectiveProfile();
        when(profileIntegrationService.loadAvailableJobProfiles(any(ServiceErrors.class))).thenReturn(
                generateSingleJobProfileList(false));
        when(userInformationIntegrationService.loadUserInformation(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(
                createUserInfo(generateJobProfile()));
        UserProfile userProfile = userProfileServiceSpring.switchActiveProfile("fakeProfileId1");
        assertThat(userProfile, not(nullValue()));
    }

    @Test
    public void testSwitchActiveProfileWithNoId() {
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        userProfileServiceSpring.getEffectiveProfile();
        when(profileIntegrationService.loadAvailableJobProfiles(any(ServiceErrors.class))).thenReturn(
                generateSingleJobProfileList(false));
        when(userInformationIntegrationService.loadUserInformation(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(
                createUserInfo(generateJobProfile()));
        UserProfile userProfile = userProfileServiceSpring.switchActiveProfile(null);
        assertThat(userProfile, not(nullValue()));
        assertThat(userProfile.getProfileId(), is("fakeProfileId1"));
        assertThat(userProfile.getJob().getId(), is("fakeJobKey1"));
        assertThat(userProfile.getClientKey().getId(), is("fakeClientKey1"));
    }

    @Test
    public void testSwitchActiveProfileWithmatchingId() {
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        userProfileServiceSpring.getEffectiveProfile().setCurrentProfileId("ProfileId1");

        when(profileIntegrationService.loadAvailableJobProfiles(any(ServiceErrors.class))).thenReturn(
                generateSingleJobProfileList(false));
        when(userInformationIntegrationService.loadUserInformation(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(
                createUserInfo(generateJobProfileForMatchingID()));
        when(brokerHelperService.getBrandSiloForIntermediary(any(UserProfile.class),any(FailFastErrorsImpl.class))).thenReturn("WPAC");
        UserProfile userProfile = userProfileServiceSpring.switchActiveProfile("ProfileId1");
        assertThat(userProfile, not(nullValue()));
        verify(httpSession, times(2)).getAttribute(BRAND_SILO);
        verify(userProfileServiceSpring, times(6)).getActiveProfile();
    }

    @Test
    public void testSwitchActiveProfileWithNonmatchingId() {
        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        userProfileServiceSpring.getEffectiveProfile().setCurrentProfileId("ProfileId1");

        when(profileIntegrationService.loadAvailableJobProfiles(any(ServiceErrors.class))).thenReturn(
                generateSingleJobProfileListForMatchingID());
        when(userInformationIntegrationService.loadUserInformation(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(
                createUserInfo(generateJobProfileForMatchingID()));
        when(httpSession.getAttribute(any(String.class))).thenReturn("WPAC");
        UserProfile userProfile = userProfileServiceSpring.switchActiveProfile("ProfileId2");
        assertThat(userProfile, not(nullValue()));
        verify(userProfileServiceSpring, times(1)).getActiveProfile(anyBoolean());
    }

    @Test
    public void test_existingAvaloqUser() {
        assertTrue(userProfileServiceSpring.isExistingAvaloqUser());
    }

    @Test
    public void test_notExistingAvaloqUser() {
        createLoggedInProfile("123456789", "investor", ROLE_INVESTOR, true);
        assertTrue(userProfileServiceSpring.isExistingAvaloqUser());
        // assertFalse(userProfileServiceSpring.isExistingAvaloqUser()); TODO: this is the correct response when wplIntegration is
        // true
    }

    @Test
    public void testUpdateSafiDeviceStatus() {
        userProfileServiceSpring.updateSafiDeviceStatus(true);
        verify(clientIntegrationService, times(1)).updateDeviceStatus(any(ClientKey.class), eq(true), any(ServiceErrors.class));
        assertThat(userProfileServiceSpring.getEffectiveProfile().isSafiDeviceActive(), is(true));
    }

    @Test
    public void testGetPpId() {
        TwoFASecuredClient twoFASecuredClient = Mockito.mock(TwoFASecuredClient.class);
        when(twoFASecuredClient.getPpId()).thenReturn("123456789");
        when(clientIntegrationService.loadGenericClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(
                twoFASecuredClient);
        assertThat("PPID is returned from Client object", userProfileServiceSpring.getPpId(), is("123456789"));
    }

    private ClientDetail getIndividualDetails() {
        IndividualDetailImpl individualDetails = new IndividualDetailImpl();
        individualDetails.setSafiDeviceId("10101010");
        return individualDetails;
    }

    private TwoFASecuredClient getGenericClientDetails() {
        GenericClientInvestorImpl client = new GenericClientInvestorImpl();
        client.setSafiDeviceId("10101010");
        client.setSafiActive(true);
        return client;
    }

    public WrapAccount getWrapAccount(final AccountStatus status) {
        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getAccountStatus()).thenReturn(status);
        Mockito.when(account.isOpen()).thenReturn(false);
        Mockito.when(account.getAccountKey()).thenReturn(AccountKey.valueOf("id1"));
        Mockito.when(account.getMinCashAmount()).thenReturn(BigDecimal.ZERO);
        Mockito.when(account.isHasMinCash()).thenReturn(false);
        return account;
    }

    public User getUserRepositoryObj(boolean accepted, Date date) {
        User userRepositoryObj = new User();
        userRepositoryObj.setTncAccepted(accepted);
        userRepositoryObj.setTncAcceptedOn(date);
        return userRepositoryObj;
    }

    public ApplicationDocument getApplicationDocument(ApplicationStatus status, boolean approver, boolean hasApproved) {
        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppState(status);
        applicationDocument.setPersonDetails(Arrays.asList(getAssociatedPerson(approver, hasApproved)));
        applicationDocument.setPortfolio(new ArrayList<>(Arrays.asList(getPortfolio("id1"))));
        return applicationDocument;
    }

    public AssociatedPerson getAssociatedPerson(boolean approver, boolean hasApproved) {
        AssociatedPerson associatedPerson = new AssociatedPersonImpl();
        associatedPerson.setClientKey(ClientKey.valueOf("fakeClientKey1"));
        associatedPerson.setHasToAcceptTnC(approver);
        associatedPerson.setHasApprovedTnC(hasApproved);
        return associatedPerson;
    }

    public LinkedPortfolioDetails getPortfolio(String accountId) {
        LinkedPortfolioDetails portfolio = new LinkedPortfolioDetailsImpl();
        portfolio.setPortfolioId(accountId);
        return portfolio;
    }

    private class UserInformationTestImpl implements UserInformation {

        private final JobProfile profile;

        private final ClientKey clientKey;

        private final List<FunctionalRole> functionalRoles;

        UserInformationTestImpl(JobProfile profile, ClientKey clientKey, List<FunctionalRole> functionalRoles) {
            this.profile = profile;
            this.clientKey = clientKey;
            this.functionalRoles = functionalRoles;
        }

        @Override
        public ClientKey getClientKey() {
            return this.clientKey;
        }

        @Override
        public void setClientKey(ClientKey personId) {

        }

        @Override
        public List<FunctionalRole> getFunctionalRoles() {
            return this.functionalRoles;
        }

        @Override
        public void setFunctionalRoles(List<FunctionalRole> functionalRoles) {

        }

        @Override
        public List<String> getUserRoles() {
            return null;
        }

        @Override
        public JobKey getJob() {
            return profile.getJob();
        }

        @Override
        public String getProfileId() {
            return profile.getProfileId();
        }

        @Override
        public String getFullName() {
            return null;
        }

        @Override
        public String getFirstName() {
            return null;
        }

        @Override
        public String getLastName() {
            return null;
        }

        @Override
        public String getSafiDeviceId() {
            return null;
        }

    }

    @Test
    public void test_PortfolioManagerJobRole() {
        assertFalse(userProfileServiceSpring.isPortfolioManager());

        createLoggedInProfile("adviser", "adviser", ROLE_ADVISER, false);
        JobProfileImpl tmpProfile = new JobProfileImpl();
        tmpProfile.setJob(JobKey.valueOf("fakeJobKey2"));
        tmpProfile.setProfileId("fakeProfileId2");
        tmpProfile.setJobRole(JobRole.PORTFOLIO_MANAGER);
        tmpProfile.setCloseDate(null);
        tmpProfile.setPersonJobId("fakePersonJobId2");
        tmpProfile.setUserExperience(UserExperience.ADVISED);

        List<JobProfile> fakeJobProfileList = new ArrayList<JobProfile>();
        fakeJobProfileList.add(tmpProfile);
        when(profileIntegrationService.loadAvailableJobProfiles(any(ServiceErrors.class))).thenReturn(fakeJobProfileList);

        when(userInformationIntegrationService.loadUserInformation(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(
                createUserInfo(tmpProfile));
        when(clientIntegrationService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(
                (getIndividualDetails()));
        assertTrue(userProfileServiceSpring.isPortfolioManager());
    }

    @Test
    public void test_getPortfolioManagerBroker_withNoPMBroker() {
        when(brokerIntegrationService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(
                Collections.EMPTY_LIST);
        Broker broker = userProfileServiceSpring.getInvestmentManager(new ServiceErrorsImpl());
        Assert.isNull(broker);
    }

    @Test
    public void test_getPortfolioManagerBroker_wherePMBrokerExists() {
        Broker advBroker = mock(Broker.class);
        when(advBroker.getBrokerType()).thenReturn(BrokerType.ADVISER);
        Broker pmBroker1 = mock(Broker.class);
        when(pmBroker1.getKey()).thenReturn(BrokerKey.valueOf("pm1"));
        when(pmBroker1.getBrokerType()).thenReturn(BrokerType.PORTFOLIO_MANAGER);
        Broker pmBroker2 = mock(Broker.class);
        when(pmBroker2.getBrokerType()).thenReturn(BrokerType.PORTFOLIO_MANAGER);
        when(pmBroker2.getKey()).thenReturn(BrokerKey.valueOf("pm2"));

        List<Broker> brokers = Arrays.asList(advBroker, pmBroker1, pmBroker2);
        when(brokerIntegrationService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(
                brokers);
        Broker broker = userProfileServiceSpring.getInvestmentManager(new ServiceErrorsImpl());
        assertEquals(BrokerType.PORTFOLIO_MANAGER, broker.getBrokerType());
        assertEquals(pmBroker1.getKey(), broker.getKey());
    }
}
