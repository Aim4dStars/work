package com.bt.nextgen.api.investmentfinder.v1.controller;

import com.bt.nextgen.api.investmentfinder.v1.model.InvestmentFinderAssetDto;
import com.bt.nextgen.api.investmentfinder.v1.service.InvestmentFinderDtoService;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class InvestmentFinderApiControllerTest {

    @EnableWebMvc
    @ComponentScan(basePackages = { "com.bt.nextgen.api.investmentfinder.v1.controller" })
    public static class TestConfiguration {

        @Bean
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/investmentfinder/v1/UriConfig.properties"));
            return pc;
        }

        @Bean
        InvestmentFinderDtoService investmentFinderDtoService() {
            return investmentFinderDtoService;
        }

    }

    private org.springframework.test.web.server.MockMvc mockMvc;

    @InjectMocks
    InvestmentFinderApiController investmentFinderApiController;

    @Mock
    static InvestmentFinderDtoService investmentFinderDtoService;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        InvestmentFinderAssetDto investmentFinderAssetDto = new InvestmentFinderAssetDto();
        investmentFinderAssetDto.setAssetCode("Code-Red");
        Mockito.when(investmentFinderDtoService.search(Mockito.any(List.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(investmentFinderAssetDto));
    }

    @Test
    public final void testSearchByQueryName() throws Exception {
        this.mockMvc.perform(get("/secure/api/investmentfinder/v1_0/findEtfOrderByName").accept(MediaType.ALL))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data.resultList").exists())
                .andExpect(jsonPath("$.data.resultList[0].assetCode").value("Code-Red"));
    }
}
