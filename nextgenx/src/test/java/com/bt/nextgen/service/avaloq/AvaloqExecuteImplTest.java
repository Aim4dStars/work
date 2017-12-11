package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.core.tracking.RequestIdTracking;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.search.PersonSearchRequest;
import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by M041926 on 20/06/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AvaloqExecuteImplTest {

    @Mock
    private AvaloqBankingAuthorityService avaloqBankingAuthorityService;

    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    @InjectMocks
    private AvaloqExecuteImpl service;

    @Before
    public void setup() {
        when(webserviceClient.sendSystemRequestToWebService(Matchers.anyObject(), any(AvaloqOperation.class), any(ServiceErrors.class))).thenReturn(new Object());
        when(webserviceClient.sendToWebService(Matchers.anyObject(), any(AvaloqOperation.class))).thenReturn(new Object());
        when(webserviceClient.sendToWebService(Matchers.anyObject(), any(AvaloqOperation.class), any(Class.class), any(ServiceErrors.class))).thenReturn(new Object());
    }

    @Test
    public void executeReportRequest() throws Exception {
        AvaloqReportRequest request = mock(AvaloqReportRequest.class);
        Object resp = service.executeReportRequest(request);
        assertNotNull(resp);
        assertNull(RequestIdTracking.getCurrentRequestId());
    }

    @Test
    public void executeSearchOperationRequest() throws Exception {
        PersonSearchRequest request = mock(PersonSearchRequest.class);
        when(request.getRoleType()).thenReturn("ROLE_INVESTOR");
        Object resp = service.executeSearchOperationRequest(request, Object.class, new ServiceErrorsImpl());
        assertNotNull(resp);
        assertNull(RequestIdTracking.getCurrentRequestId());
    }

    @Test
    public void executeReportRequestToDomain() throws Exception {
        AvaloqReportRequest request = mock(AvaloqReportRequest.class);
        Object resp = service.executeReportRequestToDomain(request, Object.class, new ServiceErrorsImpl());
        assertNotNull(resp);
        assertNull(RequestIdTracking.getCurrentRequestId());
    }

}