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

import com.bt.nextgen.api.movemoney.v2.model.BpayBillerDto;
import com.bt.nextgen.api.movemoney.v2.service.BPayBillerDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class BPayBillerApiControllerTest {

    @InjectMocks
    private BPayBillerApiController bPayBillerApiController = new BPayBillerApiController();

    @Mock
    private BPayBillerDtoService bpayBillerDtoService;

    private BpayBillerDto bpayBillerDto;

    @Before
    public void setUp() throws Exception {
        bpayBillerDto = new BpayBillerDto();
        bpayBillerDto.setBillerCode("Biller code");
        bpayBillerDto.setBillerName("Biller name");
    }

    @Test
    public final void testGetBillerCodeList() throws Exception {
        List<BpayBillerDto> bpayBillerDtos = new ArrayList<>();
        bpayBillerDtos.add(bpayBillerDto);

        Mockito.when(bpayBillerDtoService.findAll(Mockito.any(ServiceErrors.class))).thenReturn(bpayBillerDtos);

        ApiResponse apiResponse = bPayBillerApiController.getBillerCodeList();
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(bpayBillerDtoService).findAll(Mockito.any(ServiceErrorsImpl.class));
    }

    @Test
    public final void testGetBiller() throws Exception {
        Mockito.when(bpayBillerDtoService.validate(Mockito.any(BpayBillerDto.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(bpayBillerDto);

        ApiResponse apiResponse = bPayBillerApiController.getBiller("biller-code");
        assertThat(apiResponse, is(notNullValue()));

        Mockito.verify(bpayBillerDtoService).validate(Mockito.any(BpayBillerDto.class), Mockito.any(ServiceErrors.class));
    }
}
