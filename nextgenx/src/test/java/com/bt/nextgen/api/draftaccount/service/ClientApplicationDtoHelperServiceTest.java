package com.bt.nextgen.api.draftaccount.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ProcessInvestorApplicationRequestMsgType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.ProcessInvestorApplicationResponseMsgType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.StatusTypeCode;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.bt.nextgen.api.adviser.model.AdviserSearchDto;
import com.bt.nextgen.api.adviser.service.AdviserSearchDtoService;
import com.bt.nextgen.api.draftaccount.FormDataConstantsForTests;
import com.bt.nextgen.api.draftaccount.builder.ProcessInvestorApplicationRequestMsgTypeBuilder;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoAdvisedImpl;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.AccountTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.api.exception.NotAllowedException;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationRepository;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.DateTimeService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import static com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoBuilder.aDraftAccountDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationDtoHelperServiceTest {

    @Mock
    private PermittedClientApplicationRepository repository;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private DateTimeService dateTimeService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private ProcessInvestorApplicationRequestMsgTypeBuilder requestMsgTypeBuilder;

    @Mock
    private OnboardingApplicationRepository onboardingApplicationRepository;

    @Mock
    private OnboardingPartyService onboardingPartyService;

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private ClientApplicationDto dto;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private AdviserSearchDtoService adviserSearchDtoService;

    @InjectMocks
    private ClientApplicationDtoHelperService service;

    private static final String PRODUCT_ID = "SOME PRODUCT ID";
    private static final String ADVISER_ID = "SOME_BROKER_KEY";;
    public static final String DEFAULT_ENCODED_ADVISER_ID = "DD17ADAF0D6E2F1847BDFDD2493B41C35AED2A82A53607B2";
    public static final String DEFAULT_ENCODED_PRODUCT_ID = "A13EEBEC6074850121E40555F623A1A479986A9E316B5F70";

    @Before
    public void setUp() throws Exception {
        when(dateTimeService.getCurrentDateTime()).thenReturn(DateTime.parse("1984-01-01T16:34:22"));
        Product defaultProduct = mock(Product.class);
        when(defaultProduct.getProductKey()).thenReturn(ProductKey.valueOf(PRODUCT_ID));

        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(defaultProduct));
        BrokerUser brokerUser = mock(BrokerUser.class);
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(brokerIntegrationService.getBrokerUser(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(brokerIntegrationService.getBrokerUser(any(UserKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);

        Broker broker = createBroker("SOME_BROKER_KEY");
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
        when(brokerIntegrationService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(broker));

        AdviserSearchDto adviser = mock(AdviserSearchDto.class);
        when(adviser.getAdviserPositionId()).thenReturn(DEFAULT_ENCODED_ADVISER_ID);
        when(adviserSearchDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(Arrays.asList(adviser));

        when(dto.getAdviserId()).thenReturn(DEFAULT_ENCODED_ADVISER_ID);
        when(dto.getProductId()).thenReturn(DEFAULT_ENCODED_PRODUCT_ID);

        Product product = mock(Product.class);
        when(product.getProductKey()).thenReturn(ProductKey.valueOf(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID)));
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrorsImpl.class))).thenReturn(Arrays.asList(product));

        List<String> brokerProductAssets = new ArrayList<>();
        brokerProductAssets.add("1234");

        when(assetIntegrationService.loadAvailableAssetsForBrokerAndProduct(any(BrokerKey.class),
                any(ProductKey.class), any(ServiceErrorsImpl.class))).thenReturn(brokerProductAssets);

        ClientApplicationDto clientApplicationDto = aDraftAccountDto().build();
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), any(ServiceErrorsImpl.class))).thenReturn(clientApplicationDto);
        when(userSamlService.getSamlToken()).thenReturn(new SamlToken(""));
        when(applicationContext.getBean("jsonObjectMapper")).thenReturn(new JsonObjectMapper());
    }

    @SuppressWarnings("unused")
    @Test
    public void submitShouldUpdateStatusToProcessingAndTheLastModifiedDetails() throws IOException {
        ClientApplicationDto dto = aDraftAccountDto().build();

        mockSetupForResponse(StatusTypeCode.SUCCESS);
        mockSetUpForOnboardingApplication(dto.getKey().getClientApplicationKey(), OnboardingApplicationStatus.processing);

        DateTime datetime = new DateTime();
        String lastModifiedId = "123456";
        String fullName = "John";

        ClientApplication clientApplication = createDraftAccount();
        clientApplication.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        clientApplication.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));
        assertThat(clientApplication.getStatus(), not(is(ClientApplicationStatus.processing)));

        when(repository.find(dto.getKey().getClientApplicationKey())).thenReturn(clientApplication);
        when(dateTimeService.getCurrentDateTime()).thenReturn(datetime);
        when(userProfileService.getGcmId()).thenReturn(lastModifiedId);

        ServiceErrorsImpl _serviceErrors = new ServiceErrorsImpl();
        mockLastModifiedUser(lastModifiedId, fullName, _serviceErrors);
        final ClientApplicationDto clientApplicationDto = service.submitDraftAccount(dto, _serviceErrors, clientApplication); // what we test!

        assertThat(clientApplication.getStatus(), is(ClientApplicationStatus.processing));

        ArgumentCaptor<ClientApplication> captor = ArgumentCaptor.forClass(ClientApplication.class);
        verify(clientApplicationDtoConverterService).convertToMinimalDto(captor.capture());

        assertThat(captor.getValue().getLastModifiedAt(), is(datetime));
        assertThat(captor.getValue().getLastModifiedId(), is(lastModifiedId));
    }

    @Test
    public void forAdvisedApplications_whenFormDataIsNotJsonSchemaSupported_submitShouldUpdateApprovalTypeAsOnlineEvenWhenOfflineFlagIsTrue() throws IOException {
        ClientApplicationDto dto = aDraftAccountDto().build();
        dto.setOffline(true);

        mockSetupForResponse(StatusTypeCode.SUCCESS);
        mockSetUpForOnboardingApplication(dto.getKey().getClientApplicationKey(), OnboardingApplicationStatus.processing);

        String lastModifiedId = "123456";
        String fullName = "John";

        ClientApplication clientApplication = createDraftAccount();
        clientApplication.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        clientApplication.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));

        when(repository.find(dto.getKey().getClientApplicationKey())).thenReturn(clientApplication);

        ServiceErrorsImpl _serviceErrors = new ServiceErrorsImpl();
        mockLastModifiedUser(lastModifiedId, fullName, _serviceErrors);

        service.submitDraftAccount(dto, _serviceErrors, clientApplication);
        assertThat(clientApplication.getClientApplicationForm().getApplicationApprovalType(), is(IClientApplicationForm.ApprovalType.ONLINE));

        ArgumentCaptor<ClientApplication> captor = ArgumentCaptor.forClass(ClientApplication.class);
        verify(clientApplicationDtoConverterService).convertToMinimalDto(captor.capture());

        assertThat(captor.getValue().getClientApplicationForm().getApplicationApprovalType(), is(IClientApplicationForm.ApprovalType.ONLINE));
    }

    @Test
    public void forAdvisedApplications_whenFormDataIsNotJsonSchemaSupported_submitShouldUpdateApprovalTypeAsOnlineWhenOfflineFlagIsFalse() throws IOException {
        ClientApplicationDto dto = aDraftAccountDto().build();
        dto.setOffline(false);

        mockSetupForResponse(StatusTypeCode.SUCCESS);
        mockSetUpForOnboardingApplication(dto.getKey().getClientApplicationKey(), OnboardingApplicationStatus.processing);

        String lastModifiedId = "123456";
        String fullName = "John";

        ClientApplication clientApplication = createDraftAccount();
        clientApplication.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        clientApplication.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));

        when(repository.find(dto.getKey().getClientApplicationKey())).thenReturn(clientApplication);

        ServiceErrorsImpl _serviceErrors = new ServiceErrorsImpl();
        mockLastModifiedUser(lastModifiedId, fullName, _serviceErrors);

        service.submitDraftAccount(dto, _serviceErrors, clientApplication);
        assertThat(clientApplication.getClientApplicationForm().getApplicationApprovalType(), is(IClientApplicationForm.ApprovalType.ONLINE));

        ArgumentCaptor<ClientApplication> captor = ArgumentCaptor.forClass(ClientApplication.class);
        verify(clientApplicationDtoConverterService).convertToMinimalDto(captor.capture());

        assertThat(captor.getValue().getClientApplicationForm().getApplicationApprovalType(), is(IClientApplicationForm.ApprovalType.ONLINE));
    }

    @Test
    public void forAdvisedApplications_whenFormDataIsJsonSchemaSupported_submitShouldUpdateApprovalTypeAsOnlineWhenOfflineFlagIsFalse() throws IOException {
        ClientApplicationDto dto = aDraftAccountDto().build(ClientApplicationDtoAdvisedImpl.class);

        ObjectMapper mapper = new JsonObjectMapper();

        //Set the actual onboarding applicaton form data
        OnboardingApplicationFormData onboardingApplicationFormData = new OnboardingApplicationFormData();
        onboardingApplicationFormData.setVersion("1.0");
        onboardingApplicationFormData.setAccountType(AccountTypeEnum.INDIVIDUAL);
        onboardingApplicationFormData.setInvestors(new ArrayList<Customer>());
        dto.setFormData(onboardingApplicationFormData);

        dto.setOffline(false);

        mockSetupForResponse(StatusTypeCode.SUCCESS);
        mockSetUpForOnboardingApplication(dto.getKey().getClientApplicationKey(), OnboardingApplicationStatus.processing);
        when(applicationContext.getBean("jsonObjectMapper", ObjectMapper.class)).thenReturn(mapper);

        String lastModifiedId = "123456";
        String fullName = "John";

        ClientApplication clientApplication = createDraftAccount();
        clientApplication.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        clientApplication.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));

        when(repository.find(dto.getKey().getClientApplicationKey())).thenReturn(clientApplication);

        ServiceErrorsImpl _serviceErrors = new ServiceErrorsImpl();
        mockLastModifiedUser(lastModifiedId, fullName, _serviceErrors);

        service.submitDraftAccount(dto, _serviceErrors, clientApplication);
        assertThat(clientApplication.getClientApplicationForm().getApplicationApprovalType(), is(IClientApplicationForm.ApprovalType.ONLINE));

        ArgumentCaptor<ClientApplication> captor = ArgumentCaptor.forClass(ClientApplication.class);
        verify(clientApplicationDtoConverterService).convertToMinimalDto(captor.capture());

        assertThat(captor.getValue().getClientApplicationForm().getApplicationApprovalType(), is(IClientApplicationForm.ApprovalType.ONLINE));
    }


    @Test
    public void forAdvisedApplications_whenFormDataIsJsonSchemaSupported_submitShouldUpdateApprovalTypeAsOfflineWhenOfflineFlagIsTrue() throws IOException {
        ClientApplicationDto dto = aDraftAccountDto().build(ClientApplicationDtoAdvisedImpl.class);


        ObjectMapper mapper = new JsonObjectMapper();

        //Set the actual onboarding applicaton form data
        OnboardingApplicationFormData onboardingApplicationFormData = new OnboardingApplicationFormData();
        onboardingApplicationFormData.setVersion("1.0");
        onboardingApplicationFormData.setAccountType(AccountTypeEnum.INDIVIDUAL);
        onboardingApplicationFormData.setInvestors(new ArrayList<Customer>());
        dto.setFormData(onboardingApplicationFormData);

        dto.setOffline(true);

        mockSetupForResponse(StatusTypeCode.SUCCESS);
        mockSetUpForOnboardingApplication(dto.getKey().getClientApplicationKey(), OnboardingApplicationStatus.processing);
        when(applicationContext.getBean("jsonObjectMapper", ObjectMapper.class)).thenReturn(mapper);

        String lastModifiedId = "123456";
        String fullName = "John";

        ClientApplication clientApplication = createDraftAccount();
        clientApplication.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        clientApplication.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));

        when(repository.find(dto.getKey().getClientApplicationKey())).thenReturn(clientApplication);

        ServiceErrorsImpl _serviceErrors = new ServiceErrorsImpl();
        mockLastModifiedUser(lastModifiedId, fullName, _serviceErrors);

        service.submitDraftAccount(dto, _serviceErrors, clientApplication);
        assertThat(clientApplication.getClientApplicationForm().getApplicationApprovalType(), is(IClientApplicationForm.ApprovalType.OFFLINE));

        ArgumentCaptor<ClientApplication> captor = ArgumentCaptor.forClass(ClientApplication.class);
        verify(clientApplicationDtoConverterService).convertToMinimalDto(captor.capture());

        assertThat(captor.getValue().getClientApplicationForm().getApplicationApprovalType(), is(IClientApplicationForm.ApprovalType.OFFLINE));
    }


    @Test
    public void submitShouldNotUpdateApplicationIdIfAlreadySet(){
        ClientApplicationDto dto = aDraftAccountDto().build();
        mockSetupForResponse(StatusTypeCode.SUCCESS);
        OnBoardingApplication application = mockSetUpForOnboardingApplication(dto.getKey().getClientApplicationKey(), OnboardingApplicationStatus.processing);

        ClientApplication clientApplication = createDraftAccount();
        clientApplication.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        clientApplication.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));
        clientApplication.setOnboardingApplication(application);
        when(repository.find(dto.getKey().getClientApplicationKey())).thenReturn(clientApplication);

        service.submitDraftAccount(dto, serviceErrors, clientApplication);

        verify(onboardingApplicationRepository, never()).save(any(OnBoardingApplication.class));
    }

    @Test
    public void submitShouldSendWebServiceRequestWithCorrectPayload() throws IOException {
        ClientApplicationDto dto = aDraftAccountDto().build();
        mockSetupForResponse(StatusTypeCode.SUCCESS);
        mockSetUpForOnboardingApplication(dto.getKey().getClientApplicationKey(), OnboardingApplicationStatus.processing);
        String adviserPositionId = EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID);
        String productId = EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID);
        OnboardingApplicationKey applicationKey = OnboardingApplicationKey.valueOf(123123L);

        ClientApplication clientApplication = createDraftAccount(applicationKey);
        clientApplication.setAdviserPositionId(adviserPositionId);
        clientApplication.setProductId(productId);

        when(repository.find(dto.getKey().getClientApplicationKey())).thenReturn(clientApplication);
        ProcessInvestorApplicationRequestMsgType requestMessageBuilderResponse = mock(ProcessInvestorApplicationRequestMsgType.class);
        BrokerUser brokerUser = mock(BrokerUser.class);

        BrokerKey brokerKey = BrokerKey.valueOf(adviserPositionId);
        BrokerKey dealerKey = BrokerKey.valueOf("dealerKey");
        Broker broker = mock(Broker.class);
        Broker dealer = mock(Broker.class);
        when(brokerIntegrationService.getAdviserBrokerUser(eq(brokerKey), any(ServiceErrors.class))).thenReturn(brokerUser);
        when(brokerIntegrationService.getBroker(eq(brokerKey), any(ServiceErrors.class))).thenReturn(broker);
        when(broker.getDealerKey()).thenReturn(dealerKey);
        when(brokerIntegrationService.getBroker(eq(dealerKey), any(ServiceErrors.class))).thenReturn(dealer);

        when(requestMsgTypeBuilder.buildFromForm(any(IClientApplicationForm.class), eq(brokerUser),
                eq(applicationKey), eq(productId), eq(dealer), any(ServiceErrors.class))).thenReturn(requestMessageBuilderResponse);

        when(brokerIntegrationService.getAdviserBrokerUser(eq(BrokerKey.valueOf(adviserPositionId)), any(ServiceErrors.class))).thenReturn(brokerUser);

        service.submitDraftAccount(dto, serviceErrors, clientApplication);
        verify(provider, times(1)).
                sendWebServiceWithSecurityHeader(any(SamlToken.class), eq(Attribute.APPLICATION_SUBMISSION_KEY), eq(requestMessageBuilderResponse));
    }

    @Test(expected = NotAllowedException.class)
    public void checkProductIdAndAdviserIdAreAllowedForLoggedInUser_shouldThrowAnException_WhenTheAdviserIdIsNotWithinAdvisersThatTheUserHasAccessTo(){
        AdviserSearchDto mockedAdviser = mock(AdviserSearchDto.class);
        when(mockedAdviser.getAdviserPositionId()).thenReturn(EncodedString.fromPlainText("SOME_ID_VALUE").toString());
        when(adviserSearchDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(Arrays.asList(mockedAdviser));
        service.checkProductIdAndAdviserIdAreAllowedForLoggedInUser(ADVISER_ID, PRODUCT_ID);
    }


    @Test(expected = NotAllowedException.class)
    public void submit_ShouldThrowAnException_WhenProductIdIsNotWithinTheProductsThatTheUserHasAccessTo(){

        Product defaultProduct = mock(Product.class);
        when(defaultProduct.getProductKey()).thenReturn(ProductKey.valueOf("SOME_OTHER_PRODUCT_ID"));
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(Arrays.asList(defaultProduct));

        service.checkProductIdAndAdviserIdAreAllowedForLoggedInUser(ADVISER_ID, PRODUCT_ID);

    }

    private void mockSetupForResponse(StatusTypeCode status) {
        ProcessInvestorApplicationResponseMsgType response = new ProcessInvestorApplicationResponseMsgType();
        response.setStatus(status);
        when(provider.sendWebServiceWithSecurityHeader(eq(userSamlService.getSamlToken()), eq(Attribute.APPLICATION_SUBMISSION_KEY), any(ProcessInvestorApplicationResponseMsgType.class))).thenReturn(response);
        when(requestMsgTypeBuilder.isSuccessful(response)).thenReturn(StatusTypeCode.SUCCESS == status);
    }

    private OnBoardingApplication mockSetUpForOnboardingApplication(Long applicationId, OnboardingApplicationStatus status) {
        OnBoardingApplication application = mock(OnBoardingApplication.class);
        when(application.getKey()).thenReturn(OnboardingApplicationKey.valueOf(applicationId));
        when(application.getStatus()).thenReturn(status);
        when(application.getApplicationType()).thenReturn("individual");
        when(application.isOffline()).thenReturn(false);
        when(onboardingApplicationRepository.save(any(OnBoardingApplication.class))).thenReturn(application);
        return application;
    }

    private void mockLastModifiedUser(String user_id, String fullName, ServiceErrors errors) {
        BrokerUser mockedAdviser = mock(BrokerUser.class);
        when(mockedAdviser.getFullName()).thenReturn(fullName);
        when(brokerIntegrationService.getBrokerUser(UserKey.valueOf(user_id), errors)).thenReturn(mockedAdviser);
    }

    private ClientApplication createDraftAccount() {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setFormData(FormDataConstantsForTests.FORM_DATA_FOR_SUBMIT);
        clientApplication.setApplicationContext(applicationContext);
        return clientApplication;
    }

    private ClientApplication createDraftAccount(OnboardingApplicationKey applicationKey) {
        ClientApplication clientApplication = createDraftAccount();
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getKey()).thenReturn(applicationKey);
        when(onBoardingApplication.getApplicationType()).thenReturn("individual");
        when(onBoardingApplication.isOffline()).thenReturn(false);
        clientApplication.setOnboardingApplication(onBoardingApplication);
        return clientApplication;
    }

    private Broker createBroker(String myBrokerId) {
        Broker broker = mock(Broker.class);
        when(broker.getBrokerType()).thenReturn(BrokerType.ADVISER);
        when(broker.getKey()).thenReturn(BrokerKey.valueOf(myBrokerId));
        when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("DEALER_KEY"));
        return broker;
    }

}