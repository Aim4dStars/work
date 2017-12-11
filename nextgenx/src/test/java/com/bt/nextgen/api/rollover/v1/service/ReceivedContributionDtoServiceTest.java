package com.bt.nextgen.api.rollover.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.rollover.v1.model.ReceivedContributionDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.rollover.AvaloqRolloverService;
import com.bt.nextgen.service.integration.rollover.ContributionReceived;
import com.bt.nextgen.service.integration.rollover.RolloverContributionStatus;
import com.bt.nextgen.web.controller.cash.util.Attribute;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ReceivedContributionDtoServiceTest {

    @InjectMocks
    private ReceivedContributionDtoServiceImpl contributionDtoService;

    @Mock
    private AvaloqRolloverService rollService;

    private List<ContributionReceived> contList;

    @Before
    public void setup() {
        ContributionReceived cont = Mockito.mock(ContributionReceived.class);
        Mockito.when(cont.getContributionId()).thenReturn("contId");
        Mockito.when(cont.getDescription()).thenReturn("description");
        Mockito.when(cont.getPaymentDate()).thenReturn(new DateTime("2016-01-02"));
        Mockito.when(cont.getAmount()).thenReturn(BigDecimal.ONE);
        Mockito.when(cont.getContributionStatus()).thenReturn(RolloverContributionStatus.IN_PROGRESS);

        ContributionReceived cont2 = Mockito.mock(ContributionReceived.class);
        Mockito.when(cont2.getContributionId()).thenReturn("contId2");
        Mockito.when(cont2.getDescription()).thenReturn("description2");
        Mockito.when(cont2.getPaymentDate()).thenReturn(new DateTime("2016-02-02"));
        Mockito.when(cont2.getAmount()).thenReturn(BigDecimal.TEN);
        Mockito.when(cont2.getContributionStatus()).thenReturn(RolloverContributionStatus.RECEIVED);

        contList = Arrays.asList(cont, cont2);
        Mockito.when(rollService.getContributionReceived(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(contList);
    }

    @Test
    public void testGetReceivedRollover_whenEmptyResult() {
        List<ContributionReceived> result = Collections.emptyList();
        Mockito.when(rollService.getContributionReceived(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(result);

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, EncodedString.fromPlainText("accountId")
                .toString(), OperationType.STRING));

        List<ReceivedContributionDto> dtoList = contributionDtoService.search(criteria, new FailFastErrorsImpl());
        Assert.assertNotNull(dtoList);
        Assert.assertEquals(0, dtoList.size());
    }

    @Test
    public void testGetReceivedRollover() {

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, EncodedString.fromPlainText("accountId")
                .toString(), OperationType.STRING));

        List<ReceivedContributionDto> dtoList = contributionDtoService.search(criteria, new FailFastErrorsImpl());

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(2, dtoList.size());

        ReceivedContributionDto dto = dtoList.get(0);
        Assert.assertEquals("contId", dto.getContributionId());
        Assert.assertEquals("description", dto.getDescription());
        Assert.assertEquals(new DateTime("2016-01-02"), dto.getPaymentDate());
        Assert.assertEquals(BigDecimal.ONE, dto.getAmount());
        Assert.assertEquals(RolloverContributionStatus.IN_PROGRESS.getDisplayName(), dto.getContributionStatus());

        dto = dtoList.get(1);
        Assert.assertEquals(RolloverContributionStatus.RECEIVED.getDisplayName(), dto.getContributionStatus());
    }
}
