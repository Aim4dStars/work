package com.bt.nextgen.api.pension.controller;


import com.bt.nextgen.api.pension.model.PensionTrxnDto;
import com.bt.nextgen.api.pension.service.PensionCommencementDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test Case for PensionCommencementApiController
 * Created by L067218 on 16/09/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PensionCommencementApiControllerTest {

    @InjectMocks
    PensionCommencementApiController pensionCommencementApiController;

    @Mock
    PensionCommencementDtoService pensionCommencementDtoService;

    private final String ACCOUNT_ID = "676AA77A418C5BC1AB5E2DEBC7E023DA15A6C416331D7421";

    private static PensionTrxnDto pensionTrxnDtoResponse;

    @BeforeClass
    public static void init() {
        pensionTrxnDtoResponse= new PensionTrxnDto();
        pensionTrxnDtoResponse.setTransactionStatus("saved");
    }

    @Test(expected = IllegalArgumentException.class)
    public void commencePensionWithEmptyAccountId() {
        pensionCommencementApiController.commencePension(null);
    }

    @Test
    public void commencePension() {
        when(pensionCommencementDtoService.submit(Mockito.any(PensionTrxnDto.class), any(ServiceErrorsImpl.class))).thenReturn(pensionTrxnDtoResponse);

        ApiResponse apiResponse = pensionCommencementApiController.commencePension(ACCOUNT_ID);
        verify(pensionCommencementDtoService, times(1)).submit(Mockito.any(PensionTrxnDto.class), any(ServiceErrors.class));
        assertThat(apiResponse, is(notNullValue()));

        PensionTrxnDto resultDtoObject = (PensionTrxnDto) apiResponse.getData();
        assertThat("TransactionStatus", resultDtoObject.getTransactionStatus(), is("saved"));
    }
}
