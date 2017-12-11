package com.bt.nextgen.api.tracking.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.tracking.model.PersonInfo;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingAccount;
import com.bt.nextgen.core.repository.OnboardingAccountRepository;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyRepository;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationIdentifierImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.ApplicationIdentifier;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.broker.BrokerWrapperImpl;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;

@RunWith(MockitoJUnitRunner.class)
public class TrackingDtoServiceImplTest {

    @Mock
    private ClientApplicationRepository repository;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private TrackingDtoConverterService trackingDtoConverter;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private AccActivationIntegrationService accActivationIntegrationService;

    @Mock
    private OnboardingPartyRepository partyRepository;

    @Mock
    private OnboardingAccountRepository accountRepository;

    @Mock
    private ClientIntegrationService clientService;

    @Mock
    private ProductIntegrationService productIntegrationService;
    

    @InjectMocks
    private TrackingDtoServiceImpl trackingDtoService;

    private void mockDataForAdviserIdAndLastModified(String adviserId, String lastModified) {
        BrokerUser brokerUser = mock(BrokerUser.class);  
        Broker broker = mock(Broker.class);
        when(brokerService.getBroker(eq(BrokerKey.valueOf(adviserId)), any(ServiceErrors.class))).thenReturn(broker);

        when(brokerService.getBrokerUser(eq(UserKey.valueOf(lastModified)), any(ServiceErrors.class))).thenReturn(brokerUser);
        
        Map<BrokerKey, BrokerWrapper> mapOfBrokerWrapper = new HashMap<>();
        BrokerWrapper brokerWrapper = new BrokerWrapperImpl(BrokerKey.valueOf(adviserId), brokerUser, true, "");
        mapOfBrokerWrapper.put(BrokerKey.valueOf(adviserId), brokerWrapper);
        when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(mapOfBrokerWrapper);
    }

    private void mockDataForAdviserIdAndLastModifiedWithDetailInfo(String adviserId, String lastModified, boolean businessPhoneNeeded) {
        Email email = new EmailImpl(AddressKey.valueOf("something"), AddressMedium.EMAIL_PRIMARY, "test@test.com", "1", false, AddressType.ELECTRONIC);

        Phone mobilePhone = new PhoneImpl(AddressKey.valueOf("something"), AddressMedium.MOBILE_PHONE_PRIMARY, "98989898", "61", "04", "2", false, AddressType.ELECTRONIC);
        List<Phone> phones = new ArrayList<Phone>();
        phones.add(mobilePhone);

        if(businessPhoneNeeded) {
            Phone businessPhone = new PhoneImpl(AddressKey.valueOf("something"), AddressMedium.BUSINESS_TELEPHONE, "44445555", "61", "02", "2", false, AddressType.ELECTRONIC);
            phones.add(businessPhone);
        }

        BrokerUser adviserBrokerUser = mock(BrokerUser.class);
        when(adviserBrokerUser.getFirstName()).thenReturn("FirstName");
        when(adviserBrokerUser.getLastName()).thenReturn("LastName");
        when(adviserBrokerUser.getEmails()).thenReturn(Arrays.asList(email));
        when(adviserBrokerUser.getPhones()).thenReturn(phones);

        Broker broker = mock(Broker.class);
        when(broker.getPositionName()).thenReturn("PositionName");
        when(brokerService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);

        BrokerUser lastModifiedBrokerUser = mock(BrokerUser.class);
        when(lastModifiedBrokerUser.getBankReferenceKey()).thenReturn(UserKey.valueOf(lastModified));
        when(brokerService.getBrokerUser(eq(UserKey.valueOf(lastModified)), any(ServiceErrors.class))).thenReturn(lastModifiedBrokerUser);
        Map<BrokerKey, BrokerWrapper> mapOfBrokerWrapper = new HashMap<>();
        BrokerWrapper brokerWrapper = new BrokerWrapperImpl(BrokerKey.valueOf(adviserId), adviserBrokerUser, true, "");
        mapOfBrokerWrapper.put(BrokerKey.valueOf(adviserId), brokerWrapper);
        when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(mapOfBrokerWrapper);
    }


