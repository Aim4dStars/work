package com.bt.nextgen.api.supermatch.v1.controller;

import com.bt.nextgen.api.supermatch.v1.model.RolloverDetailsDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDto;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchDtoKey;
import com.bt.nextgen.api.supermatch.v1.model.SuperMatchFundDto;
import com.bt.nextgen.api.supermatch.v1.service.SuperMatchDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.MvcResult;
import org.springframework.test.web.server.request.MockMvcRequestBuilders;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class SuperMatchApiControllerTest {

    @InjectMocks
    private SuperMatchApiController superMatchApiController;

    @Mock
    private static SuperMatchDtoService superMatchDtoService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.annotationConfigSetup(SuperMatchApiControllerTest.TestConfiguration.class).build();
    }

    @EnableWebMvc
    @ComponentScan(basePackages = {"com.bt.nextgen.api.supermatch.v1.controller"}, resourcePattern = "**/SuperMatchApiController.class")
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigureBean() {
            PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
            configurer.setLocation(new ClassPathResource("/com/bt/nextgen/api/supermatch/v1/UriConfig.properties"));
            return configurer;
        }

        @Bean(name = "SuperMatchDtoService")
        SuperMatchDtoService superMatchDtoServiceBean() {
            return superMatchDtoService;
        }
    }

    @Test
    public void getSuperDetailsForAccount() throws Exception {
        SuperMatchDto superMatch = new SuperMatchDto();
        superMatch.setKey(new SuperMatchDtoKey("1222"));
        superMatch.setConsentProvided(true);

        when(superMatchDtoService.find(Mockito.any(SuperMatchDtoKey.class), Mockito.any(ServiceErrors.class))).thenReturn(superMatch);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/secure/api/supermatch/v1_0/account/1222"))
                .andExpect(status().isOk()).andReturn();

        String strResult = result.getResponse().getContentAsString();
        assertNotNull(strResult);

        JsonParser parser = new JsonParser();
        JsonObject jsonResult = parser.parse(strResult).getAsJsonObject();
        JsonObject data = jsonResult.get("data").getAsJsonObject();

        assertEquals(data.get("key").getAsJsonObject().get("accountId").getAsString(), "1222");
        assertEquals(data.get("consentProvided").getAsBoolean(), true);
    }

    @Test
    public void updateSuperMatchDetails_withoutRequestBody() throws Exception {
        SuperMatchDto superMatch = new SuperMatchDto();
        superMatch.setKey(new SuperMatchDtoKey("1222"));
        superMatch.setConsentProvided(true);

        when(superMatchDtoService.update(Mockito.any(SuperMatchDto.class), Mockito.any(ServiceErrors.class))).thenReturn(superMatch);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/secure/api/supermatch/v1_0/account/1222/consent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String strResult = result.getResponse().getContentAsString();
        assertNotNull(strResult);

        JsonParser parser = new JsonParser();
        JsonObject jsonResult = parser.parse(strResult).getAsJsonObject();
        JsonObject data = jsonResult.get("data").getAsJsonObject();

        assertEquals(data.get("key").getAsJsonObject().get("accountId").getAsString(), "1222");
        assertEquals(data.get("consentProvided").getAsBoolean(), true);
    }

    @Test
    public void updateSuperMatchDetails() throws Exception {
        SuperMatchDto superMatch = new SuperMatchDto();
        superMatch.setKey(new SuperMatchDtoKey("1222"));
        superMatch.setConsentProvided(true);

        when(superMatchDtoService.update(Mockito.any(SuperMatchDto.class), Mockito.any(ServiceErrors.class))).thenReturn(superMatch);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/secure/api/supermatch/v1_0/account/1222/consent")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"key\":{\"accountId\":\"1222\"},\"consentProvided\":true}".getBytes()))
                .andExpect(status().isOk()).andReturn();

        String strResult = result.getResponse().getContentAsString();
        assertNotNull(strResult);

        JsonParser parser = new JsonParser();
        JsonObject jsonResult = parser.parse(strResult).getAsJsonObject();
        JsonObject data = jsonResult.get("data").getAsJsonObject();

        assertEquals(data.get("key").getAsJsonObject().get("accountId").getAsString(), "1222");
        assertEquals(data.get("consentProvided").getAsBoolean(), true);
    }

    @Test
    public void updateSuperMatchDetails_Rollover() throws Exception {
        SuperMatchDto superMatch = new SuperMatchDto();
        superMatch.setKey(new SuperMatchDtoKey("1222"));
        superMatch.setConsentProvided(true);
        SuperMatchFundDto fund = new SuperMatchFundDto();
        fund.setAccountNumber("2702938");
        fund.setUsi("11225861252001");

        RolloverDetailsDto rolloverDetail = new RolloverDetailsDto();
        rolloverDetail.setRolloverAmount(BigDecimal.valueOf(10));
        fund.setRolloverDetails(Collections.singletonList(rolloverDetail));
        superMatch.setSuperMatchFundList(Collections.singletonList(fund));

        when(superMatchDtoService.update(Mockito.any(SuperMatchDto.class), Mockito.any(ServiceErrors.class))).thenReturn(superMatch);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/secure/api/supermatch/v1_0/account/1222/rollover")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"superMatchFundList\" : [ {\"accountNumber\":\"2702938\",\"usi\":\"11225861252001\", \"rolloverDetails\": [{\"rolloverAmount\":\"10\"}] }] }".getBytes()))
                .andExpect(status().isOk()).andReturn();

        String strResult = result.getResponse().getContentAsString();
        assertNotNull(strResult);

        JsonParser parser = new JsonParser();
        JsonObject jsonResult = parser.parse(strResult).getAsJsonObject();
        JsonObject data = jsonResult.get("data").getAsJsonObject();

        assertEquals(data.get("key").getAsJsonObject().get("accountId").getAsString(), "1222");
        JsonObject fundObject = data.get("superMatchFundList").getAsJsonArray().get(0).getAsJsonObject();
        assertEquals(fundObject.get("accountNumber").getAsString(), "2702938");
        assertEquals(fundObject.get("usi").getAsString(), "11225861252001");
        assertEquals(fundObject.get("rolloverDetails").getAsJsonArray().get(0).getAsJsonObject().get("rolloverAmount").getAsString(), "10");
    }

    @Test
    public void notifyCustomer() throws Exception {
        when(superMatchDtoService.notifyCustomer(anyString(), anyString(), Mockito.any(ServiceErrors.class))).thenReturn(true);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/secure/api/supermatch/v1_0/account/1222/notify")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).param("email", "ab@cd.com"))
                .andExpect(status().isOk()).andReturn();

        String strResult = result.getResponse().getContentAsString();
        assertNotNull(strResult);

        JsonParser parser = new JsonParser();
        JsonObject jsonResult = parser.parse(strResult).getAsJsonObject();
        assertEquals(jsonResult.get("success").getAsBoolean(), true);
    }
}