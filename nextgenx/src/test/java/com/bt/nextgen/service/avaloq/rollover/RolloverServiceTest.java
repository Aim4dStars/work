package com.bt.nextgen.service.avaloq.rollover;

import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.service.AvaloqReportService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.rollover.RolloverHistory;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RolloverServiceTest {

    @InjectMocks
    private AvaloqRolloverServiceImpl rolloverIntegrationService;

    @Mock
    private AvaloqReportService avaloqService;

    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    @Test(expected = IllegalArgumentException.class)
    public void testGetRolloverHistoryNoAccountId() {
        List<RolloverHistory> response = rolloverIntegrationService.getRolloverHistory(null, new FailFastErrorsImpl());
    }

    @Test
    public void testGetRolloverHistory() {

        final RolloverHistoryResponseImpl mockResponse = Mockito.mock(RolloverHistoryResponseImpl.class);
        List<RolloverHistory> rollovers = Collections.emptyList();
        Mockito.when(mockResponse.getRolloverHistory()).thenReturn(rollovers);

        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<RolloverHistoryResponseImpl>() {

            @Override
            public RolloverHistoryResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];
                Assert.assertEquals(RolloverTemplate.ROLLOVER_HISTORY, req.getTemplate());
                Assert.assertEquals("accountId", ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0)
                        .getVal()).getVal());
                return mockResponse;
            }
        });

        List<RolloverHistory> response = rolloverIntegrationService.getRolloverHistory("accountId", new FailFastErrorsImpl());
    }
}
