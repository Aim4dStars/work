package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.RebalanceAction;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalance;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceAccount;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceExclusion;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderGroup;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.abs.trxservice.rebal.v1_0.RebalReq;
import com.btfin.abs.trxservice.rebaldet.v1_0.RebalDetReq;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioRebalanceIntegrationServiceTest {
    @InjectMocks
    private ModelPortfolioRebalanceIntegrationServiceImpl integrationServiceImpl;

    @Mock
    private AvaloqReportService avaloqService;

    @Mock
    private ModelRebalanceConverter rebalConverter;

    @Mock
    private ModelPortfolioExclusionConverter exclusionConverter;

    @Mock
    private ModelPortfolioSubmitConverter submitConverter;

    @Mock
    AvaloqGatewayHelperService avaloqTransactionService;

    private ServiceErrors serviceErrors;

    @Before
    public void setUp() {
        serviceErrors = new ServiceErrorsImpl();
    }

    @Test
    public void whenModelRebalanceLoadRequested_thenCorrectParametersArePassedToTheService() throws Exception {
        final ModelPortfolioRebalanceResponseImpl mockResponse = Mockito.mock(ModelPortfolioRebalanceResponseImpl.class);
        List<ModelPortfolioRebalance> rebalances = Collections.emptyList();
        Mockito.when(mockResponse.getModelPortfolioRebalances()).thenReturn(rebalances);

        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<ModelPortfolioRebalanceResponseImpl>() {

                    @Override
                    public ModelPortfolioRebalanceResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                        AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];
                        Assert.assertEquals(ModelPortfolioRebalanceTemplate.REBALANCE_SUMMARY, req.getTemplate());
                        Assert.assertEquals("key",
                                ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0).getVal()).getVal());

                        return mockResponse;
                    }
                });
        List<ModelPortfolioRebalance> response = integrationServiceImpl.loadModelPortfolioRebalances(BrokerKey.valueOf("key"),
                serviceErrors);
        Assert.assertEquals(rebalances, response);
    }

    @Test
    public void whenModelRebalanceUpdateRequested_thenCorrectParametersArePassedToTheService() throws Exception {
        final ModelRebalanceUpdateResponseImpl response = new ModelRebalanceUpdateResponseImpl();
        serviceErrors = new ServiceErrorsImpl();
        final RebalReq request = new RebalReq();
        when(avaloqTransactionService.sendToWebService(Mockito.any(RebalReq.class), Mockito.any(AvaloqOperation.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class)))
                        .thenAnswer(new Answer<ModelRebalanceUpdateResponseImpl>() {

                            @Override
                            public ModelRebalanceUpdateResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                                RebalReq req = (RebalReq) invocation.getArguments()[0];

                                Assert.assertEquals(request, req);
                                return response;
                            }
                        });
        Mockito.when(rebalConverter.toSubmitRequest(Mockito.any(IpsKey.class), Mockito.any(RebalanceAction.class)))
                .thenReturn(request);

        final ModelPortfolioRebalanceResponseImpl mockResponse = Mockito.mock(ModelPortfolioRebalanceResponseImpl.class);
        List<ModelPortfolioRebalance> rebalances = Collections.emptyList();
        when(mockResponse.getModelPortfolioRebalances()).thenReturn(rebalances);

        serviceErrors = new ServiceErrorsImpl();
        when(avaloqService.executeReportRequestToDomain(any(AvaloqRequest.class), any(Class.class), any(ServiceErrors.class)))
                .thenAnswer(new Answer<ModelPortfolioRebalanceResponseImpl>() {
                    @Override
                    public ModelPortfolioRebalanceResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                        AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];
                        Assert.assertEquals(ModelPortfolioRebalanceTemplate.REBALANCE_SUMMARY, req.getTemplate());
                        Assert.assertEquals("broker",
                                ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0).getVal()).getVal());

                        return mockResponse;
                    }
                });
        integrationServiceImpl.updateModelPortfolioRebalance(BrokerKey.valueOf("broker"), IpsKey.valueOf("key"),
                RebalanceAction.RECALCULATE, serviceErrors);
    }

    @Test
    public void whenSubmitRequested_thenCorrectParametersArePassedToTheService() throws Exception {
        final ModelPortfolioSubmitResponseImpl response = new ModelPortfolioSubmitResponseImpl();
        serviceErrors = new ServiceErrorsImpl();
        final RebalDetReq request = new RebalDetReq();
        when(avaloqTransactionService.sendToWebService(Mockito.any(RebalReq.class), Mockito.any(AvaloqOperation.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class)))
                        .thenAnswer(new Answer<ModelPortfolioSubmitResponseImpl>() {

                            @Override
                            public ModelPortfolioSubmitResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                                RebalDetReq req = (RebalDetReq) invocation.getArguments()[0];

                                Assert.assertEquals(request, req);
                                return response;
                            }
                        });
        Mockito.when(submitConverter.toSubmitRequest(Mockito.any(List.class))).thenReturn(request);

        final ModelPortfolioRebalanceResponseImpl mockResponse = Mockito.mock(ModelPortfolioRebalanceResponseImpl.class);
        List<ModelPortfolioRebalance> rebalances = Collections.emptyList();
        when(mockResponse.getModelPortfolioRebalances()).thenReturn(rebalances);

        serviceErrors = new ServiceErrorsImpl();
        when(avaloqService.executeReportRequestToDomain(any(AvaloqRequest.class), eq(ModelPortfolioRebalanceResponseImpl.class),
                any(ServiceErrors.class))).thenAnswer(new Answer<ModelPortfolioRebalanceResponseImpl>() {

                    @Override
                    public ModelPortfolioRebalanceResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                        AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];
                        Assert.assertEquals(ModelPortfolioRebalanceTemplate.REBALANCE_SUMMARY, req.getTemplate());
                        Assert.assertEquals("broker",
                                ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0).getVal()).getVal());

                        return mockResponse;
                    }
                });

        final RebalanceAccountsResponseImpl mockAccountsResponse = Mockito.mock(RebalanceAccountsResponseImpl.class);
        List<RebalanceAccount> accountRebalances = Collections.emptyList();
        when(mockAccountsResponse.getAccountRebalances()).thenReturn(accountRebalances);

        when(avaloqService.executeReportRequestToDomain(any(AvaloqRequest.class), eq(RebalanceAccountsResponseImpl.class),
                any(ServiceErrors.class))).thenAnswer(new Answer<RebalanceAccountsResponseImpl>() {

                    @Override
                    public RebalanceAccountsResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                        AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];
                        Assert.assertEquals(ModelPortfolioRebalanceTemplate.REBALANCE_ACCOUNTS, req.getTemplate());
                        Assert.assertEquals("key",
                                ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0).getVal()).getVal());

                        return mockAccountsResponse;
                    }
                });
        integrationServiceImpl.submitModelPortfolioRebalance(BrokerKey.valueOf("broker"), IpsKey.valueOf("key"), serviceErrors);
    }

    @Test
    public void whenRebalanceExclusionsUpdated_thenCorrectParametersArePassedToTheService() throws Exception {
        final ModelPortfolioExclusionResponseImpl response = new ModelPortfolioExclusionResponseImpl();
        serviceErrors = new ServiceErrorsImpl();
        final RebalDetReq request = new RebalDetReq();
        Mockito.when(exclusionConverter.toExcludeRequest(Mockito.anyList(), Mockito.anyList())).thenReturn(request);
        Mockito.when(avaloqTransactionService.sendToWebService(Mockito.any(RebalReq.class), Mockito.any(AvaloqOperation.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<ModelPortfolioExclusionResponseImpl>() {

                    @Override
                    public ModelPortfolioExclusionResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                        RebalDetReq req = (RebalDetReq) invocation.getArguments()[0];

                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        final RebalanceAccountsResponseImpl mockAccountsResponse = Mockito.mock(RebalanceAccountsResponseImpl.class);
        List<RebalanceAccount> accountRebalances = Collections.emptyList();
        when(mockAccountsResponse.getAccountRebalances()).thenReturn(accountRebalances);

        when(avaloqService.executeReportRequestToDomain(any(AvaloqRequest.class), eq(RebalanceAccountsResponseImpl.class),
                any(ServiceErrors.class))).thenAnswer(new Answer<RebalanceAccountsResponseImpl>() {

                    @Override
                    public RebalanceAccountsResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                        AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];
                        Assert.assertEquals(ModelPortfolioRebalanceTemplate.REBALANCE_ACCOUNTS, req.getTemplate());
                        Assert.assertEquals("key",
                                ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0).getVal()).getVal());

                        return mockAccountsResponse;
                    }
                });
        List<RebalanceExclusion> exclusions = new ArrayList<>();
        integrationServiceImpl.updateRebalanceExclusions(BrokerKey.valueOf("broker"), IpsKey.valueOf("key"), exclusions,
                serviceErrors);
    }

    @Test
    public void whenRebalanceOrdersForIpsLoadRequested_thenCorrectParametersArePassedToTheService() throws Exception {

        final RebalanceOrdersResponseImpl mockResponse = Mockito.mock(RebalanceOrdersResponseImpl.class);

        List<RebalanceOrderGroup> rebalances = Collections.emptyList();
        Mockito.when(mockResponse.getRebalanceOrders()).thenReturn(rebalances);

        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<RebalanceOrdersResponseImpl>() {

            @Override
            public RebalanceOrdersResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];
                Assert.assertEquals(ModelPortfolioRebalanceTemplate.REBALANCE_ORDERS, req.getTemplate());
                Assert.assertEquals("10000",
                        ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0).getVal()).getVal());
                return mockResponse;
            }
        });

        List<RebalanceOrderGroup> response = integrationServiceImpl.loadRebalanceOrdersForIps(IpsKey.valueOf("10000"),
                serviceErrors);

        Assert.assertEquals(rebalances, response);
    }

    @Test
    public void whenRebalanceOrdersLoadRequested_thenCorrectParametersArePassedToTheService() throws Exception {

        final RebalanceOrdersResponseImpl mockResponse = Mockito.mock(RebalanceOrdersResponseImpl.class);

        List<RebalanceOrderGroup> rebalances = Collections.emptyList();
        Mockito.when(mockResponse.getRebalanceOrders()).thenReturn(rebalances);

        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<RebalanceOrdersResponseImpl>() {

            @Override
            public RebalanceOrdersResponseImpl answer(InvocationOnMock invocation) throws Throwable {
                AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];
                Assert.assertEquals(ModelPortfolioRebalanceTemplate.REBALANCE_ORDERS, req.getTemplate());
                Assert.assertEquals("200", ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0)
                        .getValList().getVal().get(0)).getVal());
                Assert.assertEquals("201", ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0)
                        .getValList().getVal().get(1)).getVal());

                return mockResponse;
            }
        });

        List<String> docIds = Arrays.asList("200", "201");
        List<RebalanceOrderGroup> response = integrationServiceImpl.loadRebalanceOrders(docIds, serviceErrors);

        Assert.assertEquals(rebalances, response);
    }

}
