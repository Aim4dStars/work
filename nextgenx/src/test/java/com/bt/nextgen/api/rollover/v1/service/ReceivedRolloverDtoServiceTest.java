package com.bt.nextgen.api.rollover.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.rollover.v1.model.ReceivedRolloverFundDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.rollover.AvaloqRolloverService;
import com.bt.nextgen.service.integration.rollover.RolloverReceived;
import com.bt.nextgen.service.integration.rollover.RolloverType;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ReceivedRolloverDtoServiceTest {

    @InjectMocks
    private ReceivedRolloverDtoServiceImpl rolloverDtoService;

    @Mock
    private AvaloqRolloverService rollService;

    @Test
    public void testGetReceivedRollover() {

        RolloverReceived rollover1 = Mockito.mock(RolloverReceived.class);
        Mockito.when(rollover1.getFundId()).thenReturn("fundId");
        Mockito.when(rollover1.getFundName()).thenReturn("fundName");
        Mockito.when(rollover1.getFundAbn()).thenReturn("fundAbn");
        Mockito.when(rollover1.getFundUsi()).thenReturn("fundUsi");
        Mockito.when(rollover1.getReceivedDate()).thenReturn(new DateTime("2016-01-02"));
        Mockito.when(rollover1.getAmount()).thenReturn(BigDecimal.ONE);
        Mockito.when(rollover1.getRolloverType()).thenReturn(RolloverType.CASH_ROLLOVER);

        Mockito.when(rollService.getReceivedFunds(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                Arrays.asList(rollover1));

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, EncodedString.fromPlainText("accountId")
                .toString(), OperationType.STRING));

        List<ReceivedRolloverFundDto> dtoList = rolloverDtoService.search(criteria, new FailFastErrorsImpl());

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(1, dtoList.size());

        ReceivedRolloverFundDto dto = dtoList.get(0);
        Assert.assertEquals("fundName", dto.getFundName());
        Assert.assertEquals("fundAbn", dto.getFundAbn());
        Assert.assertEquals("fundUsi", dto.getFundUsi());
        Assert.assertEquals("fundId", dto.getFundId());
        Assert.assertEquals(new DateTime("2016-01-02"), dto.getDateReceived());
        Assert.assertEquals(BigDecimal.ONE, dto.getAmount());
        Assert.assertEquals(RolloverType.CASH_ROLLOVER.getShortDisplayName(), dto.getRolloverType());
    }
}
