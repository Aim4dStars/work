package com.bt.nextgen.api.fees.v2.controller;

import com.bt.nextgen.api.fees.controller.RCTInvoicesApiController;
import com.bt.nextgen.api.fees.model.RCTInvoicesDto;
import com.bt.nextgen.api.fees.service.RCTInvoicesDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RCTInvoicesApiControllerTest {

    @InjectMocks
    private RCTInvoicesApiController apiController;

    @Mock
    private RCTInvoicesDtoService dtoService;

    private List<RCTInvoicesDto> dto;

    @Before
    public void setUp() throws Exception {
    	dto = new ArrayList<>();
    }

    @Test
    public final void getCashMovements() throws Exception {
        Mockito.when(dtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class))).thenReturn(dto);

        ApiResponse apiResponse = apiController.getRecipientCreatedTaxInvoices("2017-01-01", "2017-02-01");

        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(dtoService).search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class));
    }
}
