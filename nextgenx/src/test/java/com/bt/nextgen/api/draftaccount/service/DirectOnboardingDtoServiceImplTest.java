package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoBuilder;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoDirectImpl;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.repository.CisKeyClientApplication;
import com.bt.nextgen.core.repository.CisKeyClientApplicationRepositoryImpl;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.DateTimeService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductDetailImpl;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.util.SamlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class DirectOnboardingDtoServiceImplTest {

    public static final String DEFAULT_CIS_KEY = "1234";
    public static final String ADVISER_ID = "79260";;

    @Mock
    private PermittedClientApplicationRepository clientApplicationRepository;

    @Mock
    private ClientApplicationDtoHelperService clientApplicationHelperService;

    @Mock
    private ClientApplicationFormDataConverterService clientApplicationFormDataConverterService;

    @Mock
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private CisKeyClientApplicationRepositoryImpl cisKeyClientApplicationRepository;

    @Mock
    private UserProfileService profileService;

    @Mock
    private DateTimeService dateTimeService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private DirectOnboardingDtoServiceImpl service;

    private ClientApplicationDto clientApplicationDto;
    private DateTime dateTime = new DateTime();

    private ObjectMapper mapper;


    @Before
    public void setUp() {
        //setup jsonObjectMapper for tests
        mapper = new JsonObjectMapper();
        applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"), any(Class.class))).thenReturn(mapper);
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"))).thenReturn(mapper);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);

       clientApplicationDto = ClientApplicationDtoBuilder.aDraftAccountDto().build();
        when(clientApplicationHelperService.submitDraftAccount(any(ClientApplicationDto.class), eq(serviceErrors), any(ClientApplication.class)))
                .thenReturn(clientApplicationDto);
        when(clientApplicationFormDataConverterService.convertFormDataForDirect(Matchers.<Map<String, Object>>anyObject())).thenReturn(defaultFormData());
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), eq(serviceErrors))).thenReturn(mock(Broker.class));
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), eq(serviceErrors))).thenReturn(Arrays.asList(createProduct()));
        configureSamlTokenForWPLUser();

        when(dateTimeService.getCurrentDateTime()).thenReturn(dateTime);
    }

    private void configureSamlTokenForWPLUser() {
        SamlToken token = new SamlToken(SamlUtil.loadSaml("saml-sample-wpl.xml"));
        when(profileService.getSamlToken()).thenReturn(token);
    }

    private Product createProduct() {
        ProductDetailImpl product = new ProductDetailImpl();
        product.setProductKey(ProductKey.valueOf("product"));
        product.setProductName("BT Invest");
        product.setProductId("product");
        product.setSuper(false);
        product.setDirect(true);
        product.setProductLevel(ProductLevel.WHITE_LABEL);
        return product;
    }

    private Product createSuperAccumOrSuperPensionProduct() {
        ProductDetailImpl product = new ProductDetailImpl();
        product.setProductKey(ProductKey.valueOf("product"));
        product.setProductName("BT Super Invest");
        product.setProductId("super");
        product.setSuper(true);
        product.setDirect(true);
        product.setProductLevel(ProductLevel.WHITE_LABEL);
        return product;
    }

    @Test
    public void submitShouldSaveTheApplicationInRepository() {
        service.submit(clientApplicationDto, serviceErrors);
        ArgumentCaptor<ClientApplication> argumentCaptor = ArgumentCaptor.forClass(ClientApplication.class);
        verify(clientApplicationRepository).save(argumentCaptor.capture());

        ClientApplication clientApplication = argumentCaptor.getValue();
        assertThat(clientApplication.getAdviserPositionId(), is(ADVISER_ID));
        assertThat(clientApplication.getProductId(), is("product"));
        assertThat(clientApplication.getLastModifiedId(), is("12117400014"));
        assertThat(clientApplication.getLastModifiedAt(), is(dateTime));
    }

    @Test
    public void submitShouldConvertFormData() {
        Map<String,Object> formData = new HashMap<>();
        formData.put("Hello", "World");
        clientApplicationDto.setFormData(formData);

        service.submit(clientApplicationDto, serviceErrors);
        verify(clientApplicationFormDataConverterService, times(1)).convertFormDataForDirect(eq(formData));
    }

    @Test
    public void submitShouldInvokeClientApplicationHelperSubmit() {
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(serviceErrors))).thenReturn(clientApplicationDto);
        service.submit(clientApplicationDto, serviceErrors);
        verify(clientApplicationHelperService, times(1)).submitDraftAccount(eq(clientApplicationDto), eq(serviceErrors), any(ClientApplication.class));
    }

    @Test
    public void submitShouldSaveCisKeyAndApplicationInRepository(){
        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), eq(serviceErrors))).thenReturn(clientApplicationDto);
        service.submit(clientApplicationDto, serviceErrors);
        verify(cisKeyClientApplicationRepository, times(1)).save(any(CisKeyClientApplication.class));
    }

    @Test
    public void submitShouldNotInvokeConverterServiceWhenFormDataContainsVersion() throws IOException {
        String mobileNo = "0423232323";
        HashMap<String, Object> map = getFormDataWithNewFormat(mobileNo, false);
        final String formDataString = mapper.writeValueAsString(map);
        final DirectClientApplicationFormData directClientApplicationFormData = mapper.readValue(formDataString, DirectClientApplicationFormData.class);
        clientApplicationDto = new ClientApplicationDtoDirectImpl(directClientApplicationFormData);

        service.submit(clientApplicationDto, serviceErrors);
        verify(clientApplicationFormDataConverterService, times(0)).convertFormDataForDirect(Matchers.anyMap());
        verify(cisKeyClientApplicationRepository, times(1)).save(any(CisKeyClientApplication.class));
    }

    @Test
    public void submitShouldSetGcmIdForExistingUser() throws IOException {

        String mobileNo = "0423232323";
        HashMap<String, Object> map = getFormDataWithNewFormat(mobileNo, true);
        final String formDataString = mapper.writeValueAsString(map);
        final DirectClientApplicationFormData directClientApplicationFormData = mapper.readValue(formDataString, DirectClientApplicationFormData.class);

        clientApplicationDto = new ClientApplicationDtoDirectImpl(directClientApplicationFormData);

        service.submit(clientApplicationDto, serviceErrors);
        verify(clientApplicationFormDataConverterService, times(0)).convertFormDataForDirect(Matchers.anyMap());
        verify(userProfileService, times(1)).getGcmId();
        verify(cisKeyClientApplicationRepository, times(1)).save(any(CisKeyClientApplication.class));
    }

    @Test
    public void submitDirectSuperHaveCorrectProductId() throws IOException {
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), eq(serviceErrors))).thenReturn(Arrays.asList(createSuperAccumOrSuperPensionProduct()));

        String mobileNo = "0423232323";
        HashMap<String, Object> map = getFormDataWithNewFormat(mobileNo, true);
        map.put("accountType", "superAccumulation");
        final String formDataString = mapper.writeValueAsString(map);
        final DirectClientApplicationFormData directClientApplicationFormData = mapper.readValue(formDataString, DirectClientApplicationFormData.class);
        clientApplicationDto = new ClientApplicationDtoDirectImpl(directClientApplicationFormData);

        service.submit(clientApplicationDto, serviceErrors);
        ArgumentCaptor<ClientApplication> argumentCaptor = ArgumentCaptor.forClass(ClientApplication.class);
        verify(clientApplicationRepository).save(argumentCaptor.capture());

        ClientApplication clientApplication = argumentCaptor.getValue();
        assertThat(clientApplication.getAdviserPositionId(), is("107164"));
        assertThat(clientApplication.getProductId(), is("super"));
        assertThat(clientApplication.getClientApplicationForm().getAccountType(), is(IClientApplicationForm.AccountType.SUPER_ACCUMULATION));

    }

    @Test
    public void submitDirectBtInvestHaveCorrectProductId() throws IOException {
        when(productIntegrationService.getDealerGroupProductList(any(BrokerKey.class), eq(serviceErrors))).thenReturn(Arrays.asList(createProduct()));
        String mobileNo = "0423232323";
        HashMap<String, Object> map = getFormDataWithNewFormat(mobileNo, true);
        map.put("accountType", "individual");
        final String formDataString = mapper.writeValueAsString(map);
        final DirectClientApplicationFormData directClientApplicationFormData = mapper.readValue(formDataString, DirectClientApplicationFormData.class);
        clientApplicationDto = new ClientApplicationDtoDirectImpl(directClientApplicationFormData);

        service.submit(clientApplicationDto, serviceErrors);
        ArgumentCaptor<ClientApplication> argumentCaptor = ArgumentCaptor.forClass(ClientApplication.class);
        verify(clientApplicationRepository).save(argumentCaptor.capture());

        ClientApplication clientApplication = argumentCaptor.getValue();
        assertThat(clientApplication.getAdviserPositionId(), is(ADVISER_ID));
        assertThat(clientApplication.getProductId(), is("product"));
        assertThat(clientApplication.getClientApplicationForm().getAccountType(), is(IClientApplicationForm.AccountType.INDIVIDUAL));

    }

    private HashMap<String, Object> getFormDataWithNewFormat(String mobileNo, boolean isExisting) {
        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("version", "1.1.1");
        map.put("applicationOrigin", "WestpacLive");

        HashMap<String, Object> investors = new LinkedHashMap<>();

        if(isExisting) {
            HashMap<String, Object> clientKey = new HashMap<>();
            clientKey.put("clientId", "123212213");
            investors.put("key", clientKey);
        } else {
            HashMap<String, Object> mobile = new HashMap<>();
            mobile.put("value", EncodedString.fromPlainText(mobileNo).toString());
            investors.put("mobile", mobile);
        }
        map.put("investors", Arrays.asList(investors));
        return map;
    }


    private LinkedHashMap<String,Object> defaultFormData() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("accountType", "individual");

        LinkedHashMap<String, Object> investors = new LinkedHashMap<>();
        investors.put("ciskey", DEFAULT_CIS_KEY);

        map.put("investors", Arrays.asList(investors));
        return map;
    }
}