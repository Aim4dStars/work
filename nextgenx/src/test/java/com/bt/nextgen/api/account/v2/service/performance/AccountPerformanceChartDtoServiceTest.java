package com.bt.nextgen.api.account.v2.service.performance;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.performance.AccountPerformance;
import com.bt.nextgen.api.performance.service.BenchmarkDtoService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
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
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AccountPerformanceChartDtoServiceTest {
    @InjectMocks
    private AccountPerformanceChartDtoServiceImpl performanceChartService;

    @Mock
    private AccountPerformanceIntegrationService accountPerformanceService;

    @Mock
    private BenchmarkDtoService benchmarkService;

    private WrapAccountPerformanceImpl accountPerformance;

    @Before
    public void setup() throws Exception {
        accountPerformance = new WrapAccountPerformanceImpl();

        List<Performance> weeklyPerformance = new ArrayList<>();
        weeklyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-01-05")));
        weeklyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-06"), DateTime.parse("2014-01-12")));
        weeklyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-13"), DateTime.parse("2014-01-19")));
        weeklyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-20"), DateTime.parse("2014-01-25")));

        List<Performance> dailyPerformance = new ArrayList<>();
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-01-01")));
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-02"), DateTime.parse("2014-01-02")));
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-07"), DateTime.parse("2014-01-07")));
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-07"), DateTime.parse("2014-01-07")));
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-14"), DateTime.parse("2014-01-14")));
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-24"), DateTime.parse("2014-01-24")));

        Performance periodPerformance = createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-01-25"));

        accountPerformance.setPeriodPerformanceData(periodPerformance);
        accountPerformance.setWeeklyPerformanceData(weeklyPerformance);
        accountPerformance.setDailyPerformanceData(dailyPerformance);
        accountPerformance.setStartDate(DateTime.parse("2014-01-01"));
        accountPerformance.setEndDate(DateTime.parse("2014-01-25"));

        Mockito.when(
                accountPerformanceService.loadAccountPerformanceReport(Mockito.any(AccountKey.class), Mockito.any(String.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                accountPerformance);
    }

    @Test
    public void testGetColHeaders() {
        DateTime startDate = DateTime.parse("2014-01-01");
        DateTime endDate = DateTime.parse("2014-01-25");
        DateRangeAccountKey key = new DateRangeAccountKey(EncodedString.fromPlainText("31442").toString(), startDate, endDate);
        AccountPerformance accountPerformance = performanceChartService.find(key, new ServiceErrorsImpl());
        Assert.assertNotNull(accountPerformance);
        Assert.assertTrue(accountPerformance.getColHeaders().size() == 4);
        Assert.assertTrue(accountPerformance.getColHeaders().get(0).equals("WK1^"));
        Assert.assertTrue(accountPerformance.getColHeaders().get(1).equals("WK2"));
        Assert.assertTrue(accountPerformance.getColHeaders().get(2).equals("WK3"));
        Assert.assertTrue(accountPerformance.getColHeaders().get(3).equals("WK4^"));

    }

    @Test
    public void testGetAccountPerformance() {
        DateTime startDate = DateTime.parse("2014-01-01");
        DateTime endDate = DateTime.parse("2014-01-25");
        DateRangeAccountKey key = new DateRangeAccountKey(EncodedString.fromPlainText("31442").toString(), startDate, endDate);
        AccountPerformance accountPerformance = performanceChartService.find(key, new ServiceErrorsImpl());
        Assert.assertNotNull(accountPerformance);
        Assert.assertNotNull(accountPerformance.getCapitalPerformance());
        Assert.assertNotNull(accountPerformance.getIncomePerformance());
        Assert.assertNotNull(accountPerformance.getPeriodPerformance());
        Assert.assertNotNull(accountPerformance.getPeriodDollar());
        Assert.assertNotNull(accountPerformance.getCumulativePerformance());
        Assert.assertNotNull(accountPerformance.getPortfolioValue());
        Assert.assertNotNull(accountPerformance.getPeriodDollar());
        Assert.assertNotNull(accountPerformance.getPerformanceSummaryDto().getPercentagePeriodReturn());
        Assert.assertNotNull(accountPerformance.getPerformanceSummaryDto().getPercentagePeriodReturn());

        DateTime startDate1 = DateTime.parse("2014-01-01");
        DateTime endDate1 = DateTime.parse("2014-02-25");
        DateRangeAccountKey key1 = new DateRangeAccountKey(EncodedString.fromPlainText("31442").toString(), startDate1, endDate1);
        AccountPerformance accountPerformance1 = performanceChartService.find(key1, new ServiceErrorsImpl());
        Assert.assertNotNull(accountPerformance1);
        Assert.assertNotNull(accountPerformance1.getCapitalPerformance());
        Assert.assertNotNull(accountPerformance1.getIncomePerformance());
        Assert.assertNotNull(accountPerformance1.getPeriodPerformance());
        Assert.assertNotNull(accountPerformance1.getPeriodDollar());
        Assert.assertNotNull(accountPerformance1.getCumulativePerformance());
        Assert.assertNotNull(accountPerformance1.getPortfolioValue());
        Assert.assertNotNull(accountPerformance1.getPerformanceSummaryDto().getPercentagePeriodReturn());
        Assert.assertNotNull(accountPerformance1.getPerformanceSummaryDto().getPercentagePeriodReturn());

        DateTime startDate2 = DateTime.parse("2014-01-01");
        DateTime endDate2 = DateTime.parse("2015-02-25");
        DateRangeAccountKey key2 = new DateRangeAccountKey(EncodedString.fromPlainText("31442").toString(), startDate2, endDate2);
        AccountPerformance accountPerformance2 = performanceChartService.find(key2, new ServiceErrorsImpl());
        Assert.assertNotNull(accountPerformance2);
        Assert.assertNotNull(accountPerformance2.getCapitalPerformance());
        Assert.assertNotNull(accountPerformance2.getIncomePerformance());
        Assert.assertNotNull(accountPerformance2.getPeriodPerformance());
        Assert.assertNotNull(accountPerformance2.getPeriodDollar());
        Assert.assertNotNull(accountPerformance2.getCumulativePerformance());
        Assert.assertNotNull(accountPerformance2.getPortfolioValue());
        Assert.assertNotNull(accountPerformance2.getPerformanceSummaryDto().getPercentagePeriodReturn());
        Assert.assertNotNull(accountPerformance2.getPerformanceSummaryDto().getPercentagePeriodReturn());
    }

    private Performance createPerformanceTestData(DateTime startDate, DateTime endDate) {
        PerformanceImpl p2 = new PerformanceImpl();
        p2.setBmrkRor(random());
        p2.setPerformance(random());
        p2.setCapitalGrowth(random());
        p2.setIncomeRtn(random());
        p2.setActiveRor(random());
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

    private BigDecimal random() {
        double max = 0.99;
        double min = -0.99;
        double r = Math.random();
        if (r < 0.5) {
            return new BigDecimal(((1 - Math.random()) * (max - min) + min));
        }
        return new BigDecimal((Math.random() * (max - min) + min));
    }
}
