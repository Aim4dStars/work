package com.bt.nextgen.api.account.v3.controller;

import com.bt.nextgen.api.account.v3.model.IncomePreferenceDto;
import com.bt.nextgen.api.account.v3.service.IncomePreferenceDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.IncomePreference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class IncomePreferenceApiControllerTest {

    @InjectMocks
    private IncomePreferenceApiController incomePrefApiController;

    @Mock
    private IncomePreferenceDtoService incomePrefDtoService;


    @Test
    public void test_whenEmulating_thenSubmitNotAllowed() throws IOException {
        IncomePreferenceDto dto = Mockito.mock(IncomePreferenceDto.class);
        Mockito.when(dto.getIncomePreference()).thenReturn(IncomePreference.REINVEST);
        
        Mockito.when(incomePrefDtoService.update(Mockito.any(IncomePreferenceDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(dto);
        ApiResponse response = incomePrefApiController.updateIncomePreference("accountId", "subAccountId",
                IncomePreference.REINVEST.name());

        Assert.assertNotNull(response);
    }

}
