package com.bt.nextgen.api.verifylinkedaccountstatus;


import com.bt.nextgen.api.account.v3.model.LinkedAccountStatusDto;
import com.bt.nextgen.api.chesssponsor.model.ChessSponsorDataDto;
import com.bt.nextgen.api.chesssponsor.model.ChessSponsorDto;
import com.bt.nextgen.api.verifylinkedaccount.controller.VerifyLinkedAccountController;
import com.bt.nextgen.api.verifylinkedaccount.model.LinkedAccountDetailsDto;
import com.bt.nextgen.api.verifylinkedaccount.service.VerifyLinkedDtoService;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.payeedetails.LinkedAccountStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.bind.WebDataBinder;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by l078480 on 22/08/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class VerifyLinkedAccountControllerTest {

    @InjectMocks
    private VerifyLinkedAccountController verifyLinkedAccountController;


    @Mock
    private VerifyLinkedDtoService verifyLinkedDtoService;

    @Mock
    WebDataBinder binder;

    private LinkedAccountDetailsDto linkedAccountDetailsDto;

    @Before
    public void setUp() throws Exception
    {
        linkedAccountDetailsDto =new LinkedAccountDetailsDto();
        LinkedAccountStatusDto linkedAccountStatusDto =new LinkedAccountStatusDto();
        linkedAccountStatusDto.setLinkedAccountStatus(LinkedAccountStatus.EXPIRED);
        linkedAccountStatusDto.setVfyCode(false);
        linkedAccountStatusDto.setGenCode(false);
        linkedAccountDetailsDto.setAccountNumber("123456");
        linkedAccountDetailsDto.setBsb("062032");
        linkedAccountDetailsDto.setKey(new AccountKey("123456"));
        linkedAccountDetailsDto.setLinkedAccountStatus(linkedAccountStatusDto);
        verifyLinkedAccountController.linkedAccountsDtoModelBinder(binder);
    }
    @Test
    public void test_VerifyLinkedController() throws Exception{
       Mockito.when(verifyLinkedDtoService.submit(Mockito.any(LinkedAccountDetailsDto.class),Mockito.any(ServiceErrors.class))).thenReturn(linkedAccountDetailsDto);
        ApiResponse apiResponse = verifyLinkedAccountController.verifyLinkedAccount("80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA",linkedAccountDetailsDto);
        assertThat(apiResponse, is(notNullValue()));
    }



}
