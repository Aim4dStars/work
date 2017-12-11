package com.bt.nextgen.api.movemoney.v3.controller;

import com.bt.nextgen.api.movemoney.v3.model.DepositDto;
import com.bt.nextgen.api.movemoney.v3.model.DepositKey;
import com.bt.nextgen.api.movemoney.v3.model.RecurringDepositKey;
import com.bt.nextgen.api.movemoney.v3.service.DepositDtoService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)

public class DepositsApiControllerTest {

    @InjectMocks
    private final DepositsApiController depositsApiController = new DepositsApiController();

    @Mock
    private DepositDtoService depositDtoService;

    @Mock
    private UserProfileService profileService;

    private DepositDto depositDto;

    @Before
    public void setUp() throws Exception {
        depositDto = new DepositDto();
    }

    @Test
    public final void testGetPayeesForAccount() throws Exception {
        List<DepositDto> depositDtos = new ArrayList<>();

        Mockito.when(depositDtoService.search(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(depositDtos);

        ApiResponse apiResponse = depositsApiController.getAccountDeposits(null,
                "80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA");
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(depositDtoService).search(Mockito.anyList(), Mockito.any(ServiceErrorsImpl.class));
    }

    @Test
    public final void testConfirmDeposit() throws Exception {
        Mockito.when(depositDtoService.validate(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(depositDto);

        KeyedApiResponse<DepositKey> apiResponse = depositsApiController
                .confirmDeposit("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", depositDto);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(depositDtoService).validate(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testSubmitDeposit() throws Exception {
        Mockito.when(depositDtoService.submit(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(depositDto);

        KeyedApiResponse<DepositKey> apiResponse = depositsApiController
                .submitDeposit("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", depositDto);
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(depositDtoService).submit(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testSaveDeposit() throws Exception {
        depositDto.setKey(new DepositKey("depositId"));
        Mockito.when(depositDtoService.create(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(depositDto);

        KeyedApiResponse<DepositKey> apiResponse = depositsApiController
                .saveDeposit("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", depositDto);

        assertThat(apiResponse, is(notNullValue()));
        Mockito.verify(depositDtoService).create(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testUpdateDeposit() throws Exception {
        depositDto.setKey(new DepositKey("depositId"));
        Mockito.when(depositDtoService.update(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(depositDto);

        KeyedApiResponse<DepositKey> apiResponse = depositsApiController
                .updateDeposit("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", depositDto);

        assertThat(apiResponse, is(notNullValue()));
        Mockito.verify(depositDtoService).update(Mockito.any(DepositDto.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testDeleteDeposit_whenOneOff_thenOneOffKeyUsed() throws Exception {
        DepositKey key = new DepositKey("123456");
        Mockito.when(profileService.isEmulating()).thenReturn(false);
        Mockito.doNothing().when(depositDtoService).delete(Mockito.any(DepositKey.class), Mockito.any(ServiceErrors.class));
        ApiResponse apiResponse = depositsApiController.deleteDeposit("oneoff",
                "80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", key.getDepositId());
        Mockito.verify(depositDtoService).delete(Mockito.any(DepositKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testDeleteDeposit_whenRecurring_thenRecuringKeyUsed() throws Exception {
        RecurringDepositKey key = new RecurringDepositKey("456789");
        Mockito.when(profileService.isEmulating()).thenReturn(false);
        Mockito.doNothing().when(depositDtoService).delete(Mockito.any(DepositKey.class), Mockito.any(ServiceErrors.class));
        ApiResponse apiResponse = depositsApiController.deleteDeposit("recurring",
                "80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", key.getDepositId());
        Mockito.verify(depositDtoService).delete(Mockito.any(RecurringDepositKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test
    public final void testDeleteDeposit_whenNoContributionType_thenError() throws Exception {
        Mockito.when(profileService.isEmulating()).thenReturn(false);
        try {
            ApiResponse apiResponse = depositsApiController.deleteDeposit(null,
                    "80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", "key");
        } catch (BadRequestException exception) {
            assert (true);
            return;
        }
        fail("BadRequestException Not Thrown");
    }

    @Test
    public final void testDeleteDeposit_whenEmulating_thenAccessDenied() throws Exception {
        Mockito.when(profileService.isEmulating()).thenReturn(true);
        try {
            ApiResponse apiResponse = depositsApiController.deleteDeposit("oneoff",
                    "80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA", "key");
        } catch (AccessDeniedException exception) {
            assert (true);
            return;
        }
        fail("AccessDeniedException Not Thrown");
    }
}
