package com.bt.nextgen.api.income.v2.service;

import com.bt.nextgen.api.income.v2.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsType;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.service.integration.income.IncomeIntegrationService;
import com.bt.nextgen.service.integration.income.WrapAccountIncomeDetails;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
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
public class IncomeDetailsDtoServiceTest {
    @InjectMocks
    private IncomeDetailsDtoServiceImpl incomeDetailsService;

    @Mock
    private IncomeSubAccountIncomeAggregator aggregator;

    @Mock
    private IncomeIntegrationService incomeService;

    @Mock
    private PortfolioIntegrationService portfolioService;

    @Mock
    private CorporateActionIntegrationService corporateActionIntegrationService;

    @Test
    public void testFind_whenReceivedIncomeType_thenIncomeLoadedAndAggregated() {
        final DateTime d1 = new DateTime();
        final DateTime d2 = new DateTime();
        final List<WrapAccountIncomeDetails> integrationResult = new ArrayList<>();

        IncomeDetailsKey key = new IncomeDetailsKey(EncodedString.fromPlainText("accountId").toString(),
                IncomeDetailsType.RECEIVED, d1, d2);
        Mockito.when(incomeService.loadIncomeReceivedDetails(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<WrapAccountIncomeDetails>>() {

                    @Override
                    public List<WrapAccountIncomeDetails> answer(InvocationOnMock invocation) throws Throwable {
                        Object args[] = invocation.getArguments();
                        Assert.assertEquals(AccountKey.valueOf("accountId"), args[0]);
                        Assert.assertEquals(d1, args[1]);
                        Assert.assertEquals(d2, args[2]);
                        return integrationResult;
                    }
                });

        incomeDetailsService.find(key, new FailFastErrorsImpl());

    }

    @Test
    public void testFind_whenAccruedIncomeType_thenValautionLoadedAndAggregated() {
        final DateTime d1 = new DateTime();
        final List<WrapAccountIncomeDetails> integrationResult = new ArrayList<>();
        final WrapAccountValuation valuation = Mockito.mock(WrapAccountValuation.class);

        IncomeDetailsKey key = new IncomeDetailsKey(EncodedString.fromPlainText("accountId").toString(),
                IncomeDetailsType.ACCRUED, d1, d1);
        Mockito.when(incomeService.loadCashDividendDetails(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.any(String.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<WrapAccountIncomeDetails>>() {

                    @Override
                    public List<WrapAccountIncomeDetails> answer(InvocationOnMock invocation) throws Throwable {
                        Object args[] = invocation.getArguments();
                        Assert.assertEquals(AccountKey.valueOf("accountId"), args[0]);
                        Assert.assertEquals(d1, args[1]);
                        Assert.assertEquals(IncomeDetailsType.ACCRUED.getStatus(), args[2]);
                        return integrationResult;
                    }
                });

        Mockito.when(portfolioService.loadWrapAccountValuation(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<WrapAccountValuation>() {

                    @Override
                    public WrapAccountValuation answer(InvocationOnMock invocation) throws Throwable {
                        Object args[] = invocation.getArguments();
                        Assert.assertEquals(AccountKey.valueOf("accountId"), args[0]);
                        Assert.assertEquals(d1, args[1]);
                        return valuation;
                    }
                });


        incomeDetailsService.find(key, new FailFastErrorsImpl());

    }

}
