package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.AvailableCashDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.AvailableCashImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class AvailableCashDtoServiceImplTest {
    @InjectMocks
    private AvailableCashDtoServiceImpl availableCashService;

    @Mock
    AccountIntegrationService accountService;

    AvailableCashImpl availableCashModelNull = null;
    AvailableCashImpl availableCashModel;
    AccountKey key;
    ServiceErrors serviceErrors;

    @Before
    public void setup() throws Exception {
        key = new AccountKey(EncodedString.fromPlainText("36846").toString());
        availableCashModel = new AvailableCashImpl();
        availableCashModel.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("1234"));
        availableCashModel.setAvailableCash(BigDecimal.valueOf(10000));
        availableCashModel.setPendingSells(BigDecimal.valueOf(5000));
        availableCashModel.setQueuedBuys(BigDecimal.valueOf(3000));
        availableCashModel.setPendingBuys(BigDecimal.valueOf(1000));
        Mockito.when(
                accountService.loadAvailableCash(Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(availableCashModel);
    }

    @Test
    public void testGetAvailableCash_When_Null() {
        AvailableCashDto availableCashDto = availableCashService.convertToDto(availableCashModelNull);
        assertNull(availableCashDto);
    }

    @Test
    public void testGetAvailableCash_When_Not_Null() {
        AvailableCashDto availableCashDto = availableCashService.convertToDto(availableCashModel);
        assertNotNull(availableCashDto);
    }

    @Test
    public void testGetAvailableCash_ValueMatches() {
        AvailableCashDto availableCashDto = availableCashService.convertToDto(availableCashModel);
        Assert.assertEquals(availableCashModel.getAvailableCash(), availableCashDto.getAvailableCash());
        Assert.assertEquals(availableCashModel.getPendingSells(), availableCashDto.getPendingSells());
        Assert.assertEquals(availableCashModel.getQueuedBuys(), availableCashDto.getQueuedBuys().negate());
        Assert.assertEquals(availableCashModel.getPendingBuys(), availableCashDto.getPendingBuys().negate());
    }

    @Test
    public void testFindSingle_matchesAvailableCashModelWhenPortfolioKeyPassed() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        AvailableCashDto availableCashDto = availableCashService.find(key, serviceErrors);
        Assert.assertNotNull(availableCashDto);
        Assert.assertEquals(availableCashModel.getAvailableCash(), availableCashDto.getAvailableCash());
        Assert.assertEquals(availableCashModel.getPendingSells(), availableCashDto.getPendingSells());
        Assert.assertEquals(availableCashModel.getQueuedBuys(), availableCashDto.getQueuedBuys().negate());
        Assert.assertEquals(availableCashModel.getPendingBuys(), availableCashDto.getPendingBuys().negate());
    }

}
