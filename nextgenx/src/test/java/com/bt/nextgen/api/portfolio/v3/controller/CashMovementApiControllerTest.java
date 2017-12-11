package com.bt.nextgen.api.portfolio.v3.controller;

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.CashMovementsDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.cashmovements.CashMovementsDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
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

@RunWith(MockitoJUnitRunner.class)
public class CashMovementApiControllerTest {

    @InjectMocks
    private CashMovementApiController apiController;

    @Mock
    private CashMovementsDtoService dtoService;

    private CashMovementsDto dto;

    @Before
    public void setUp() throws Exception {
        dto = Mockito.mock(CashMovementsDto.class);
    }

    @Test
    public final void getCashMovements() throws Exception {
        Mockito.when(dtoService.find(Mockito.any(DatedValuationKey.class), Mockito.any(ServiceErrors.class))).thenReturn(dto);

        ApiResponse apiResponse = apiController.getCashMovements("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", null);

        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(dtoService).find(Mockito.any(DatedValuationKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void getCashMovements_effectiveDate() throws Exception {
        Mockito.when(dtoService.find(Mockito.any(DatedValuationKey.class), Mockito.any(ServiceErrors.class))).thenReturn(dto);

        ApiResponse apiResponse = apiController.getCashMovements("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", "2010-10-26");

        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(dtoService).find(Mockito.any(DatedValuationKey.class), Mockito.any(ServiceErrors.class));
    }
}
