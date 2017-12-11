package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.movemoney.DepositDetails;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class DepositDelegateServiceTest {

    @InjectMocks
    DepositIntegrationServiceImpl depositService;

    @Mock
    private AvaloqExecute avaloqExecute;

    private DepositHolderImpl response;
    private List<DepositDetails> deposits = new ArrayList<>();
    private WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();

    @Before
    public void setUp() {
        identifier.setBpId("123456");
        response = Mockito.mock(DepositHolderImpl.class);
        deposits.add(new DepositDetailsImpl());
        Mockito.when(response.getDeposits()).thenReturn(deposits);
    }

    @Test
    public void testLoadSavedDeposit_whenValidRequest_thenDepositReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(response);

        DepositDetails detail = depositService.loadSavedDeposit("deposit-id", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertNotNull(detail);
    }

    @Test
    public void testLoadSavedDeposit_whenNullResponse_thenEmptyListReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);

        DepositDetails detail = depositService.loadSavedDeposit("deposit-id", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertNotNull(detail);
    }

    @Test
    public void testLoadSavedDeposit_whenNullDepositResponse_thenEmptyListReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(new DepositHolderImpl());

        DepositDetails detail = depositService.loadSavedDeposit("deposit-id", new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertNotNull(detail);
    }

    @Test
    public void testLoadSavedDeposits_whenValidRequest_thenDepositsReturned() throws Exception {
        deposits.add(new DepositDetailsImpl());
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(response);

        List<DepositDetails> details = depositService.loadSavedDeposits(identifier, new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(details.size(), is(2));
    }

    @Test
    public void testLoadSavedDeposits_whenNullResponse_thenEmptyListReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);

        List<DepositDetails> details = depositService.loadSavedDeposits(identifier, new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(details.size(), is(0));
    }

    @Test
    public void testLoadSavedDeposits_whenNullDepositsResponse_thenEmptyListReturned() throws Exception {
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class),
                Mockito.any(ServiceErrors.class))).thenReturn(new DepositHolderImpl());

        List<DepositDetails> details = depositService.loadSavedDeposits(identifier, new ServiceErrorsImpl());
        Mockito.verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        assertThat(details.size(), is(0));
    }
}
