package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoMapImpl;
import com.bt.nextgen.api.draftaccount.model.PensionEligibility;
import com.bt.nextgen.api.draftaccount.model.SuperPensionApplicationDetailsDto;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerAnnotationHolder;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationDetailsDtoConverterService_SuperPensionTest extends AbstractJsonReaderTest {

    @Mock
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private IndividualDtoConverter individualDtoConverter;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @InjectMocks
    private ClientApplicationDetailsDtoConverterService clientApplicationDetailsDtoConverterService;

    @Mock
    private ObjectMapper jsonObjectMapper;

    private ClientApplication clientApplication;

    private ClientApplicationDto clientApplicationDto;

    private BrokerUser brokerUser;

    private SuperPensionApplicationDetailsDto superPensionApplicationsDetailsDto;

    @Mock
    private ClientApplicationDetailsDtoHelperService clientApplicationDetailsDtoHelperService;

    @Before
    public void setUp() throws IOException {
        ObjectMapper mapper = new JsonObjectMapper();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"), any(Class.class))).thenReturn(mapper);
        Mockito.when(applicationContext.getBean(eq("jsonObjectMapper"))).thenReturn(mapper);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);

        clientApplication = new ClientApplication();
        Map<String, Object> jsonRequest = readJsonFromFile("superpension-with-condition-release.json");

        clientApplication.setFormData(jsonRequest);
        clientApplication.setAdviserPositionId("BROKER_ID");

        clientApplicationDto = new ClientApplicationDtoMapImpl();
        clientApplicationDto.setProductName("ProdName");
        clientApplicationDto.setReferenceNumber("Ref001");

        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), any(ServiceErrors.class))).thenReturn(clientApplicationDto);

        brokerUser = mock(BrokerUser.class);
        when(brokerIntegrationService.getAdviserBrokerUser(BrokerKey.valueOf("BROKER_ID"), null)).thenReturn(brokerUser);
        
        Broker broker = new BrokerAnnotationHolder();
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrorsImpl.class))).thenReturn(broker);


        when(clientApplicationDetailsDtoHelperService.eligibilityCriteria(any(String.class),any(ServiceErrors.class))).thenReturn("All of the client's superannuation benefits are unrestricted non-preserved");
        when(clientApplicationDetailsDtoHelperService.conditionOfRelease(any(String.class),any(ServiceErrors.class))).thenReturn("Permanent Incapacity");

        when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), any(ServiceErrors.class))).thenReturn(clientApplicationDto);
        superPensionApplicationsDetailsDto = (SuperPensionApplicationDetailsDto) clientApplicationDetailsDtoConverterService.convert(clientApplication, null);
    }

    @Test
    public void convert_shouldReturnDtoWithInvestorDetails() {
        PensionEligibility pensionEligibility = superPensionApplicationsDetailsDto.getPensionEligibility();

        assertEquals(pensionEligibility.getEligibilityCriteria(), "All of the client's superannuation benefits are unrestricted non-preserved");
        assertEquals(pensionEligibility.getConditionRelease(), "Permanent Incapacity");
    }
}
