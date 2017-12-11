package com.bt.nextgen.api.client.controller;

import com.bt.nextgen.api.client.model.JsonItemDto;
import com.bt.nextgen.api.client.service.ClientQuickSearchDtoService;
import com.bt.nextgen.api.client.v3.controller.ClientQuickSearchApiController;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientQuickSearchApiControllerTest {

    @Mock
    private ClientQuickSearchDtoService clientQuickSearchDtoService;

    @InjectMocks
    private ClientQuickSearchApiController clientQuickSearchApiController;

    @Test
    public void getClients() throws Exception {
        when(clientQuickSearchDtoService.search(anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class))).thenReturn(Collections.singletonList(new JsonItemDto("{test:\"test\"}")));
        ApiResponse response = clientQuickSearchApiController.getClients("12009");
        assertThat(response, is(notNullValue()));
        assertThat(response.getError(), is(nullValue()));
        verify(clientQuickSearchDtoService).search(anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class));
    }
}