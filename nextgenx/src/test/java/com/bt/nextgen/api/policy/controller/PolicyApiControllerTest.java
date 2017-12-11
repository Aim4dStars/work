package com.bt.nextgen.api.policy.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.broker.model.BrokerKey;
import com.bt.nextgen.api.policy.model.*;
import com.bt.nextgen.api.policy.service.*;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class PolicyApiControllerTest {

    @EnableWebMvc
    @ComponentScan(basePackages = { "com.bt.nextgen.api.policy.controller" })
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/policy/v1/UriConfig.properties"));
            return pc;
        }

        @Bean(name = "PolicyDtoService")
        PolicyDtoService policyDtoService() {
            return policyDtoService;
        }

        @Bean(name = "PolicySummaryDtoService")
        PolicySummaryDtoService policySummaryDtoService() {
          return policySummaryDtoService;
        }

        @Bean(name = "PolicyAccountsDtoService")
        PolicyAccountsDtoService policyAccountsDtoService() {
            return policyAccountsDtoService;
        }

        @Bean(name = "PolicyDocumentDtoService")
        PolicyDocumentDtoService policyDocumentDtoService() {
            return policyDocumentDtoService;
        }

        @Bean(name = "PolicyTrackingDtoService")
        PolicyTrackingDtoService policyTrackingDtoService() {
            return policyTrackingDtoService;
        }
    }

    private org.springframework.test.web.server.MockMvc mockMvc;

    @InjectMocks
    PolicyApiController policyApiController;

    @Mock
    static PolicyDtoService policyDtoService;

    @Mock
    static PolicySummaryDtoService policySummaryDtoService;

    @Mock
    static PolicyAccountsDtoService policyAccountsDtoService;

    @Mock
    static PolicyDocumentDtoService policyDocumentDtoService;

    @Mock
    static PolicyTrackingDtoService policyTrackingDtoService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        PolicyDto policyDto = new PolicyDto();
        policyDto.setAccountNumber("123");
        policyDto.setPolicyNumber("456");
        when(policyDtoService.find(any(PolicyKey.class), any(ServiceErrors.class))).thenReturn(policyDto);

        List<AccountPolicyDto> accountPolicyDtoList = new ArrayList<>();
        AccountPolicyDto accountPolicyDto = new AccountPolicyDto();
        accountPolicyDto.setAccountNumber("123");
        accountPolicyDtoList.add(accountPolicyDto);
        when(policyAccountsDtoService.search(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(accountPolicyDtoList);

        List<PolicyTrackingDto> policyTrackingDtoList = new ArrayList<>();
        PolicyTrackingDto policyTrackingDto = new PolicyTrackingDto();
        policyTrackingDto.setKey(new BrokerKey("123"));
        String [] fNumbers = { "123", "456", "789" };
        policyTrackingDtoList.add(policyTrackingDto);
        policyTrackingDto.setFNumberList(Arrays.asList(fNumbers));

        when(policySummaryDtoService.search(any(List.class), any(ServiceErrors.class))).thenReturn(policyTrackingDtoList);
        when(policySummaryDtoService.find(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(policyTrackingDto);
        when(policySummaryDtoService.findOne(any(ServiceErrors.class))).thenReturn(policyTrackingDto);

        List<PolicyDocumentDto> policyDocumentDtoList = new ArrayList<>();
        PolicyDocumentDto policyDocumentDto = new PolicyDocumentDto();
        policyDocumentDto.setDocumentId("123");
        policyDocumentDtoList.add(policyDocumentDto);
        when(policyDocumentDtoService.search(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(policyDocumentDtoList);

        List<PolicyTrackingIdentifier> policyTrackingIdentifierList = new ArrayList<>();
        PolicyTrackingIdentifier policyTrackingIdentifier = new PolicyTrackingIdentifier();
        policyTrackingIdentifier.setKey(new CustomerKey("123"));
        policyTrackingIdentifierList.add(policyTrackingIdentifier);
        when(policyTrackingDtoService.search(any(List.class), any(ServiceErrors.class))).thenReturn(policyTrackingIdentifierList);
        when(policyTrackingDtoService.search(any(CustomerKey.class), any(List.class), any(ServiceErrors.class))).thenReturn(policyTrackingIdentifierList);
    }

    @Test
    public final void testGetPoliciesForAccount() throws Exception {
        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts").accept(MediaType.ALL))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts/").accept(MediaType.ALL))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts/accountid123").accept(MediaType.ALL))
                .andExpect(status().isOk());
    }

    @Test
    public final void testGetPolicyDetails() throws Exception {
        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts/accountid123/policies").accept(MediaType.ALL))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts/accountid123/policies/").accept(MediaType.ALL))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts//policies").accept(MediaType.ALL))
                .andExpect(status().isOk());

        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts//policies/").accept(MediaType.ALL))
                .andExpect(status().isOk());

        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts/accountid123/policies/").accept(MediaType.ALL))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts/accountid123/policies").accept(MediaType.ALL))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts/accountid123/policies/policyid123").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.policyNumber").value("456"));
    }

    @Test
    public final void testGetAccountsForPolicy() throws Exception {
        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts/accountid123/relatedaccounts").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.resultList[0].accountNumber").value("123"));
    }

    @Test
    public final void testGetPoliciesForFNumber() throws Exception {
        this.mockMvc
                .perform(get("/secure/api/policy/v1_0/policies").param("fnumber", "123").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.resultList[0].key.brokerId").value("123"));

        this.mockMvc
                .perform(get("/secure/api/policy/v1_0/policies").param("customer", "456").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.resultList[0].key.brokerId").value("123"));

        this.mockMvc
                .perform(get("/secure/api/policy/v1_0/policies").param("brokerid", "789").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.resultList[0].key.brokerId").value("123"));
    }

    @Test
    public final void testGetFNumbersForIntermediary() throws Exception {
        this.mockMvc
                .perform(get("/secure/api/policy/v1_0/fnumbers").param("brokerid", "789").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.key.brokerId").value("123"));

        this.mockMvc
                .perform(get("/secure/api/policy/v1_0/fnumbers").param("brokerid", "").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.key.brokerId").value("123"));

        this.mockMvc
                .perform(get("/secure/api/policy/v1_0/fnumbers").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.key.brokerId").value("123"));
    }

    @Test
    public final void testGetInsuranceDocuments() throws Exception {
        this.mockMvc.perform(
                get("/secure/api/policy/v1_0/accounts/accountid123/documents").accept(MediaType.ALL))
                .andExpect(status().isOk());
    }

    @Test
    public final void testGetPolicies() throws Exception {
        this.mockMvc
                .perform(get("/secure/api/policy/v1_0/tracking").param("fnumber", "123").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists());

        this.mockMvc
                .perform(get("/secure/api/policy/v1_0/tracking")
                        .param("fnumber", "123")
                        .param("brokerid", "456")
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public final void testGetUnderwritingNotes() throws Exception {
        this.mockMvc
                .perform(get("/secure/api/policy/v1_0/applications/123/underwritingnotes")
                        .param("fnumber", "123")
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists());

        this.mockMvc
                .perform(get("/secure/api/policy/v1_0/applications/123/underwritingnotes")
                        .param("fnumber", "123")
                        .param("brokerid", "456")
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data").exists());
    }
}
