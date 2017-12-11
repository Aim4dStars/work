package com.bt.nextgen.api.superpersonaltaxdeduction.service;

import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

/**
 * Created by L067218 on 17/11/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SavePersonalTaxDeductionDtoServiceImplTest {

    @InjectMocks
    private SavePersonalTaxDeductionDtoServiceImpl savePersonalTaxDeductionDtoService;

    @Mock
    private PersonalTaxDeductionIntegrationService personalTaxDeductionIntegrationService;

    @Mock
    private AccountIntegrationService accountService;

    private ServiceErrors serviceErrors;
    private WrapAccountDetailImpl account;
    private PersonalTaxDeductionNoticeTrxnDto trxnDto;
    private PersonalTaxDeductionNoticeTrxnDto trxnResponseDto;


    @Before
    public void setup() {
        trxnResponseDto = new PersonalTaxDeductionNoticeTrxnDto();
        trxnResponseDto.setTransactionStatus("saved");

        trxnDto = new PersonalTaxDeductionNoticeTrxnDto();
        trxnDto.setKey(new com.bt.nextgen.api.account.v2.model.AccountKey("15E67EC04FBD44111C4EC74731CF06E74D5C8FD5E765FAB3"));
        trxnDto.setDate("2016-07-01");
        trxnDto.setAmount(new BigDecimal(100));

        serviceErrors = new ServiceErrorsImpl();

        account = new WrapAccountDetailImpl();
        account.setAccountNumber("12345645");
        Mockito.when(accountService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
    }

    @Test
    public void testCreateNewDeductionNotice() {
        Mockito.when(personalTaxDeductionIntegrationService.createTaxDeductionNotice(anyString(), any(DateTime.class),
                any(DateTime.class), any(BigDecimal.class))).thenReturn(trxnResponseDto);
        PersonalTaxDeductionNoticeTrxnDto result = savePersonalTaxDeductionDtoService.submit(trxnDto,serviceErrors);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getTransactionStatus(), "saved");
    }

    @Test
    public void testVaryDeductionNotice() {
        trxnDto.setDocId("12345678");
        Mockito.when(personalTaxDeductionIntegrationService.varyTaxDeductionNotice(anyString(), anyString(), any(DateTime.class),
                any(DateTime.class), any(BigDecimal.class))).thenReturn(trxnResponseDto);
        PersonalTaxDeductionNoticeTrxnDto result = savePersonalTaxDeductionDtoService.submit(trxnDto,serviceErrors);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getTransactionStatus(), "saved");
    }
}
