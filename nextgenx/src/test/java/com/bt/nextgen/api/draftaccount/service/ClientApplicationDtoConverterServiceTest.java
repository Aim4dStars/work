package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.AccountTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.DirectAccountTypeEnum;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.bt.nextgen.draftaccount.repository.ClientApplicationBuilder.aDraftAccount;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.util.Assert.isTrue;


@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationDtoConverterServiceTest {

    @Mock
    private ProductIntegrationService productIntegrationService;
    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @InjectMocks
    private ClientApplicationDtoConverterService service;

    private static final String ADVISER_ID =  "SOME TYPE";

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        //setup jsonObjectMapper for tests
        mapper = new JsonObjectMapper();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean("jsonObjectMapper")).thenReturn(mapper);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);

        ProductImpl product = new ProductImpl();
        product.setProductName("my-name");
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(product);

        BrokerUser brokerUser = mock(BrokerUser.class);
        when(brokerUser.getFirstName()).thenReturn("FirstName");
        when(brokerUser.getLastName()).thenReturn("LastName");

        service.setObjectMapper(new JsonObjectMapper());

        when(brokerIntegrationService.getBrokerUser(any(UserKey.class), any(ServiceErrorsImpl.class))).thenReturn(brokerUser);
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrorsImpl.class))).thenReturn(brokerUser);
    }

    @Test
    public void shouldConvertIndividualAccountToDto() throws Exception {
        ClientApplication draftAccount = aDraftAccount().withId(1L)
                .build();
        draftAccount.setFormData("{\"accountType\":\"corporateSMSF\"}");
        draftAccount.setAdviserPositionId(ADVISER_ID);

        ClientApplicationDto dto = service.convertToDto(draftAccount, null);
        assertThat(EncodedString.toPlainText(dto.getAdviserId()), equalTo(ADVISER_ID));
        assertThat(dto.getReferenceNumber(), is("R000000001"));
    }

    @Test
    public void shouldConvertToMinimalDtoObject_map() throws Exception {
        ClientApplication draftAccount = aDraftAccount().withId(1L)
                .build();
        draftAccount.setFormData("{\"accountType\":\"individual\", \"status\":\"draft\"}");

        draftAccount.setAdviserPositionId(ADVISER_ID);
        ClientApplicationDto dto = service.convertToMinimalDto(draftAccount);
        assertThat(dto.getAccountType(), is("individual"));
        assertThat(dto.getStatus(), is(ClientApplicationStatus.draft));
        assertThat(dto.isJsonSchemaSupported(), is(false));
        Assert.isNull(dto.getAdviserId());
        Assert.isNull(dto.getAdviserName());
    }

    @Test
    public void shouldConvertToMinimalDtoObject_directApplication() throws Exception {
        ClientApplication draftAccount = aDraftAccount().withId(1L)
                .build();
        DirectClientApplicationFormData formData = new DirectClientApplicationFormData();
        formData.setAccountType(DirectAccountTypeEnum.INDIVIDUAL);
        formData.setVersion("1.0.0");

        draftAccount.setFormData(formData);
        draftAccount.setAdviserPositionId(ADVISER_ID);

        ClientApplicationDto dto = service.convertToMinimalDto(draftAccount);
        assertThat(dto.getAccountType(), is("individual"));
        assertThat(dto.getStatus(), is(ClientApplicationStatus.draft));
        assertThat(dto.isJsonSchemaSupported(), is(true));
        Assert.isNull(dto.getAdviserId());
        Assert.isNull(dto.getAdviserName());
    }

    @Test
    public void shouldConvertToMinimalDtoObject_onboardingApplication() throws Exception {
        ClientApplication draftAccount = aDraftAccount().withId(1L)
                .build();
        OnboardingApplicationFormData formData = new OnboardingApplicationFormData();
        formData.setAccountType(AccountTypeEnum.INDIVIDUAL_SMSF);
        formData.setVersion("1.0.0");

        draftAccount.setFormData(formData);
        draftAccount.setAdviserPositionId(ADVISER_ID);

        ClientApplicationDto dto = service.convertToMinimalDto(draftAccount);
        assertThat(dto.getAccountType(), is("individualSMSF"));
        assertThat(dto.getStatus(), is(ClientApplicationStatus.draft));
        assertThat(dto.isJsonSchemaSupported(), is(true));
        Assert.isNull(dto.getAdviserId());
        Assert.isNull(dto.getAdviserName());
    }

    @Test
    public void shouldConvertToDto_onboardingApplication() throws Exception {
        ClientApplication draftAccount = aDraftAccount().withId(1L)
                .build();
        OnboardingApplicationFormData formData = new OnboardingApplicationFormData();
        formData.setAccountType(AccountTypeEnum.INDIVIDUAL_SMSF);
        formData.setVersion("1.0.0");

        draftAccount.setFormData(formData);
        draftAccount.setAdviserPositionId(ADVISER_ID);

        ClientApplicationDto dto = service.convertToDto(draftAccount, new ServiceErrorsImpl());
        assertThat(dto.getAccountType(), is("individualSMSF"));
        assertThat(dto.getStatus(), is(ClientApplicationStatus.draft));
        assertThat(dto.isJsonSchemaSupported(), is(true));
    }


    @Test
    public void shouldCreateADTOThatHasFormDataConvertedFromString() throws Exception {
        ClientApplication draftAccount = aDraftAccount().build();
        draftAccount.setFormData("{\"name\":\"my-name\",\"accountType\":\"corporateSMSF\"}");
        ProductImpl product = new ProductImpl();
        product.setProductName("prod-name");
        when(productIntegrationService.getProductDetail(eq(ProductKey.valueOf(draftAccount.getProductId())), any(ServiceErrors.class))).thenReturn(product);

        ClientApplicationDto dto = service.convertToDto(draftAccount, null);

        assertThat((String) ((Map<String, Object>) dto.getFormData()).get("name"), is("my-name"));
    }

    @Test
    public void shouldCreateADTOThatHasFormDataConvertedFromString_forInvalidProduct() throws Exception {
        ClientApplication draftAccount = aDraftAccount().build();
        draftAccount.setFormData("{\"name\":\"my-name\",\"accountType\":\"corporateSMSF\"}");
        when(productIntegrationService.getProductDetail(eq(ProductKey.valueOf(draftAccount.getProductId())), any(ServiceErrors.class))).thenReturn(null);

        ClientApplicationDto dto = service.convertToDto(draftAccount, null);

        assertNull(dto.getProductName());
    }

    @Test
    public void shouldCreateADTOThatHasProductName() throws Exception {
        ClientApplication draftAccount = aDraftAccount().build();
        draftAccount.setFormData("{\"accountType\":\"corporateSMSF\"}");

        ProductImpl product = new ProductImpl();
        product.setProductName("prod-name");
        when(productIntegrationService.getProductDetail(eq(ProductKey.valueOf(draftAccount.getProductId())), any(ServiceErrors.class))).thenReturn(product);

        ClientApplicationDto dto = service.convertToDto(draftAccount, null);
        assertThat(dto.getStatus(), is(ClientApplicationStatus.draft));
        assertThat(dto.getProductName(), is("prod-name"));
    }

    @Test
    public void shouldCreateADTOThatHasLastModifiedName() throws Exception {
        ClientApplication draftAccount = aDraftAccount().build();
        draftAccount.setFormData("{\"accountType\":\"individual\"}");
        draftAccount.setLastModifiedId("someId");

        ClientApplicationDto dto = service.convertToDto(draftAccount, null);
        assertThat(dto.getStatus(), is(ClientApplicationStatus.draft));
        assertThat(dto.getLastModifiedByName(), is("LastName, FirstName"));
    }

    @Test
    public void shouldCreateADTOThatHasLastModifiedNameForDirectAccount() throws Exception {
        ClientApplication draftAccount = aDraftAccount().build();
        draftAccount.setFormData("{\"accountType\":\"individual\"," +
                "\"adviceType\":\"NoAdvice\"," +
                "\"applicationOrigin\":\"WestpacLive\"}");
        draftAccount.setLastModifiedId("someId");

        ClientApplicationDto dto = service.convertToDto(draftAccount, null);
        assertThat(dto.getStatus(), is(ClientApplicationStatus.draft));
        assertThat(dto.getLastModifiedByName(), is("someId"));
    }

    @Test
    public void shouldCreateDtoWhichHasLastModifiedAt(){
        ClientApplication draftAccount = aDraftAccount().build();
        DateTime dt = new DateTime("2004-12-13T21:39:45.618-08:00");
        draftAccount.setLastModifiedAt(dt);
        ClientApplicationDto dto = service.convertToDto(draftAccount, null);
        assertThat(dto.getLastModified(),is(dt));
    }

    @Test
    public void shouldCreateADtoThatHasAdviserName() {
        ClientApplication draftAccount = aDraftAccount().build();
        draftAccount.setFormData("{\"accountType\":\"individual\"}");
        draftAccount.setAdviserPositionId(ADVISER_ID);
        ClientApplicationDto dto = service.convertToDto(draftAccount, null);

        assertThat(dto.getAdviserName(), is("LastName, FirstName"));
    }

    @Test
    public void shouldCreateDtoWithFormDataOfTypeDirectApplicationFormData() throws JsonProcessingException {
        ClientApplication draftAccount = aDraftAccount().build();
        final HashMap<String, Object> map = getJsonSchemaSupportedDirectFormData();
        final String formDataString = mapper.writeValueAsString(map);
        draftAccount.setFormData(formDataString);
        draftAccount.setAdviserPositionId(ADVISER_ID);
        ClientApplicationDto dto = service.convertToDto(draftAccount, null);
        isTrue(dto.getFormData() instanceof DirectClientApplicationFormData);
    }

    private HashMap<String, Object> getJsonSchemaSupportedDirectFormData() {
        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("version", "1.1.1");
        map.put("applicationOrigin", "WestpacLive");

        HashMap<String, Object> investors = new LinkedHashMap<>();
        investors.put("firstname", "kumar");
        investors.put("lastname", "kumar");

        HashMap<String, Object> clientKey = new HashMap<>();
        clientKey.put("clientId", "123212213");
        investors.put("key", clientKey);
        map.put("investors", Arrays.asList(investors));
        return map;
    }


}
