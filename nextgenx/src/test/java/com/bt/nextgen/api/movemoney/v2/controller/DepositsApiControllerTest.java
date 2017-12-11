package com.bt.nextgen.api.movemoney.v2.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.service.DepositDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class DepositsApiControllerTest {

    @InjectMocks
    private DepositsApiController depositsApiController = new DepositsApiController();

    @Mock
    private DepositDtoService depositDtoService;

    private DepositDto depositDto;

    @Before
    public void setUp() throws Exception {
        depositDto = new DepositDto();
    }

    @Test
    public final void testGetPayeesForAccount() throws Exception {
        List<DepositDto> depositDtos = new ArrayList<>();

        Mockito.when(depositDtoService.search(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(depositDtos);

        ApiResponse apiResponse = depositsApiController.getPayeesForAccount("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA");
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(depositDtoService).search(Mockito.any(AccountKey.class), Mockito.any(ServiceErrorsImpl.class));
    }

    @Test
    public final void testConfirmDeposit() throws Exception {
        Mockito.when(depositDtoService.validate(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(depositDto);

        KeyedApiResponse<AccountKey> apiResponse = depositsApiController
                .confirmDeposit("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", depositDto);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(depositDtoService).validate(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testSubmitDeposit() throws Exception {
        Mockito.when(depositDtoService.submit(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(depositDto);

        KeyedApiResponse<AccountKey> apiResponse = depositsApiController
                .submitDeposit("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", depositDto);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(depositDtoService).submit(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testSaveDeposit() throws Exception {
        Mockito.when(depositDtoService.create(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(depositDto);

        KeyedApiResponse<AccountKey> apiResponse = depositsApiController
                .saveDeposit("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", depositDto);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(depositDtoService).create(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testUpdateDeposit() throws Exception {
        Mockito.when(depositDtoService.update(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(depositDto);

        KeyedApiResponse<AccountKey> apiResponse = depositsApiController
                .updateDeposit("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", depositDto);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(depositDtoService).update(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class));
    }
}
