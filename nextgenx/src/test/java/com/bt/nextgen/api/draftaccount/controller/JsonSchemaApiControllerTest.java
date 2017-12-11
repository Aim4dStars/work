package com.bt.nextgen.api.draftaccount.controller;

import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.model.JsonSchemaEnumsDto;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.AccountTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ApprovalTypeEnum;
import com.bt.nextgen.api.draftaccount.service.JsonSchemaHelperService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;


/**
 * Created by M040398 on 26/08/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonSchemaApiControllerTest {

    @EnableWebMvc
    @ComponentScan(basePackages = {"com.bt.nextgen.api.draftaccount.controller"}, resourcePattern = "**/JsonSchemaApiController.class")
    public static class WebTestConfig extends WebMvcConfigurerAdapter {

        //reuse the same json object mapper where required
        private ObjectMapper jsonObjectMapper = new JsonObjectMapper();

        @Bean(name = "JsonSchemaHelperService")
        JsonSchemaHelperService jsonSchemaHelperService() {
            return mockSchemaHelperService;
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter <? >> converters) {
            converters.add(jsonMessageConverter());
            super.configureMessageConverters(converters);
        }

        @Bean
        ObjectMapper jsonObjectMapper() {
            return jsonObjectMapper;
        }

        @Bean
        ClientApplicationDtoDeserializer ClientApplicationDtoDeserializer() {
            return new ClientApplicationDtoDeserializer();
        }

        @Bean
        FormDataValidator FormDataValidator() {
            return formDataValidator;
        }

        @Bean
        public HttpMessageConverter <? > jsonMessageConverter() {
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setObjectMapper(jsonObjectMapper);
            return converter;
        }
    }

    private MockMvc mockMvc;

    @InjectMocks
    private JsonSchemaApiController schemaApiController;


    @Mock
    private static FormDataValidator formDataValidator;

    @Mock
    private static JsonSchemaHelperService mockSchemaHelperService;

    @Before
    public void setUp() throws ClassNotFoundException {
        JsonSchemaEnumsDto dto = new JsonSchemaEnumsDto();
        dto.addEnumValues("AccountTypeEnum", AccountTypeEnum.values());
        dto.addEnumValues("ApprovalTypeEnum", ApprovalTypeEnum.values());
        when(mockSchemaHelperService.getJsonSchemaEnums()).thenReturn(dto);
        mockMvc = MockMvcBuilders.annotationConfigSetup(WebTestConfig.class).build();
    }

    @Test
    public void test4AdvisedApi() throws Exception {
        mockMvc.perform(get("/secure/api/v1_0/ob_schema/enums"))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json")).

                //check AccountTypeEnum json
                andExpect(jsonPath("$.data.AccountTypeEnum").exists()).
                andExpect(jsonPath("$.data.AccountTypeEnum.INDIVIDUAL").value("individual")).
                andExpect(jsonPath("$.data.AccountTypeEnum.JOINT").value("joint")).
                andExpect(jsonPath("$.data.AccountTypeEnum.COMPANY").value("company")).
                andExpect(jsonPath("$.data.AccountTypeEnum.INDIVIDUAL_TRUST").value("individualTrust")).
                andExpect(jsonPath("$.data.AccountTypeEnum.CORPORATE_TRUST").value("corporateTrust")).
                andExpect(jsonPath("$.data.AccountTypeEnum.NEW_INDIVIDUAL_SMSF").value("newIndividualSMSF")).
                andExpect(jsonPath("$.data.AccountTypeEnum.NEW_CORPORATE_SMSF").value("newCorporateSMSF")).
                andExpect(jsonPath("$.data.AccountTypeEnum.INDIVIDUAL_SMSF").value("individualSMSF")).
                andExpect(jsonPath("$.data.AccountTypeEnum.CORPORATE_SMSF").value("corporateSMSF")).
                andExpect(jsonPath("$.data.AccountTypeEnum.SUPER_ACCUMULATION").value("superAccumulation")).
                andExpect(jsonPath("$.data.AccountTypeEnum.SUPER_PENSION").value("superPension")).

                //check ApprovalTypeEnum json
                andExpect(jsonPath("$.data.ApprovalTypeEnum").exists()).
                andExpect(jsonPath("$.data.ApprovalTypeEnum.ONLINE").value("online")).
                andExpect(jsonPath("$.data.ApprovalTypeEnum.OFFLINE").value("offline"));

        verify(mockSchemaHelperService, times(1)).getJsonSchemaEnums();
        verifyNoMoreInteractions(mockSchemaHelperService);
    }

    @Test
    public void test4DirectApi() throws Exception {
        mockMvc.perform(get("/onboard/api/v1_0/ob_schema/enums"))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json")).

                //check AccountTypeEnum json
                andExpect(jsonPath("$.data.AccountTypeEnum").exists()).
                andExpect(jsonPath("$.data.AccountTypeEnum.INDIVIDUAL").value("individual")).
                andExpect(jsonPath("$.data.AccountTypeEnum.JOINT").value("joint")).
                andExpect(jsonPath("$.data.AccountTypeEnum.COMPANY").value("company")).
                andExpect(jsonPath("$.data.AccountTypeEnum.INDIVIDUAL_TRUST").value("individualTrust")).
                andExpect(jsonPath("$.data.AccountTypeEnum.CORPORATE_TRUST").value("corporateTrust")).
                andExpect(jsonPath("$.data.AccountTypeEnum.NEW_INDIVIDUAL_SMSF").value("newIndividualSMSF")).
                andExpect(jsonPath("$.data.AccountTypeEnum.NEW_CORPORATE_SMSF").value("newCorporateSMSF")).
                andExpect(jsonPath("$.data.AccountTypeEnum.INDIVIDUAL_SMSF").value("individualSMSF")).
                andExpect(jsonPath("$.data.AccountTypeEnum.CORPORATE_SMSF").value("corporateSMSF")).
                andExpect(jsonPath("$.data.AccountTypeEnum.SUPER_ACCUMULATION").value("superAccumulation")).
                andExpect(jsonPath("$.data.AccountTypeEnum.SUPER_PENSION").value("superPension")).

                //check ApprovalTypeEnum json
                andExpect(jsonPath("$.data.ApprovalTypeEnum").exists()).
                andExpect(jsonPath("$.data.ApprovalTypeEnum.ONLINE").value("online")).
                andExpect(jsonPath("$.data.ApprovalTypeEnum.OFFLINE").value("offline"));

        verify(mockSchemaHelperService, times(1)).getJsonSchemaEnums();
        verifyNoMoreInteractions(mockSchemaHelperService);
    }


}
