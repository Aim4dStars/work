package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.AvailableCashDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.account.AvailableCashImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
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
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

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
        availableCashModel.setTotalPendingSells(BigDecimal.valueOf(8000));
        availableCashModel.setPendingSells(BigDecimal.valueOf(5000));
        availableCashModel.setQueuedBuys(BigDecimal.valueOf(3000));
        availableCashModel.setPendingBuys(BigDecimal.valueOf(1000));
        availableCashModel.setPendingSellsListedSecurities(BigDecimal.valueOf(3000));
        availableCashModel.setQueuedBuysListedSecurities(BigDecimal.valueOf(2000));
        Mockito.when(
                accountService.loadAvailableCash(Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(availableCashModel);

        Mockito.when(accountIntegrationServiceFactory.getInstance(Mockito.any(String.class))).thenReturn(accountService);
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
        Assert.assertEquals(availableCashModel.getTotalPendingSells(), availableCashDto.getTotalPendingSells());
        Assert.assertEquals(availableCashModel.getPendingSells(), availableCashDto.getPendingSells());
        Assert.assertEquals(availableCashModel.getQueuedBuys(), availableCashDto.getQueuedBuys().negate());
        Assert.assertEquals(availableCashModel.getPendingBuys(), availableCashDto.getPendingBuys().negate());
        Assert.assertEquals(availableCashModel.getPendingSellsListedSecurities(),
                availableCashDto.getPendingSellsListedSecurities());
        Assert.assertEquals(availableCashModel.getQueuedBuysListedSecurities(), availableCashDto.getQueuedBuysListedSecurities()
                .negate());
    }

    @Test
    public void testFindSingle_matchesAvailableCashModelWhenPortfolioKeyPassed() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        AvailableCashDto availableCashDto = availableCashService.find(key, serviceErrors);
        Assert.assertNotNull(availableCashDto);
        Assert.assertEquals(availableCashModel.getAvailableCash(), availableCashDto.getAvailableCash());
        Assert.assertEquals(availableCashModel.getTotalPendingSells(), availableCashDto.getTotalPendingSells());
        Assert.assertEquals(availableCashModel.getPendingSells(), availableCashDto.getPendingSells());
        Assert.assertEquals(availableCashModel.getQueuedBuys(), availableCashDto.getQueuedBuys().negate());
        Assert.assertEquals(availableCashModel.getPendingBuys(), availableCashDto.getPendingBuys().negate());
        Assert.assertEquals(availableCashModel.getPendingSellsListedSecurities(),
                availableCashDto.getPendingSellsListedSecurities());
        Assert.assertEquals(availableCashModel.getQueuedBuysListedSecurities(), availableCashDto.getQueuedBuysListedSecurities()
                .negate());
    }

}
