package com.bt.nextgen.api.marketdata.v1.controller;

import com.bt.nextgen.api.marketdata.v1.service.MarkitOnDemandDtoServerService;
import com.bt.nextgen.api.marketdata.v1.service.MarkitOnDemandDtoServerServiceImpl;
import com.bt.nextgen.api.marketdata.v1.service.MarkitOnDemandPodCastDtoService;
import com.bt.nextgen.api.marketdata.v1.service.MarkitOnDemandShareDtoService;
import com.bt.nextgen.api.marketdata.v1.service.SsoKeyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.server.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class MarkitOnDemandApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private static SsoKeyService paramEncryptionService;

    @Mock
    private static MarkitOnDemandPodCastDtoService markitOnDemandPodCastDtoService;

    @Mock
    private static MarkitOnDemandShareDtoService markitOnDemandShareDtoService;

    @Before
    public void setUp() {
        mockMvc = org.springframework.test.web.server.setup.MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();
    }

    @Test
    public void testMarketData() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/secure/api/marketdata/v1/mod/communicationkey").accept(MediaType.ALL))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json;charset=UTF-8")).andDo(print());
    }

    @Test
    public void testMarketOnDemandTest() throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.get("/secure/api/marketdata/v1/mod/server").accept(MediaType.ALL))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.data.serverUrl").exists()).andReturn();
    }

    @EnableWebMvc
    @ComponentScan(basePackages = {"com.bt.nextgen.api.marketdata.v1.controller"})
    public static class TestConfiguration {
        @Bean
        public MarkitOnDemandPodCastDtoService markitOnDemandPodCastDtoService() {
            return markitOnDemandPodCastDtoService;
        }
        @Bean
        public MarkitOnDemandShareDtoService markitOnDemandShareDtoService() {
            return markitOnDemandShareDtoService;
        }
        @Bean
        public SsoKeyService ssoKeyService() {
            return paramEncryptionService;
        }
        @Bean
        public MarkitOnDemandDtoServerService markitOnDemandDtoServerService() {
            return new MarkitOnDemandDtoServerServiceImpl();
        }
    }

}