    @Test
    public void searchShouldReturnListOfDraftAccountDtoBetweenDatesAndApplicableAdvisersInCriteria() {
        String adviserId = "ADVISER_ID";
        String orderId = "someOrderId";
        String lastModifiedId = "lastModifiedId";
        mockDataForAdviserIdAndLastModified(adviserId, lastModifiedId);
        UserProfile activeProfile = mock(UserProfile.class);
        List<BrokerIdentifier> brokerIdentifiers = Arrays.<BrokerIdentifier>asList(getBrokerIdentifier(adviserId));
        List<WrapAccount> accounts = Arrays.asList(createWrapAccount());

        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerService.getAdvisersForUser(activeProfile, serviceErrors)).thenReturn(brokerIdentifiers);
        when(accountService.loadWrapAccounts(serviceErrors)).thenReturn(accounts);

        DateTime from = new DateTime(2011, 1, 1, 0, 0);
        DateTime to = new DateTime(2012, 1, 1, 0, 0);

        ClientApplication clientApplication = createApplication(adviserId, 1, orderId, ClientApplicationStatus.processing, false, lastModifiedId);
        ClientApplication clientApplicationWithoutOrder = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);

        List<ApiSearchCriteria> searchCriteria = getApiSearchCriterias();

        when(repository.findNonActiveApplicationsBetweenDates(from.toDate(), to.plusDays(1).minusSeconds(1).toDate(), brokerIdentifiers)).thenReturn(Arrays.asList(clientApplicationWithoutOrder, clientApplication));

        ApplicationIdentifierImpl applicationIdentifier = new ApplicationIdentifierImpl();
        applicationIdentifier.setDocId(orderId);

        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppNumber(orderId);
        when(accActivationIntegrationService.loadAccApplicationForApplicationId(Arrays.asList((ApplicationIdentifier) applicationIdentifier), userProfileService.getActiveProfile().getJobRole(),userProfileService.getActiveProfile().getClientKey(),serviceErrors)).thenReturn(Arrays.asList(applicationDocument));

        List<TrackingDto> trackingDtos = trackingDtoService.search(searchCriteria, serviceErrors);
        assertThat(trackingDtos.size(), equalTo(2));
        verify(accountRepository, times(1)).findByOnboardingApplicationIds(anyList()); // Do not return account info on tracking page.
    }

    @Test
    public void searchShouldIncludeOnlyApplicableAdviserInCriteriaBeforeFetchingApplications() {
        String adviserOne = "102345";
        String adviserTwo = "123456";
        mockDataForAdviserIdAndLastModified(adviserOne, "any");
        UserProfile activeProfile = mock(UserProfile.class);
        List<BrokerIdentifier> brokerIdentifiers = Arrays.<BrokerIdentifier>asList(getBrokerIdentifier(adviserOne), getBrokerIdentifier
        (adviserTwo));

        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerService.getAdvisersForUser(activeProfile, serviceErrors)).thenReturn(brokerIdentifiers);

        List<ApiSearchCriteria> searchCriteria = getApiSearchCriteriaWithAdviser(adviserOne);

        when(accActivationIntegrationService.loadAccApplicationForApplicationId(any(List.class),any(JobRole.class), any(ClientKey.class), eq(serviceErrors))).thenReturn(new
                ArrayList<ApplicationDocument>());

        trackingDtoService.search(searchCriteria, serviceErrors);

        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(repository).findNonActiveApplicationsBetweenDates(any(Date.class), any(Date.class), argument.capture());
        assertThat(argument.getValue().size(), is(1));
    }

    @Test
    public void searchShouldNotFetchApplicationDocumentIfThereAreNoApplicationsInAvaloq() {
        String adviserId = "ADVISER_ID";
        String lastModifiedId = "lastModifiedId";
        mockDataForAdviserIdAndLastModified(adviserId, lastModifiedId);
        UserProfile activeProfile = mock(UserProfile.class);
        List<BrokerIdentifier> brokerIdentifiers = Arrays.<BrokerIdentifier>asList(getBrokerIdentifier(adviserId));

        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerService.getAdvisersForUser(activeProfile, serviceErrors)).thenReturn(brokerIdentifiers);

        DateTime from = new DateTime(2011, 1, 1, 0, 0);
        DateTime to = new DateTime(2012, 1, 1, 0, 0);

        ClientApplication clientApplication = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);
        ClientApplication clientApplicationWithoutOrder = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);

        List<ApiSearchCriteria> searchCriteria = getApiSearchCriterias();
        when(repository.findNonActiveApplicationsBetweenDates(from.toDate(), to.plusDays(1).minusSeconds(1).toDate(), brokerIdentifiers))
        .thenReturn(Arrays.asList(clientApplicationWithoutOrder, clientApplication));
        List<TrackingDto> trackingDtos = trackingDtoService.search(searchCriteria, serviceErrors);
        assertThat(trackingDtos.size(), equalTo(2));
    }

    @Test
    public void searchShouldInvokeBrokerServiceOnlyOnceIfAllApplicationsHaveSameAdviserIdWithDetailInfo() {
        final String adviserId = "ADVISER_ID";
        final String lastModifiedId = "lastModifiedId";
        mockDataForAdviserIdAndLastModifiedWithDetailInfo(adviserId, lastModifiedId, true);

        ClientApplication clientApplication = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);
        ClientApplication clientApplicationWithoutOrder = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);

        when(trackingDtoConverter.convertToDto(Mockito.any(ClientApplication.class), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap()
                , Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<TrackingDto>>() {

            @Override
            public List<TrackingDto> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Map<String, PersonInfo> adviserIdMap = (Map<String, PersonInfo>) invocationOnMock.getArguments()[5];
                Map<String, BrokerUser> lastModifiedMap = (Map<String, BrokerUser>) invocationOnMock.getArguments()[6];

                assertThat(lastModifiedMap.size(), is(1));
                assertNotNull(lastModifiedMap.get(lastModifiedId));

                assertThat(adviserIdMap.size(), is(1));
                PersonInfo personInfo = adviserIdMap.get(adviserId);
                assertThat(personInfo.getEmailId(), is("test@test.com"));
                assertThat(personInfo.getBusinessPhone(), is("0244445555"));
                assertThat(personInfo.getMobilePhone(), is("0498989898"));

                return null;
            }
        });

        trackingDtoService.getTrackingDtos(Arrays.asList(clientApplication, clientApplicationWithoutOrder), true, true, serviceErrors);

        verify(brokerService, times(1)).getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), any(ServiceErrors.class));
    }

    @Test
    public void shouldNotThrowExceptionIfBusinessPhoneMissingForAdviserWhenIfDoFetchAdviserDetailsFlagSetAsTrue() throws Exception {
        final String adviserId = "ADVISER_ID";
        final String lastModifiedId = "lastModifiedId";
        mockDataForAdviserIdAndLastModifiedWithDetailInfo(adviserId, lastModifiedId, false);

        ClientApplication clientApplication = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);

        when(trackingDtoConverter.convertToDto(Mockito.any(ClientApplication.class), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap()
                , Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<TrackingDto>>() {

            @Override
            public List<TrackingDto> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Map<String, PersonInfo> adviserIdMap = (Map<String, PersonInfo>) invocationOnMock.getArguments()[5];

                assertThat(adviserIdMap.size(), is(1));
                PersonInfo personInfo = adviserIdMap.get(adviserId);
                assertThat(personInfo.getEmailId(), is("test@test.com"));
                assertNull(personInfo.getBusinessPhone());
                assertThat(personInfo.getMobilePhone(), is("0498989898"));

                return null;
            }
        });

        trackingDtoService.getTrackingDtos(Arrays.asList(clientApplication), true, true, serviceErrors);
        verify(brokerService, times(1)).getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), any(ServiceErrors.class));
    }

    @Test
    public void shouldHaveBlankAdviserIfMappingFromIdToBrokerUserFailsWithIllegalArgumentException() throws Exception {
        final String adviserId = "ADVISER_ID";
        final String lastModifiedId = "lastModifiedId";
        when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenThrow(IllegalArgumentException.class);

        ClientApplication clientApplication = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);

        when(trackingDtoConverter.convertToDto(Mockito.any(ClientApplication.class), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap()
                , Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<TrackingDto>>() {

            @Override
            public List<TrackingDto> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Map<String, PersonInfo> adviserIdMap = (Map<String, PersonInfo>) invocationOnMock.getArguments()[5];

                assertThat(adviserIdMap.size(), is(0));
                return null;
            }
        });
        trackingDtoService.getTrackingDtos(Arrays.asList(clientApplication), true, true, serviceErrors);
    }

    @Test
    public void shouldHaveBlankAdviserIfMappingFromIdToBrokerUserFailsWithIllegalStateException() throws Exception {
        final String adviserId = "ADVISER_ID";
        final String lastModifiedId = "lastModifiedId";
        when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenThrow(IllegalStateException.class);

        ClientApplication clientApplication = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);

        when(trackingDtoConverter.convertToDto(Mockito.any(ClientApplication.class), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap()
                , Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<TrackingDto>>() {

            @Override
            public List<TrackingDto> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Map<String, PersonInfo> adviserIdMap = (Map<String, PersonInfo>) invocationOnMock.getArguments()[5];

                assertThat(adviserIdMap.size(), is(0));
                return null;
            }
        });
        trackingDtoService.getTrackingDtos(Arrays.asList(clientApplication), true, true, serviceErrors);
    }
    
    @Test
    public void shouldHaveBlankAdviserNull() throws Exception {
        final String adviserId = "ADVISER_ID";
        final String lastModifiedId = "lastModifiedId";
        
        
          
        Broker broker = mock(Broker.class);
        Map<BrokerKey, BrokerWrapper> mapOfBrokerWrapper = new HashMap<>();
        BrokerWrapper brokerWrapper = new BrokerWrapperImpl(BrokerKey.valueOf(adviserId), null, true, "");
        mapOfBrokerWrapper.put(BrokerKey.valueOf(adviserId), brokerWrapper);
        
        when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(mapOfBrokerWrapper);

        ClientApplication clientApplication = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);

        when(trackingDtoConverter.convertToDto(Mockito.any(ClientApplication.class), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap()
                , Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<TrackingDto>>() {

            @Override
            public List<TrackingDto> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Map<String, PersonInfo> adviserIdMap = (Map<String, PersonInfo>) invocationOnMock.getArguments()[5];

                assertThat(adviserIdMap.size(), is(1));
                PersonInfo personInfo = adviserIdMap.get(adviserId);
                assertThat(personInfo.getFirstName(), is(""));
                assertThat(personInfo.getLastName(), is(""));
                return null;
            }
        });
        trackingDtoService.getTrackingDtos(Arrays.asList(clientApplication), true, true, serviceErrors);
    }

    @Test
    public void shouldNotTryToFetchLastModifiedIdForDirectAccountType() throws Exception {
        final String adviserId = "ADVISER_ID";
        final String lastModifiedId = "lastModifiedId";
        when(brokerService.getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenThrow(IllegalArgumentException.class);

        ClientApplication clientApplication = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, true, lastModifiedId);

        trackingDtoService.getTrackingDtos(Arrays.asList(clientApplication), true, true, serviceErrors);
        verify(brokerService, times(0)).getBrokerUser(eq(UserKey.valueOf(lastModifiedId)), any(ServiceErrors.class));
    }

    @Test
    public void shouldHaveMinimumAdviserInfoByDefault() throws Exception {
        final String adviserId = "ADVISER_ID";
        final String lastModifiedId = "lastModifiedId";
        mockDataForAdviserIdAndLastModifiedWithDetailInfo(adviserId, lastModifiedId, false);

        ClientApplication clientApplication = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);

        when(trackingDtoConverter.convertToDto(Mockito.any(ClientApplication.class), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap()
                , Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<List<TrackingDto>>() {

            @Override
            public List<TrackingDto> answer(InvocationOnMock invocationOnMock) throws Throwable {
                Map<String, PersonInfo> adviserIdMap = (Map<String, PersonInfo>) invocationOnMock.getArguments()[5];

                assertThat(adviserIdMap.size(), is(1));
                PersonInfo personInfo = adviserIdMap.get(adviserId);
                Assert.assertThat(personInfo.getFirstName(), is("FirstName"));
                Assert.assertThat(personInfo.getLastName(), is("LastName"));
                assertNull(personInfo.getEmailId());
                assertNull(personInfo.getBusinessPhone());
                assertNull(personInfo.getMobilePhone());
                return null;
            }
        });

        trackingDtoService.getTrackingDtos(Arrays.asList(clientApplication), true, false, serviceErrors);

        verify(brokerService, times(1)).getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), any(ServiceErrors.class));
        verify(brokerService, times(1)).getBrokerUser(eq(UserKey.valueOf(lastModifiedId)), any(ServiceErrors.class));
    }


    @Test
    public void getTrackingDtosShouldInvokeBrokerServiceTwiceIfAllApplicationsHaveTwoDistinctAdviserIds() {
        String adviserId1 = "adviserId_1";
        String adviserId2 = "adviserId_2";
        String lastModifiedId = "lastModifiedId";
        mockDataForAdviserIdAndLastModified(adviserId1, lastModifiedId);
        mockDataForAdviserIdAndLastModified(adviserId2, lastModifiedId);

        ClientApplication clientApplication = createApplication(adviserId1, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);
        ClientApplication clientApplicationWithoutOrder = createApplication(adviserId2, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);
        List<TrackingDto> trackingDtos = trackingDtoService.getTrackingDtos(Arrays.asList(clientApplication, clientApplicationWithoutOrder), true, true, serviceErrors);
        assertThat(trackingDtos.size(), equalTo(2));
        verify(brokerService, times(1)).getAdviserBrokerUser(Mockito.anyListOf(BrokerKey.class), any(ServiceErrors.class));
    }

    @Test
    public void searchShouldInvokeBrokerServiceOnlyOnceIfAllApplicationsHaveSameLastModifiedId() {
        String adviserId = "ADVISER_ID";
        String lastModifiedId = "lastModifiedId";
        mockDataForAdviserIdAndLastModified(adviserId, lastModifiedId);

        ClientApplication clientApplication = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);
        ClientApplication clientApplicationWithoutOrder = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);

        List<TrackingDto> trackingDtos = trackingDtoService.getTrackingDtos(Arrays.asList(clientApplication, clientApplicationWithoutOrder), true, true, serviceErrors);
        assertThat(trackingDtos.size(), equalTo(2));
        verify(brokerService, times(1)).getBrokerUser(eq(UserKey.valueOf(lastModifiedId)), any(ServiceErrors.class));
    }

    @Test
    public void searchShouldReturnListOfUnapprovedAccountDtoBetweenDatesAndApplicableAdvisersInCriteria() throws ParseException {
        String adviserId = "ADVISER_ID";
        String orderId = "someOrderId";
        String lastModifiedId = "lastModifiedId";
        mockDataForAdviserIdAndLastModified(adviserId, lastModifiedId);
        UserProfile activeProfile = mock(UserProfile.class);
        List<BrokerIdentifier> brokerIdentifiers = Arrays.<BrokerIdentifier>asList(getBrokerIdentifier(adviserId));
        List<WrapAccount> accounts = Arrays.asList(createWrapAccount());

        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerService.getAdvisersForUser(activeProfile, serviceErrors)).thenReturn(brokerIdentifiers);
        when(accountService.loadWrapAccounts(serviceErrors)).thenReturn(accounts);

        DateTime from = new DateTime(2011, 1, 1, 0, 0);
        DateTime to = new DateTime(2012, 1, 1, 0, 0);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
        Date fromDate = formatter.parse("01/12/2010");
        Date toDate = formatter.parse("31/01/2012");

        ClientApplication clientApplication = createApplication(adviserId, 1, orderId, ClientApplicationStatus.processing, false, lastModifiedId);
        ClientApplication clientApplicationWithoutOrder = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);

        when(repository.findNonActiveApplicationsBetweenDates(from.toDate(), to.plusDays(1).minusSeconds(1).toDate(), brokerIdentifiers)).thenReturn(Arrays.asList(clientApplicationWithoutOrder, clientApplication));

        ApplicationIdentifierImpl applicationIdentifier = new ApplicationIdentifierImpl();
        applicationIdentifier.setDocId(orderId);

        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppNumber(orderId);
        OnboardingAccount onboardingAccount = new OnboardingAccount(new Long(221), OnboardingApplicationKey.valueOf(1));
        when(accActivationIntegrationService.loadAccApplicationForApplicationId(Arrays.asList((ApplicationIdentifier)
        applicationIdentifier), userProfileService.getActiveProfile().getJobRole(),userProfileService.getActiveProfile().getClientKey(),serviceErrors)).thenReturn(Arrays.asList(applicationDocument));
        when(repository.findNonActiveApplicationsBetweenDates(fromDate, toDate)).thenReturn(Arrays.asList(clientApplication));
        when(productIntegrationService.loadProductsMap(Mockito.any(ServiceErrors.class))).thenReturn(null);
        when(accountRepository.findByOnboardingApplicationIds(Mockito.anyList())).thenReturn(Arrays.asList(onboardingAccount));
        when(partyRepository.findOnboardingPartiesByApplicationIds(Mockito.anyList())).thenReturn(new ArrayList<OnboardingParty>());
        TrackingDto convertedDto = new TrackingDto(DateTime.now(), "newIndividualSMSF", new ClientApplicationKey());
        convertedDto.setStatus(OnboardingApplicationStatus.smsfinProgress);
        when(trackingDtoConverter.convertToDto(Mockito.any(ClientApplication.class), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap()
        , Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.any(ServiceErrors.class))).thenReturn(convertedDto);
        List<TrackingDto> trackingDtos = trackingDtoService.searchForUnapprovedApplications(fromDate, toDate, serviceErrors);
        assertThat(trackingDtos.size(), equalTo(1));
        verify(accountRepository, times(1)).findByOnboardingApplicationIds(anyList());
    }

    @Test
    public void searchShouldReturnListOfUnapprovedAccountDto_notInProgress() throws ParseException {
        String adviserId = "ADVISER_ID";
        String orderId = "someOrderId";
        String lastModifiedId = "lastModifiedId";
        mockDataForAdviserIdAndLastModified(adviserId, lastModifiedId);
        UserProfile activeProfile = mock(UserProfile.class);
        List<BrokerIdentifier> brokerIdentifiers = Arrays.<BrokerIdentifier>asList(getBrokerIdentifier(adviserId));
        List<WrapAccount> accounts = Arrays.asList(createWrapAccount());

        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerService.getAdvisersForUser(activeProfile, serviceErrors)).thenReturn(brokerIdentifiers);
        when(accountService.loadWrapAccounts(serviceErrors)).thenReturn(accounts);

        DateTime from = new DateTime(2011, 1, 1, 0, 0);
        DateTime to = new DateTime(2012, 1, 1, 0, 0);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
        Date fromDate = formatter.parse("01/12/2010");
        Date toDate = formatter.parse("31/01/2012");

        ClientApplication clientApplication = createApplication(adviserId, 1, orderId, ClientApplicationStatus.processing, false, lastModifiedId);
        ClientApplication clientApplicationWithoutOrder = createApplication(adviserId, 1, null, ClientApplicationStatus.processing, false, lastModifiedId);

        when(repository.findNonActiveApplicationsBetweenDates(from.toDate(), to.plusDays(1).minusSeconds(1).toDate(), brokerIdentifiers)).thenReturn(Arrays.asList(clientApplicationWithoutOrder, clientApplication));

        ApplicationIdentifierImpl applicationIdentifier = new ApplicationIdentifierImpl();
        applicationIdentifier.setDocId(orderId);

        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppNumber(orderId);
        OnboardingAccount onboardingAccount = new OnboardingAccount(new Long(221), OnboardingApplicationKey.valueOf(1));
        when(accActivationIntegrationService.loadAccApplicationForApplicationId(Arrays.asList((ApplicationIdentifier)
        applicationIdentifier), userProfileService.getActiveProfile().getJobRole(),userProfileService.getActiveProfile().getClientKey(),serviceErrors)).thenReturn(Arrays.asList(applicationDocument));
        when(repository.findNonActiveApplicationsBetweenDates(fromDate, toDate)).thenReturn(Arrays.asList(clientApplication));
        when(productIntegrationService.loadProductsMap(Mockito.any(ServiceErrors.class))).thenReturn(null);
        when(accountRepository.findByOnboardingApplicationIds(Mockito.anyList())).thenReturn(Arrays.asList(onboardingAccount));
        when(partyRepository.findOnboardingPartiesByApplicationIds(Mockito.anyList())).thenReturn(new ArrayList<OnboardingParty>());
        TrackingDto convertedDto = new TrackingDto(DateTime.now(), "newIndividualSMSF", new ClientApplicationKey());
        convertedDto.setStatus(OnboardingApplicationStatus.awaitingApproval);
        when(trackingDtoConverter.convertToDto(Mockito.any(ClientApplication.class), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap()
        , Mockito.anyMap(), Mockito.anyMap(), Mockito.anyMap(), Mockito.any(ServiceErrors.class))).thenReturn(convertedDto);
        List<TrackingDto> trackingDtos = trackingDtoService.searchForUnapprovedApplications(fromDate, toDate, serviceErrors);
        assertThat(trackingDtos.size(), equalTo(1));
        verify(accountRepository, times(1)).findByOnboardingApplicationIds(anyList());
    }

    private WrapAccount createWrapAccount() {
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountKey(AccountKey.valueOf("123"));
        return wrapAccount;
    }

    private ClientApplication createApplication(final String adviserId, final long id, final String orderId, final ClientApplicationStatus status, boolean isDirect, String lastModifiedId) {
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getAdviserPositionId()).thenReturn(adviserId);
        when(clientApplication.getId()).thenReturn(id);
        when(clientApplication.getStatus()).thenReturn(status);
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        when(clientApplicationForm.isDirectAccount()).thenReturn(isDirect);
        when(clientApplication.getClientApplicationForm()).thenReturn(clientApplicationForm);
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getAvaloqOrderId()).thenReturn(orderId);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        when(clientApplication.getLastModifiedId()).thenReturn(lastModifiedId);
        when(onBoardingApplication.getKey()).thenReturn(OnboardingApplicationKey.valueOf(id));
        return clientApplication;
    }

    private List<ApiSearchCriteria> getApiSearchCriterias() {
        List<ApiSearchCriteria> searchCriteria = new ArrayList<>(2);
        searchCriteria.add(new ApiSearchCriteria("fromdate", ApiSearchCriteria.SearchOperation.GREATER_THAN,
                "01-01-2011", ApiSearchCriteria.OperationType.DATE));
        searchCriteria.add(new ApiSearchCriteria("todate", ApiSearchCriteria.SearchOperation.LESS_THAN,
                "01-01-2012", ApiSearchCriteria.OperationType.DATE));
        return searchCriteria;
    }

    private List<ApiSearchCriteria> getApiSearchCriteriaWithAdviser(String adviserId) {
        List<ApiSearchCriteria> searchCriteria = new ArrayList<>(2);
        searchCriteria.add(new ApiSearchCriteria("fromdate", ApiSearchCriteria.SearchOperation.GREATER_THAN,
                "01-01-2011", ApiSearchCriteria.OperationType.DATE));
        searchCriteria.add(new ApiSearchCriteria("todate", ApiSearchCriteria.SearchOperation.LESS_THAN,
                "01-01-2012", ApiSearchCriteria.OperationType.DATE));
        searchCriteria.add(new ApiSearchCriteria("adviserPositionId", ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText(adviserId).toString(), ApiSearchCriteria.OperationType.STRING));
        return searchCriteria;
    }

    private BrokerIdentifierImpl getBrokerIdentifier(String adviserId) {
        BrokerIdentifierImpl broker = new BrokerIdentifierImpl();
        broker.setKey(BrokerKey.valueOf(adviserId));
        return broker;
    }
}