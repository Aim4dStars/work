package com.bt.nextgen.api.account.v2.controller;

import com.bt.nextgen.api.account.v2.service.AccountBalanceDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AccountListApiControllerTest {

    @InjectMocks
    AccountApiController accountApiController;

    @Mock
    private AccountBalanceDtoService accountBalanceDtoService;
    @Mock
    ApiSearchCriteria apiSearchCriteria;
    @Mock
    ServiceErrors serviceErrors;
    @Captor
    private ArgumentCaptor<List<ApiSearchCriteria>> listArgumentCaptor;

    // To ensure search method has been called with correct arguments passed.
    @Test
    public void testSearchMethod() throws Exception {

        ApiResponse apiResponse = accountApiController.getAccountBalances(Arrays.asList("CC70F19A491DCC514CFDA7CDCFB92E5AEF81407A445CFE9F",
                "0C821C970A8CE7F4C4B3FF60215B2847CF093C666DCD0381"));
        assertThat(apiResponse, is(notNullValue()));
        Mockito.verify(accountBalanceDtoService).search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrorsImpl.class));

        Mockito.verify(accountBalanceDtoService).search(listArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));

        List<ApiSearchCriteria> apiSearchCriteriaList1 = listArgumentCaptor.getValue();
        ApiSearchCriteria apiSearchCriteria1 = apiSearchCriteriaList1.get(0);
        assertThat(apiSearchCriteria1.getProperty(), is("CC70F19A491DCC514CFDA7CDCFB92E5AEF81407A445CFE9F"));
        assertThat(apiSearchCriteria1.getValue(), is("CC70F19A491DCC514CFDA7CDCFB92E5AEF81407A445CFE9F"));
        ApiSearchCriteria apiSearchCriteria2 = apiSearchCriteriaList1.get(1);
        assertThat(apiSearchCriteria2.getProperty(), is("0C821C970A8CE7F4C4B3FF60215B2847CF093C666DCD0381"));
        assertThat(apiSearchCriteria2.getValue(), is("0C821C970A8CE7F4C4B3FF60215B2847CF093C666DCD0381"));
        assertThat(apiSearchCriteriaList1.size(), is(2));
    }
}
