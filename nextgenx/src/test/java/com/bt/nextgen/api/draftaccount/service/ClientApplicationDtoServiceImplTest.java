package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.adviser.model.AdviserSearchDto;
import com.bt.nextgen.api.adviser.service.AdviserSearchDtoService;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.api.client.service.ClientListDtoService;
import com.bt.nextgen.api.client.service.CustomerDataDto;
import com.bt.nextgen.api.client.service.DirectInvestorDataDtoService;
import com.bt.nextgen.api.client.service.GlobalCustomerDtoService;
import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.builder.ProcessInvestorApplicationRequestMsgTypeBuilder;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoAdvisedImpl;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoMapImpl;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.form.ClientApplicationFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IExtendedPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Phone;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.api.exception.NotAllowedException;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationRepository;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.core.service.DateTimeService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationIdentifierImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.ApplicationIdentifier;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.bt.nextgen.test.AttributeMatcher;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoBuilder.aDraftAccountDto;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationDtoServiceImplTest extends AbstractJsonReaderTest {

    public static final String DEFAULT_ENCODED_ADVISER_ID = "DD17ADAF0D6E2F1847BDFDD2493B41C35AED2A82A53607B2";
    public static final String DEFAULT_ENCODED_PRODUCT_ID = "A13EEBEC6074850121E40555F623A1A479986A9E316B5F70";
    @Mock
    private PermittedClientApplicationRepository repository;

    @Mock
    private ClientListDtoService clientListDtoService;

    @Mock
    private AccActivationIntegrationService accActivationIntegrationService;

    @Mock
    private ClientApplicationDto dto;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private DateTimeService dateTimeService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private OnboardingApplicationRepository onboardingApplicationRepository;

    @Mock
    private OnboardingPartyService onboardingPartyService;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private ProcessInvestorApplicationRequestMsgTypeBuilder requestMsgTypeBuilder;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private AdviserSearchDtoService adviserSearchDtoService;

    @Mock
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private ClientApplicationDtoHelperService clientApplicationDtoHelperService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private DirectInvestorDataDtoService directInvestorDataDtoService;

    @Mock
    private FeatureTogglesService featureTogglesService;

    @InjectMocks
    ClientApplicationDtoServiceImpl service;


    UserProfile activeProfile;

    @Mock
    private GlobalCustomerDtoService globalCustomerDtoService;

    private static final Long CLIENT_APPLICATION_ID = 100L;
    private static final String PRODUCT_ID = "SOME PRODUCT ID";

    @Before
    public void setUp() throws Exception {
        //setup jsonObjectMapper for tests
        ObjectMapper mapper = new JsonObjectMapper();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean("jsonObjectMapper")).thenReturn(mapper);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);
        service.setObjectMapper(mapper);

        when(dateTimeService.getCurrentDateTime()).thenReturn(DateTime.parse("1984-01-01T16:34:22"));
        Product defaultProduct = mock(Product.class);
        when(defaultProduct.getProductKey()).thenReturn(ProductKey.valueOf(PRODUCT_ID));
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
        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        FeatureToggles retrieveCrsEnabled  = new FeatureToggles();
        retrieveCrsEnabled.setFeatureToggle("retrieveCrsEnabled", true);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(retrieveCrsEnabled);

    }



    private UserProfile getProfile(final JobRole role, final String jobId, final String customerId) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf(customerId));

        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));

        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }


    @Test
    public void createShouldSetStatusDraft() throws Exception {
        service.create(aDraftAccountDto().build(), null);
        verify(repository).save(argThat(new AttributeMatcher<ClientApplication>("status", ClientApplicationStatus.draft)));
    }

    @Test
    public void createShouldSetProductId() throws Exception {
        final ClientApplicationDto clientApplicationDto = aDraftAccountDto().build();
        ArgumentCaptor<ClientApplication> captor = ArgumentCaptor.forClass(ClientApplication.class);

        service.create(clientApplicationDto, null);

        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getProductId(), is(EncodedString.toPlainText(clientApplicationDto.getProductId())));
    }

    @Test(expected = NotAllowedException.class)
    public void create_ShouldThrowAnException_WhenCheckProductIdAndAdviserIdAreAllowedForLoggedInUserThrowsException(){
        final ClientApplicationDto clientApplicationDto = aDraftAccountDto().build();
        doThrow(new NotAllowedException("")).when(clientApplicationDtoHelperService).checkProductIdAndAdviserIdAreAllowedForLoggedInUser(any(String.class),any(String.class));
        service.create(clientApplicationDto, null);
    }

    @Test
    public void create_ShouldThrowAnException_WhenTheClientApplicationAttemptedToSimulateIsForADifferentAdviser()  throws Exception{

        ServiceErrors errors =  null;
        Map<String, Object> jsonRequest = readJsonFromFile("draft_application_company_form_data.json");
        ClientApplication clientApplication = mock(ClientApplication.class);
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl(jsonRequest);


        clientApplicationDto.setAdviserId(DEFAULT_ENCODED_ADVISER_ID);
        when(repository.find(anyLong())).thenReturn(clientApplication);
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(setIndividualDto("Y"));
        when(userProfileService.getPositionId()).thenReturn("1234");

        AdviserSearchDto adviser = mock(AdviserSearchDto.class);
        when(adviser.getAdviserPositionId()).thenReturn(DEFAULT_ENCODED_ADVISER_ID);
        when(adviserSearchDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(Arrays.asList(adviser));

        ClientApplicationDto newClientApplicationDto= service.simulateDraftAccount(new ClientApplicationKey(12345852013l), any(ServiceErrorsImpl.class));
        assertNull(newClientApplicationDto);

    }

    @Test
    public void create_ShouldSimulateAnApplication_WhenTheClientApplicationIsOfTheSameAdviser()  throws Exception{

        ServiceErrors errors =  null;
        Map<String, Object> jsonRequest = readJsonFromFile("draft_application_company_form_data.json");
        ClientApplication clientApplication = mock(ClientApplication.class);
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl(jsonRequest);


        clientApplicationDto.setAdviserId(DEFAULT_ENCODED_ADVISER_ID);
        clientApplicationDto.setProductId(DEFAULT_ENCODED_PRODUCT_ID);
        when(repository.find(anyLong())).thenReturn(clientApplication);
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(setIndividualDto("Y"));
        when(userProfileService.getPositionId()).thenReturn("68532");

        AdviserSearchDto adviser = mock(AdviserSearchDto.class);
        when(adviser.getAdviserPositionId()).thenReturn(DEFAULT_ENCODED_ADVISER_ID);
        when(adviserSearchDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(Arrays.asList(adviser));

        ClientApplicationDto newClientApplicationDto= service.simulateDraftAccount(new ClientApplicationKey(12345852013l), any(ServiceErrorsImpl.class));
        verify(repository).find(any(Long.class));
        verify(repository).save(any(ClientApplication.class));
        assertNotNull(newClientApplicationDto);
        assert(newClientApplicationDto.getFormData()).equals(clientApplicationDto.getFormData());
        assert(newClientApplicationDto.getAdviserId()).equals(clientApplicationDto.getAdviserId());
        assert(newClientApplicationDto.getProductId()).equals(clientApplicationDto.getProductId());
    }

    @Test
    public void create_shouldCreateAnApplication_WhenTheAdviserIdIsWithinTheAdvisersThatTheUserHasAccessTo(){
        final ClientApplicationDto clientApplicationDto = aDraftAccountDto().build();

        AdviserSearchDto adviser = mock(AdviserSearchDto.class);
        when(adviser.getAdviserPositionId()).thenReturn(clientApplicationDto.getAdviserId());
        when(adviserSearchDtoService.search(any(List.class), any(ServiceErrorsImpl.class))).thenReturn(Arrays.asList(adviser));

        service.create(clientApplicationDto, null);
        verify(repository).save(any(ClientApplication.class));
    }

    @Test
    // TODO: When user is a paraplanner that can act for many advisers, this info needs to come from the front-end
    public void createShouldSetAdviserPositionIdToAdviserIdRetrievedFromRequestData() {
        ClientApplicationDto dto = aDraftAccountDto().withAdviserId(DEFAULT_ENCODED_ADVISER_ID).build();
        service.create(dto, null);

        verify(repository).save(argThat(hasAttributeValue("adviserPositionId", "68532")));
    }

    @Test
    public void createShouldSetLastModifiedIdToLoggedInUser() {
        when(userProfileService.getGcmId()).thenReturn("MY_GCM_ID");
        ClientApplicationDto dto = aDraftAccountDto().build();
        service.create(dto, null);

        verify(repository).save(argThat(hasAttributeValue("lastModifiedId", "MY_GCM_ID")));
    }

    @Test
    public void updateShouldSetLastModifiedIdToLoggedInUser() {
        when(userProfileService.getGcmId()).thenReturn("MY_GCM_ID");
        long draftAccountId = 123L;
        when(dto.getKey()).thenReturn(new ClientApplicationKey(draftAccountId));
        ClientApplication draftAccount = new ClientApplication();
        draftAccount.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        draftAccount.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));

        when(repository.find(draftAccountId)).thenReturn(draftAccount);

        service.update(dto, null);

        assertThat(draftAccount.getLastModifiedId(), equalTo("MY_GCM_ID"));
    }


    private ClientApplicationDto setDocUploadedStatusDto(long draftId){
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl();
        clientApplicationDto.setStatus(ClientApplicationStatus.docuploaded);
        clientApplicationDto.setKey(new ClientApplicationKey(draftId));
        clientApplicationDto.setAdviserId(DEFAULT_ENCODED_ADVISER_ID);
        clientApplicationDto.setProductId(DEFAULT_ENCODED_PRODUCT_ID);

        return clientApplicationDto;
    }

    private ClientApplication createClientApplication(ClientApplicationStatus status, Boolean offline){

        ClientApplication draftAccount = new ClientApplication();
        draftAccount.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        draftAccount.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));
        draftAccount.setClientApplicationsStatus(status);

        OnBoardingApplication onBoardingApplication = new OnBoardingApplication();
        onBoardingApplication.setOffline(offline);
        draftAccount.setOnboardingApplication(onBoardingApplication);

        return draftAccount;

    }

    @Test
    public void update_should_ModifyStatus_to_DocUploaded() {
        long draftAccountId = 7777;

        ClientApplication draftAccount = createClientApplication(ClientApplicationStatus.processing, true);
        ClientApplicationDto clientApplicationDto = setDocUploadedStatusDto(draftAccountId);
        when(repository.find(draftAccountId)).thenReturn(draftAccount);

        service.update(clientApplicationDto, serviceErrors);
        assertThat(draftAccount.getStatus(), equalTo(ClientApplicationStatus.docuploaded));
    }

    @Test (expected = IllegalStateException.class)
    public void update_should_NOT_ModifyStatus_When_applicationType_ONLINE() {
        long draftAccountId = 7777;

        ClientApplication draftAccount = createClientApplication(ClientApplicationStatus.processing, false);
        ClientApplicationDto clientApplicationDto = setDocUploadedStatusDto(draftAccountId);
        when(repository.find(draftAccountId)).thenReturn(draftAccount);

        service.update(clientApplicationDto, serviceErrors);
        assertThat(draftAccount.getStatus(), equalTo(ClientApplicationStatus.processing));
    }

    @Test (expected = IllegalStateException.class)
     public void update_should_NOT_ModifyStatus_When_application_DRAFT() {
        long draftAccountId = 7777;

        ClientApplication draftAccount = createClientApplication(ClientApplicationStatus.draft, true);
        ClientApplicationDto clientApplicationDto = setDocUploadedStatusDto(draftAccountId);
        when(repository.find(draftAccountId)).thenReturn(draftAccount);

        service.update(clientApplicationDto, serviceErrors);
        assertThat(draftAccount.getStatus(), equalTo(ClientApplicationStatus.draft));
    }

    @Test (expected = IllegalStateException.class)
    public void update_should_NOT_ModifyStatus_When_application_ACTIVE() {
        long draftAccountId = 7777;

        ClientApplication draftAccount = createClientApplication(ClientApplicationStatus.active, true);
        ClientApplicationDto clientApplicationDto = setDocUploadedStatusDto(draftAccountId);
        when(repository.find(draftAccountId)).thenReturn(draftAccount);

        service.update(clientApplicationDto, serviceErrors);
        assertThat(draftAccount.getStatus(), equalTo(ClientApplicationStatus.active));
    }



    @Test(expected = NotAllowedException.class)
    public void update_ShouldThrowAnException__WhenCheckProductIdAndAdviserIdAreAllowedForLoggedInUserThrowsException(){
        final ClientApplicationDto clientApplicationDto = aDraftAccountDto().build();
        ClientApplication clientApplication = mock(ClientApplication.class);
        when(repository.find(clientApplicationDto.getKey().getClientApplicationKey())).thenReturn(clientApplication);
        doNothing().when(clientApplication).assertCanBeModified();
        doThrow(new NotAllowedException("")).when(clientApplicationDtoHelperService).checkProductIdAndAdviserIdAreAllowedForLoggedInUser(any(String.class),any(String.class));

        service.update(clientApplicationDto, null);
    }

    @Test
    public void createShouldMapDtoToDraftAccountFormDataJSON() throws IOException {
        ClientApplicationDto dto = aDraftAccountDto().build();

        Map<String, Object> map = new HashMap<>();
        map.put("accountType", "individual");
        dto.setFormData(map);

        ArgumentCaptor<ClientApplication> draftAccountArgumentCaptor = ArgumentCaptor.forClass(ClientApplication.class);
        service.create(dto, null);

        verify(repository).save(draftAccountArgumentCaptor.capture());
        ClientApplication savedDraftAccount = draftAccountArgumentCaptor.getValue();
        assertThat(IClientApplicationForm.AccountType.INDIVIDUAL, equalTo(savedDraftAccount.getClientApplicationForm().getAccountType()));
    }

    @Test
    public void shouldUpdateDraftAccountFormData() throws Exception {

        ClientApplication existingDraftAccount = new ClientApplication();
        existingDraftAccount.setFormData("{\"ORIGINAL\":\"VALUE\"}");
        String newFormDataValue = "{\"NEW\":\"IMPROVED\"}";
        existingDraftAccount.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        existingDraftAccount.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));

        ClientApplicationDto dto = aDraftAccountDto().withFormData(newFormDataValue).build();
        when(repository.find(dto.getKey().getClientApplicationKey())).thenReturn(existingDraftAccount);
        service.update(dto, null);

        assertThat(existingDraftAccount.getFormData(), equalTo(newFormDataValue));
    }

    @Test
    public void shouldUpdateCompanyDraftAccountWithGCMData() throws Exception {
        ServiceErrors errors =  null;
        Map<String, Object> jsonRequest = readJsonFromFile("draft_application_company_form_data.json");
        ClientApplication clientApplication = mock(ClientApplication.class);
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl(jsonRequest);

        when(repository.find(anyLong())).thenReturn(clientApplication);
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(setIndividualDto("Y"));

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013l), errors);
        assertThat((Map)applicationDto.getFormData(), isA(Map.class));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        assertThat(form.getDirectorsSecretariesSignatories().get(0).getFirstName(), is("George"));
        assertThat(form.getDirectorsSecretariesSignatories().get(0).getLastName(), is("Test"));
        assertThat(form.getDirectorsSecretariesSignatories().get(0).getMiddleName(), is("Bumpkin"));
        assertThat(form.getDirectorsSecretariesSignatories().get(0).getAddresses().size(), is(1));
        assertThat(form.getDirectorsSecretariesSignatories().get(0).getPhones().size(), is(2));
        assertThat(form.getDirectorsSecretariesSignatories().get(0).getEmails().size(), is(2));
        assertThat(form.getDirectorsSecretariesSignatories().get(0).isGcmUpdated(), is(true));
    }

    @Test
    public void shouldUpdateIndividualTrustDraftAccountWithGCMData() throws Exception {
        ServiceErrors errors =  null;
        Map<String, Object> jsonRequest = readJsonFromFile("draft_application_individualtrust_family_form_data_minimal.json");
        ClientApplication clientApplication = mock(ClientApplication.class);
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl(jsonRequest);

        when(repository.find(anyLong())).thenReturn(clientApplication);
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(setIndividualDto("Y"));

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013l), errors);
        assertThat((Map)applicationDto.getFormData(), isA(Map.class));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        assertThat(form.getTrustees().get(0).getFirstName(), is("George"));
        assertThat(form.getTrustees().get(0).getLastName(), is("Test"));
        assertThat(form.getTrustees().get(0).getMiddleName(), is("Bumpkin"));
        assertThat(form.getTrustees().get(0).isGcmUpdated(), is(true));
    }


    @Test
    public void shouldUpdateIndividualDraftAccountWithGCMData() throws Exception {
        ServiceErrors errors =  null;
        Map<String, Object> jsonRequest = readJsonFromFile("draft_application_form_data.json");
        ClientApplication clientApplication = mock(ClientApplication.class);
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl(jsonRequest);
        clientApplicationDto.setFormData(jsonRequest);
        when(repository.find(anyLong())).thenReturn(clientApplication);
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setIndividualDto("Y");
        individualDto.setGcmTitleLabel("GCM_Mr");
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        assertThat((Map)applicationDto.getFormData(), isA(Map.class));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        assertThat(form.getInvestors().get(0).getFirstName(), is("George"));
        assertThat(form.getInvestors().get(0).getLastName(), is("Test"));
        assertThat(form.getInvestors().get(0).getMiddleName(), is("Bumpkin"));
        assertThat(form.getInvestors().get(0).isGcmUpdated(), is(true));
    }

    @Test
    public void shouldUpdateIndvDraftWithCrsGCMData() throws Exception {
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_cis_crs.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("us"), any(ServiceErrors.class))).thenReturn(new CodeImpl("codeId", "US", "United States of America", "us"));

        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        IClientApplicationForm originalForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
        assertThat(originalForm.getInvestors().get(0).getIsForeignRegistered(), is("N"));
        assertThat(originalForm.getInvestors().get(0).getOverseasTaxDetails().size(), is(0));

        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setIndividualDto("Y");
        individualDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("us","btfg$tin_never_iss",null)));
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);
        assertThat(modifiedPersonDetails.isGcmUpdated(), is(true));
        assertThat(modifiedPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getOverseasTaxCountry(), is("US"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getTINExemptionReason(), is("btfg$tin_never_iss"));
    }



    @Test
    public void shouldUpdateAvaloqIndvDraftWithForeignRegisteredAsN() throws Exception {
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_cis_crs.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("us"), any(ServiceErrors.class))).thenReturn(new CodeImpl("codeId", "US", "United States of America", "us"));
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        IClientApplicationForm originalForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
        assertThat(originalForm.getInvestors().get(0).getIsForeignRegistered(), is("N"));
        assertThat(originalForm.getInvestors().get(0).getOverseasTaxDetails().size(), is(0));
        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setAvaloqIndividualDto();
        individualDto.setGcmTitleLabel("GCM_Mr");
        individualDto.setKey(new ClientKey("12345"));
        individualDto.setGcmId("12345");
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("us","btfg$tin_never_iss",null),getTaxResidenceCountry("FOREIGN","","Y")));
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), eq(errors))).thenReturn(customerDataDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);
        assertThat(modifiedPersonDetails.isGcmUpdated(), is(false));
        assertThat(modifiedPersonDetails.getIsOverseasTaxRes(),is(true));
        assertThat(modifiedPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getOverseasTaxCountry(), is("US"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getTINExemptionReason(), is("btfg$tin_never_iss"));
    }

    @Test
    public void shouldNotUpdateAvaloqIndvDraftWhenRetreiveCrsEnabledFlagisFalse() throws Exception {
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(new FeatureToggles());//Making all feature toggles as false
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_cis_crs.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("us"), any(ServiceErrors.class))).thenReturn(new CodeImpl("codeId", "US", "United States of America", "us"));
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        IClientApplicationForm originalForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
        assertThat(originalForm.getInvestors().get(0).getIsForeignRegistered(), is("N"));
        assertThat(originalForm.getInvestors().get(0).getOverseasTaxDetails().size(), is(0));
        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setAvaloqIndividualDto();
        individualDto.setGcmTitleLabel("GCM_Mr");
        individualDto.setKey(new ClientKey("12345"));
        individualDto.setGcmId("12345");
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("us","btfg$tin_never_iss",null),getTaxResidenceCountry("FOREIGN","","Y")));
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), eq(errors))).thenReturn(customerDataDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);
        assertThat(modifiedPersonDetails.isGcmUpdated(), is(false));
        assertNull(modifiedPersonDetails.getIsOverseasTaxRes());
        assertThat(modifiedPersonDetails.getIsForeignRegistered(), is("N"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(0));
    }

    @Test
    public void shouldUpdateAvaloqIndvDraftWithForeignRegisteredAsNull() throws Exception {
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_cis_crs.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("us"), any(ServiceErrors.class))).thenReturn(new CodeImpl("codeId", "US", "United States of America", "us"));
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        IClientApplicationForm originalForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
        originalForm.getInvestors().get(0).setIsForeignRegistered(null);
        assertThat(originalForm.getInvestors().get(0).getOverseasTaxDetails().size(), is(0));
        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setAvaloqIndividualDto();
        individualDto.setGcmTitleLabel("GCM_Mr");
        individualDto.setKey(new ClientKey("12345"));
        individualDto.setGcmId("12345");
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("us","btfg$tin_never_iss",null)));
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), eq(errors))).thenReturn(customerDataDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);
        assertThat(modifiedPersonDetails.isGcmUpdated(), is(false));
        assertNull(modifiedPersonDetails.getIsForeignRegistered());
        assertNull(modifiedPersonDetails.getIsOverseasTaxRes());
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getOverseasTaxCountry(), is("US"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getTINExemptionReason(), is("btfg$tin_never_iss"));
    }

    @Test
    public void shouldUpdateAvaloqIndvDraftWithCRSDetails() throws Exception {
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existingavaloq_crs.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("us"), any(ServiceErrors.class))).thenReturn(new CodeImpl("codeId", "US", "United States of America", "us"));
        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setAvaloqIndividualDto();
        individualDto.setGcmTitleLabel("GCM_Mr");
        individualDto.setKey(new ClientKey("12345"));
        individualDto.setGcmId("12345");
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("us","btfg$tin_never_iss",null),getTaxResidenceCountry("FOREIGN","","N")));
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), eq(errors))).thenReturn(customerDataDto);
        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);
        assertThat(modifiedPersonDetails.getIsForeignRegistered(),is("N"));
        assertThat(modifiedPersonDetails.getIsOverseasTaxRes(),is(false));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getOverseasTaxCountry(), is("US"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getTINExemptionReason(), is("btfg$tin_never_iss"));
    }

    @Test
    public void shouldUpdateIndvDraftWithCrsGCMData_WhenOverseasCountriesListSizeIsDifferent() throws Exception {
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_cis_crs_with_overseas_countries.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);

        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        IClientApplicationForm originalForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
        assertThat(originalForm.getInvestors().get(0).getIsForeignRegistered(), is("Y"));
        assertThat(originalForm.getInvestors().get(0).getOverseasTaxDetails().size(), is(1));

        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setIndividualDto("Y");
        individualDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("US","Exempt",null), getTaxResidenceCountry("AF","Exempt",null)));
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);
        assertThat(modifiedPersonDetails.isGcmUpdated(), is(true));
        assertThat(modifiedPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(2));
    }

    @Test
    public void shouldUpdateIndvDraftWithCrsGCMData_whenOverseasCountryIsDifferent() throws Exception {
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_cis_crs_with_overseas_countries.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);

        when(staticIntegrationService.loadCodeByAvaloqId(eq(CodeCategory.COUNTRY), eq("af"), any(ServiceErrors.class))).thenReturn(new CodeImpl("codeId", "AF", "Afghanistan", "af"));

        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        IClientApplicationForm originalForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
        IExtendedPersonDetailsForm originalPersonDetails = originalForm.getInvestors().get(0);
        assertThat(originalPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(originalPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(originalPersonDetails.getOverseasTaxDetails().get(0).getTIN(), is("123456789"));
        assertThat(originalPersonDetails.getOverseasTaxDetails().get(0).getOverseasTaxCountry(), is("US"));

        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setIndividualDto("Y");
        individualDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("af",null, "123456789")));

        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);
        assertThat(modifiedPersonDetails.isGcmUpdated(), is(true));
        assertThat(modifiedPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getTIN(), is("123456789"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getOverseasTaxCountry(), is("AF"));
    }


    @Test
    public void shouldUpdateIndvDraftWithCrsGCMData_whenOverseasCountryTinIsDifferent() throws Exception {
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_cis_crs_with_overseas_countries.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);

        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        IClientApplicationForm originalForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
        IExtendedPersonDetailsForm originalPersonDetails = originalForm.getInvestors().get(0);
        assertThat(originalPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(originalPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(originalPersonDetails.getOverseasTaxDetails().get(0).getTIN(), is("123456789"));

        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setIndividualDto("Y");
        individualDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("US",null, "NEW TIN")));

        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);
        assertThat(modifiedPersonDetails.isGcmUpdated(), is(true));
        assertThat(modifiedPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getTIN(), is("NEW TIN"));
    }

    @Test
    public void shouldUpdateIndvDraftWithCrsGCMData_whenOverseasCountryExemptionIsDifferent() throws Exception {
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_cis_crs_with_overseas_countries.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);

        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        IClientApplicationForm originalForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
        IExtendedPersonDetailsForm originalPersonDetails = originalForm.getInvestors().get(0);
        assertThat(originalPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(originalPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(originalPersonDetails.getOverseasTaxDetails().get(0).getTIN(), is("123456789"));

        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setIndividualDto("Y");
        individualDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("US","btfg$tin_pend", null)));

        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);
        assertThat(modifiedPersonDetails.isGcmUpdated(), is(true));
        assertThat(modifiedPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getTINExemptionReason(), is("btfg$tin_pend"));
    }

    @Test
    public void shouldNotUpdateIndvDraftWithCrsGCMData_whenOverseasCountryExemptionIsSame() throws Exception {
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_cis_crs_with_tinexemption.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);

        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        IClientApplicationForm originalForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
        IExtendedPersonDetailsForm originalPersonDetails = originalForm.getInvestors().get(0);
        assertThat(originalPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(originalPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(originalPersonDetails.getOverseasTaxDetails().get(0).getTINExemptionReason(), is("btfg$tin_pend"));

        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setIndividualDto("Y");
        individualDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("AF","btfg$tin_pend", null)));

        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);
        assertThat(modifiedPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(1));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().get(0).getTINExemptionReason(), is("btfg$tin_pend"));
    }

    @Test
    public void shouldUpdateIndvDraftWithCrsGCMData_whenOverseasCountryIsRemovedFromGCM() throws Exception {
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_cis_crs_with_overseas_countries.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);

        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        IClientApplicationForm originalForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
        IExtendedPersonDetailsForm originalPersonDetails = originalForm.getInvestors().get(0);
        assertThat(originalPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(originalPersonDetails.getOverseasTaxDetails().size(), is(1));

        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setIndividualDto("N");

        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);
        assertThat(modifiedPersonDetails.isGcmUpdated(), is(true));
        assertThat(modifiedPersonDetails.getIsForeignRegistered(), is("N"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(0));
    }


    @Test
    public void shouldNotSet_UpdateGCM_WhenOverseasCountryIsNull_AndRestAllDataIsSame() throws Exception {
        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_cis_crs_with_overseas_countriesNotPresent.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);

        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);
        IClientApplicationForm originalForm = ClientApplicationFormFactory.getNewClientApplicationForm(clientApplicationDto.getFormData());
        IExtendedPersonDetailsForm originalPersonDetails = originalForm.getInvestors().get(0);
        assertThat(originalPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(originalPersonDetails.getOverseasTaxDetails().size(), is(0));

        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setSameIndividualDto();
        individualDto.setIsForeignRegistered("Y");
        individualDto.setPhones(Arrays.asList(setPhoneDto("61", "41", "1111111")));

        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);

        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm modifiedForm = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        IExtendedPersonDetailsForm modifiedPersonDetails = modifiedForm.getInvestors().get(0);

        assertThat(modifiedPersonDetails.getIsForeignRegistered(), is("Y"));
        assertThat(modifiedPersonDetails.getOverseasTaxDetails().size(), is(0));
        assertThat(modifiedPersonDetails.isGcmUpdated(), is(false));
    }

    @Test
    public void shouldUpdateIndividualDraftAccountWithAvaloqData() throws Exception {

        ServiceErrors errors =  null;
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(new FeatureToggles());//Making all feature toggles as false
        Map<String, Object> jsonRequest = readJsonFromFile("draft_application_form_data.json");
        ClientApplication clientApplication = mock(ClientApplication.class);
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl(jsonRequest);
        clientApplicationDto.setFormData(jsonRequest);
        when(repository.find(anyLong())).thenReturn(clientApplication);
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setAvaloqIndividualDto();
        individualDto.setGcmTitleLabel("GCM_Mr");
        individualDto.setKey(new ClientKey("12345"));
        individualDto.setGcmId("12345");
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);
        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        assertThat((Map)applicationDto.getFormData(), isA(Map.class));
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        assertThat(form.getInvestors().get(0).getFirstName(), is("First"));
        assertThat(form.getInvestors().get(0).getLastName(), is("Last"));
        assertThat(form.getInvestors().get(0).isGcmUpdated(), is(false));
        assertThat(form.getInvestors().get(0).isExistingPerson() ,is(true) );
        assertThat( form.getInvestors().get(0).getClientKey(), is("12345"));
        assertThat( form.getInvestors().get(0).isGcmRetrievedPerson(), is(false));
        assertThat( form.getInvestors().get(0).getPanoramaNumber(), is("12345"));
        assertThat((String)  ((Map)form.getInvestors().get(0).getPhones().get(0)).get("countryCode"), is("+61"));
        assertThat((String)  ((Map)form.getInvestors().get(0).getPhones().get(0)).get("areaCode"), is("04"));
        assertThat((String)  ((Map)form.getInvestors().get(0).getPhones().get(0)).get("number"), is("34569090"));
        assertThat(form.getInvestors().get(0).getEmails().size(), is(2));

    }

    @Test
    public void shouldUpdateIndividualAccountWithAvaloqData() throws Exception {

        ServiceErrors errors =  null;
        ObjectMapper mapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile("individual_existing_panorama_crs.json");
        Object formData = mapper.readValue(jsonRequest, OnboardingApplicationFormData.class);

        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl((OnboardingApplicationFormData) formData);


        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        IndividualDto individualDto = setAvaloqIndividualDto();
        individualDto.setGcmTitleLabel("GCM_Mr");
        individualDto.setKey(new ClientKey("12345"));
        individualDto.setGcmId("12345");
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(individualDto);
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("us","btfg$tin_never_iss",null),getTaxResidenceCountry("FOREIGN","","Y")));
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), eq(errors))).thenReturn(customerDataDto);
        ClientApplicationDto applicationDto = service.find(new ClientApplicationKey(12345852013L), errors);
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(applicationDto.getFormData());
        assertThat(form.getInvestors().get(0).getFirstName(), is("George"));
        assertThat(form.getInvestors().get(0).getLastName(), is("Test"));
        assertThat(form.getInvestors().get(0).isExistingPerson() ,is(true) );
        assertThat( form.getInvestors().get(0).getClientKey(), is("12345"));
        assertThat( form.getInvestors().get(0).isGcmRetrievedPerson(), is(false));
        assertThat( form.getInvestors().get(0).getPanoramaNumber(), is("12345"));
        assertThat(((Phone)form.getInvestors().get(0).getPhones().get(0)).getCountryCode(), is("+61"));
        assertThat(((Phone)form.getInvestors().get(0).getPhones().get(0)).getAreaCode(), is("04"));
        assertThat(((Phone)form.getInvestors().get(0).getPhones().get(0)).getNumber(), is("34569090"));
        assertThat(form.getInvestors().get(0).getEmails().size(), is(2));
        IAddressForm resAddress = form.getInvestors().get(0).getResidentialAddress();
        IAddressForm postalAddress = form.getInvestors().get(0).getPostalAddress();
        assertThat(resAddress.getFloor(),is("10"));
        assertThat(resAddress.getStreetNumber(),is("150"));
        assertThat(resAddress.getStreetName(),is("Collins"));
        assertThat(resAddress.getPostcode(),is("3000"));
        assertThat(resAddress.getSuburb(),is("Melbourne"));

        assertThat(postalAddress.getFloor(),is("10"));
        assertThat(postalAddress.getStreetNumber(),is("120"));
        assertThat(postalAddress.getStreetName(),is("Burke"));
        assertThat(postalAddress.getPostcode(),is("3024"));
        assertThat(postalAddress.getSuburb(),is("Collinswood"));
    }

    @Test
    public void shouldNotUpdateIndividualDraftAccountWithGCMData() throws IOException {
        ServiceErrors errors =  null;
        Map<String, Object> jsonRequest = readJsonFromFile("draft_application_form_data.json");
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl(jsonRequest);

        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(setSameIndividualDto());
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(service.find(new ClientApplicationKey(12345852013L), errors).getFormData());
        assertThat(form.getInvestors().get(0).getTitle(), is("Mr"));
        assertThat(form.getInvestors().get(0).getFirstName(), is("First"));
        assertThat(form.getInvestors().get(0).getLastName(), is("Last"));
        assertThat(form.getInvestors().get(0).getGender(), is(Gender.MALE));
        assertThat(form.getInvestors().get(0).getMiddleName(), is(nullValue()));
        assertThat(form.getInvestors().get(0).getDateOfBirth(), is("01 Oct 1971"));
        assertThat(form.getInvestors().get(0).isIdVerified(), is(false));
        assertThat((String)((Map) form.getInvestors().get(0).getAddresses().get(0)).get("state"), is("New South Wales"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("stateCode"), is("NSW"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("postcode"), is("2000"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("countryCode"), is("AU"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("country"), is("Australia"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("streetNumber"), is("135-151"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("streetName"), is("Clarence"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("streetType"), is("Street"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("suburb"), is("SYDNEY"));
        assertThat((String)  ((Map)form.getInvestors().get(0).getEmails().get(0)).get("email"), is("test@test.com"));
        assertThat((String)  ((Map)form.getInvestors().get(0).getPhones().get(0)).get("countryCode"), is("+61"));
        assertThat((String)  ((Map)form.getInvestors().get(0).getPhones().get(0)).get("areaCode"), is("41"));
        assertThat((String)  ((Map)form.getInvestors().get(0).getPhones().get(0)).get("number"), is("1111111"));
        assertThat(form.getInvestors().get(0).isGcmUpdated(), is(false));
    }



    @Test
    public void shouldResetPreferredContactAndUpdateIndividualDraftAccountWithGCMData() throws IOException {
        ServiceErrors errors =  null;
        Map<String, Object> jsonRequest = readJsonFromFile("draft_application_form_data.json");
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl(jsonRequest);

        when(repository.find(anyLong())).thenReturn(mock(ClientApplication.class));
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        when(globalCustomerDtoService.find(any(ClientKey.class), eq(errors))).thenReturn(setSameIndividualDto());
        IClientApplicationForm form = ClientApplicationFormFactory.getNewClientApplicationForm(service.find(new ClientApplicationKey(12345852013L), errors).getFormData());
        assertThat(form.getInvestors().get(0).getTitle(), is("Mr"));
        assertThat(form.getInvestors().get(0).getFirstName(), is("First"));
        assertThat(form.getInvestors().get(0).getLastName(), is("Last"));
        assertThat(form.getInvestors().get(0).getGender(), is(Gender.MALE));
        assertThat(form.getInvestors().get(0).getMiddleName(), is(nullValue()));
        assertThat(form.getInvestors().get(0).getDateOfBirth(), is("01 Oct 1971"));
        assertThat(form.getInvestors().get(0).isIdVerified(), is(false));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("state"), is("New South Wales"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("stateCode"), is("NSW"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("postcode"), is("2000"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("countryCode"), is("AU"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("country"), is("Australia"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("streetNumber"), is("135-151"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("streetName"), is("Clarence"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("streetType"), is("Street"));
        assertThat((String) ((Map)form.getInvestors().get(0).getAddresses().get(0)).get("suburb"), is("SYDNEY"));
        assertThat((String)  ((Map)form.getInvestors().get(0).getEmails().get(0)).get("email"), is("test@test.com"));
        assertThat((String)  ((Map)form.getInvestors().get(0).getPhones().get(0)).get("countryCode"), is("+61"));
        assertThat((String)  ((Map)form.getInvestors().get(0).getPhones().get(0)).get("areaCode"), is("41"));
        assertThat((String)  ((Map)form.getInvestors().get(0).getPhones().get(0)).get("number"), is("1111111"));
        assertThat(form.getInvestors().get(0).isGcmUpdated(), is(false));
    }

    private static IndividualDto setAvaloqIndividualDto() {
        IndividualDto individual =  new IndividualDto();
        individual.setTitle("Mr");
        individual.setFirstName("George");
        individual.setMiddleName("Bumpkin");
        individual.setLastName("Test");
        individual.setGender("Male");
        individual.setDateOfBirth("12/04/1978");
        individual.setIdVerified(true);
        individual.setRegistered(true);
        individual.setAddresses(Arrays.asList(setResidentialAddressDto(), setPostalAddressDto()));
        individual.setPhones(Arrays.asList(setPhoneDto("+61", "04", "34569090"), setPhoneDto("+61", "02", "82542234")));
        individual.setEmails(Arrays.asList(setEmailDto("test@test.com"), setEmailDto("test2@test.com")));
        return individual;
    }


    private static IndividualDto setIndividualDto(String isForeignRegistered) {
        IndividualDto individual =  new IndividualDto();
        individual.setTitle("Mr");
        individual.setFirstName("George");
        individual.setMiddleName("Bumpkin");
        individual.setLastName("Test");
        individual.setGender("Male");
        individual.setDateOfBirth("12/04/1978");
        individual.setIdVerified(true);
        individual.setRegistered(false);
        individual.setAddresses(singletonList(setResidentialAddressDto()));
        individual.setPhones(Arrays.asList(setPhoneDto("+61", "04", "34569090"), setPhoneDto("+61", "02", "82542234")));
        individual.setEmails(Arrays.asList(setEmailDto("test@test.com"), setEmailDto("test2@test.com")));
        individual.setIsForeignRegistered(isForeignRegistered);
        return individual;
    }

    private static IndividualDto setSameIndividualDto(){
        IndividualDto individual = new IndividualDto();
        individual.setTitle("Mr");
        individual.setFirstName("First");
        individual.setLastName("Last");
        individual.setGender("Male");
        individual.setDateOfBirth("01 Oct 1971");
        individual.setIdVerified(false);
        individual.setRegistered(false);
        individual.setAddresses(singletonList(setSameAddressDto()));
        individual.setPhones(Arrays.asList(setPhoneDto("+61", "41", "1111111")));
        individual.setEmails(Arrays.asList(setEmailDto("test@test.com")));
        return individual;
    }


    private static AddressDto setSameAddressDto(){
        AddressDto addressDto = new AddressDto();
        addressDto.setState("New South Wales");
        addressDto.setStateCode("NSW");
        addressDto.setPostcode("2000");
        addressDto.setCountryCode("AU");
        addressDto.setCountry("Australia");
        addressDto.setStreetNumber("135-151");
        addressDto.setStreetName("Clarence");
        addressDto.setStreetType("Street");
        addressDto.setSuburb("SYDNEY");

        return addressDto;
    }

    private static AddressDto setResidentialAddressDto(){
        AddressDto addressDto = new AddressDto();
        addressDto.setState("Victoria");
        addressDto.setStateCode("VIC");
        addressDto.setPostcode("3000");
        addressDto.setCountryCode("AU");
        addressDto.setCountry("Australia");
        addressDto.setFloor("10");
        addressDto.setStreetNumber("150");
        addressDto.setStreetName("Collins");
        addressDto.setStreetType("Street");
        addressDto.setSuburb("Melbourne");
        addressDto.setDomicile(true);
        addressDto.setMailingAddress(false);
        return addressDto;
    }

    private static AddressDto setPostalAddressDto(){
        AddressDto addressDto = new AddressDto();
        addressDto.setState("Victoria");
        addressDto.setStateCode("VIC");
        addressDto.setPostcode("3024");
        addressDto.setCountryCode("AU");
        addressDto.setCountry("Australia");
        addressDto.setFloor("10");
        addressDto.setStreetNumber("120");
        addressDto.setStreetName("Burke");
        addressDto.setStreetType("Street");
        addressDto.setSuburb("Collinswood");
        addressDto.setDomicile(false);
        addressDto.setMailingAddress(true);
        return addressDto;
    }

    private static PhoneDto setPhoneDto(String countryCode, String areaCode, String number){
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setCountryCode(countryCode);
        phoneDto.setAreaCode(areaCode);
        phoneDto.setNumber(number);
        return phoneDto;
    }

    private static EmailDto setEmailDto(String emailAddress){
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail(emailAddress);
        return emailDto;
    }
    @Test /*
    WTF moment: the service needs to do this so that the Update operation throws a NotFoundException.
    The service can not throw the itself NotFoundException as it does not know the API version so it
    can not fill in the correct value on the exception.
    */
    public void updateShouldReturnNullIfEntityDoesNotExist() throws Exception {
        when(dto.getKey()).thenReturn(new ClientApplicationKey(-1L));
        when(repository.find(anyLong())).thenThrow(new NoResultException());
        assertNull(service.update(dto, null));
    }

    @Test
    public void updateShouldRaiseErrorWhenApplicationIsNotDraft() throws Exception {
        ClientApplicationDto dto = aDraftAccountDto().build();

        ClientApplication submittedDraftAccount = new ClientApplication();
        submittedDraftAccount.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        submittedDraftAccount.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));

        submittedDraftAccount.markSubmitted();
        when(repository.find(dto.getKey().getClientApplicationKey())).thenReturn(submittedDraftAccount);

        try {
            service.update(dto, new ServiceErrorsImpl());
            fail("update should have raised an exception");
        } catch (IllegalStateException ex) {
            assertThat(ex.getMessage(), containsString("Cannot update Client Application"));
        }
    }

    @Test
    public void createShouldSetCurrentDateTimeToDraftAccount() throws Exception {
        DateTime someDateTime = DateTime.parse("2000-01-01T16:34:22");
        when(dateTimeService.getCurrentDateTime()).thenReturn(someDateTime);
        ClientApplicationDto dto = aDraftAccountDto().build();
        service.create(dto, serviceErrors);
        verify(repository).save(argThat(hasLastModified(someDateTime)));
    }

    @Test
    public void updateShouldSetCurrentDateTimeToDraftAccount() throws Exception {
        DateTime someDateTime = DateTime.parse("2000-01-01T16:34:22");
        when(dateTimeService.getCurrentDateTime()).thenReturn(someDateTime);
        ClientApplication draftAccount = new ClientApplication();
        draftAccount.setAdviserPositionId(EncodedString.toPlainText(DEFAULT_ENCODED_ADVISER_ID));
        draftAccount.setProductId(EncodedString.toPlainText(DEFAULT_ENCODED_PRODUCT_ID));

        when(dto.getKey()).thenReturn(new ClientApplicationKey(123L));
        when(repository.find(123L)).thenReturn(draftAccount);
        service.update(dto, serviceErrors);
        assertThat(draftAccount.getLastModifiedAt(),equalTo(someDateTime));
    }

    private Matcher<ClientApplication> hasLastModified(final DateTime dateTime) {
        return new TypeSafeMatcher<ClientApplication>() {
            @Override
            protected boolean matchesSafely(ClientApplication item) {
                return dateTime.equals(item.getLastModifiedAt());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("DraftAccount with lastModified " + dateTime);
            }
        };
    }

    private static Matcher<ClientApplication> hasAttributeValue(String attributeName, String attributeValue) {
        return new AttributeMatcher<>(attributeName, attributeValue);
    }

    @Test
    public void deleteShouldUpdateDraftStatusToDeleted() {

        String lastModifiedAdviserID = "SOME_ADVISER_ID";
        when(userProfileService.getGcmId()).thenReturn(lastModifiedAdviserID);

        DateTime someDateTime = DateTime.parse("2013-08-23T12:12:11");
        when(dateTimeService.getCurrentDateTime()).thenReturn(someDateTime);

        ClientApplication existingDraftAccount = new ClientApplication();
        existingDraftAccount.setAdviserPositionId("ORIGINAL ADVISER ID");

        when(repository.find(CLIENT_APPLICATION_ID)).thenReturn(existingDraftAccount);
        service.delete(new ClientApplicationKey(CLIENT_APPLICATION_ID), serviceErrors);
        assertThat(existingDraftAccount.getStatus(), equalTo(ClientApplicationStatus.deleted));
        assertThat(existingDraftAccount.getLastModifiedAt(), equalTo(someDateTime));
        assertThat(existingDraftAccount.getLastModifiedId(), equalTo(lastModifiedAdviserID));
    }

    @Test
    public void deleteShouldThrowExceptionIfStatusIsNotDraftAndNoOnboardingApplicationExists(){

        ClientApplication existingDraftAccount = new ClientApplication();
        existingDraftAccount.setAdviserPositionId("ORIGINAL ADVISER ID");
        existingDraftAccount.markSubmitted();

        when(repository.find(CLIENT_APPLICATION_ID)).thenReturn(existingDraftAccount);
        try {
            service.delete(new ClientApplicationKey(CLIENT_APPLICATION_ID), serviceErrors);
            fail("Should have thrown exception");
        } catch (NullPointerException ex) {
            // correct!
        }
    }

    @Test
    public void deleteShouldThrowExceptionIfStatusIsNotDraftAndNotDiscardedInAvaloq(){
        String avaloqOrderId = "avaloqOrderId";
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setAdviserPositionId("ORIGINAL ADVISER ID");
        clientApplication.markSubmitted();
        OnBoardingApplication onboardingApplication = new OnBoardingApplication(OnboardingApplicationStatus.processing, avaloqOrderId);
        clientApplication.setOnboardingApplication(onboardingApplication);

        ApplicationIdentifier applicationIdentifier = new ApplicationIdentifierImpl();
        applicationIdentifier.setDocId(avaloqOrderId);

        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppState(ApplicationStatus.PEND_ACCEPT);

        when(accActivationIntegrationService.loadAccApplicationForApplicationId(Arrays.asList(applicationIdentifier),activeProfile.getJobRole(),activeProfile.getClientKey(), serviceErrors)).thenReturn(Arrays.asList(applicationDocument));

        when(repository.find(CLIENT_APPLICATION_ID)).thenReturn(clientApplication);
        try {
            service.delete(new ClientApplicationKey(CLIENT_APPLICATION_ID), serviceErrors);
            fail("Should have thrown exception");
        } catch (IllegalStateException ex) {
            // correct!
        }
    }


    @Test
    public void deleteShouldDeleteAnApplicationIfItsWithdrawnInAvaloq(){

        String avaloqOrderId = "avaloqOrderId";
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setAdviserPositionId("ORIGINAL ADVISER ID");
        clientApplication.markSubmitted();
        OnBoardingApplication onboardingApplication = new OnBoardingApplication(OnboardingApplicationStatus.processing, avaloqOrderId);
        clientApplication.setOnboardingApplication(onboardingApplication);

        ApplicationIdentifier applicationIdentifier = new ApplicationIdentifierImpl();
        applicationIdentifier.setDocId(avaloqOrderId);

        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppState(ApplicationStatus.DISCARDED);

        when(accActivationIntegrationService.loadAccApplicationForApplicationId(Arrays.asList(applicationIdentifier), activeProfile.getJobRole(),activeProfile.getClientKey(),serviceErrors)).thenReturn(Arrays.asList(applicationDocument));
        when(repository.find(CLIENT_APPLICATION_ID)).thenReturn(clientApplication);
        service.delete(new ClientApplicationKey(CLIENT_APPLICATION_ID), serviceErrors);
        assertThat(clientApplication.getStatus(), is(ClientApplicationStatus.deleted));
    }


    @Test
    public void findShouldReturnADraftAccountObject() throws Exception {
        ServiceErrors errors = null;
        Map<String, Object> jsonRequest = readJsonFromFile("individualExisting.json");
        ClientApplication clientApplication = mock(ClientApplication.class);
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl(jsonRequest);

        when(repository.find(anyLong())).thenReturn(clientApplication);
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(errors))).thenReturn(clientApplicationDto);
        CustomerDataDto customerDataDto = new CustomerDataDto();
        customerDataDto.setTaxResidenceCountries(Arrays.asList(getTaxResidenceCountry("us","btfg$tin_never_iss",null),getTaxResidenceCountry("FOREIGN","","N")));
        when(directInvestorDataDtoService.find(any(ClientUpdateKey.class), eq(errors))).thenReturn(customerDataDto);
        ClientApplicationDto newclientApplication = service.find(new ClientApplicationKey(CLIENT_APPLICATION_ID),null);
        assertNotNull(newclientApplication);
    }


    @Test
    public void findShouldReturnNullIfThereIsNoRecordOfTheKey() {
        ClientApplication existingDraftAccount = new ClientApplication();
        existingDraftAccount.setAdviserPositionId("Some Adviser");
        when(repository.find(CLIENT_APPLICATION_ID)).thenThrow(NoResultException.class);
        ClientApplicationDto clientApplicationDto = service.find(new ClientApplicationKey(CLIENT_APPLICATION_ID), serviceErrors);
        assertNull(clientApplicationDto);
    }

    @Test(expected = IllegalStateException.class)
    public void assertCanApplicationBeDeleted_should_throw_when_application_is_done() {
        OnBoardingApplication application = mockSetUpForOnboardingApplication(aDraftAccountDto().build().getKey().getClientApplicationKey(), OnboardingApplicationStatus.processing);
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.markSubmitted();

        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppState(ApplicationStatus.PEND_ACCEPT);

        clientApplication.setOnboardingApplication(application);
        when(accActivationIntegrationService.loadAccApplicationForApplicationId((List<ApplicationIdentifier>)anyObject(), (JobRole)anyObject(),(com.bt.nextgen.service.integration.userinformation.ClientKey)anyObject(),(ServiceErrors)anyObject())).thenReturn(Arrays.asList(applicationDocument));
        service.assertCanApplicationBeDeleted(clientApplication, serviceErrors);
    }

    @Test
    public void assertCanApplicationBeDeleted_should_not_throw_for_withdrawn_applications() {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.markSubmitted();

        OnBoardingApplication application = mockSetUpForOnboardingApplication(aDraftAccountDto().build().getKey().getClientApplicationKey(), OnboardingApplicationStatus.withdrawn);
        clientApplication.setOnboardingApplication(application);

        ApplicationDocument applicationDocument = new ApplicationDocumentImpl();
        applicationDocument.setAppState(ApplicationStatus.DISCARDED);
        when(accActivationIntegrationService.loadAccApplicationForApplicationId((List<ApplicationIdentifier>)anyObject(), (JobRole)anyObject(),(com.bt.nextgen.service.integration.userinformation.ClientKey)anyObject(),(ServiceErrors)anyObject())).thenReturn(Arrays.asList(applicationDocument));

        service.assertCanApplicationBeDeleted(clientApplication, serviceErrors);
    }

    @Test
    public void assertCanApplicationBeDeleted_should_not_throw_for_draft_applications() {
        ClientApplication clientApplication = new ClientApplication();
        service.assertCanApplicationBeDeleted(clientApplication, serviceErrors);
    }

    @Test
    public void assertCanApplicationBeDeleted_should__not_throw_for_failed_applications() {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.markSubmitted();
        OnBoardingApplication application = mockSetUpForOnboardingApplication(
                aDraftAccountDto().build().getKey().getClientApplicationKey(),
                OnboardingApplicationStatus.ApplicationCreationFailed);
        clientApplication.setOnboardingApplication(application);

        service.assertCanApplicationBeDeleted(clientApplication, serviceErrors);
    }

    private Broker createBroker(String myBrokerId) {
        Broker broker = mock(Broker.class);
        when(broker.getBrokerType()).thenReturn(BrokerType.ADVISER);
        when(broker.getKey()).thenReturn(BrokerKey.valueOf(myBrokerId));
        when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("DEALER_KEY"));
        return broker;
    }


    private OnBoardingApplication mockSetUpForOnboardingApplication(final Long applicationId, final OnboardingApplicationStatus status) {
        OnBoardingApplication application = mock(OnBoardingApplication.class);
        when(application.getKey()).thenReturn(OnboardingApplicationKey.valueOf(applicationId));
        when(application.getStatus()).thenReturn(status);
        when(onboardingApplicationRepository.save(any(OnBoardingApplication.class))).thenReturn(application);
        return application;
    }

    private static TaxResidenceCountriesDto getTaxResidenceCountry(String residenceCountry, String exemptionReason, String tin) {
        TaxResidenceCountriesDto taxResidenceCountriesDto =  new TaxResidenceCountriesDto();
        taxResidenceCountriesDto.setTaxResidenceCountry(residenceCountry);
        taxResidenceCountriesDto.setTaxExemptionReason(exemptionReason);
        taxResidenceCountriesDto.setTin(tin);

        return taxResidenceCountriesDto;
    }
}
