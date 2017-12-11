package com.bt.nextgen.service.avaloq.modelpreferences;

import com.avaloq.abs.bb.fld_def.IdFld;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelpreferences.AccountModelPreferences;
import com.bt.nextgen.service.integration.modelpreferences.ModelPreference;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.abs.reportservice.reportrequest.v1_0.RepReq;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqModelPreferenceIntegrationServiceImplTest {
    @InjectMocks
    private AvaloqModelPreferenceIntegrationServiceImpl preferenceService;

    @Mock
    private AvaloqReportService reportService;

    @Test
    public void getPreferencesForAccount_whenInvoked_CorrectRequestIsSentToAvaloqReportService() {
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final AccountKey accountKey = AccountKey.valueOf("getPreferencesForAccount");
        final List<ModelPreference> prefs = new ArrayList<>();
        final AccountModelPreferences report = Mockito.mock(AccountModelPreferences.class);
        Mockito.when(report.getPreferences()).thenReturn(prefs);
        Mockito.when(
                reportService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).then(new Answer<AccountModelPreferences>() {

            @Override
            public AccountModelPreferences answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertEquals(invocation.getArguments()[2], serviceErrors);
                Assert.assertEquals(invocation.getArguments()[1], AccountModelPreferencesImpl.class);
                AvaloqRequest req = (AvaloqRequest) invocation.getArguments()[0];
                Assert.assertEquals(ModelPreferenceTemplate.ACCOUNT_PREFERENCES, req.getTemplate());
                RepReq repreq = (RepReq) req.getRequestObject();
                Assert.assertEquals(accountKey.getId(),
                        ((IdFld) repreq.getTask().getParamList().getParam().get(0).getVal()).getVal());
                        Assert.assertEquals(ModelPreferenceParams.PARAM_ACCOUNT_LIST_ID.getName(),
                                repreq.getTask().getParamList()
                        .getParam().get(0).getName());
                return report;
            }

        });

        AccountModelPreferences result = preferenceService.getPreferencesForAccount(accountKey, serviceErrors);
        Assert.assertTrue(result == report);
    }

    @Test
    public void getPreferencesForSubaccount_whenInvoked_CorrectRequestIsSentToAvaloqReportService() throws Exception {
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final AccountKey accountKey = AccountKey.valueOf("getPreferencesForSubaccount");
        final List<ModelPreference> prefs = new ArrayList<>();
        final SubaccountPreferencesHolder report = Mockito.mock(SubaccountPreferencesHolder.class);
        Mockito.when(report.getPreferences()).thenReturn(prefs);
        Mockito.when(
                reportService.executeReportRequestToDomain(Mockito.any(AvaloqRequest.class), Mockito.any(Class.class),
                        Mockito.any(ServiceErrors.class))).then(new Answer<SubaccountPreferencesHolder>() {

            @Override
            public SubaccountPreferencesHolder answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertEquals(invocation.getArguments()[2], serviceErrors);
                Assert.assertEquals(invocation.getArguments()[1], SubaccountPreferencesHolder.class);
                AvaloqRequest req = (AvaloqRequest) invocation.getArguments()[0];
                Assert.assertEquals(ModelPreferenceTemplate.SUBACCOUNT_PREFERENCES, req.getTemplate());
                RepReq repreq = (RepReq) req.getRequestObject();
                Assert.assertEquals(accountKey.getId(),
                        ((IdFld) repreq.getTask().getParamList().getParam().get(0).getVal()).getVal());
                Assert.assertEquals(ModelPreferenceParams.PARAM_SUBACCOUNT_LIST_ID.getName(), repreq.getTask().getParamList()
                        .getParam().get(0).getName());
                return report;
            }

        });

        List<ModelPreference> result = preferenceService.getPreferencesForSubaccount(accountKey, serviceErrors);
        Assert.assertTrue(result == prefs);
    }
}
