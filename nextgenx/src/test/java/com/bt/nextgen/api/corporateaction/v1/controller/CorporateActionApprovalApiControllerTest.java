package com.bt.nextgen.api.corporateaction.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.server.request.MockMvcRequestBuilders;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionApprovalDecisionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionApprovalDecisionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionApprovalDtoService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionApprovalListDtoService;
import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.controller.ClientApplicationDtoDeserializer;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.service.ServiceErrors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CorporateActionApprovalApiControllerTest {
    @Mock
    private static CorporateActionApprovalDtoService corporateActionApprovalDtoService;

    @Mock
    private static CorporateActionApprovalListDtoService corporateActionApprovalListDtoService;

    @InjectMocks
    private CorporateActionApprovalApiController corporateActionApprovalApiController;

    @Captor
    private ArgumentCaptor<CorporateActionApprovalDecisionListDto> argumentCaptor;

    @Before
    public void setup() {
        CorporateActionListDto corporateActionListDto = new CorporateActionListDto(Boolean.FALSE, null);
        CorporateActionApprovalDecisionListDto corporateActionApprovalDecisionListDto = new CorporateActionApprovalDecisionListDto();

        when(corporateActionApprovalListDtoService.find(any(CorporateActionListDtoKey.class), any(ServiceErrors.class)))
                .thenReturn(corporateActionListDto);
        when(corporateActionApprovalDtoService.submit(any(CorporateActionApprovalDecisionListDto.class), any(ServiceErrors.class)))
                .thenReturn(corporateActionApprovalDecisionListDto);
    }

    @Test
    public void testGetTrusteeCorporateActions() {
        assertNotNull(corporateActionApprovalApiController.getCorporateActionApprovalList("2017-01-01", "2017-03-01"));
    }

    @Test
    public void testSubmitCorporateActionApprovalDecisions_whenParamsPassed_thenDtoMappedCorrectly() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
        MockHttpServletRequestBuilder post = MockMvcRequestBuilders
                .post("/secure/api/trusteecorporateactions/v1_0/submit");
        post.body(("{\"corporateActionApprovalDecisions\":[{\"id\":0,\"approvalDecision\":\"APPROVED\"}]").getBytes());

        int status = mockMvc.perform(post).andReturn().getResponse().getStatus();

        verify(corporateActionApprovalDtoService, atLeastOnce()).submit(argumentCaptor.capture(), any(ServiceErrors.class));

        CorporateActionApprovalDecisionListDto approvalDecisionListDto = argumentCaptor.getValue();

        assertNotNull(approvalDecisionListDto.getCorporateActionApprovalDecisions());

        CorporateActionApprovalDecisionDto approvalDecisionDto =
                approvalDecisionListDto.getCorporateActionApprovalDecisions().iterator().next();

        assertEquals("0", approvalDecisionDto.getId());
        assertEquals("APPROVED", approvalDecisionDto.getApprovalDecision());
        assertNull(approvalDecisionDto.getHoldingLimit());

        assertEquals(200, status);
    }

    @EnableWebMvc
    @ComponentScan(basePackages = "com.bt.nextgen.api.corporateaction.v1.controller",
            excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {CorporateActionApiController.class,
                                                                                                CorporateActionElectionApiController.class,
                                                                                                CorporateActionPersistenceApiController
                                                                                                        .class})})
    private static class TestConfiguration {
        @Bean(name = "PropertyPlaceholderConfigurer")
        private PropertyPlaceholderConfigurer getPropertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/corporateaction/v1/UriConfig.properties"));
            return pc;
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

        @Bean(name = "corporateActionApprovalDtoService")
        private CorporateActionApprovalDtoService getCorporateActionApprovalDtoService() {
            return corporateActionApprovalDtoService;
        }

        @Bean(name = "corporateActionApprovalListDtoService")
        private CorporateActionApprovalListDtoService getCorporateActionApprovalListDtoService() {
            return corporateActionApprovalListDtoService;
        }
    }
}
