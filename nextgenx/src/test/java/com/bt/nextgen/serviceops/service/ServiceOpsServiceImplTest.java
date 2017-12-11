package com.bt.nextgen.serviceops.service;


import com.bt.nextgen.api.account.v3.model.PersonRelationDto;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v3.service.WrapAccountDetailDtoService;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.*;
import com.bt.nextgen.api.client.v2.model.InvestorDto;
import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.ServiceOpsClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ApprovalTypeEnum;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationDetailsDtoConverterService;
import com.bt.nextgen.api.draftaccount.service.ServiceOpsClientApplicationDtoConverterService;
import com.bt.nextgen.api.draftaccount.service.ViewClientApplicationDetailsService;
import com.bt.nextgen.api.tracking.model.PersonInfo;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.api.tracking.service.AccountStatusService;
import com.bt.nextgen.api.tracking.service.TrackingDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.exception.ServiceException;
import com.bt.nextgen.core.repository.CisKeyClientApplicationRepository;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.userauthority.web.Action;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.integration.domain.Phone;
import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.core.security.UserRole;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.CredentialService;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailResponseImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.search.PersonResponseImpl;
import com.bt.nextgen.service.avaloq.search.ProfileUserRoleImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementIntegrationServiceV6;
import com.bt.nextgen.service.integration.account.*;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.*;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.serviceops.model.LeftNavPermissionModel;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.search.PersonResponse;
import com.bt.nextgen.service.integration.search.PersonSearchIntegrationService;
import com.bt.nextgen.service.integration.search.PersonSearchRequest;
import com.bt.nextgen.service.integration.search.ProfileUserRole;
import com.bt.nextgen.service.integration.user.CISKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.integration.userprofile.ProfileIntegrationService;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.bt.nextgen.serviceops.model.WrapAccountModel;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServiceOpsServiceImplTest {
    public static final String CIS_KEY = "123456";

    @InjectMocks
    private ServiceOpsServiceImpl serviceOpsServiceImpl;

    @Mock
    private UserAccountStatusService userAccountStatusService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private CredentialService credentialService;

    @Mock
    private PermittedClientApplicationRepository permittedClientApplicationRepository;

    @Mock
    private ServiceOpsClientApplicationDtoConverterService serviceOpsClientApplicationDtoConverterService;

    @Mock
    private AccountStatusService accountStatusService;

    @Mock
    private TrackingDtoService trackingDtoService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private CisKeyClientApplicationRepository cisKeyClientApplicationRepository;

    @Mock
    private ClientApplicationDetailsDtoConverterService clientApplicationDetailsDtoConverterService;

    @Mock
    ApplicationDocumentIntegrationService applicationDocumentIntegrationService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private ProfileIntegrationService profileIntegrationService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    ViewClientApplicationDetailsService viewClientApplicationDetailsService;

    @Mock
    private CustomerCredentialManagementIntegrationServiceV6 customerCredentialManagementIntegrationServiceV6;
    
    @Mock
    private StaticIntegrationService staticIntegrationService;
    
    @Mock
    private PersonSearchIntegrationService personSearchIntegrationService;
    
    @Mock
    private UserInformationIntegrationService userInformationIntegrationService;

    @Mock
    private WrapAccountDetailDtoService wrapAccountDetailDtoService;
    
    private static String SEARCH_CRITERIA = "e";

    @Mock
    private ProductIntegrationService productService;

    @Mock
    private CmsService cmsService;

    @Before
    public  void setUp(){
        when(cmsService.getContent("uim0140"))
                .thenReturn("The user's mobile number and email are required before the user record can be created.");
    }

    @Test
    public void getUserDetail_DoesntReturnDuplicateDealerGroups() throws Exception {
        mockClientDetails();
        mockUserAccountService();
        mockJobProfile();
        mockAdviserListForInvestor();
        mockAccountDetailsByGCMForMigratedClient(false);
        UserProfile userProfile = mock(UserProfile.class);
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfileService.getActiveProfile().getUserRoles()).thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC.getRole()));
        final ServiceOpsModel userDetail = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());
        final Set<String> dealerGroupList = userDetail.getDealerGroupList();
        assertThat(dealerGroupList.size(), is(1));
        assertThat(dealerGroupList.iterator().next(), is("Direct"));
    }
    @Test
    public void getUserDetail_checkWestPacLiveRole()throws Exception{
        mockClientDetails();
        mockUserAccountService();
        mockJobProfile();
        mockAdviserListForInvestor();
        mockCredentialServiceForWestPacLiveCustomer();
        mockAccountDetailsByGCMForMigratedClient(false);
        UserProfile userProfile = mock(UserProfile.class);
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfileService.getActiveProfile().getUserRoles()).thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC.getRole()));
        ServiceOpsModel userDetail = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());
        assertThat(userDetail.isWestpacLive(),is(true));
        mockCredentialServiceForNonWestPacLiveCustomer();
        userDetail = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());
        assertThat(userDetail.isWestpacLive(),is(false));
        mockCredentialServiceForEmptyResult();
        userDetail = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());
        assertThat(userDetail.isWestpacLive(),is(false));

    }

    private void mockCredentialServiceForNonWestPacLiveCustomer() {
        List<Roles> roles=new ArrayList<>();
        roles.add(Roles.ROLE_INVESTOR);
        //final  CredentialService credentialService=mock(CredentialService.class);
        when(credentialService.getCredentialGroups("gcmId")).thenReturn(roles);
    }

    private void mockCredentialServiceForWestPacLiveCustomer() {
        List<Roles> roles=new ArrayList<>();
        roles.add(Roles.ROLE_INVESTOR);
        roles.add(Roles.ROLE_WPL);
        //final  CredentialService credentialService=mock(CredentialService.class);
        when(credentialService.getCredentialGroups("gcmId")).thenReturn(roles);
    }

    private void mockCredentialServiceForEmptyResult() {
        //final  CredentialService credentialService=mock(CredentialService.class);
        when(credentialService.getCredentialGroups("gcmId")).thenReturn(null);
    }

    private void mockAdviserListForInvestor() {
        final Broker broker1 = mock(Broker.class);
        when(broker1.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerKey1"));

        final Broker broker2 = mock(Broker.class);
        when(broker2.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerKey1"));

        when(brokerHelperService.getAdviserListForInvestor(any(BankingCustomerIdentifier.class), any(ServiceErrorsImpl.class)))
                .thenReturn(Arrays.asList(broker1, broker2));

        final Broker dealerGroup = mock(Broker.class);
        when(dealerGroup.getPositionName()).thenReturn("Direct");
        when(brokerIntegrationService.getBroker(eq(BrokerKey.valueOf("dealerKey1")), any(ServiceErrorsImpl.class))).thenReturn(dealerGroup);
        when(brokerIntegrationService.getBroker(eq(BrokerKey.valueOf("dealerKey1")), any(ServiceErrorsImpl.class))).thenReturn(dealerGroup);

    }

    private void mockJobProfile() {
        final JobProfile jobProfile = mock(JobProfile.class);
        when(jobProfile.getJobRole()).thenReturn(JobRole.INVESTOR);
        when(profileIntegrationService.loadAvailableJobProfilesForUser(any(BankingCustomerIdentifier.class), any(ServiceErrorsImpl.class)))
                .thenReturn(Arrays.asList(jobProfile));
        List<JobProfile> jobProfileList = new ArrayList<>();
        JobProfileImpl jp = new JobProfileImpl();
        jobProfileList.add(jp);
        jp.setJobRole(JobRole.ADVISER);
        when(userProfileService.getAvailableProfiles()).thenReturn(jobProfileList);
        UserProfile userProfile = mock(UserProfile.class);
        when(userProfile.getUserRoles()).thenReturn(Collections.singletonList("$UR_SERVICE_UI"));
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);

    }

    private void mockUserAccountService() {
        final UserAccountStatusModel userAccountStatusModel = mock(UserAccountStatusModel.class);
        when(userAccountStatusModel.getUserAccountStatus()).thenReturn(UserAccountStatus.ACTIVE);
        when(userAccountStatusService.lookupStatus(anyString(), anyString(), anyBoolean())).thenReturn(userAccountStatusModel);
    }

    private void mockClientDetails() {
        final PhoneImpl phone =  mock(PhoneImpl.class);
        when(phone.getNumber()).thenReturn("123456789");
        when(phone.getType()).thenReturn(AddressMedium.MOBILE_PHONE_PRIMARY);
        List<Phone> phones = new ArrayList<>();
        phones.add(phone);
        final IndividualDetailImpl clientDetail = mock(IndividualDetailImpl.class);
        when(clientDetail.getFirstName()).thenReturn("FirstName");
        when(clientDetail.getOpenDate()).thenReturn(DateTime.now());
        when(clientDetail.getDateOfBirth()).thenReturn(DateTime.now());
        when(clientDetail.getPhones()).thenReturn(phones);
        when(clientDetail.getClientKey()).thenReturn(ClientKey.valueOf("clientId"));
        when(clientDetail.getAddresses()).thenReturn(Collections.EMPTY_LIST);
        when(clientDetail.getCISKey()).thenReturn(CISKey.valueOf("cisKey"));
        when(clientDetail.getWestpacCustomerNumber()).thenReturn("WpacCustNo");
        when(clientDetail.getSafiDeviceId()).thenReturn("safiId");
        when(clientDetail.getCustomerId()).thenReturn("gcmId");
        when(clientDetail.getGcmId()).thenReturn("gcmId");
        when(clientDetail.getPhones()).thenReturn(phones);
        when(clientDetail.isRegistrationOnline()).thenReturn(true);

        when(clientIntegrationService.loadClientDetails(any(ClientKey.class), any(ServiceErrorsImpl.class))).thenReturn(clientDetail);
    }

    private void mockAccountDetailsByGCMForMigratedClient(boolean isMigratedClient){
        WrapAccountDetailResponse wrapAccountDetailResponse = mock(WrapAccountDetailResponse.class);
        WrapAccountDetailImpl wrapAccountDetail = mock(WrapAccountDetailImpl.class);
        when(wrapAccountDetail.getMigrationKey()).thenReturn(isMigratedClient ? "BTWRAP12345": null);
        when(wrapAccountDetail.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(wrapAccountDetail.getAccountKey()).thenReturn(AccountKey.valueOf("1234567881"));
        when(wrapAccountDetail.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        List<WrapAccountDetail> wrapAccounts = new ArrayList<>();
        wrapAccounts.add(wrapAccountDetail);
        when(wrapAccountDetailResponse.getWrapAccountDetails()).thenReturn(wrapAccounts);
        when(accountIntegrationService.loadWrapAccountDetailByGcm(any(BankReferenceIdentifier.class), any(ServiceErrors.class))).thenReturn(wrapAccountDetailResponse);
    }

    @Test
    public void getFailedApplicationDetails_shouldReturnNullIfTheAccountTypeIsIndividualDirect() {
        ClientApplication clientApplication = getClientApplication(111L, new DateTime(10, 10, 10, 10, 10),
                OnboardingApplicationStatus.failed, IClientApplicationForm.AccountType.INDIVIDUAL, true);
        when(permittedClientApplicationRepository.findByClientApplicationId(eq(111L))).thenReturn(clientApplication);
        when(serviceOpsClientApplicationDtoConverterService.convertToDto(eq(clientApplication),any(ServiceErrors.class))).thenAnswer(new Answer<ServiceOpsClientApplicationDto>() {
            @Override
            public ServiceOpsClientApplicationDto answer(InvocationOnMock invocationOnMock) throws Throwable {
                ClientApplication clientApplication = (ClientApplication) invocationOnMock.getArguments()[0];
                ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = new ServiceOpsClientApplicationDto();
                serviceOpsClientApplicationDto.setKey(new ClientApplicationKey(clientApplication.getId()));
                return serviceOpsClientApplicationDto;
            }
        });
        ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = serviceOpsServiceImpl.getFailedApplicationDetails("R000000111");
        assertNull(serviceOpsClientApplicationDto);
    }

    @Test
    public void getFailedApplicationDetails_shouldReturnTheFailedApplicationIfAccountIsAdvised() {
        ClientApplication clientApplication = getClientApplication(111L, new DateTime(10, 10, 10, 10, 10),
                OnboardingApplicationStatus.failed, IClientApplicationForm.AccountType.COMPANY, false);
        when(permittedClientApplicationRepository.findByClientApplicationId(eq(111L))).thenReturn(clientApplication);
        when(serviceOpsClientApplicationDtoConverterService.convertToDto(eq(clientApplication), any(ServiceErrors.class))).thenAnswer(new Answer<ServiceOpsClientApplicationDto>() {
            @Override
            public ServiceOpsClientApplicationDto answer(InvocationOnMock invocationOnMock) throws Throwable {
                ClientApplication clientApplication = (ClientApplication) invocationOnMock.getArguments()[0];
                ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = new ServiceOpsClientApplicationDto();
                serviceOpsClientApplicationDto.setKey(new ClientApplicationKey(clientApplication.getId()));
                return serviceOpsClientApplicationDto;
            }
        });
        ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = serviceOpsServiceImpl.getFailedApplicationDetails("R000000111");
        assertThat(serviceOpsClientApplicationDto.getKey().getClientApplicationKey(), is(111l));
    }

    @Test
    public void shouldReturnListOfFailedClientApplications() {
        ClientApplication clientApplication1 = getClientApplication(111L, new DateTime(10, 10, 10, 10, 10),
                OnboardingApplicationStatus.failed, IClientApplicationForm.AccountType.INDIVIDUAL, true);
        ClientApplication clientApplication2 = getClientApplication(222L, new DateTime(10, 10, 10, 11, 11),
                OnboardingApplicationStatus.failed, IClientApplicationForm.AccountType.INDIVIDUAL, true);
        when(cisKeyClientApplicationRepository.findClientApplicationsForCisKey(CIS_KEY)).thenReturn(Arrays.asList(clientApplication1, clientApplication2));
        when(serviceOpsClientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), any(ServiceErrors.class))).thenAnswer(new Answer<ServiceOpsClientApplicationDto>() {
            @Override
            public ServiceOpsClientApplicationDto answer(InvocationOnMock invocationOnMock) throws Throwable {
                ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = new ServiceOpsClientApplicationDto();
                serviceOpsClientApplicationDto.setStatus(ServiceOpsClientApplicationStatus.FAILED);
                return serviceOpsClientApplicationDto;
            }
        });
        List<ServiceOpsClientApplicationDto> failedDirectApplications = serviceOpsServiceImpl.getFailedDirectApplications(CIS_KEY);
        assertThat(failedDirectApplications.size(), is(2));
        assertThat(failedDirectApplications.get(0).getStatus(), is(ServiceOpsClientApplicationStatus.FAILED));
    }

    @Test
    public void shouldReturnFailedClientApplicationBasedOnCisKey_whenBothFailedAndProcessingApplicationExist() {
        ClientApplication clientApplication1 = getClientApplication(111L, new DateTime(10, 10, 10, 10, 10),
                OnboardingApplicationStatus.failed, IClientApplicationForm.AccountType.INDIVIDUAL, true);
        ClientApplication clientApplication2 = getClientApplication(222L, new DateTime(10, 10, 10, 11, 11),
                OnboardingApplicationStatus.processing, IClientApplicationForm.AccountType.INDIVIDUAL, true);
        when(cisKeyClientApplicationRepository.findClientApplicationsForCisKey(CIS_KEY)).thenReturn(Arrays.asList(clientApplication1, clientApplication2));
        when(serviceOpsClientApplicationDtoConverterService.convertToDto(eq(clientApplication1), any(ServiceErrors.class))).thenAnswer(new Answer<ServiceOpsClientApplicationDto>() {
            @Override
            public ServiceOpsClientApplicationDto answer(InvocationOnMock invocationOnMock) throws Throwable {
                ClientApplication clientApplication = (ClientApplication) invocationOnMock.getArguments()[0];
                ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = new ServiceOpsClientApplicationDto();
                serviceOpsClientApplicationDto.setKey(new ClientApplicationKey(clientApplication.getId()));
                return serviceOpsClientApplicationDto;
            }
        });
        List<ServiceOpsClientApplicationDto> failedDirectApplications = serviceOpsServiceImpl.getFailedDirectApplications(CIS_KEY);
        assertThat(failedDirectApplications.get(0).getKey().getClientApplicationKey(), is(111l));
    }

	@Test
	public void shouldReturnNull_whenNoFailedApplicationExists() {
        ClientApplication clientApplication = getClientApplication(222L, new DateTime(10, 10, 10, 11, 11),
                OnboardingApplicationStatus.processing, IClientApplicationForm.AccountType.INDIVIDUAL, true);
		when(cisKeyClientApplicationRepository.findClientApplicationsForCisKey(CIS_KEY)).thenReturn(Arrays.asList(clientApplication));
		List<ServiceOpsClientApplicationDto> failedDirectApplications = serviceOpsServiceImpl.getFailedDirectApplications(CIS_KEY);
		assertNull(failedDirectApplications);
	}

	@Test
	public void testDownloadCsvOfAllUnapprovedApplications() throws IOException {
		Date fromDate = DateTime.now().minusMonths(1).toDate();
		Date toDate = DateTime.now().toDate();
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		PersonInfo pInfo1 = mock(PersonInfo.class);
		when(pInfo1.getFirstName()).thenReturn("My first name");
		when(pInfo1.getLastName()).thenReturn("My last name");
		PersonInfo pInfo2 = mock(PersonInfo.class);
		when(pInfo2.getFirstName()).thenReturn("His first name");
		when(pInfo2.getLastName()).thenReturn("His last name");
        TrackingDto dto1 = mockUnapprovedApplication("My first name", "My last name", OnboardingApplicationStatus.smsfcorporateinProgress, ApprovalTypeEnum.ONLINE);
        TrackingDto dto2 = mockUnapprovedApplication("His first name", "His last name", OnboardingApplicationStatus.awaitingApproval, ApprovalTypeEnum.ONLINE);
        TrackingDto dto3 = mockUnapprovedApplication("Offline first", "Offline second", OnboardingApplicationStatus.awaitingApprovalOffline, ApprovalTypeEnum.OFFLINE);
        TrackingDto dto4 = mockUnapprovedApplication("Active First", "Active second", OnboardingApplicationStatus.active, ApprovalTypeEnum.ONLINE);

		List<TrackingDto> dtos = Arrays.asList(dto1, dto2, dto3, dto4);
		when(trackingDtoService.searchForUnapprovedApplications(fromDate, toDate, serviceErrors)).thenReturn(dtos);

		String csvContent = serviceOpsServiceImpl.downloadCsvOfAllUnapprovedApplications(fromDate, toDate, serviceErrors);

		List<String> outputCsvContent = IOUtils.readLines(new StringReader(csvContent.toString()));
		assertThat(outputCsvContent.size(),is(4));//it should filter out active accounts
        String[] header = outputCsvContent.get(0).split(",", -1);

		assertThat("Should be a header and three datalines.", outputCsvContent.size(), is(4));
		assertTrue("Header should contain first column name", header[0].equals("Dealer Group Name"));
		assertTrue("Header should contain last modified column name", header[16].equals("Last Modified Last Name"));
        assertTrue("Header should contain approval type column name", header[11].equals("Approval Type"));

        String[] record1 = outputCsvContent.get(1).split(",", -1);
        String[] record2 = outputCsvContent.get(2).split(",", -1);
        String[] record3 = outputCsvContent.get(3).split(",", -1);

		assertTrue("First data line should contain dto1 values", record1[1].equals(dto1.getAdviser().getFirstName()));
		assertTrue("First data line should contain dto1 values", record1[2].equals(dto1.getAdviser().getLastName()));
		assertFalse("First data line should not contain dto2 values", record1[1].equals(dto2.getAdviser().getFirstName()));
		assertFalse("First data line should not contain dto2 values", record1[2].equals(dto2.getAdviser().getLastName()));
        assertFalse("First data line should not contain dto3 values", record1[1].equals(dto3.getAdviser().getFirstName()));
        assertFalse("First data line should not contain dto3 values", record1[2].equals(dto3.getAdviser().getLastName()));

		assertTrue("Second data line should contain dto2 values", record2[1].equals(dto2.getAdviser().getFirstName()));
		assertTrue("Second data line should contain dto2 values", record2[2].equals(dto2.getAdviser().getLastName()));
		assertFalse("Second data line should not contain dto1 values", record2[1].equals(dto1.getAdviser().getFirstName()));
		assertFalse("Second data line should not contain dto1 values", record2[2].equals(dto1.getAdviser().getLastName()));
        assertFalse("Second data line should not contain dto3 values", record2[1].equals(dto3.getAdviser().getFirstName()));
        assertFalse("Second data line should not contain dto3 values", record2[2].equals(dto3.getAdviser().getLastName()));

        assertTrue(record1[10].equals("CorporateTrusteeEstablishmentinprogress"));
        assertTrue(record2[10].equals("awaitingApproval"));
        assertTrue(record3[10].equals("awaitingApprovalOffline"));

        assertFalse(record1[11].equals("Offline"));
        assertFalse(record2[11].equals("Offline"));
        assertTrue(record3[11].equals("Offline"));
	}

    private TrackingDto mockUnapprovedApplication(String firstName, String lastName, OnboardingApplicationStatus status, ApprovalTypeEnum approvalType) {
        PersonInfo pInfo = mock(PersonInfo.class);
        when(pInfo.getFirstName()).thenReturn(firstName);
        when(pInfo.getLastName()).thenReturn(lastName);

        TrackingDto dto = mock(TrackingDto.class);
        when(dto.getAdviser()).thenReturn(pInfo);
        when(dto.getStatus()).thenReturn(status);
        when(dto.getApprovalType()).thenReturn(approvalType);
        return dto;
    }

    @Test
	public void testDownloadCsvOfAllUnapprovedApplicationsWithEmptyList() throws IOException {
		Date fromDate = DateTime.now().minusMonths(1).toDate();
		Date toDate = DateTime.now().toDate();
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		List<TrackingDto> l = Collections.emptyList();
		when(trackingDtoService.searchForUnapprovedApplications(fromDate, toDate, serviceErrors)).thenReturn(l);

		String csvContent = serviceOpsServiceImpl.downloadCsvOfAllUnapprovedApplications(fromDate, toDate, serviceErrors);

		List<String> outputCsvContent = IOUtils.readLines(new StringReader(csvContent));
		assertThat("Should only contain the header line.", outputCsvContent.size(), is(1));
		String header = outputCsvContent.get(0);
		assertTrue("Header should contain first colomn name", header.contains("Dealer Group Name"));
		assertTrue("Header should contain last colomn name", header.contains("Last Modified Last Name"));
	}

	@Test
	public void testMoveFailedApplicationToDraft() {
		final String productId01 = "productId01";
		final String lastModifiedId01 = "lastModifiedId01";
		final String adviserPositionId = "adviserPositionId";
		DateTime dt = new DateTime().minusDays(1); //Earlier that 'now' which will be set in the new draft.

		ClientApplication ca = new ClientApplication();
		ca.setProductId(productId01);
		ca.setLastModifiedId(lastModifiedId01);
		ca.setLastModifiedAt(dt);
		ca.setFormData(loadFromFile("/com.bt.nextgen.serviceops.service/correlationIdStructure.json"));
		ca.setAdviserPositionId(adviserPositionId);
		when(permittedClientApplicationRepository.findByClientApplicationId(Mockito.anyLong())).thenReturn(ca);

		serviceOpsServiceImpl.moveFailedApplicationToDraft("R000003744");

		// Verify that the client application key is extracted correctly.
		ArgumentCaptor<Long> argument1 = ArgumentCaptor.forClass(Long.class);
		verify(permittedClientApplicationRepository).findByClientApplicationId(argument1.capture());
		assertThat(argument1.getValue(), is(3744L));

		// Verify that the saved application is a draft version of the original application and the original application is marked as deleted.
		ArgumentCaptor<ClientApplication> argument2 = ArgumentCaptor.forClass(ClientApplication.class);
		verify(permittedClientApplicationRepository, times(2)).save(argument2.capture());
		ClientApplication newCa = argument2.getAllValues().get(0);
		assertThat(newCa.getStatus(), is(ClientApplicationStatus.draft));
		assertThat(newCa.getProductId(), is(productId01));
		assertFalse("CorrelationsIds must be filtered out", newCa.getFormData().contains(FormDataConstants.FIELD_CORRELATION_ID));
		assertThat(newCa.getLastModifiedId(), is("SERVICE_OPS"));
		assertThat(newCa.getLastModifiedAt(), is(not(dt)));
		assertThat(newCa.getAdviserPositionId(), is(adviserPositionId));
		ClientApplication orgCa = argument2.getAllValues().get(1);
		assertThat(orgCa.getStatus(), is(ClientApplicationStatus.deleted));
	}

	private String loadFromFile(String filename) {
		try {
			return IOUtils.toString(getClass().getResourceAsStream(filename));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

    @Test
    public void testFindWrapAccountDetail_WhenAccountNoPassed() {

        WrapAccountDetailImpl impl = new WrapAccountDetailImpl();
        impl.setAccountNumber("123456");
        AccountKey accountKey = AccountKey
                .valueOf("789");
        impl.setAccountKey(accountKey);
        impl.setAccountStatus(AccountStatus.ACTIVE);
        impl.setAccountNumber("123456789");
        impl.setAccountStructureType(AccountStructureType.SMSF);
        impl.setAccountName("Test Account Name");
        impl.setBsb("012563");
        Mockito.when(accountIntegrationService.loadWrapAccountDetailByAccountNo(Matchers.any(AccountKey.class), Matchers.any(ServiceErrors.class))).thenReturn(impl);

        List<WrapAccountModel> wrapAccountModels = serviceOpsServiceImpl.findWrapAccountDetail("123456789");
        assertNotNull(wrapAccountModels);
        assertTrue(wrapAccountModels.get(0).getAccountNumber().equals("123456789"));

    }

    @Test
    public void testFindWrapAccountDetail_WhenGcmPassed() {

        WrapAccountDetailResponseImpl wrapAccountDetailResponse = new WrapAccountDetailResponseImpl();
        WrapAccountDetailImpl impl = new WrapAccountDetailImpl();
        impl.setAccountNumber("123456");
        AccountKey accountKey = AccountKey
                .valueOf("789");
        impl.setAccountKey(accountKey);
        impl.setAccountStatus(AccountStatus.ACTIVE);
        impl.setAccountNumber("123456789");
        impl.setAccountStructureType(AccountStructureType.SMSF);
        impl.setAccountName("Test Account Name");
        impl.setAdviserName("Test Adviser Name");
        List<String> owners = new ArrayList<>();
        owners.add("First Owner");
        impl.setOwnerNames(owners);
        impl.setBsb("012563");

        wrapAccountDetailResponse.setWrapAccountDetails(new ArrayList<WrapAccountDetail>());
        wrapAccountDetailResponse.getWrapAccountDetails().add(impl);
        Mockito.when(accountIntegrationService.loadWrapAccountDetailByGcm(Matchers.any(BankingCustomerIdentifier.class),
                Matchers.any(ServiceErrors.class))).thenReturn(wrapAccountDetailResponse);

        List<WrapAccountModel> wrapAccountModels = serviceOpsServiceImpl.findWrapAccountDetailsByGcm("123456789");
        assertNotNull(wrapAccountModels);
        assertEquals(wrapAccountModels.size(), 1);
        assertTrue(wrapAccountModels.get(0).getAccountNumber().equals("123456789"));
    }

    @Test
    public void getApprovedClientApplicationsByCISKeyShouldReturnListOfDirectApplications() {
        String cisKey = "11111111111";
        ClientApplication clientApplication1 = getClientApplication(111L, new DateTime(10, 10, 10, 10, 10),
                OnboardingApplicationStatus.processing, IClientApplicationForm.AccountType.INDIVIDUAL, true);
        ClientApplication clientApplication2 = getClientApplication(222L, new DateTime(10, 10, 10, 11, 11),
                OnboardingApplicationStatus.processing, IClientApplicationForm.AccountType.INDIVIDUAL, true);
        final List<ClientApplication> clientApplications = Arrays.asList(clientApplication1, clientApplication2);
        when(cisKeyClientApplicationRepository.findClientApplicationsForCisKey(cisKey)).thenReturn(clientApplications);

        TrackingDto trackingDto1 = getTrackingDto(OnboardingApplicationStatus.active);
        TrackingDto trackingDto2 = getTrackingDto(OnboardingApplicationStatus.failed);
        final List<TrackingDto> trackingDtos = Arrays.asList(trackingDto1, trackingDto2);
        when(trackingDtoService.getTrackingDtos(eq(clientApplications), eq(true), eq(true), any(ServiceErrorsImpl.class))).thenReturn(trackingDtos);

        when(serviceOpsClientApplicationDtoConverterService.convertToDto(eq(trackingDto1), eq(ServiceOpsClientApplicationStatus.APPROVED)))
                .thenReturn(new ServiceOpsClientApplicationDto());

        List<ServiceOpsClientApplicationDto> approvedClientApplicationsByCISKey = serviceOpsServiceImpl.getApprovedClientApplicationsByCISKey(cisKey);
        assertThat(approvedClientApplicationsByCISKey.size(), is(1));
    }

    @Test
    public void getApprovedClientApplicationsByCISKeyShouldReturnNullIfThereAreNotDirectApprovedApplications() {
        String cisKey = "11111111111";
        ClientApplication clientApplication1 = getClientApplication(111L, new DateTime(10, 10, 10, 10, 10),
                OnboardingApplicationStatus.processing, IClientApplicationForm.AccountType.INDIVIDUAL, true);
        ClientApplication clientApplication2 = getClientApplication(222L, new DateTime(10, 10, 10, 11, 11),
                OnboardingApplicationStatus.processing, IClientApplicationForm.AccountType.INDIVIDUAL, true);
        final List<ClientApplication> clientApplications = Arrays.asList(clientApplication1, clientApplication2);
        when(cisKeyClientApplicationRepository.findClientApplicationsForCisKey(cisKey)).thenReturn(clientApplications);

        TrackingDto trackingDto1 = getTrackingDto(OnboardingApplicationStatus.failed);
        TrackingDto trackingDto2 = getTrackingDto(OnboardingApplicationStatus.failed);
        final List<TrackingDto> trackingDtos = Arrays.asList(trackingDto1, trackingDto2);
        when(trackingDtoService.getTrackingDtos(eq(clientApplications), eq(true), eq(true), any(ServiceErrorsImpl.class))).thenReturn(trackingDtos);

        List<ServiceOpsClientApplicationDto> approvedClientApplicationsByCISKey = serviceOpsServiceImpl.getApprovedClientApplicationsByCISKey(cisKey);
        //Just chaining two sub-services
        assertNull(approvedClientApplicationsByCISKey);
    }

    @Test
    public void getClientApplicationDetailsShouldReturnDetails() {
        serviceOpsServiceImpl.getClientApplicationDetails("R000000011");
        verify(viewClientApplicationDetailsService).viewClientApplicationById(eq(11L), any(ServiceErrors.class));
    }

    @Test
    public void getClientApplicationDetailsByAccountNumberShouldReturnDetails() {
        String accountNumber = "accountNumber";
        String applicationOrigin = "applicationOrigin";
        serviceOpsServiceImpl.getClientApplicationDetailsByAccountNumber(accountNumber);
        verify(viewClientApplicationDetailsService).viewClientApplicationByAccountNumber(eq(accountNumber), any(ServiceErrors.class));
    }

    private ClientApplication getClientApplication(long applicationId, DateTime dateTime, OnboardingApplicationStatus status, IClientApplicationForm.AccountType accountType, boolean isDirect) {
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(clientApplication.getId()).thenReturn(applicationId);
        when(clientApplication.getLastModifiedAt()).thenReturn(dateTime);
        IClientApplicationForm clientApplicationForm = mock(IClientApplicationForm.class);
        when(clientApplicationForm.getAccountType()).thenReturn(accountType);
        when(clientApplicationForm.isDirectAccount()).thenReturn(isDirect);
        when(clientApplication.getClientApplicationForm()).thenReturn(clientApplicationForm);

        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(clientApplication.getOnboardingApplication()).thenReturn(onBoardingApplication);
        when(accountStatusService.getApplicationStatus(eq(clientApplication), anyMap())).thenReturn(status);
        return clientApplication;
    }

    private TrackingDto getTrackingDto(OnboardingApplicationStatus status) {
        final TrackingDto trackingDto = mock(TrackingDto.class);
        when(trackingDto.getStatus()).thenReturn(status);
        return trackingDto;
    }


    @Test
    public void testUpdatePPIDException() {
        SoapMessage faultMessage = mock(SoapMessage.class);
        SoapFaultClientException exp = new SoapFaultClientException(faultMessage);
        when(customerCredentialManagementIntegrationServiceV6.updatePPID(anyString(), anyString(), any(ServiceErrors.class))).thenThrow(new ServiceException(exp));
        ClientDetail client = mock(ClientDetail.class);
        when(clientIntegrationService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(client);
        boolean result = serviceOpsServiceImpl.updatePPID("12345678", "98765432", any(ServiceErrors.class));
        assertFalse(result);
    }


    @Test
    public void testBeneficiaryUsers() {
        CodeImpl personCode = new CodeImpl("2061", "AU", "Australia", "au");
        when(staticIntegrationService.loadCodeByName(Mockito.any(CodeCategory.class), anyString(), Mockito.any(ServiceErrors.class))).thenReturn(personCode);
        List<PersonResponse> personSearchResult = mockAndReturnPersonResponse();
        personSearchResult.get(0).setBenef(true);
        when(personSearchIntegrationService.searchUser(Mockito.any(PersonSearchRequest.class), Mockito.any(ServiceErrors.class))).thenReturn(personSearchResult);
        ServiceOpsModel model = serviceOpsServiceImpl.getSortedUsers(SEARCH_CRITERIA);
        assertThat(model.getClients().size(), is(1));
        assertThat(model.getClients().get(0).getCity(), is("Perth"));
    }

    @Test
    public void testMemberUsers() {
        CodeImpl personCode = new CodeImpl("2061", "AU", "Australia", "au");
        when(staticIntegrationService.loadCodeByName(Mockito.any(CodeCategory.class), anyString(), Mockito.any(ServiceErrors.class))).thenReturn(personCode);
        List<PersonResponse> personSearchResult = mockAndReturnPersonResponse();
        personSearchResult.get(1).setMember(true);
        when(personSearchIntegrationService.searchUser(Mockito.any(PersonSearchRequest.class), Mockito.any(ServiceErrors.class))).thenReturn(personSearchResult);
        ServiceOpsModel model = serviceOpsServiceImpl.getSortedUsers(SEARCH_CRITERIA);
        assertThat(model.getClients().size(), is(1));
        assertThat(model.getClients().get(0).getCity(), is("Sydney"));
    }

    @Test
    public void testGetSortedUsers() {
        CodeImpl country = new CodeImpl("2061", "AU", "Australia", "au");
        when(staticIntegrationService.loadCodeByName(Mockito.any(CodeCategory.class), anyString(), Mockito.any(ServiceErrors.class))).thenReturn(country);
        PersonResponse person = new PersonResponseImpl();
        person.setDomiState("MH");
        person.setDomiSuburb("Sydney");
        person.setFirstName("Test1");
        person.setLastName("User");
        person.setClientKey(ClientKey.valueOf("12345"));
        List<ProfileUserRole> userProfileRoles = new ArrayList<>();
        ProfileUserRole profileUserRole = new ProfileUserRoleImpl();
        profileUserRole.setUserRole(JobRole.FUND_MANAGER);
        userProfileRoles.add(profileUserRole);
        person.setProfileUserRoles(userProfileRoles);
        List<PersonResponse> personSearchResult = new ArrayList<>();
        personSearchResult.add(person);

        person = new PersonResponseImpl();
        person.setDomiState("MHH");
        person.setDomiSuburb("Perth");
        person.setFirstName("Test");
        person.setLastName("User1");
        person.setClientKey(ClientKey.valueOf("12356"));
        userProfileRoles = new ArrayList<>();
        profileUserRole = new ProfileUserRoleImpl();
        profileUserRole.setUserRole(JobRole.ADVISER);
        userProfileRoles.add(profileUserRole);
        person.setProfileUserRoles(userProfileRoles);
        personSearchResult.add(person);

        when(personSearchIntegrationService.searchUser(Mockito.any(PersonSearchRequest.class), Mockito.any(ServiceErrors.class))).thenReturn(personSearchResult);
        ServiceOpsModel model = serviceOpsServiceImpl.getSortedUsers(SEARCH_CRITERIA);
        assertThat(model.getIntermediaries().get(0).getCity(), is("Sydney"));
        assertThat(model.getIntermediaries().get(1).getCity(), is("Perth"));

    }

    @Test
    public void testGetSortedAccounts() {
        List<WrapAccount> accounts = new ArrayList<>();
        WrapAccountDetailImpl account1 = new WrapAccountDetailImpl();
        WrapAccountDetailImpl account2 = new WrapAccountDetailImpl();
        ClientKey approver1 = ClientKey.valueOf("5678");
        ClientKey approver2 = ClientKey.valueOf("8765");

        ArrayList<ClientKey> approverList1 = new ArrayList<>();
        approverList1.add(approver1);

        ArrayList<ClientKey> approverList2 = new ArrayList<>();
        approverList2.add(approver1);
        approverList2.add(approver2);

        BrokerUserImpl brokerUser1 = new BrokerUserImpl(UserKey.valueOf("adviserId"));
        brokerUser1.setFirstName("Tom");
        brokerUser1.setLastName("Daly");

        IndividualDetailImpl client1 = new IndividualDetailImpl();
        client1.setFirstName("Client");
        client1.setLastName("One");

        IndividualDetailImpl client2 = new IndividualDetailImpl();
        client1.setFirstName("Client");
        client1.setLastName("Two");

        account1.setAccountKey(AccountKey.valueOf("1234"));
        account1.setAccountNumber("120005251");
        account1.setAccountName("Test Account2");
        account1.setAccountStatus(AccountStatus.ACTIVE);
        account1.setAccountStructureType(AccountStructureType.Individual);
        account1.setProductKey(ProductKey.valueOf("108285"));
        account1.setApprovers(approverList1);
        account1.setAdviserPersonId(ClientKey.valueOf("9876"));
        accounts.add(account1);

        account2.setAccountKey(AccountKey.valueOf("5678"));
        account2.setAccountNumber("120006261");
        account2.setAccountName("Test Account1");
        account2.setAccountStatus(AccountStatus.ACTIVE);
        account2.setAccountStructureType(AccountStructureType.Joint);
        account2.setProductKey(ProductKey.valueOf("108285"));
        account2.setApprovers(approverList2);
        account2.setAdviserPersonId(ClientKey.valueOf("9876"));
        accounts.add(account2);

        ProductImpl product = new ProductImpl();
        product.setProductKey(ProductKey.valueOf("108285"));
        product.setProductName("BT Panorama Investments");

        when(accountIntegrationService.searchWrapAccounts(Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(accounts);

        when(clientIntegrationService.loadClientDetails(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(client1);

        when(brokerIntegrationService.getPersonDetailsOfBrokerUser(Mockito.any(ClientKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerUser1);

        when(productService.getProductDetail(Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class))).thenReturn(product);

        ServiceOpsModel model = serviceOpsServiceImpl.getSortedAccounts(eq(SEARCH_CRITERIA), any(ServiceErrors.class));

        assertThat(model.getAccounts().get(0).getAccountNumber(), is("120006261"));
        assertThat(model.getAccounts().get(0).getAccountName(), is("Test Account1"));
        assertThat(model.getAccounts().get(0).getProduct(), is("BT Panorama Investments"));

        assertThat(model.getAccounts().get(1).getAccountNumber(), is("120005251"));
        assertThat(model.getAccounts().get(1).getAccountName(), is("Test Account2"));
        assertThat(model.getAccounts().get(1).getProduct(), is("BT Panorama Investments"));
    }

    @Test
    public void testGetAccountDetail() throws Exception {
        WrapAccountDetailDto wrapAccountDetail = new WrapAccountDetailDto();
        wrapAccountDetail.setRegisteredSinceDate(new DateTime(2016, 1, 15, 10, 0));
        wrapAccountDetail.setBsb("062032");
        wrapAccountDetail.setAccountType(AccountStructureType.Individual.name());
        wrapAccountDetail.setAccountName("Test Account");

        BrokerDto adviser = new BrokerDto();
        adviser.setFirstName("Test");
        adviser.setLastName("Adviser");

        List<AddressDto> addressList = new ArrayList<>();

        AddressDto address = new AddressDto();
        address.setAddressType(AddressTypeV2.POSTAL.name());
        address.setStreetNumber("33");
        address.setStreetName("Pitt");
        address.setStreetType("Street");
        address.setCity("Sydney");
        address.setStateAbbr("NSW");
        address.setPostcode("2000");
        addressList.add(address);

        adviser.setAddresses(addressList);

        wrapAccountDetail.setAdviser(adviser);

        List<PersonRelationDto> personRelationDtos = new ArrayList<>();
        PersonRelationDto personRelationDto = new PersonRelationDto();
        personRelationDto.setPermissions("Linked accounts only");
        personRelationDto.setPrimaryContactPerson(true);
        com.bt.nextgen.api.client.model.ClientKey clientKey1 = new com.bt.nextgen.api.client.model.ClientKey("1234");
        personRelationDto.setClientKey(clientKey1);

        Set<InvestorRole> investorRoles = new HashSet<>();
        investorRoles.add(InvestorRole.Director);
        investorRoles.add(InvestorRole.Signatory);
        personRelationDto.setPersonRoles(investorRoles);
        personRelationDtos.add(personRelationDto);

        List<InvestorDto> owners = new ArrayList<>();
        InvestorDto owner1 = new InvestorDto();
        owner1.setFirstName("Test");
        owner1.setLastName("Owner");
        owner1.setFullName("Test Owner");
        owner1.setGcmId("201612345");
        com.bt.nextgen.api.client.model.ClientKey clientKey2 = new com.bt.nextgen.api.client.model.ClientKey("5678");
        owner1.setKey(clientKey2);
        owner1.setAddresses(addressList);
        owners.add(owner1);

        InvestorDto owner2 = new InvestorDto();
        owner2.setFirstName("Test");
        owner2.setLastName("Adviser");
        owner2.setFullName("Test Adviser");
        owner2.setGcmId("201656789");
        com.bt.nextgen.api.client.model.ClientKey clientKey3 = new com.bt.nextgen.api.client.model.ClientKey("1234");
        owner2.setKey(clientKey3);
        owner2.setAddresses(addressList);
        owners.add(owner2);


        wrapAccountDetail.setOwners(owners);
        wrapAccountDetail.setSettings(personRelationDtos);

        when(wrapAccountDetailDtoService.search(Mockito.any(List.class), Mockito.any(ServiceErrors.class))).thenReturn(wrapAccountDetail);
        CodeImpl personCode = new CodeImpl("5003", "ONLINE", "Online", "btfg$online");
        when(staticIntegrationService.loadCode(Mockito.any(CodeCategory.class), anyString(), Mockito.any(ServiceErrors.class))).thenReturn(personCode);
        ServiceOpsModel model = serviceOpsServiceImpl.getAccountDetail(eq("1234"), any(ServiceErrors.class));

        assertThat(model.getWrapAccountDetail().getBsb(), is("062-032"));
        assertThat(model.getWrapAccountDetail().getAccountName(), is("Test Account"));
        assertThat(model.getLinkedClients().get(0).getFirstName(), is("Test"));
        assertThat(model.getLinkedClients().get(0).getLastName(), is("Adviser"));
        assertThat(model.getLinkedClients().get(1).getFirstName(), is("Test"));
        assertThat(model.getLinkedClients().get(1).getLastName(), is("Owner"));

    }

    private List<PersonResponse> mockAndReturnPersonResponse(){
    	PersonResponse person = new PersonResponseImpl();
		person.setDomiState("MH");
		person.setDomiSuburb("Sydney");
		person.setFirstName("Test1");
		person.setPrimaryMobile("9701234597");
		person.setLastName("User");
		person.setClientKey(ClientKey.valueOf("12345"));
		List<ProfileUserRole> userProfileRoles = new ArrayList<>();
		ProfileUserRole profileUserRole = new ProfileUserRoleImpl();
		profileUserRole.setUserRole(JobRole.INVESTOR);
		userProfileRoles.add(profileUserRole);
		person.setProfileUserRoles(userProfileRoles);
		List<PersonResponse> personSearchResult = new ArrayList<>();
    	personSearchResult.add(person);

    	person = new PersonResponseImpl();
    	person.setDomiState("MHH");
    	person.setDomiSuburb("Perth");
    	person.setFirstName("Test");
    	person.setLastName("User1");
    	person.setPrimaryEmail("test@bt.com");
    	person.setClientKey(ClientKey.valueOf("12356"));
		userProfileRoles = new ArrayList<>();
		profileUserRole = new ProfileUserRoleImpl();
		profileUserRole.setUserRole(JobRole.INVESTOR);
		userProfileRoles.add(profileUserRole);
		person.setProfileUserRoles(userProfileRoles);
		personSearchResult.add(person);
		
		return personSearchResult;
    }
    
    @Test
    public void testGetSortedUsersForInvestorRole() {  
    	CodeImpl country = new CodeImpl("2061", "AU", "Australia", "au");
    	when(staticIntegrationService.loadCodeByName(Mockito.any(CodeCategory.class), anyString(), Mockito.any(ServiceErrors.class))).thenReturn(country);
    	List<PersonResponse> personSearchResult = mockAndReturnPersonResponse();   	 	
    	when(personSearchIntegrationService.searchUser(Mockito.any(PersonSearchRequest.class), Mockito.any(ServiceErrors.class))).thenReturn(personSearchResult);
    	ServiceOpsModel model = serviceOpsServiceImpl.getSortedUsers(SEARCH_CRITERIA);
    	assertThat(model.getClients().get(0).getCity(), is("Sydney"));
    	assertThat(model.getClients().get(1).getCity(), is("Perth"));
    }
    
    @Test
    public void getUserDetailWithJobProfiles() throws Exception {
        mockClientDetails();
        mockUserAccountService();
        mockAccountDetailsByGCMForMigratedClient(false);
        final JobProfile jobProfile = mock(JobProfile.class);
        when(jobProfile.getJobRole()).thenReturn(JobRole.ACCOUNTANT);
        when(profileIntegrationService.loadAvailableJobProfilesForUser(any(BankingCustomerIdentifier.class), any(ServiceErrorsImpl.class)))
                .thenReturn(Arrays.asList(jobProfile));
        
        mockAdviserListForInvestor();
        Broker dealerGroup = mock(Broker.class);
      
        final Broker broker1 = mock(Broker.class);
        when(broker1.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerKey1"));

        final Broker broker2 = mock(Broker.class);
        when(broker2.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerKey2"));
        
        CodeImpl country = new CodeImpl("2061", "AU", "Australia", "au");
    	when(staticIntegrationService.loadCodeByName(Mockito.any(CodeCategory.class), anyString(), Mockito.any(ServiceErrors.class))).thenReturn(country);
        
        when(dealerGroup.getPositionName()).thenReturn("Direct");
        when(brokerIntegrationService.getBrokersForJob(any(JobProfile.class), any(ServiceErrorsImpl.class))).thenReturn(Arrays.asList(broker1, broker2));
        
        when(brokerIntegrationService.getBroker(eq(BrokerKey.valueOf("dealerKey1")), any(ServiceErrorsImpl.class))).thenReturn(dealerGroup);
        
        List<JobProfile> jobProfileList = new ArrayList<>();
        JobProfileImpl jp = new JobProfileImpl();
        jobProfileList.add(jp);
        jp.setJobRole(JobRole.SERVICE_AND_OPERATION);
        when(userProfileService.getAvailableProfiles()).thenReturn(jobProfileList);

        UserInformationImpl permission = new UserInformationImpl();
        List<String> roles = new ArrayList<>();
        roles.add("$UR_DEC_EST_BAS");
        roles.add("$UR_SERVICE_UI");
        permission.setRoles(roles);
        UserProfile userProfile = mock(UserProfile.class);
        when(userInformationIntegrationService.getAvailableRoles(any(JobProfile.class), any(ServiceErrorsImpl.class))).thenReturn(permission);
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfileService.getActiveProfile().getUserRoles()).thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC.getRole()));

        final ServiceOpsModel userDetail = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());
        final Set<String> dealerGroupList = userDetail.getDealerGroupList();
        assertThat(dealerGroupList.size(), is(1));
        assertThat(dealerGroupList.iterator().next(), is("Direct"));
    }

    @Test
    public void testIsServiceOpsSuperRole() throws Exception {
        UserProfile userProfile = mock(UserProfile.class);
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfileService.getActiveProfile().getUserRoles()).thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC.getRole()));
    	boolean isServiceOpsSupperRole = serviceOpsServiceImpl.isServiceOpsSuperRole();
    	assertTrue(isServiceOpsSupperRole);
    }

    @Test
    public void testLeftNavPermissions() throws Exception {
        UserProfile userProfile = mock(UserProfile.class);
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfileService.getActiveProfile().getUserRoles()).thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC.getRole()));
        LeftNavPermissionModel leftNavPermissionModel = serviceOpsServiceImpl.getLeftNavPermissions();
        assertTrue(leftNavPermissionModel.isDocLibrary());
        assertFalse(leftNavPermissionModel.isGcmHome());
    }
    
    @Test
    public void testupdatePPID() throws Exception {    	
    	doNothing().when(clientIntegrationService).updatePPID(any(ClientKey.class), anyString(), any(ServiceErrorsImpl.class));
    	mockClientDetails();
    	SoapMessage faultMessage = mock(SoapMessage.class);
        SoapFaultClientException exp = new SoapFaultClientException(faultMessage);
        when(customerCredentialManagementIntegrationServiceV6.updatePPID(anyString(), anyString(), any(ServiceErrors.class))).thenThrow(new ServiceException(exp));
        boolean isUpdated = serviceOpsServiceImpl.updatePPID("abc", "12345", new ServiceErrorsImpl());
        assertFalse(isUpdated);
    }

    @Test
    public void testGetUserDetail_whenThereIsNoClient_thenReturnAnEmptyServiceOpModel() throws Exception {
        when(clientIntegrationService.loadClient(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(null);

        ServiceOpsModel serviceOpsModel = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());

        assertNotNull(serviceOpsModel);
        assertNull(serviceOpsModel.getUserName());
    }

    @Test
    public void testGetUserDetail_whenTheUserIsServiceOpsSuperRole_thenSetPPIDDropDownAndId() throws Exception {
        mockClientDetails();
        mockUserAccountService();
        mockAccountDetailsByGCMForMigratedClient(false);
        JobProfile userJobProfile = mock(JobProfile.class);
        when(userJobProfile.getJobRole()).thenReturn(JobRole.ADVISER);
        when(profileIntegrationService.loadAvailableJobProfilesForUser(any(BankingCustomerIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(userJobProfile));
        when(credentialService.getPPID(anyString())).thenReturn("0");

        JobProfile jobProfile = mock(JobProfile.class);
        UserInformationImpl userInformation = mock(UserInformationImpl.class);
        UserProfile userProfile = mock(UserProfile.class);
        when(jobProfile.getJobRole()).thenReturn(JobRole.SERVICE_AND_OPERATION);
        when(userInformation.getUserRoles()).thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC.getRole()));
        when(userProfileService.getAvailableProfiles()).thenReturn(Arrays.asList(jobProfile));
        when(userInformationIntegrationService.getAvailableRoles(any(JobProfile.class), any(ServiceErrors.class)))
                .thenReturn(userInformation);
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfileService.getActiveProfile().getUserRoles()).thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC.getRole()));
        ServiceOpsModel serviceOpsModel = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());

        assertNotNull(serviceOpsModel);
        assertEquals("0", serviceOpsModel.getPpId());

        when(userInformation.getUserRoles()).thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_DG_RESTRICTED.getRole()));

        serviceOpsModel = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());

        assertNotNull(serviceOpsModel);
        assertEquals("0", serviceOpsModel.getPpId());
    }

    @Test
    public void testGetUserDetailForEmulation() throws Exception {
        mockClientDetails();
        mockUserAccountService();
        mockJobProfile();
        mockAdviserListForInvestor();
        mockAccountDetailsByGCMForMigratedClient(false);
        mockCredentialServiceForNonWestPacLiveCustomer();
        final ServiceOpsModel userDetail = serviceOpsServiceImpl.getUserDetail("clientId", false, new ServiceErrorsImpl());

        assertNotNull(userDetail);
        assertThat(userDetail.getGcmId(), is("gcmId"));
        assertNotNull(userDetail.getJobProfiles());
        assertThat(userDetail.getJobProfiles().get(0).getJobRole(), is(JobRole.INVESTOR));
    }

    @Test
    public void testGetUserDetailsForMigratedCustomer() throws Exception {
        mockClientDetails();
        mockUserAccountService();
        mockJobProfile();
        mockAdviserListForInvestor();
        mockUserInformationServiceForInvestor();
        mockAccountDetailsByGCMForMigratedClient(true);
        mockCredentialServiceForNonWestPacLiveCustomer();
        final ServiceOpsModel userDetail = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());

        assertNotNull(userDetail);
        assertNotNull(userDetail.getJobProfiles());
        assertThat(userDetail.getJobProfiles().get(0).getJobRole(), is(JobRole.INVESTOR));
        assertTrue(userDetail.isMigratedCustomer());
        assertNotNull(userDetail.getActionValues());
        assertThat(userDetail.getActionValues().get(Action.PROVISION_MFA_DEVICE),is("Setup mobile with SAFI"));
    }

    @Test
    public void testGetUserDetailsForNonMigratedCustomer() throws Exception {
        mockClientDetails();
        mockUserAccountService();
        mockJobProfile();
        mockAdviserListForInvestor();
        mockUserInformationServiceForInvestor();
        mockAccountDetailsByGCMForMigratedClient(false);
        mockCredentialServiceForNonWestPacLiveCustomer();
        final ServiceOpsModel userDetail = serviceOpsServiceImpl.getUserDetail("clientId", true, new ServiceErrorsImpl());

        assertNotNull(userDetail);
        assertNotNull(userDetail.getJobProfiles());
        assertThat(userDetail.getJobProfiles().get(0).getJobRole(), is(JobRole.INVESTOR));
        assertFalse(userDetail.isMigratedCustomer());
        assertNotNull(userDetail.getActionValues());
        assertNull(userDetail.getActionValues().get(Action.PROVISION_MFA_DEVICE));
    }

    private void mockUserInformationServiceForInvestor() {
        UserInformationImpl permission = new UserInformationImpl();
        List<String> roles = new ArrayList<>();
        roles.add("$UR_SERVICE_UI");
        permission.setRoles(roles);
        UserProfile userProfile = mock(UserProfile.class);
        when(userInformationIntegrationService.getAvailableRoles(any(JobProfile.class), any(ServiceErrorsImpl.class))).thenReturn(permission);
    }
}