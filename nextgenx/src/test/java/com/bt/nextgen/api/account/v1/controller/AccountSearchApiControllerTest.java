/**
 *
 */
package com.bt.nextgen.api.account.v1.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.account.v1.service.AccountSearchJsonDtoService;
import com.bt.nextgen.api.client.model.JsonItemDto;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

/**
 * @author L095519 created on 11.08.2017
 */

@RunWith(MockitoJUnitRunner.class)
public class AccountSearchApiControllerTest {

    @Mock
    private AccountSearchJsonDtoService accountSearchJsonDtoService;

    @InjectMocks
    private AccountSearchApiController accountSearchApiController;

    @Test
    public void testSearchAccount() {
        Mockito.when(accountSearchJsonDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class))).thenReturn(Collections.singletonList(new JsonItemDto("{test:\"test\"}")));
        ApiResponse apiResponse = accountSearchApiController.searchAccount("40");
        assertThat(apiResponse, is(notNullValue()));
        assertThat(apiResponse.getError(), is(nullValue()));
        assertThat(apiResponse.getData(), is(notNullValue()));
        Mockito.verify(accountSearchJsonDtoService).search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrorsImpl.class));
    }
}
