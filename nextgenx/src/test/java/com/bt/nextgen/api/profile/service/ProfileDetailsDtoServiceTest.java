package com.bt.nextgen.api.profile.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.api.profile.v1.service.ProfileUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bt.nextgen.api.profile.model.HomePageEnum;
import com.bt.nextgen.api.profile.model.ProfileDetailsDto;
import com.bt.nextgen.badge.model.Badge;
import com.bt.nextgen.badge.service.BadgingService;
import com.bt.nextgen.core.repository.User;
import com.bt.nextgen.core.repository.UserPreference;
import com.bt.nextgen.core.repository.UserPreferenceKey;
import com.bt.nextgen.core.repository.UserPreferenceRepository;
import com.bt.nextgen.core.repository.UserRepository;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.bt.nextgen.service.avaloq.accountactivation.AssociatedPersonImpl;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetails;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedPortfolioDetailsImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepository;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.bt.nextgen.web.controller.HomePageController;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfileDetailsDtoServiceTest {
    @InjectMocks
    private ProfileDetailsDtoServiceImpl profileDetailsDtoService;

    @Mock
    private InvestorProfileService profileService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private CustomerCredentialInformation customerCredentialInformation;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleTermsAndConditionsRepository userRoleTermsAndConditionsRepository;

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    @Mock
    private BadgingService badgingService;

    @Mock
    private InvestorHomepageEvaluator investorHomepageEvaluator;

    @Mock
    private ProfileUtil profileUtil;

    private UserProfile activeProfile;
    private List<JobProfile> availableProfiles;
    private ServiceErrors serviceErrors;
    private Map<AccountKey, WrapAccount> accountList;
    private List<ApplicationDocument> applications;
    private BrokerUser brokerUser;
    private Broker dealerGroup;
    private BrokerImpl broker;

    @Before
    public void setup() {
        availableProfiles = getAvailableProfiles();
        serviceErrors = new FailFastErrorsImpl();
        brokerUser = getBrokerUser();
        dealerGroup = getDealerGroup();
        activeProfile = getProfile(JobRole.INVESTOR, "job id 2", "client2", "cust2");
        when(profileService.isExistingAvaloqUser()).thenReturn(true);
        when(profileService.getEffectiveProfile()).thenReturn(new MockProfile(null));
        when(profileService.getFullName()).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);
        when(profileUtil.getProfileId()).thenReturn("1234");
        final Broker adviser = getAdviser();
        when(brokerService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(singletonList(adviser));
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(brokerService.getAdvisersForUser(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(
            singletonList(getBrokerIdentifier()));
        broker = new BrokerImpl(BrokerKey.valueOf("broker_key"), BrokerType.ADVISER);
        broker.setParentEBIKey(ExternalBrokerKey.valueOf("not a WAPC"));
        broker.setPositionName("Broker Company");
        broker.setPracticeKey(BrokerKey.valueOf("123"));
        when(brokerService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
        when(profileService.getDealerGroupBroker()).thenReturn(broker);
        when(profileService.switchActiveProfile(anyString())).thenReturn(activeProfile);
        when(profileService.getUsername()).thenReturn("user1");
        when(profileService.getPositionId()).thenAnswer(
            new Answer<String>() {
                @Override
                public String answer(InvocationOnMock invocation) throws Throwable {
                    if (activeProfile.getJobRole().equals(JobRole.INVESTOR)) {
                        return "";
                    } else {
                        return "26180";
                    }
                }
            });

        Badge badge = mock(Badge.class);
        when(badge.getBadgeName()).thenReturn("badgeName");
        when(badge.getLogo()).thenReturn("logo");
        when(badgingService.getBadgeForCurrentUser(any(ServiceErrors.class))).thenReturn(badge);
        UserRoleTermsAndConditions userRoleTnc = new UserRoleTermsAndConditions();
        userRoleTnc.setModifyDatetime(new Date());
        userRoleTnc.setTncAccepted("Y");
        userRoleTnc.setTncAcceptedOn(new Date());
        userRoleTnc.setVersion(1);
        userRoleTnc.setUserRoleTermsAndConditionsKey(new UserRoleTermsAndConditionsKey("200160234", "12345"));
        when(userRoleTermsAndConditionsRepository.find(any(UserRoleTermsAndConditionsKey.class))).thenReturn(userRoleTnc);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession mockHttpSession = (MockHttpSession) request.getSession();
        mockHttpSession.setAttribute("originatingSystem", "WLIVE");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        doNothing().when(investorHomepageEvaluator).setInvestorHomepageDetails(any(ProfileDetailsDto.class), anyMap(), any(ServiceErrors.class));
    }

    @Test
    public void testFindSuccessForAdviser() {
        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 6);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(2).getProfileId().toString()), "job id 1");
        assertEquals(result.getRoles().get(2).getRole(), "Adviser");
        assertEquals(result.getRoles().get(2).isActive(), true);
        assertEquals(result.getRoles().get(2).getCount(), 1);
        assertEquals(result.getRoles().get(2).getNames().size(), 1);
        assertEquals(result.getRoles().get(2).getNames(), asList("Broker Company"));
        assertEquals(EncodedString.toPlainText(result.getRoles().get(3).getProfileId().toString()), "job id 4");
        assertEquals(result.getRoles().get(3).getRole(), "Assistant");
        assertEquals(result.getRoles().get(3).isActive(), false);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(4).getProfileId().toString()), "job id 2");
        assertEquals(result.getRoles().get(4).getRole(), "Investor");
        assertEquals(result.getRoles().get(4).isActive(), false);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(5).getProfileId().toString()), "job id 3");
        assertEquals(result.getRoles().get(5).getRole(), "Paraplanner");
        assertEquals(result.getRoles().get(5).isActive(), false);
        assertEquals(result.getHomePage(), "actDashboard");
        assertEquals(result.isClientMessage(), true);
        assertEquals(result.isIntermediary(), true);
        assertEquals(result.getIntermediaryCount(), 1);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertEquals(result.getDealerGroupName(), "Dealer Group 1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
        assertEquals(result.getLogoUrl(), "logo");
        assertEquals(result.getLogoTitle(), "badgeName");
    }

    @Test
    public void testFindSuccessForAccountant() {
        activeProfile = getProfile(JobRole.ACCOUNTANT, "job id 5", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 6);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(0).getProfileId().toString()), "job id 5");
        assertEquals(result.getRoles().get(0).getRole(), "Accountant");
        assertEquals(result.getRoles().get(0).isActive(), true);
        assertEquals(result.getRoles().get(0).getCount(), 0);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(1).getProfileId().toString()), "job id 6");
        assertEquals(result.getRoles().get(1).getRole(), "Accountant Assistant");
        assertEquals(result.getRoles().get(1).isActive(), false);
        assertEquals(result.getHomePage(), "clientList");
        assertEquals(result.isClientMessage(), true);
        assertEquals(result.isIntermediary(), true);
        assertEquals(result.getIntermediaryCount(), 0);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertEquals(result.getDealerGroupName(), "Dealer Group 1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testFindSuccessForAccountantStaff() {
        activeProfile = getProfile(JobRole.ACCOUNTANT_SUPPORT_STAFF, "job id 6", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 6);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(1).getProfileId().toString()), "job id 6");
        assertEquals(result.getRoles().get(1).getRole(), "Accountant Assistant");
        assertEquals(result.getRoles().get(1).isActive(), true);
        assertEquals(result.getRoles().get(1).getCount(), 0);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(2).getProfileId().toString()), "job id 1");
        assertEquals(result.getRoles().get(2).getRole(), "Adviser");
        assertEquals(result.getRoles().get(2).isActive(), false);
        assertEquals(result.getHomePage(), "clientList");
        assertEquals(result.isClientMessage(), true);
        assertEquals(result.isIntermediary(), true);
        assertEquals(result.getIntermediaryCount(), 0);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertEquals(result.getDealerGroupName(), "Dealer Group 1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testFindSuccessForParaplannerLinkedToMultipleAdvisers() {
        activeProfile = getProfile(JobRole.PARAPLANNER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);

        List<Broker> advisers = asList(getAdviser(), getAdviser(), getAdviser());
        when(brokerService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(advisers);
        when(brokerService.getAdvisersForUser(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(
                asList(getBrokerIdentifier(), getBrokerIdentifier(), getBrokerIdentifier()));
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getHomePage(), "actDashboard");
        assertEquals(result.getIntermediaryCount(), 3);
        assertEquals(result.isClientMessage(), true);
        assertEquals(result.isIntermediary(), true);
        assertEquals(result.getDealerGroupName(), "Dealer Group 1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
        assertEquals(result.getRoles().get(5).getCount(), 3);
        assertEquals(result.getRoles().get(5).getNames().size(), 3);
        assertEquals(result.getRoles().get(5).getNames(), asList("Homer Simpson", "Homer Simpson", "Homer Simpson"));
    }

    @Test
    public void testFindSuccessForParaplannerLinkedToMoreThanFiveAdvisers() {
        activeProfile = getProfile(JobRole.PARAPLANNER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);

        List<Broker> advisers = asList(getAdviser(), getAdviser(), getAdviser(), getAdviser(), getAdviser(), getAdviser(),
                getAdviser(), getAdviser());
        when(brokerService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(advisers);
        when(brokerService.getAdvisersForUser(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(
                asList(getBrokerIdentifier(), getBrokerIdentifier(), getBrokerIdentifier()));
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().get(3).getCount(), 8);
        assertEquals(result.getRoles().get(3).getNames().size(), 5);
        assertEquals(result.getRoles().get(3).getNames(),
                asList("Homer Simpson", "Homer Simpson", "Homer Simpson", "Homer Simpson", "Homer Simpson"));
    }

    @Test
    public void testFindSuccessForParaplannerLinkedToOneAdviser() {
        activeProfile = getProfile(JobRole.PARAPLANNER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);
        when(brokerService.getAdvisersForUser(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(
                asList(getBrokerIdentifier()));
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 6);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(2).getProfileId()), "job id 1");
        assertEquals(result.getRoles().get(2).getRole(), "Adviser");
        assertEquals(result.getRoles().get(2).isActive(), false);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(4).getProfileId()), "job id 2");
        assertEquals(result.getRoles().get(4).getRole(), "Investor");
        assertEquals(result.getRoles().get(4).isActive(), false);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(5).getProfileId()), "job id 3");
        assertEquals(result.getRoles().get(5).getRole(), "Paraplanner");
        assertEquals(result.getRoles().get(5).isActive(), true);
        assertEquals(result.getHomePage(), "actDashboard");
        assertEquals(result.isClientMessage(), true);
        assertEquals(result.isIntermediary(), true);
        assertEquals(result.getIntermediaryCount(), 1);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertEquals(result.getDealerGroupName(), "Dealer Group 1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testFindSuccessForParaplannerLinkedToDealerGroup() {
        activeProfile = getProfile(JobRole.PARAPLANNER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(asList(getJobProfile(JobRole.PARAPLANNER, "job id 3")));
        when(brokerService.getAdvisersForUser(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(null);
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        List<Broker> brokers = asList(getDealerGroup());
        when(brokerService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(brokers);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 1);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(0).getProfileId()), "job id 3");
        assertEquals(result.getRoles().get(0).getRole(), "Paraplanner");
        assertEquals(result.getRoles().get(0).isActive(), true);
        assertEquals(result.getRoles().get(0).getNames().size(), 1);
        assertEquals(result.getRoles().get(0).getNames(), asList("Dealer Group 1"));
        assertEquals(result.getHomePage(), "actDashboard");
        assertEquals(result.isClientMessage(), true);
        assertEquals(result.isIntermediary(), true);
        assertEquals(result.getIntermediaryCount(), 0);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertEquals(result.getDealerGroupName(), "Dealer Group 1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testFindSuccessForParaplanner() {
        activeProfile = getProfile(JobRole.PARAPLANNER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);
        when(profileService.getDealerGroupBroker()).thenReturn(null);
        when(brokerService.getAdvisersForUser(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(
            asList(getBrokerIdentifier(), getBrokerIdentifier(), getBrokerIdentifier()));
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 6);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(2).getProfileId()), "job id 1");
        assertEquals(result.getRoles().get(2).getRole(), "Adviser");
        assertEquals(result.getRoles().get(2).isActive(), false);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(4).getProfileId()), "job id 2");
        assertEquals(result.getRoles().get(4).getRole(), "Investor");
        assertEquals(result.getRoles().get(4).isActive(), false);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(5).getProfileId()), "job id 3");
        assertEquals(result.getRoles().get(5).getRole(), "Paraplanner");
        assertEquals(result.getRoles().get(5).isActive(), true);
        assertEquals(result.getHomePage(), "actDashboard");
        assertEquals(result.isClientMessage(), true);
        assertEquals(result.isIntermediary(), true);
        assertEquals(result.getIntermediaryCount(), 3);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertNull(result.getDealerGroupName());
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testFindSuccessForAssistantLinkedToOneAdviser() {
        activeProfile = getProfile(JobRole.ASSISTANT, "job id 4", "client1", "cust1");
        when(profileService.switchActiveProfile(anyString())).thenReturn(activeProfile);
        when(profileService.getFullName()).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);
        when(brokerService.getAdvisersForUser(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(
                singletonList(getBrokerIdentifier()));
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 6);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(0).getProfileId().toString()), "job id 5");
        assertEquals(result.getRoles().get(0).getRole(), "Accountant");
        assertEquals(result.getRoles().get(0).isActive(), false);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(2).getProfileId().toString()), "job id 1");
        assertEquals(result.getRoles().get(2).getRole(), "Adviser");
        assertEquals(result.getRoles().get(2).isActive(), false);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(3).getProfileId().toString()), "job id 4");
        assertEquals(result.getRoles().get(3).getRole(), "Assistant");
        assertEquals(result.getRoles().get(3).isActive(), true);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(1).getProfileId().toString()), "job id 6");
        assertEquals(result.getRoles().get(1).getRole(), "Accountant Assistant");
        assertEquals(result.getRoles().get(1).isActive(), false);
        assertEquals(result.getHomePage(), "actDashboard");
        assertEquals(result.isClientMessage(), true);
        assertEquals(result.isIntermediary(), true);
        assertEquals(result.getIntermediaryCount(), 1);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testFindSuccessForAssistantLinkedToMultipleAdvisers() {
        activeProfile = getProfile(JobRole.ASSISTANT, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);
        when(brokerService.getAdvisersForUser(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(
                asList(getBrokerIdentifier(), getBrokerIdentifier(), getBrokerIdentifier(), getBrokerIdentifier()));
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getHomePage(), "actDashboard");
        assertEquals(result.isIntermediary(), true);
        assertEquals(result.getIntermediaryCount(), 4);
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testFindSuccessForAssistantLinkedToDealerGroup() {
        activeProfile = getProfile(JobRole.PARAPLANNER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(asList(getJobProfile(JobRole.ASSISTANT, "job id 3")));
        when(brokerService.getAdvisersForUser(any(UserProfile.class), any(ServiceErrors.class))).thenReturn(null);
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);

        List<Broker> brokers = asList(getDealerGroup());
        when(brokerService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(brokers);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 1);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(0).getProfileId()), "job id 3");
        assertEquals(result.getRoles().get(0).getRole(), "Assistant");
        assertEquals(result.getRoles().get(0).isActive(), true);
        assertEquals(result.getRoles().get(0).getNames().size(), 1);
        assertEquals(result.getRoles().get(0).getNames(), asList("Dealer Group 1"));
        assertEquals(result.getHomePage(), "actDashboard");
        assertEquals(result.isClientMessage(), true);
        assertEquals(result.isIntermediary(), true);
        assertEquals(result.getIntermediaryCount(), 0);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertEquals(result.getDealerGroupName(), "Dealer Group 1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testFindSuccessForInvestorWithAdviserDetailsAndMoreThanOneAccount() {
        activeProfile = getProfile(JobRole.INVESTOR, "job id 2", "client2", "cust2");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);

        List<Broker> advisers = asList(getAdviser(), getAdviser(), getAdviser());
        when(brokerService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(advisers);
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 6);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(0).getProfileId().toString()), "job id 5");
        assertEquals(result.getRoles().get(0).getRole(), "Accountant");
        assertEquals(result.getRoles().get(0).isActive(), false);
        assertEquals(result.getRoles().get(0).getCount(), 0);
        assertEquals(result.getRoles().get(4).getNames(), null);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(4).getProfileId().toString()), "job id 2");
        assertEquals(result.getRoles().get(4).getRole(), "Investor");
        assertEquals(result.getRoles().get(4).isActive(), true);
        assertEquals(result.getRoles().get(4).getCount(), 0);
        assertEquals(result.getRoles().get(4).getNames(), null);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(5).getProfileId().toString()), "job id 3");
        assertEquals(result.getRoles().get(5).getRole(), "Paraplanner");
        assertEquals(result.getRoles().get(5).isActive(), false);
        assertEquals(result.getRoles().get(5).getCount(), 3);
        assertEquals(result.getRoles().get(5).getNames().size(), 3);
        assertEquals(result.isClientMessage(), false);
        assertEquals(result.isIntermediary(), false);
        assertEquals(result.getIntermediaryCount(), 0);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client2");
        assertEquals(result.getId(), "cust2");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(null, result.getPositionId());
        assertNull(result.getAccountCategory());
        assertNull(result.getAccountId());
    }

    @Test
    public void testFindSuccessForInvestorWithMoreThanOneAccountAndLastAccessedActiveAccount() {
        activeProfile = getProfile(JobRole.INVESTOR, "job id 2", "client2", "cust2");
        accountList = new HashMap<>();
        accountList.put(AccountKey.valueOf("accountId1"), getAccount("accountId1", AccountStatus.ACTIVE));
        accountList.put(AccountKey.valueOf("accountId2"), getAccount("accountId2", AccountStatus.PEND_OPN));
        List<AssociatedPerson> associatedPersons = new ArrayList<>(asList(getAssociatedPerson("client2", false),
                getAssociatedPerson("client1", false), getAssociatedPerson("client3", false)));
        applications = asList(
                getApplication(ApplicationStatus.OPEN, associatedPersons, new Date(1, 1, 2015), "id1"),
                getApplication(ApplicationStatus.PEND_ACCEPT, associatedPersons, new Date(1, 1, 2015), "id2"));
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);

        List<Broker> advisers = asList(getAdviser(), getAdviser(), getAdviser());
        when(brokerService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(advisers);
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountList);
        when(userPreferenceRepository.find(anyString(), anyString())).thenReturn(getLastAccessedAccount("accountId1"));

        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client2");
        assertEquals(result.getId(), "cust2");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(null, result.getPositionId());
        assertNotNull(result.getAccountId());
    }

    @Test
    public void testFindSuccessForInvestorWithMoreThanOneAccountAndLastAccessedInActiveAccount() {
        ApplicationDocument applicationDocument = mock(ApplicationDocument.class);
        activeProfile = getProfile(JobRole.INVESTOR, "job id 2", "client2", "cust2");
        accountList = new HashMap<>();
        accountList.put(AccountKey.valueOf("accountId1"), getAccount("accountId1", AccountStatus.ACTIVE));
        accountList.put(AccountKey.valueOf("accountId2"), getAccount("accountId2", AccountStatus.PEND_OPN));
        List<AssociatedPerson> associatedPersons = new ArrayList<>(asList(getAssociatedPerson("client2", false),
                getAssociatedPerson("client1", false), getAssociatedPerson("client3", false)));
        applications = asList(
                getApplication(ApplicationStatus.OPEN, associatedPersons, new Date(1, 1, 2015), "id1"),
                getApplication(ApplicationStatus.PEND_ACCEPT, associatedPersons, new Date(1, 1, 2015), "id2"));
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        when(applicationDocument.getBpid()).thenReturn(AccountKey.valueOf("TEST2"));
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);

        List<Broker> advisers = asList(getAdviser(), getAdviser(), getAdviser());
        when(brokerService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(advisers);
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountList);
        when(userPreferenceRepository.find(anyString(), anyString())).thenReturn(getLastAccessedAccount("accountId2"));

        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client2");
        assertEquals(result.getId(), "cust2");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(null, result.getPositionId());
        assertNull(result.getAccountCategory());
        assertNull(result.getAccountId());
    }

    @Test
    public void testFindSuccessForInvestorWithoutAdviserBrokerAndOneAccount() {
        activeProfile = getProfile(JobRole.INVESTOR, "job id 2", "client2", "cust2");
        accountList = new HashMap<>();
        accountList.put(AccountKey.valueOf("id1"), getAccount("id1", AccountStatus.ACTIVE));
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);
        when(profileService.getPositionId()).thenReturn("");
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.isClientMessage(), false);
        assertEquals(result.getIntermediaryCount(), 0);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client2");
        assertEquals(result.getId(), "cust2");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(null, result.getPositionId());
        assertNull(result.getAccountCategory());
        assertNull(result.getAccountId());
    }

    @Test
    public void testFindSuccessForInvestorWithoutAdviserDetailsAndOneAccount() {
        activeProfile = getProfile(JobRole.INVESTOR, "job id 2", "client2", "cust 2");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when(profileService.getAvailableProfiles()).thenReturn(availableProfiles);
        when(brokerService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(profileService.getPositionId()).thenReturn("");
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 6);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(0).getProfileId().toString()), "job id 5");
        assertEquals(result.getRoles().get(0).getRole(), "Accountant");
        assertEquals(result.getRoles().get(0).isActive(), false);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(4).getProfileId().toString()), "job id 2");
        assertEquals(result.getRoles().get(4).getRole(), "Investor");
        assertEquals(result.getRoles().get(4).isActive(), true);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(5).getProfileId().toString()), "job id 3");
        assertEquals(result.getRoles().get(5).getRole(), "Paraplanner");
        assertEquals(result.getRoles().get(5).isActive(), false);
        assertEquals(result.isClientMessage(), false);
        assertEquals(result.getIntermediaryCount(), 0);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client2");
        assertEquals(null, result.getPositionId());
    }

    @Test
    public void findOneShouldReturnProfileDetailsDtoWithWestpacAdviserSetFalseIfNoDealerGroupBroker() {
        when(profileService.getDealerGroupBroker()).thenReturn(null);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertFalse(result.isWestpacAdviser());
    }

    @Test
    public void findOneShouldReturnProfileDetailsDtoWithWestpacAdviserSetFalseIfNoParentEbiKeyProvided() {
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertFalse(result.isWestpacAdviser());
    }

    @Test
    public void findOneShouldReturnProfileDetailsDtoWithWestpacAdviserSetFalse() {
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertFalse(result.isWestpacAdviser());
    }

    @Test
    public void findOneShouldReturnProfileDetailsDtoWithWestpacAdviserSetTrue() {
        broker.setParentEBIKey(ExternalBrokerKey.valueOf("WPAC"));
        activeProfile = getProfile(JobRole.ADVISER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertTrue(result.isWestpacAdviser());
    }

    @Test
    public void findOneShouldReturnProfileDetailsDtoWithWestpacBrandedAdviserSetTrue() {
        activeProfile = getProfile(JobRole.ADVISER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        broker.setExternalBrokerKey(ExternalBrokerKey.valueOf("DG.WPBWS"));
        broker.setCanRetrieveGcmData(true);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertTrue(result.isWestpacBrandedAdviser());
    }

    @Test
    public void findOneShouldReturnProfileDetailsDtoWithWestpacBrandedAdviserSetFalse_forInvestor() {
        activeProfile = getProfile(JobRole.INVESTOR, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertFalse(result.isWestpacBrandedAdviser());
    }

    @Test
    public void testIntermediaryDisplayWhatsNewNullVersion() {
        activeProfile = getProfile(JobRole.ADVISER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when(userRepository.loadUser(anyString())).thenReturn(getUserDetails(null));
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.isWhatsNew(), true);
    }

    @Test
    public void testIntermediaryDisplayWhatsNewDifferentVersion() {
        activeProfile = getProfile(JobRole.ASSISTANT, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when(userRepository.loadUser(anyString())).thenReturn(getUserDetails("123"));
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.isWhatsNew(), true);
    }

    @Test
    public void testIntermediaryDisplayWhatsNewNullUser() {
        activeProfile = getProfile(JobRole.PRACTICE_MANAGER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when(userRepository.loadUser(anyString())).thenReturn(null);
        when(userRepository.newUser("user1")).thenReturn(getUserDetails(null));
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.isWhatsNew(), true);
    }

    @Test
    public void testIntermediaryDontDisplayWhatsNew() {
        activeProfile = getProfile(JobRole.PARAPLANNER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when(userRepository.loadUser(anyString())).thenReturn(
                getUserDetails(com.bt.nextgen.core.util.Properties.get("version")));
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.isWhatsNew(), false);
    }

    @Test
    public void testInvestorDisplayWhatsNewDifferentVersion() {
        activeProfile = getProfile(JobRole.INVESTOR, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when(userRepository.loadUser(anyString())).thenReturn(getUserDetails("123"));
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.isWhatsNew(), true);
    }

    @Test
    public void testInvestorDontDisplayWhatsNew() {
        activeProfile = getProfile(JobRole.INVESTOR, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when(userRepository.loadUser(anyString())).thenReturn(
                getUserDetails(com.bt.nextgen.core.util.Properties.get("version")));
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.isWhatsNew(), false);
    }

    @Test
    public void testInvestmentManagerDontDisplayWhatsNew() {
        activeProfile = getProfile(JobRole.INVESTMENT_MANAGER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.isWhatsNew(), false);
    }

    @Test
    public void testFindSuccessForPracticeManager() {
        activeProfile = getProfile(JobRole.PRACTICE_MANAGER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getHomePage(), "monitorDashboard");
        assertEquals(result.isClientMessage(), true);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testFindSuccessForDealerGroupManager() {
        activeProfile = getProfile(JobRole.DEALER_GROUP_MANAGER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getHomePage(), "monitorDashboard");
        assertEquals(result.isClientMessage(), true);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testFindSuccessForInvestmentManager() {
        activeProfile = getProfile(JobRole.INVESTMENT_MANAGER, "job id 3", "client1", "cust1");
        when((profileService.switchActiveProfile(anyString()))).thenReturn(activeProfile);
        when((profileService.getFullName())).thenReturn("Homer Simpson");
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getHomePage(), "modelStatus");
        assertEquals(result.isClientMessage(), false);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testSwitchActiveProfileToDefault() {
        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1", "cust1");
        when(profileService.getEffectiveProfile()).thenReturn(new MockProfile(null));
        when(userPreferenceRepository.find(anyString(), anyString())).thenReturn(getUserPreference());
        when(profileService.switchActiveProfile(anyString())).thenReturn(activeProfile);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 6);
        assertEquals(EncodedString.toPlainText(result.getRoles().get(2).getProfileId().toString()), "job id 1");
        assertEquals(result.getRoles().get(2).getRole(), "Adviser");
        assertEquals(result.getRoles().get(2).isActive(), true);
        assertEquals(result.getHomePage(), "actDashboard");
        assertEquals(result.isClientMessage(), true);
        assertEquals(result.isIntermediary(), true);
        assertEquals(result.getIntermediaryCount(), 1);
        assertEquals(EncodedString.toPlainText(result.getPersonId()), "client1");
        assertEquals(result.getName(), "Homer Simpson");
        assertEquals(result.getId(), "cust1");
        assertEquals(result.getDealerGroupName(), "Dealer Group 1");
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testSwitchActiveProfile_noDefaultFound() {
        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1", "cust1");
        when(profileService.getEffectiveProfile()).thenReturn(new MockProfile(null));
        when(userPreferenceRepository.find(anyString(), anyString())).thenReturn(null);
        when(profileService.switchActiveProfile(anyString())).thenReturn(activeProfile);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 6);
        assertEquals(EncodedString.toPlainText(result.getPositionId()), "26180");
    }

    @Test
    public void testEmulatingProfile() {
        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1", "cust1");
        when(profileService.getEffectiveProfile()).thenReturn(new MockProfile(null));
        when(userPreferenceRepository.find(anyString(), anyString())).thenReturn(getUserPreference());
        when(profileService.switchActiveProfile(anyString())).thenReturn(activeProfile);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        ProfileDetailsDto result = profileDetailsDtoService.findOne(serviceErrors);
        assertEquals(result.getRoles().size(), 6);
    }

    @Test
    public void isWestpacAdviserForWpacParent() {
        Broker dealerGroup = mock(Broker.class);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        when(dealerGroup.getParentEBIKey()).thenReturn(ExternalBrokerKey.valueOf("WPAC"));
        assertTrue(profileDetailsDtoService.isWestpacAdviser());
    }

    @Test
    public void isWestpacAdviserForBtfgParent() {
        Broker dealerGroup = mock(Broker.class);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        when(dealerGroup.getParentEBIKey()).thenReturn(ExternalBrokerKey.valueOf("BTFG"));
        assertFalse(profileDetailsDtoService.isWestpacAdviser());
    }

    @Test
    public void isWestpacAdviserForNullParentEbiKey() {
        Broker dealerGroup = mock(Broker.class);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        when(dealerGroup.getParentEBIKey()).thenReturn(null);
        assertFalse(profileDetailsDtoService.isWestpacAdviser());
    }

    @Test
    public void isWestpacAdviserForNullDealerGroup() {
        when(profileService.getDealerGroupBroker()).thenReturn(null);
        assertFalse(profileDetailsDtoService.isWestpacAdviser());
    }

    @Test
    public void isWestpacBrandedAdviser_whenCanRetrieveGCMData_true() {
        Broker dealerGroup = mock(Broker.class);
        when(dealerGroup.canRetrieveGcmData()).thenReturn(true);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        when(dealerGroup.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf("DG.WFP"));
        assertTrue(profileDetailsDtoService.isWestpacBrandedAdviser());
    }

    @Test
    public void isWestpacBrandedAdviser_whenCanRetrieveGCMData_false() {
        Broker dealerGroup = mock(Broker.class);
        when(dealerGroup.canRetrieveGcmData()).thenReturn(false);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        when(dealerGroup.getExternalBrokerKey()).thenReturn(ExternalBrokerKey.valueOf("DG.WFP"));
        assertFalse(profileDetailsDtoService.isWestpacBrandedAdviser());
    }

    @Test
    public void isWestpacBrandedAdviserForNullParentEbiKey() {
        Broker dealerGroup = mock(Broker.class);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        when(dealerGroup.getExternalBrokerKey()).thenReturn(null);
        assertFalse(profileDetailsDtoService.isWestpacBrandedAdviser());
    }

    @Test
    public void isWestpacBrandedAdviserForNullDealerGroup() {
        when(profileService.getDealerGroupBroker()).thenReturn(null);
        assertFalse(profileDetailsDtoService.isWestpacBrandedAdviser());
    }


    @Test
    public void existingAvaloqUser() {
        ProfileDetailsDto profile = profileDetailsDtoService.findOne(serviceErrors);
        assertNotNull(profile.getPersonId());
    }

    @Test
    public void westpacUser_notExistingInAvaloq() {
        when(profileService.isExistingAvaloqUser()).thenReturn(false);
        ProfileDetailsDto profile = profileDetailsDtoService.findOne(serviceErrors);
        assertNull(profile.getPersonId());
        assertEquals(profile.getHomePage(), HomePageEnum.DIRECT_ONBOARDING.toString());
        assertTrue(profile.isCanNavigate());
    }

    @Test
    public void findShouldReturnProfileWithOriginatingSystemForWestpacUser() {
        when(profileService.isExistingAvaloqUser()).thenReturn(false);
        ProfileDetailsDto profile = profileDetailsDtoService.findOne(serviceErrors);
        assertNull(profile.getPersonId());
        assertThat(profile.getHomePage(), is(HomePageEnum.DIRECT_ONBOARDING.toString()));
        assertThat(profile.isCanNavigate(), is(true));
        assertThat(profile.getOriginatingSystem(), is(HomePageController.CHANNEL_WESTPAC_LIVE));
    }

    @Test
    public void offlineApprovalFlag() {
        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "adviser1", "cust1");
        when(profileService.switchActiveProfile(anyString())).thenReturn(activeProfile);
        when(profileService.getDealerGroupBroker()).thenReturn(dealerGroup);
        when(dealerGroup.isOfflineApproval()).thenReturn(true);
        ProfileDetailsDto profile = profileDetailsDtoService.findOne(serviceErrors);
        assertTrue(profile.isOfflineApproval());
    }

    private UserPreference getUserPreference() {
        UserPreference userPref = new UserPreference();
        userPref.setKey(new UserPreferenceKey("user1", "defaultrole"));
        userPref.setValue(EncodedString.fromPlainText("jobprofileid1").toString());
        return userPref;
    }

    private WrapAccount getAccount(final String accountId, final AccountStatus accountStatus) {
        WrapAccount account = mock(WrapAccount.class);
        when(account.getAccountStatus()).thenReturn(accountStatus);
        when(account.isOpen()).thenReturn(false);
        when(account.getAccountKey()).thenReturn(AccountKey.valueOf(accountId));
        when(account.getMinCashAmount()).thenReturn(BigDecimal.ZERO);
        when(account.isHasMinCash()).thenReturn(false);
        return account;
    }

    private ApplicationDocument getApplication(ApplicationStatus status, List<AssociatedPerson> associatedPersons,
                                              Date appSubmittedDate, String accountId) {
        ApplicationDocument app = new ApplicationDocumentImpl();
        app.setAppState(status);
        app.setBpid(AccountKey.valueOf("accountId2"));
        app.setPersonDetails(associatedPersons);
        app.setAppSubmitDate(appSubmittedDate);
        app.setAppNumber("12345");
        app.setPortfolio(new ArrayList<>(asList(getPortfolio(accountId))));
        return app;
    }

    private AssociatedPerson getAssociatedPerson(String clientId, boolean hasToAccept) {
        AssociatedPerson associatedPerson = new AssociatedPersonImpl();
        associatedPerson.setClientKey(ClientKey.valueOf(clientId));
        associatedPerson.setHasToAcceptTnC(hasToAccept);
        return associatedPerson;
    }

    private LinkedPortfolioDetails getPortfolio(String accountId) {
        LinkedPortfolioDetails portfolio = new LinkedPortfolioDetailsImpl();
        portfolio.setPortfolioId(accountId);
        return portfolio;
    }

    private List<JobProfile> getAvailableProfiles() {
        return asList(getJobProfile(JobRole.PARAPLANNER, "job id 3"),
                getJobProfile(JobRole.ADVISER, "job id 1"),
                getJobProfile(JobRole.INVESTOR, "job id 2"),
                getJobProfile(JobRole.ASSISTANT, "job id 4"),
                getJobProfile(JobRole.ACCOUNTANT, "job id 5"),
                getJobProfile(JobRole.ACCOUNTANT_SUPPORT_STAFF, "job id 6"));
    }

    private UserProfile getProfile(final JobRole role, final String jobId, final String clientId, final String customerId) {
        UserInformation user = getUserInformation(jobId, clientId);
        user.setClientKey(ClientKey.valueOf(clientId));
        JobProfile job = getJobProfile(role, jobId);
        CustomerCredentialInformation credential = getCredentialInformation(customerId);
        return new UserProfileAdapterImpl(user, job, credential);
    }

    private UserInformation getUserInformation(final String jobId, final String clientId) {
        UserInformation userInfo = mock(UserProfile.class);
        when(userInfo.getProfileId()).thenReturn(jobId);
        when(userInfo.getJob()).thenReturn(JobKey.valueOf(jobId));
        when(userInfo.getClientKey()).thenReturn(ClientKey.valueOf(clientId));
        return userInfo;
    }

    private JobProfile getJobProfile(final JobRole role, final String jobId) {
        return getJobProfile(role, jobId, jobId);
    }

    private JobProfile getJobProfile(final JobRole role, final String jobId, final String profileId) {
        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));
        job.setProfileId(profileId);
        return job;
    }

    private CustomerCredentialInformation getCredentialInformation(final String customerId) {
        CustomerCredentialInformation credential = mock(CustomerCredentialInformation.class);
        when(credential.getBankReferenceId()).thenReturn(customerId);
        return credential;
    }

    private Broker getDealerGroup() {
        Broker dealerGroup = mock(Broker.class);
        when(dealerGroup.getDealerKey()).thenReturn(BrokerKey.valueOf("Dealer1"));
        when(dealerGroup.getPositionName()).thenReturn("Dealer Group 1");
        when(dealerGroup.getBrokerType()).thenReturn(BrokerType.DEALER);
        return dealerGroup;
    }

    private Broker getAdviser() {
        Broker adviserDetails = mock(Broker.class);
        when(adviserDetails.getKey()).thenReturn(BrokerKey.valueOf("brokerid1"));
        when(adviserDetails.getParentKey()).thenReturn(BrokerKey.valueOf("Dealer1"));
        when(adviserDetails.isPayableParty()).thenReturn(false);
        when(adviserDetails.getBrokerType()).thenReturn(BrokerType.ADVISER);
        return adviserDetails;
    }

    private BrokerUser getBrokerUser() {
        BrokerUser adviserDetails = mock(BrokerUser.class);
        when(adviserDetails.getFirstName()).thenReturn("Homer");
        when(adviserDetails.getLastName()).thenReturn("Simpson");

        List<Email> emails = asList(getEmail("my@email.com", AddressMedium.EMAIL_PRIMARY),
                getEmail("notmy@email.com", AddressMedium.EMAIL_ADDRESS_SECONDARY));
        when(adviserDetails.getEmails()).thenReturn(emails);

        List<Phone> phones = asList(getPhone("0414000111", AddressMedium.MOBILE_PHONE_PRIMARY),
                getPhone("9200111777", AddressMedium.BUSINESS_TELEPHONE));
        when(adviserDetails.getPhones()).thenReturn(phones);
        when(adviserDetails.isRegisteredOnline()).thenReturn(false);
        when(adviserDetails.isRegistrationOnline()).thenReturn(false);
        when(adviserDetails.getAge()).thenReturn(0);
        return adviserDetails;
    }

    private Phone getPhone(final String phoneNumber, final AddressMedium medium) {
        Phone phone = mock(Phone.class);
        when(phone.getType()).thenReturn(medium);
        when(phone.getNumber()).thenReturn(phoneNumber);
        when(phone.isPreferred()).thenReturn(false);
        return phone;
    }

    private Email getEmail(final String emailAddress, final AddressMedium medium) {
        Email email = mock(Email.class);
        when(email.getType()).thenReturn(medium);
        when(email.getEmail()).thenReturn(emailAddress);
        when(email.isPreferred()).thenReturn(false);
        return email;
    }

    private BrokerIdentifier getBrokerIdentifier() {
        return new BrokerIdentifierImpl();
    }

    private User getUserDetails(String version) {
        User userDetails = new User();
        userDetails.setWhatsNewVersion(version);
        return userDetails;
    }

    private class MockProfile extends Profile {

        public MockProfile(SamlToken token) {
            super(token);
        }

        @Override
        public String getGcmId() {
            return "id1";
        }

    }

    private UserPreference getLastAccessedAccount(String accountId) {
        UserPreference userPref = new UserPreference();
        userPref.setKey(new UserPreferenceKey("user1", "lastaccessedaccount"));
        userPref.setValue(accountId);
        return userPref;
    }
}
