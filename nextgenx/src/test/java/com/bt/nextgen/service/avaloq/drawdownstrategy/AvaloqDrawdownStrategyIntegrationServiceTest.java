package com.bt.nextgen.service.avaloq.drawdownstrategy;

import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AvaloqCacheManagedAccountIntegrationService;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AvaloqContainerIntegrationService;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyDetails;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.abs.trxservice.cont.v1_0.ContReq;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceException;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqDrawdownStrategyIntegrationServiceTest {

    @InjectMocks
    private AvaloqDrawdownStrategyIntegrationServiceImpl service;

    @Mock
    private DrawdownStrategyConverter converter;

    @Mock
    private AvaloqReportService avaloqService;

    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    @Mock
    private TransactionValidationConverter validationConverter;

    @Mock
    private AvaloqCacheManagedAccountIntegrationService avaloqCacheAccountIntegrationService;

    @Mock
    private AvaloqContainerIntegrationService avaloqContainerIntegrationService;

    @Before
    public void setup() {
        SubAccount mockDirectContainer = Mockito.mock(SubAccount.class);
        Mockito.when(mockDirectContainer.getSubAccountType()).thenReturn(ContainerType.DIRECT);
        Mockito.when(mockDirectContainer.getDrawdownStrategy()).thenReturn(DrawdownStrategy.HIGH_PRICE.getIntlId());
        Mockito.when(mockDirectContainer.getSubAccountKey()).thenReturn(SubAccountKey.valueOf("subAccountId"));

        AccountKey accountKey = AccountKey.valueOf("accountId");
        Map<AccountKey, List<SubAccount>> map = new HashMap<>();
        map.put(accountKey, Collections.singletonList(mockDirectContainer));

        Mockito.when(avaloqCacheAccountIntegrationService.loadSubAccounts(Mockito.any(ServiceErrors.class))).thenReturn(map);

        Mockito.when(
                avaloqContainerIntegrationService.loadSpecificContainers(Mockito.any(AccountKey.class),
                        Mockito.anyListOf(String.class), Mockito.any(ServiceErrors.class))).thenReturn(map);
    }

    @Test
    public void testLoadDrawdownAssetPreferences() {
        final DrawdownStrategyDetailsImpl mockResponse = Mockito.mock(DrawdownStrategyDetailsImpl.class);

        Mockito.when(
                avaloqService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<DrawdownStrategyDetailsImpl>() {

            @Override
            public DrawdownStrategyDetailsImpl answer(InvocationOnMock invocation) throws Throwable {
                AvaloqReportRequestImpl req = (AvaloqReportRequestImpl) invocation.getArguments()[0];
                Assert.assertEquals(DrawdownStrategyTemplate.ASSET_PRIORITY_LIST, req.getTemplate());
                Assert.assertEquals(DrawdownStrategyParams.CONT_LIST_ID.getParamName(), req.getRequestObject().getTask()
                        .getParamList().getParam().get(0).getName());
                Assert.assertEquals("subAccountId", ((IdFld) req.getRequestObject().getTask().getParamList().getParam().get(0)
                        .getVal()).getVal());
                return mockResponse;
            }
        });

        service.loadDrawdownAssetPreferences(AccountKey.valueOf("accountId"), new FailFastErrorsImpl());
    }

    @Test
    public void testLoadDrawdownStrategy() {
        DrawdownStrategy strategy = service.loadDrawdownStrategy(AccountKey.valueOf("accountId"), new FailFastErrorsImpl());
        Assert.assertEquals(DrawdownStrategy.HIGH_PRICE, strategy);
    }

    @Test
    public void testSubmitDrawdownStrategy() {
        final DrawdownStrategyDetailsImpl response = Mockito.mock(DrawdownStrategyDetailsImpl.class);

        Mockito.when(response.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        final ContReq request = Mockito.mock(ContReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(ContReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<DrawdownStrategyDetailsImpl>() {

                    @Override
                    public DrawdownStrategyDetailsImpl answer(InvocationOnMock invocation) throws Throwable {
                        ContReq req = (ContReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(
                converter.toSubmitDrawdownStrategyRequest(Mockito.any(DrawdownStrategyDetails.class),
                        Mockito.any(SubAccountKey.class))).thenReturn(request);

        service.submitDrawdownStrategy(response, new FailFastErrorsImpl());
    }

    @Test(expected = ServiceException.class)
    public void testWhenSubmitDrawdownStrategyFails_thenReturnErrorDetails() {
        final DrawdownStrategyDetailsImpl response = Mockito.mock(DrawdownStrategyDetailsImpl.class);
        Mockito.when(response.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        Mockito.when(response.isErrorResponse()).thenReturn(true);
        Mockito.when(response.getErrorMessage()).thenReturn("Error help");

        final ContReq request = Mockito.mock(ContReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(ContReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<DrawdownStrategyDetailsImpl>() {

                    @Override
                    public DrawdownStrategyDetailsImpl answer(InvocationOnMock invocation) throws Throwable {
                        ContReq req = (ContReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(
                converter.toSubmitDrawdownStrategyRequest(Mockito.any(DrawdownStrategyDetails.class),
                        Mockito.any(SubAccountKey.class))).thenReturn(request);

        service.submitDrawdownStrategy(response, new FailFastErrorsImpl());
    }

    @Test
    public void testValidateDrawdownAssetPreferences() {
        final DrawdownStrategyDetailsImpl response = Mockito.mock(DrawdownStrategyDetailsImpl.class);
        Mockito.when(response.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));

        final ContReq request = Mockito.mock(ContReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(ContReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<DrawdownStrategyDetailsImpl>() {

                    @Override
                    public DrawdownStrategyDetailsImpl answer(InvocationOnMock invocation) throws Throwable {
                        ContReq req = (ContReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(
                converter.toValidateAssetPreferencesRequest(Mockito.any(DrawdownStrategyDetails.class),
                        Mockito.any(SubAccountKey.class)))
                .thenReturn(request);

        service.validateDrawdownAssetPreferences(response, new FailFastErrorsImpl());
    }
    
    @Test(expected = ServiceException.class)
    public void testWhenValidateDrawdownAssetPreferencesFails_thenReturnErrorDetails() {
        final DrawdownStrategyDetailsImpl response = Mockito.mock(DrawdownStrategyDetailsImpl.class);
        Mockito.when(response.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        Mockito.when(response.isErrorResponse()).thenReturn(true);
        Mockito.when(response.getErrorMessage()).thenReturn("Error help");

        final ContReq request = Mockito.mock(ContReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(ContReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<DrawdownStrategyDetailsImpl>() {

                    @Override
                    public DrawdownStrategyDetailsImpl answer(InvocationOnMock invocation) throws Throwable {
                        ContReq req = (ContReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(
                converter.toValidateAssetPreferencesRequest(Mockito.any(DrawdownStrategyDetails.class),
                        Mockito.any(SubAccountKey.class)))
                .thenReturn(request);

        service.validateDrawdownAssetPreferences(response, new FailFastErrorsImpl());
    }

    @Test
    public void testSubmitDrawdownAssetPreferences() {
        final DrawdownStrategyDetailsImpl response = Mockito.mock(DrawdownStrategyDetailsImpl.class);
        Mockito.when(response.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));

        final ContReq request = Mockito.mock(ContReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(ContReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<DrawdownStrategyDetailsImpl>() {

                    @Override
                    public DrawdownStrategyDetailsImpl answer(InvocationOnMock invocation) throws Throwable {
                        ContReq req = (ContReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(
                converter.toSubmitAssetPreferencesRequest(Mockito.any(DrawdownStrategyDetails.class),
                        Mockito.any(SubAccountKey.class)))
                .thenReturn(request);

        service.submitDrawdownAssetPreferences(response, new FailFastErrorsImpl());
    }
    
    @Test(expected = ServiceException.class)
    public void testWhenSubmitDrawdownAssetPreferencesFails_thenReturnErrorDetails() {
        final DrawdownStrategyDetailsImpl response = Mockito.mock(DrawdownStrategyDetailsImpl.class);
        Mockito.when(response.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        Mockito.when(response.isErrorResponse()).thenReturn(true);
        Mockito.when(response.getErrorMessage()).thenReturn("Error help");

        final ContReq request = Mockito.mock(ContReq.class);
        Mockito.when(
                webserviceClient.sendToWebService(Mockito.any(ContReq.class), Mockito.any(AvaloqOperation.class),
                        Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<DrawdownStrategyDetailsImpl>() {

                    @Override
                    public DrawdownStrategyDetailsImpl answer(InvocationOnMock invocation) throws Throwable {
                        ContReq req = (ContReq) invocation.getArguments()[0];
                        Assert.assertEquals(request, req);
                        return response;
                    }
                });

        Mockito.when(
                converter.toSubmitAssetPreferencesRequest(Mockito.any(DrawdownStrategyDetails.class),
                        Mockito.any(SubAccountKey.class)))
                .thenReturn(request);

        service.submitDrawdownAssetPreferences(response, new FailFastErrorsImpl());
    }
}
