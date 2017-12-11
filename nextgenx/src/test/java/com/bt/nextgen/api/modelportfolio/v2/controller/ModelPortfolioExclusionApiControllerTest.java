package com.bt.nextgen.api.modelportfolio.v2.controller;

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

import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioExclusionsDto;
import com.bt.nextgen.api.modelportfolio.v2.service.rebalance.ModelPortfolioExclusionDtoService;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.service.ServiceErrors;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioExclusionApiControllerTest {
    @InjectMocks
    private ModelPortfolioExclusionApiController modelPortfolioExclusionApiController;

    private MockMvc mockMvc;

    @Mock
    static ModelPortfolioExclusionDtoService modelPortfolioExclusionDtoService;


    private final int STATUS_SUCCESS = 200;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
    }

    @EnableWebMvc
    @ComponentScan(basePackages = { "com.bt.nextgen.api.modelportfolio.v2.controller" }, resourcePattern = "**/ModelPortfolioExclusionApiController.class")
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/modelportfolio/v2/UriConfig.properties"));
            return pc;
        }
        
        @Bean(name = "ModelPortfolioExclusionDtoService")
        ModelPortfolioExclusionDtoService modelPortfolioExclusionDtoServiceBean() {
            return modelPortfolioExclusionDtoService;
         }
        
         @Bean(name = "ModelPortfolioDtoErrorMapper")
         ModelPortfolioDtoErrorMapper modelPortfolioDtoErrorMapperBean() {
            return null;
         }

    }

    @Test
    public void testModelAttributeSubmitSubAccountPreference() throws Exception {
        Mockito.when(
                modelPortfolioExclusionDtoService.submit(Mockito.any(ModelPortfolioExclusionsDto.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<ModelPortfolioExclusionsDto>() {
            @Override
            public ModelPortfolioExclusionsDto answer(InvocationOnMock invocation) throws Throwable {
                ModelPortfolioExclusionsDto dto = (ModelPortfolioExclusionsDto) invocation.getArguments()[0];
                return dto;

            }
        });

        String url = "/secure/api/modelportfolios/v2_0/1/rebalances/exclusions";

        int statusInt = mockMvc
                .perform(
                        post(url).body(
"[{\"rebalanceKey\":{\"accountId\": \"109916\",\"modelId\": \"1\"}, \"exclusionReason\": \"excluded\", \"exclusionStatus\": \"USER_EXCLUDED\"}]"
                                        .getBytes()))
                .andReturn().getResponse().getStatus();

        // URL cannot be mapped, status = 404.
        // Any controller error, status = 500.
        Assert.assertEquals(STATUS_SUCCESS, statusInt);
    }

}
