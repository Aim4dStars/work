package com.bt.nextgen.api.modelpreference.v1.controller;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.api.modelpreference.v1.model.SubaccountPreferencesActionDto;
import com.bt.nextgen.api.modelpreference.v1.service.AccountPreferencesDtoService;
import com.bt.nextgen.api.modelpreference.v1.service.SubaccountPreferencesDtoService;
import com.bt.nextgen.api.modelpreference.v1.service.SubaccountPreferencesSubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;



@RunWith(MockitoJUnitRunner.class)
public class ModelPreferenceApiControllerTest {
    @InjectMocks
    private ModelPreferenceApiController modelPreferenceApiController;

    private MockMvc mockMvc;

    @Mock
    static SubaccountPreferencesSubmitDtoService subaccountPreferencesSubmitDtoService;


    private final int STATUS_SUCCESS = 200;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
    }

    @EnableWebMvc
    @ComponentScan(basePackages = { "com.bt.nextgen.api.modelpreference.v1.controller" })
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/modelpreference/v1/UriConfig.properties"));
            return pc;
        }

        @Bean(name = "AccountPreferencesDtoService")
        AccountPreferencesDtoService accountPreferencesDtoServiceBean() {
            return null;
        }

        @Bean(name = "SubaccountPreferencesSubmitDtoService")
        SubaccountPreferencesSubmitDtoService subaccountPreferencesSubmitDtoServiceBean() {
            return subaccountPreferencesSubmitDtoService;
        }       

        @Bean(name = "SubaccountPreferencesDtoService")
        SubaccountPreferencesDtoService subaccountPreferencesDtoServiceBean() {
            return null;
        }

        @Bean(name = "ModelPortfolioDtoErrorMapper")
        ModelPortfolioDtoErrorMapper modelPortfolioDtoErrorMapperBean() {
            return null;
        }

    }
    
    @Test
    public void testModelAttributeSubmitSubAccountPreference() throws Exception {

        Mockito.when(subaccountPreferencesSubmitDtoService.submit(Mockito.any(SubaccountPreferencesActionDto.class), Mockito.any(ServiceErrors.class)))
.thenAnswer(new Answer<SubaccountPreferencesActionDto>() {
            @Override
            public SubaccountPreferencesActionDto answer(InvocationOnMock invocation) throws Throwable {
                SubaccountPreferencesActionDto dto = (SubaccountPreferencesActionDto) invocation.getArguments()[0];
                return dto;
                
            }
        });
        
        String url = "/secure/api/modelpreference/v1_0/subaccounts/47CE5CDE3F68CE193ABBE1EB0C20C9B14F8EDB7CD4173391/";

        int statusInt = mockMvc
                .perform(
                        post(url)
                                .body(
                                "[{\"issuerId\": \"109916\", \"action\": \"REMV\", \"preference\": \"PRORATA\"}]".getBytes()))
                .andReturn().getResponse().getStatus();
        
        // URL cannot be mapped, status = 404.
        // Any controller error, status = 500.
        Assert.assertEquals(STATUS_SUCCESS, statusInt);

    }

}
