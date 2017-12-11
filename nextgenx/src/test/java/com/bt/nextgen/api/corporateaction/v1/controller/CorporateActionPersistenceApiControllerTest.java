package com.bt.nextgen.api.corporateaction.v1.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.server.request.MockMvcRequestBuilders;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPersistenceDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionPersistenceDtoService;
import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.controller.ClientApplicationDtoDeserializer;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionPersistenceApiControllerTest {
    @Mock
    private static CorporateActionPersistenceDtoService corporateActionPersistenceDtoService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CorporateActionPersistenceApiController corporateActionPersistenceApiController;

    @Mock
    private ObjectReader objectReader;

    @Captor
    private ArgumentCaptor<CorporateActionPersistenceDto> argumentCaptor;

    @Before
    public void setup() {
        when(objectMapper.readerWithView(any(Class.class))).thenReturn(objectReader);
        when(objectReader.forType(any(TypeReference.class))).thenReturn(objectReader);

        when(corporateActionPersistenceDtoService.submit(any(CorporateActionPersistenceDto.class), any(ServiceErrors.class)))
                .thenReturn(new CorporateActionPersistenceDto());
    }

    @Test(expected = BadRequestException.class)
    public void testSaveCorporateActionElections_whenCorporateActionIdIsNotANumber_thenThrowBadRequestException() {
        corporateActionPersistenceApiController
                .saveCorporateActionElections(EncodedString.fromPlainText("fakeCAId").toString(), null, null, null, null);
    }

    @Test(expected = BadRequestException.class)
    public void testSaveCorporateActionElectionsByPost_whenCorporateActionIdIsNotANumber_thenThrowBadRequestException() throws IOException {
        corporateActionPersistenceApiController.saveCorporateActionElectionsByPost(EncodedString.fromPlainText("fakeCAId").toString(),
                null);
    }

    @Test
    public void testSaveCorporateActionElections_whenThereAreValuesToSave_returnResponseData() throws IOException {
        ApiResponse response = corporateActionPersistenceApiController.saveCorporateActionElections
                ("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "2017-01-01", "[]", null, null);
        assertNotNull(response.getData());

        response = corporateActionPersistenceApiController.saveCorporateActionElections
                ("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "2017-01-01", "[]", "[]", null);
        assertNotNull(response.getData());

        response = corporateActionPersistenceApiController.saveCorporateActionElections
                ("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "2017-01-01", "[]", "[]", "[]");
        assertNotNull(response.getData());
    }

    @Test
    public void testSaveCorporateActionElectionsByPost_whenThereAreValuesToSave_returnResponseData() throws IOException {
        when(objectReader.readValue(anyString())).thenReturn(new CorporateActionPersistenceDto());

        ApiResponse response = corporateActionPersistenceApiController.saveCorporateActionElectionsByPost
                ("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", null);
        assertNotNull(response.getData());
    }

    @Test
    public void testSaveCorporateActionElectionsByPost_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        MockHttpServletRequestBuilder post =
                MockMvcRequestBuilders.post("/secure/api/corporateactions/v1_0/EDCC12075B29E898A8237F3500C3B1424A2DEB6DCB807AB2/saveElections");
        post.body(("{\"options\":[{\"id\":0,\"summary\":\"Take up\",\"isDefault\":false},{\"id\":1,\"summary\":\"Lapse\"," +
                "\"isDefault\":true}],\"accounts\":[{\"clientId\":\"1625347\",\"accountId\":\"120153580\"," +
                "\"accountKey\":\"18CF0B2B4D1E45BB4FD1F58C75BD50490CAFDB475FAB7C1D\",\"positionId\":\"1628844\"," +
                "\"selectedElections\":{\"options\":[{\"optionId\":0,\"units\":10}]}},{\"clientId\":\"1625240\"," +
                "\"accountId\":\"120153481\",\"accountKey\":\"409860480329925ADBEE100DDB631CBA4F36908D46E32790\"," +
                "\"positionId\":\"1628816\",\"selectedElections\":{\"options\":[{\"optionId\":0,\"units\":20}]}}]," +
                "\"closeDate\":\"2017-11-15T01:00:00.000Z\"}").getBytes());

        int status = mockMvc.perform(post).andReturn().getResponse().getStatus();

        verify(corporateActionPersistenceDtoService, atLeastOnce()).submit(argumentCaptor.capture(), any(ServiceErrors.class));

        CorporateActionPersistenceDto corporateActionPersistenceDto = argumentCaptor.getValue();

        assertNotNull(corporateActionPersistenceDto.getCloseDate());
        assertNotNull(corporateActionPersistenceDto.getOptions());
        assertNotNull(corporateActionPersistenceDto.getAccounts());

        CorporateActionOptionDto optionDto = corporateActionPersistenceDto.getOptions().iterator().next();

        assertEquals(0, optionDto.getId().intValue());
        assertEquals("Take up", optionDto.getSummary());
        assertNull(optionDto.getIsDefault());
        assertNull(optionDto.getTitle());
        assertNull(optionDto.getIsNoAction());

        CorporateActionAccountDetailsDto accountDetailsDto = corporateActionPersistenceDto.getAccounts().iterator().next();

        assertEquals("1628844", accountDetailsDto.getPositionId());
        assertEquals("120153580", accountDetailsDto.getAccountId());
        assertEquals("18CF0B2B4D1E45BB4FD1F58C75BD50490CAFDB475FAB7C1D", accountDetailsDto.getAccountKey());
        assertNotNull(accountDetailsDto.getSelectedElections());
        assertEquals(0, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getOptionId().intValue());
        assertEquals(BigDecimal.TEN, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getUnits());

        assertNull(accountDetailsDto.getSavedElections());
        assertNull(accountDetailsDto.getSubmittedElections());
        assertNull(accountDetailsDto.getHolding());
        assertNull(accountDetailsDto.getElectionStatus());
        assertNull(accountDetailsDto.getSavedElections());
        assertNull(accountDetailsDto.getCash());
        assertNull(accountDetailsDto.getTransactionDescription());
        assertNull(accountDetailsDto.getTransactionNumber());
        assertNull(accountDetailsDto.getTransactionStatus());
        assertNull(accountDetailsDto.getAccountType());
        assertNull(accountDetailsDto.getAccountName());
        assertNull(accountDetailsDto.getClientId());
        assertNull(accountDetailsDto.getClientName());
        assertNull(accountDetailsDto.getClientEmail());
        assertNull(accountDetailsDto.getClientPhone());
        assertNull(accountDetailsDto.getOriginalHolding());
        assertNull(accountDetailsDto.getPortfolio());
        assertNull(accountDetailsDto.getPortfolioValue());
        assertNull(accountDetailsDto.getNotification());

        assertEquals(200, status);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveCorporateActionElections_whenThereIsAMapperError_thenThrowIllegalArgumentException() throws IOException {
        when(objectReader.readValue(anyString())).thenThrow(new IOException());

        corporateActionPersistenceApiController
                .saveCorporateActionElections("08E24C73A07BF3B6323E907360B880E544B662EBA8EE1BF7", "2017-01-01", null, null, null);
    }

    @Test
    public void testSaveCorporateActionElections_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        MockHttpServletRequestBuilder post =
                MockMvcRequestBuilders.post("/secure/api/corporateactions/v1_0/EDCC12075B29E898A8237F3500C3B1424A2DEB6DCB807AB2/save");
        post.param("closeDate", "2017-09-28T02:00:00.000Z");
        post.param("options", "[{\"id\":1,\"summary\":\"$22 per share & 1:2 BHP shares\",\"isDefault\":false},{\"id\":2,\"summary\":\"Do " +
                "not participate\",\"isDefault\":true}]");
        post.param("accounts", "[{\"clientId\":\"519667\",\"clientName\":\"Tom\",\"accountId\":\"120070537\",\"transactionNumber\":10," +
                "\"accountKey\":\"2713CB02D37D5339E2C6CCF3FBC0A620B74D9F8417BE817B\",\"positionId\":\"528298\",\"holding\":1000," +
                "\"selectedElections\":{\"options\":[{\"optionId\":1,\"units\":10,\"oversubscribe\":10,\"percent\":10}],\"minimumPriceId\":2},"+
                "\"savedElections\":{\"options\":[{\"optionId\":1,\"units\":10,\"oversubscribe\":10,\"percent\":10}],\"minimumPriceId\":2},"+
                "\"submittedElections\":{\"options\":[{\"optionId\":1,\"units\":10,\"oversubscribe\":10,\"percent\":10}],\"minimumPriceId\":2}}]");

        int status = mockMvc.perform(post).andReturn().getResponse().getStatus();

        verify(corporateActionPersistenceDtoService, atLeastOnce()).submit(argumentCaptor.capture(), any(ServiceErrors.class));

        CorporateActionPersistenceDto corporateActionPersistenceDto = argumentCaptor.getValue();

        assertNotNull(corporateActionPersistenceDto.getCloseDate());
        assertNotNull(corporateActionPersistenceDto.getOptions());
        assertNotNull(corporateActionPersistenceDto.getAccounts());

        CorporateActionOptionDto optionDto = corporateActionPersistenceDto.getOptions().iterator().next();

        assertEquals(1, optionDto.getId().intValue());
        assertEquals("$22 per share & 1:2 BHP shares", optionDto.getSummary());
        assertNull(optionDto.getIsDefault());
        assertNull(optionDto.getTitle());
        assertNull(optionDto.getIsNoAction());

        CorporateActionAccountDetailsDto accountDetailsDto = corporateActionPersistenceDto.getAccounts().iterator().next();

        assertEquals("528298", accountDetailsDto.getPositionId());
        assertEquals("120070537", accountDetailsDto.getAccountId());
        assertEquals("2713CB02D37D5339E2C6CCF3FBC0A620B74D9F8417BE817B", accountDetailsDto.getAccountKey());
        assertNotNull(accountDetailsDto.getSelectedElections());
        assertEquals(2, accountDetailsDto.getSelectedElections().getMinimumPriceId().intValue());
        assertEquals(1, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getOptionId().intValue());
        assertEquals(BigDecimal.TEN, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getUnits());
        assertEquals(BigDecimal.TEN, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getOversubscribe());
        assertEquals(BigDecimal.TEN, accountDetailsDto.getSelectedElections().getPrimarySelectedOption().getPercent());

        assertNull(accountDetailsDto.getSavedElections());
        assertNull(accountDetailsDto.getSubmittedElections());
        assertNull(accountDetailsDto.getHolding());
        assertNull(accountDetailsDto.getElectionStatus());
        assertNull(accountDetailsDto.getSavedElections());
        assertNull(accountDetailsDto.getCash());
        assertNull(accountDetailsDto.getTransactionDescription());
        assertNull(accountDetailsDto.getTransactionNumber());
        assertNull(accountDetailsDto.getTransactionStatus());
        assertNull(accountDetailsDto.getAccountType());
        assertNull(accountDetailsDto.getAccountName());
        assertNull(accountDetailsDto.getClientId());
        assertNull(accountDetailsDto.getClientName());
        assertNull(accountDetailsDto.getClientEmail());
        assertNull(accountDetailsDto.getClientPhone());
        assertNull(accountDetailsDto.getOriginalHolding());
        assertNull(accountDetailsDto.getPortfolio());
        assertNull(accountDetailsDto.getPortfolioValue());
        assertNull(accountDetailsDto.getNotification());

        assertEquals(200, status);
    }

    @EnableWebMvc
    @ComponentScan(basePackages = "com.bt.nextgen.api.corporateaction.v1.controller",
            excludeFilters = {@Filter(type = FilterType.ASSIGNABLE_TYPE, value = {CorporateActionApiController.class,
                                                                                  CorporateActionApprovalApiController.class,
                                                                                  CorporateActionElectionApiController.class})})
    private static class TestConfiguration {
        @Bean(name = "PropertyPlaceholderConfigurer")
        private PropertyPlaceholderConfigurer getPropertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/corporateaction/v1/UriConfig.properties"));
            return pc;
        }

        @Bean
        private CorporateActionPersistenceDtoService getCorporateActionPersistenceDtoService() {
            return corporateActionPersistenceDtoService;
        }

        @Bean(name = "ClientApplicationDtoDeserializer")
        private ClientApplicationDtoDeserializer getClientApplicationDtoDeserializer() {
            return mock(ClientApplicationDtoDeserializer.class);
        }

        @Bean(name = "jsonObjectMapper")
        private ObjectMapper getObjectMapper() {
            return new JsonObjectMapper();
        }

        @Bean(name = "SecureJsonObjectMapper")
        private ObjectMapper getSecureObjectMapper() {
            return new SecureJsonObjectMapper();
        }

        @Bean(name = "FormDataValidator")
        private FormDataValidator getFormDataValidator() {
            return mock(FormDataValidator.class);
        }
    }
}
