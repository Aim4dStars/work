/**
 *
 */
package com.bt.nextgen.api.fees.v1.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.fees.v1.model.AssetMappedAccountTransactionFeesDto;
import com.bt.nextgen.api.fees.v1.model.TransactionFeeDto;
import com.bt.nextgen.api.fees.v1.service.TransactionFeeDtoService;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

/**
 * @deprecated Use V2
 */
@Deprecated
@RunWith(MockitoJUnitRunner.class)
public class TransactionFeeApiControllerTest {

    @EnableWebMvc
    @ComponentScan(basePackages = { "com.bt.nextgen.api.fees.v1.controller" })
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/fees/v1/UriConfig.properties"));
            return pc;
        }

        @Bean(name = "TransactionFeeDtoService")
        TransactionFeeDtoService transactionFeeDtoServiceBean() {
            return transactionFeeDtoService;
        }

    }

    private static final String ACCOUNT_ID = "BC37C01C3500E7410AA9619F20132D870EE663D36F188119";

    private org.springframework.test.web.server.MockMvc mockMvc;

    @InjectMocks
    TransactionFeeApiController transactionFeeApiController;

    @Mock
    static TransactionFeeDtoService transactionFeeDtoService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        TransactionFeeDto transactionFee = new TransactionFeeDto(new BigDecimal("-10"), new BigDecimal("0.0012"), null, null);
        Map<String, TransactionFeeDto> mappedTransactionFees = new HashMap<>();
        mappedTransactionFees.put("Listed security", transactionFee);

        AccountKey accountKey = new AccountKey(ACCOUNT_ID);
        AssetMappedAccountTransactionFeesDto assetMappedAccountTransactionFeesDto = new AssetMappedAccountTransactionFeesDto(
                accountKey, mappedTransactionFees);

        Mockito.when(transactionFeeDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
        .thenReturn(assetMappedAccountTransactionFeesDto);
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.fees.v1.controller.TransactionFeeApiController#getAccountTransactionFees(java.lang.String)}
     */
    @Test
    public final void testGetAccountTransactionFees() throws Exception {
        this.mockMvc
                .perform(get("/secure/api/fees/v1_0/accounts/{account-id}/transaction-fees".replace("{account-id}", ACCOUNT_ID))
                        .accept(MediaType.ALL))
                .andExpect(status().isOk()).andExpect(content().mimeType("application/json"))
                .andExpect(jsonPath("$.data.assetTransactionFees").exists())
                .andExpect(jsonPath("$.id.accountId").value(ACCOUNT_ID));
    }

}
