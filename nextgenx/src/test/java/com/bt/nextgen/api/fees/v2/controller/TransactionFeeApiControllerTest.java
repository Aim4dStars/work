/**
 *
 */
package com.bt.nextgen.api.fees.v2.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.fees.v2.model.AssetMappedAccountTransactionFeesDto;
import com.bt.nextgen.api.fees.v2.model.TransactionFeeDto;
import com.bt.nextgen.api.fees.v2.service.TransactionFeeDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TransactionFeeApiControllerTest {

    private static final String ACCOUNT_ID = "BC37C01C3500E7410AA9619F20132D870EE663D36F188119";

    @InjectMocks
    TransactionFeeApiController transactionFeeApiController;

    @Mock
    private TransactionFeeDtoService transactionFeeDtoService;

    @Before
    public void setup() {

        TransactionFeeDto transactionFee = new TransactionFeeDto(new BigDecimal("-10"), new BigDecimal("0.0012"), null, null);
        Map<String, TransactionFeeDto> mappedTransactionFees = new HashMap<>();
        mappedTransactionFees.put("Listed security", transactionFee);

        AccountKey accountKey = new AccountKey(ACCOUNT_ID);
        AssetMappedAccountTransactionFeesDto assetMappedAccountTransactionFeesDto = new AssetMappedAccountTransactionFeesDto(
                accountKey, mappedTransactionFees);

        Mockito.when(transactionFeeDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assetMappedAccountTransactionFeesDto);
    }

    @Test
    public final void testGetAccountTransactionFees() throws Exception {

        ApiResponse apiResponse = transactionFeeApiController.getAccountTransactionFees(ACCOUNT_ID);

        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(transactionFeeDtoService).find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class));
    }

}
