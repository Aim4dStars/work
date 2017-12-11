package com.bt.nextgen.api.movemoney.v2.controller;

import com.bt.nextgen.api.safi.controller.TwoFactorAuthenticationBaseController;
import com.bt.nextgen.api.safi.model.SafiResponseDto;
import com.bt.nextgen.payments.web.model.AccountVerificationStatus;
import com.bt.nextgen.payments.web.model.SafiSMSModel;
import com.bt.nextgen.payments.web.model.TwoFactorAccountVerificationKey;
import com.bt.nextgen.payments.web.model.TwoFactorRuleModel;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.rules.AvaloqRulesIntegrationService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;

/**
 * Created by M041926 on 8/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentApiControllerVerifyAccountTest {

    @EnableWebMvc
    @ComponentScan(basePackages = {"com.bt.nextgen.api.movemoney.v2.controller"}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {BPayBillerApiController.class, DepositsApiController.class, PaymentLimitApiController.class}))
    public static class TestConfig extends PaymentApiControllerTestConfig {

        @Bean
        TwoFactorAuthenticationBaseController twoFactorAuthenticationBaseController() {
            return twoFactorAuthenticationBaseController;
        }

        @Bean
        AvaloqRulesIntegrationService avaloqRulesIntegrationService() {
            return avaloqRulesIntegrationService;
        }

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/movemoney/v2/UriConfig.properties"));
            return pc;
        }
    }

    private static MediaType jsonMediaType = MediaType.valueOf(APPLICATION_JSON_VALUE);

    private MockMvc mockMvc;

    @Mock
    private static TwoFactorAuthenticationBaseController twoFactorAuthenticationBaseController;

    @Mock
    private static AvaloqRulesIntegrationService avaloqRulesIntegrationService;

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        return mapper.writeValueAsBytes(object);
    }

    @Before
    public void setup() throws Exception {
        mockMvc = org.springframework.test.web.server.setup.MockMvcBuilders.annotationConfigSetup(TestConfig.class).build();
        SafiResponseDto responseDto = mock(SafiResponseDto.class);
        when(responseDto.isSuccessFlag()).thenReturn(true);
        when(twoFactorAuthenticationBaseController.safiAuthenticate(anyString(), anyString(), any(HttpSession.class), any(HttpServletRequest.class))).thenReturn(responseDto);
    }

    @Test
    public void testAuthenticacteSmsCode() throws Exception {

        String accountID = "123456789";
        String transactionID = "AAA-BB-123";
        String smsCode = "111111";
        String ruleId = "19831983";
        String bsb = "012345";

        MediaType jsonMediaType = MediaType.valueOf(APPLICATION_JSON_VALUE);

        MockHttpSession session = new MockHttpSession();
        TwoFactorRuleModel model = new TwoFactorRuleModel();
        TwoFactorAccountVerificationKey accountVerificationKey = new TwoFactorAccountVerificationKey(accountID, bsb);
        model.addVerificationStatus(accountVerificationKey, new AccountVerificationStatus(ruleId, false));
        session.setAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER, model);

        SafiSMSModel smsModel = new SafiSMSModel();
        smsModel.setAccountID(accountID);
        smsModel.setSmsCode(smsCode);
        smsModel.setTransactionID(transactionID);
        smsModel.setBsb(bsb);

        byte[] jsonBody = convertObjectToJsonBytes(smsModel);

        System.out.println(new String(jsonBody));

        String url = "/secure/api/movemoney/v2_0/accounts/593FB961291F3F59CD2659526BC5958CE2317D5C7ECC2444/verify";

        mockMvc.perform(MockMvcRequestBuilders.post(url).contentType(jsonMediaType).body(jsonBody).session(session)).andExpect(status().isOk())
                .andExpect(content().mimeType(jsonMediaType)).andExpect(jsonPath("$.success").value(true));

        Mockito.verify(twoFactorAuthenticationBaseController, times(1)).safiAuthenticate(eq(transactionID), eq(smsCode), any(HttpSession.class), any(HttpServletRequest.class));
        Mockito.verify(avaloqRulesIntegrationService, times(1)).updateAvaloqRuleAsync(eq(ruleId), any(Map.class));
        assertTrue("Expect authentication done", model.getAccountStatusMap().get(new TwoFactorAccountVerificationKey(accountID, bsb)).isAuthenticationDone());
    }


    @Test
    public void testDev2SmsCode() throws Exception {

        byte[] jsonBody = "{\"smsCode\":\"111111\",\"accountID\":\"120088067\",\"bsb\":\"012345\",\"transactionID\":\"62a2689b-bd91-4b63-aea7-5cd6dffe12fb\"}".getBytes();

        MockHttpSession session = new MockHttpSession();
        TwoFactorRuleModel model = new TwoFactorRuleModel();
        model.addVerificationStatus(new TwoFactorAccountVerificationKey("120088067", "012345"), new AccountVerificationStatus("19831983", false));
        session.setAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER, model);

        String url = "/secure/api/movemoney/v2_0/accounts/593FB961291F3F59CD2659526BC5958CE2317D5C7ECC2444/verify";

        mockMvc.perform(MockMvcRequestBuilders.post(url).contentType(jsonMediaType).body(jsonBody).session(session)).andExpect(status().isOk())
                .andExpect(content().mimeType(jsonMediaType)).andExpect(jsonPath("$.success").value(true));

        Mockito.verify(twoFactorAuthenticationBaseController, times(1)).safiAuthenticate(eq("62a2689b-bd91-4b63-aea7-5cd6dffe12fb"), eq("111111"), any(HttpSession.class), any(HttpServletRequest.class));
        Mockito.verify(avaloqRulesIntegrationService, times(1)).updateAvaloqRuleAsync(eq("19831983"), any(Map.class));
        assertTrue("Expect authentication done", model.getAccountStatusMap().get(new TwoFactorAccountVerificationKey("120088067", "012345")).isAuthenticationDone());
    }

    @Test
    public void testNotFoundInSession() throws Exception {

        byte[] jsonBody = "{\"smsCode\":\"111111\",\"accountID\":\"120088068\",\"bsb\":\"012345\",\"transactionID\":\"62a2689b-bd91-4b63-aea7-5cd6dffe12fb\"}".getBytes();

        MockHttpSession session = new MockHttpSession();
        TwoFactorRuleModel model = new TwoFactorRuleModel();
        model.addVerificationStatus(new TwoFactorAccountVerificationKey("120088067", "012345"), new AccountVerificationStatus("19831983", false));
        session.setAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER, model);

        String url = "/secure/api/movemoney/v2_0/accounts/593FB961291F3F59CD2659526BC5958CE2317D5C7ECC2444/verify";

        mockMvc.perform(MockMvcRequestBuilders.post(url).contentType(jsonMediaType).body(jsonBody).session(session))
                .andExpect(status().isOk())
                .andExpect(content().mimeType(jsonMediaType))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testSAFIAuthenticateFail() throws Exception {

        SafiResponseDto responseDto = mock(SafiResponseDto.class);
        when(responseDto.isSuccessFlag()).thenReturn(false);
        when(twoFactorAuthenticationBaseController.safiAuthenticate(anyString(), anyString(), any(HttpSession.class), any(HttpServletRequest.class))).thenReturn(responseDto);

        byte[] jsonBody = "{\"smsCode\":\"111111\",\"accountID\":\"120088067\",\"transactionID\":\"62a2689b-bd91-4b63-aea7-5cd6dffe12fb\"}".getBytes();

        MockHttpSession session = new MockHttpSession();
        TwoFactorRuleModel model = new TwoFactorRuleModel();
        model.addVerificationStatus(new TwoFactorAccountVerificationKey("120088067", "012345"), new AccountVerificationStatus("19831983", false));
        session.setAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER, model);

        String url = "/secure/api/movemoney/v2_0/accounts/593FB961291F3F59CD2659526BC5958CE2317D5C7ECC2444/verify";

        mockMvc.perform(MockMvcRequestBuilders.post(url).contentType(jsonMediaType).body(jsonBody).session(session)).andExpect(status().isOk())
                .andExpect(content().mimeType(jsonMediaType)).andExpect(jsonPath("$.success").value(false));

        Mockito.verify(avaloqRulesIntegrationService, never()).updateAvaloqRuleAsync(anyString(), any(Map.class));
    }
}
