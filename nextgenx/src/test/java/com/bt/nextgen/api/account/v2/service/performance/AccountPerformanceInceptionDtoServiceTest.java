package com.bt.nextgen.api.account.v2.service.performance;

import com.bt.nextgen.api.account.v2.model.DatedAccountKey;
import com.bt.nextgen.api.account.v2.model.performance.PerformanceSummaryDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.performance.PeriodicPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
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

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class AccountPerformanceInceptionDtoServiceTest {
    @InjectMocks
    private AccountPerformanceInceptionDtoServiceImpl performanceService;

    @Mock
    private AccountPerformanceIntegrationService accountPerformanceService;

    @Test
    public void testFind_whenMethodInvoked_ThenCorrectArgumentsArePassed() {
        final PeriodicPerformanceImpl periodicPerformance = new PeriodicPerformanceImpl();
        Performance performance = createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-01-25"));
        periodicPerformance.setPerformanceData(performance);

        final DateTime effectiveDate = DateTime.parse("2015-01-01");
        DatedAccountKey key = new DatedAccountKey(EncodedString.fromPlainText("accountKey").toString(), effectiveDate);

        Mockito.when(
                accountPerformanceService.loadAccountPerformanceSummarySinceInception(Mockito.any(AccountKey.class),
                        Mockito.any(String.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<PeriodicPerformance>() {

                    @Override
                    public PeriodicPerformance answer(InvocationOnMock invocation) throws Throwable {
                        Assert.assertEquals(AccountKey.valueOf("accountKey"), invocation.getArguments()[0]);
                        Assert.assertEquals(effectiveDate, invocation.getArguments()[2]);
                        return periodicPerformance;
                    }
                });
        performanceService.find(key, new FailFastErrorsImpl());
    }

    @Test
    public void testFind_whenValuesAreFetchedFromIntegration_ThenModelIsConstructed() {
        PeriodicPerformanceImpl periodicPerformance = new PeriodicPerformanceImpl();
        Performance performance = createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-01-25"));
        periodicPerformance.setPerformanceData(performance);
        Mockito.when(
                accountPerformanceService.loadAccountPerformanceSummarySinceInception(Mockito.any(AccountKey.class),
                        Mockito.any(String.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                periodicPerformance);

        DatedAccountKey key = new DatedAccountKey(EncodedString.fromPlainText("accountKey").toString(), new DateTime());

        PerformanceSummaryDto<DatedAccountKey> result = performanceService.find(key, new FailFastErrorsImpl());
        Assert.assertEquals(BigDecimal.valueOf(3), result.getCapitalPerformanceForPeriod());
        Assert.assertEquals(BigDecimal.valueOf(400), result.getDollarPeriodReturn());
        Assert.assertEquals(BigDecimal.valueOf(4), result.getIncomePerformanceForPeriod());
        Assert.assertEquals(BigDecimal.valueOf(2), result.getPercentagePeriodReturn());
    }

    private Performance createPerformanceTestData(DateTime startDate, DateTime endDate) {
        PerformanceImpl p2 = new PerformanceImpl();
        p2.setBmrkRor(BigDecimal.valueOf(100));
        p2.setPerformance(BigDecimal.valueOf(200));
        p2.setCapitalGrowth(BigDecimal.valueOf(300));
        p2.setIncomeRtn(BigDecimal.valueOf(400));
        p2.setActiveRor(BigDecimal.valueOf(500));
        p2.setPeriodSop(startDate);
        p2.setPeriodEop(endDate);

        p2.setOpeningBalance(BigDecimal.valueOf(200000));
        p2.setInflows(BigDecimal.valueOf(20));
        p2.setOutflows(BigDecimal.valueOf(40));
        p2.setIncome(BigDecimal.valueOf(4000));
        p2.setExpenses(BigDecimal.valueOf(2500));
        p2.setMktMvt(BigDecimal.valueOf(5000));
        p2.setBalanceBeforeFee(BigDecimal.valueOf(50000));
        p2.setFee(BigDecimal.valueOf(20));
        p2.setClosingBalanceAfterFee(BigDecimal.valueOf(60000));
        p2.setNetGainLoss(BigDecimal.valueOf(400));

        return p2;
    }

}
