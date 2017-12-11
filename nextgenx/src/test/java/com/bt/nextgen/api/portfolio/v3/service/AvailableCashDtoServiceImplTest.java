package com.bt.nextgen.api.portfolio.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.portfolio.v3.model.AvailableCashDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AvailableCashImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        availableCashModel.setTotalPendingSells(BigDecimal.valueOf(8000));
        availableCashModel.setPendingSells(BigDecimal.valueOf(5000));
        availableCashModel.setQueuedBuys(BigDecimal.valueOf(3000));
        availableCashModel.setPendingBuys(BigDecimal.valueOf(1000));
        availableCashModel.setPendingSellsListedSecurities(BigDecimal.valueOf(3000));
        availableCashModel.setQueuedBuysListedSecurities(BigDecimal.valueOf(2000));
        Mockito.when(accountService.loadAvailableCash(Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(availableCashModel);
    }

    @Test
    public void testGetAvailableCash_WhenNull_ThenZerValuesReturned() {
        AvailableCashDto availableCashDto = availableCashService.convertToDto(key, availableCashModelNull);
        assertNotNull(availableCashDto);
        assertEquals(BigDecimal.ZERO, availableCashDto.getAvailableCash());
        assertEquals(BigDecimal.ZERO, availableCashDto.getTotalPendingSells());
        assertEquals(BigDecimal.ZERO, availableCashDto.getPendingSells());
        assertEquals(BigDecimal.ZERO, availableCashDto.getQueuedBuys().negate());
        assertEquals(BigDecimal.ZERO, availableCashDto.getPendingBuys().negate());
        assertEquals(BigDecimal.ZERO, availableCashDto.getPendingSellsListedSecurities());
        assertEquals(BigDecimal.ZERO, availableCashDto.getQueuedBuysListedSecurities().negate());

    }

    @Test
    public void testGetAvailableCash_When_Not_Null() {
        AvailableCashDto availableCashDto = availableCashService.convertToDto(key, availableCashModel);
        assertNotNull(availableCashDto);
    }

    @Test
    public void testGetAvailableCash_ValueMatches() {
        AvailableCashDto availableCashDto = availableCashService.convertToDto(key, availableCashModel);
        Assert.assertEquals(availableCashModel.getAvailableCash(), availableCashDto.getAvailableCash());
        Assert.assertEquals(availableCashModel.getTotalPendingSells(), availableCashDto.getTotalPendingSells());
        Assert.assertEquals(availableCashModel.getPendingSells(), availableCashDto.getPendingSells());
        Assert.assertEquals(availableCashModel.getQueuedBuys(), availableCashDto.getQueuedBuys().negate());
        Assert.assertEquals(availableCashModel.getPendingBuys(), availableCashDto.getPendingBuys().negate());
        Assert.assertEquals(availableCashModel.getPendingSellsListedSecurities(),
                availableCashDto.getPendingSellsListedSecurities());
        Assert.assertEquals(availableCashModel.getQueuedBuysListedSecurities(),
                availableCashDto.getQueuedBuysListedSecurities().negate());
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
        Assert.assertEquals(availableCashModel.getQueuedBuysListedSecurities(),
                availableCashDto.getQueuedBuysListedSecurities().negate());
    }

}
