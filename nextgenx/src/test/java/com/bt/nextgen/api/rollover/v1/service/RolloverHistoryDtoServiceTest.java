package com.bt.nextgen.api.rollover.v1.service;

import com.bt.nextgen.api.rollover.v1.model.RolloverHistoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.rollover.AvaloqRolloverService;
import com.bt.nextgen.service.avaloq.rollover.RolloverHistoryImpl;
import com.bt.nextgen.service.integration.rollover.RolloverHistory;
import com.bt.nextgen.service.integration.rollover.RolloverOption;
import com.bt.nextgen.service.integration.rollover.RolloverStatus;
import com.bt.nextgen.service.integration.rollover.RolloverType;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
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
public class RolloverHistoryDtoServiceTest {

    @InjectMocks
    private RolloverHistoryDtoServiceImpl rolloverHistoryDtoService;

    @Mock
    private AvaloqRolloverService cashRolloverService;

    @Test
    public void testGetRolloverHistory() {
        RolloverHistoryImpl historyItem1 = new RolloverHistoryImpl();
        historyItem1.setRolloverId("rolloverId");
        historyItem1.setFundName("fundName");
        historyItem1.setFundAbn("fundAbn");
        historyItem1.setFundUsi("fundUsi");
        historyItem1.setFundMemberId("fundMemberId");
        historyItem1.setDateRequested(new DateTime("2016-01-02"));
        historyItem1.setRequestStatus(RolloverStatus.COMPLETE);
        historyItem1.setAmount(BigDecimal.TEN);
        historyItem1.setRolloverOption(RolloverOption.FULL);
        historyItem1.setRolloverType(RolloverType.CASH_ROLLOVER);
        historyItem1.setInitiatedByPanorama(Boolean.TRUE);

        RolloverHistory historyItem2 = Mockito.mock(RolloverHistory.class);
        Mockito.when(historyItem2.getDateRequested()).thenReturn(new DateTime("2016-01-10"));

        RolloverHistory historyItem3 = Mockito.mock(RolloverHistory.class);
        Mockito.when(historyItem3.getDateRequested()).thenReturn(new DateTime("2016-01-12"));

        Mockito.when(cashRolloverService.getRolloverHistory(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(
                Arrays.asList(historyItem1, historyItem2, historyItem3));

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, EncodedString.fromPlainText("accountId")
                .toString(), OperationType.STRING));

        List<RolloverHistoryDto> dtoList = rolloverHistoryDtoService.search(criteria, new FailFastErrorsImpl());

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(3, dtoList.size());

        RolloverHistoryDto historyDto = dtoList.get(0);
        Assert.assertEquals("rolloverId", historyDto.getRolloverId());
        Assert.assertEquals("fundName", historyDto.getFundName());
        Assert.assertEquals("fundAbn", historyDto.getFundAbn());
        Assert.assertEquals("fundUsi", historyDto.getFundUsi());
        Assert.assertEquals("fundMemberId", historyDto.getFundMemberId());
        Assert.assertEquals(new DateTime("2016-01-02"), historyDto.getDateRequested());
        Assert.assertEquals(RolloverStatus.COMPLETE.name(), historyDto.getRequestStatus());
        Assert.assertEquals(BigDecimal.TEN, historyDto.getAmount());
        Assert.assertEquals(RolloverOption.FULL.getShortDisplayName(), historyDto.getRolloverOption());
        Assert.assertEquals(RolloverType.CASH_ROLLOVER.getShortDisplayName(), historyDto.getRolloverType());
        Assert.assertEquals(Boolean.TRUE, historyDto.getInitiatedByPanorama());
    }
}
