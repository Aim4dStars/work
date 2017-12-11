package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.AccountPerformanceReportDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.performance.Performance;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AccountPerformanceReportDtoServiceDataAggreratorTest {
    @InjectMocks
    private AccountPerformanceReportDtoServiceDataAggregatorImpl accountPerformanceReportDtoServiceDataAggregatorImpl;

    @Before
    public void setup() throws Exception {
    }

    @Test
    public void testGetWeeklyPerformanceReportDto() {
        DateTime startDate = DateTime.parse("2014-01-01");
        DateTime endDate = DateTime.parse("2014-01-25");

        // Period performance data.
        Performance performance = createPerformanceTestData(startDate, endDate);

        List<Performance> q = new ArrayList<>();
        q.add(createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-01-05")));
        q.add(createPerformanceTestData(DateTime.parse("2014-01-06"), DateTime.parse("2014-01-12")));
        q.add(createPerformanceTestData(DateTime.parse("2014-01-13"), DateTime.parse("2014-01-19")));
        q.add(createPerformanceTestData(DateTime.parse("2014-01-20"), DateTime.parse("2014-01-25")));

        WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
        accountPerformance.setPeriodPerformanceData(performance);
        accountPerformance.setDailyPerformanceData(q);
        accountPerformance.setWeeklyPerformanceData(q);
        accountPerformance.setStartDate(startDate);
        accountPerformance.setEndDate(endDate);

        AccountPerformanceKey key = new AccountPerformanceKey(EncodedString.fromPlainText("31442").toString(), startDate, endDate,
                "1234");

        AccountPerformanceReportDto reportDto = accountPerformanceReportDtoServiceDataAggregatorImpl.buildReportDto(key,
                accountPerformance, performance);

        Assert.assertNotNull(reportDto);
        Assert.assertTrue(reportDto.getColHeaders().size() == 6);
        Assert.assertTrue(reportDto.getColHeaders().get(0).equals("WK1^"));
        Assert.assertTrue(reportDto.getColHeaders().get(1).equals("WK2"));
        Assert.assertTrue(reportDto.getColHeaders().get(2).equals("WK3"));
        Assert.assertTrue(reportDto.getColHeaders().get(3).equals("WK4^"));
        Assert.assertTrue(reportDto.getColHeaders().get(4).equals("Period<br/>return"));
        Assert.assertTrue(reportDto.getColHeaders().get(5).equals("Since<br/>inception"));

        // test net return data

        Assert.assertEquals(BigDecimal.valueOf(200000), reportDto.getNetReturnData().get(0).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(20), reportDto.getNetReturnData().get(1).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(40), reportDto.getNetReturnData().get(2).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(4000), reportDto.getNetReturnData().get(3).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(2500), reportDto.getNetReturnData().get(4).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(5000), reportDto.getNetReturnData().get(5).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(50000), reportDto.getNetReturnData().get(6).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(20), reportDto.getNetReturnData().get(7).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(-30000), reportDto.getNetReturnData().get(8).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(60000), reportDto.getNetReturnData().get(9).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(400), reportDto.getNetReturnData().get(10).getDataPeriod1());
    }

    @Test
    public void testGetMonthlyPerformanceReportDto() {
        DateTime startDate = DateTime.parse("2014-01-01");
        DateTime endDate = DateTime.parse("2014-12-31");

        // Period performance data.
        Performance performance = createPerformanceTestData(startDate, endDate);

        List<Performance> monthly = new ArrayList<>();
        monthly.add(createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-01-31")));
        monthly.add(createPerformanceTestData(DateTime.parse("2014-02-01"), DateTime.parse("2014-02-28")));
        monthly.add(createPerformanceTestData(DateTime.parse("2014-03-01"), DateTime.parse("2014-03-31")));
        monthly.add(createPerformanceTestData(DateTime.parse("2014-04-01"), DateTime.parse("2014-04-30")));
        monthly.add(createPerformanceTestData(DateTime.parse("2014-05-01"), DateTime.parse("2014-05-31")));
        monthly.add(createPerformanceTestData(DateTime.parse("2014-06-01"), DateTime.parse("2014-06-30")));
        monthly.add(createPerformanceTestData(DateTime.parse("2014-07-01"), DateTime.parse("2014-07-31")));
        monthly.add(createPerformanceTestData(DateTime.parse("2014-08-01"), DateTime.parse("2014-08-31")));
        monthly.add(createPerformanceTestData(DateTime.parse("2014-09-01"), DateTime.parse("2014-09-30")));
        monthly.add(createPerformanceTestData(DateTime.parse("2014-10-01"), DateTime.parse("2014-10-31")));
        monthly.add(createPerformanceTestData(DateTime.parse("2014-11-01"), DateTime.parse("2014-11-30")));
        monthly.add(createPerformanceTestData(DateTime.parse("2014-12-01"), DateTime.parse("2014-12-31")));

        WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
        accountPerformance.setPeriodPerformanceData(performance);
        accountPerformance.setWeeklyPerformanceData(monthly);
        accountPerformance.setMonthlyPerformanceData(monthly);
        accountPerformance.setStartDate(startDate);
        accountPerformance.setEndDate(endDate);

        AccountPerformanceKey key = new AccountPerformanceKey(EncodedString.fromPlainText("31442").toString(), startDate, endDate,
                "1234");
        AccountPerformanceReportDto reportDto = accountPerformanceReportDtoServiceDataAggregatorImpl.buildReportDto(key,
                accountPerformance, performance);
        Assert.assertNotNull(reportDto);
        Assert.assertTrue(reportDto.getColHeaders().get(0).equals("Jan 14"));
        Assert.assertTrue(reportDto.getColHeaders().get(1).equals("Feb 14"));
        Assert.assertTrue(reportDto.getColHeaders().get(2).equals("Mar 14"));
        Assert.assertTrue(reportDto.getColHeaders().get(10).equals("Nov 14"));
        Assert.assertTrue(reportDto.getColHeaders().get(11).equals("Dec 14"));

        // test net return data

        Assert.assertEquals(BigDecimal.valueOf(200000), reportDto.getNetReturnData().get(0).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(20), reportDto.getNetReturnData().get(1).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(40), reportDto.getNetReturnData().get(2).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(4000), reportDto.getNetReturnData().get(3).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(2500), reportDto.getNetReturnData().get(4).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(5000), reportDto.getNetReturnData().get(5).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(50000), reportDto.getNetReturnData().get(6).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(20), reportDto.getNetReturnData().get(7).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(-30000), reportDto.getNetReturnData().get(8).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(60000), reportDto.getNetReturnData().get(9).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(400), reportDto.getNetReturnData().get(10).getDataPeriod1());
    }

    @Test
    public void testGetQuarterlyPerformanceReportDto() {
        DateTime startDate = DateTime.parse("2014-01-01");
        DateTime endDate = DateTime.parse("2015-02-28");

        // Period performance data.
        Performance performance = createPerformanceTestData(startDate, endDate);

        List<Performance> q = new ArrayList<>();
        q.add(createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-03-31")));
        q.add(createPerformanceTestData(DateTime.parse("2014-04-01"), DateTime.parse("2014-06-30")));
        q.add(createPerformanceTestData(DateTime.parse("2014-07-01"), DateTime.parse("2014-09-30")));
        q.add(createPerformanceTestData(DateTime.parse("2014-10-01"), DateTime.parse("2014-12-31")));
        q.add(createPerformanceTestData(DateTime.parse("2015-01-01"), DateTime.parse("2015-02-28")));

        WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
        accountPerformance.setPeriodPerformanceData(performance);
        accountPerformance.setWeeklyPerformanceData(q);
        accountPerformance.setQuarterlyPerformanceData(q);
        accountPerformance.setStartDate(startDate);
        accountPerformance.setEndDate(endDate);

        AccountPerformanceKey key = new AccountPerformanceKey(EncodedString.fromPlainText("31442").toString(), startDate, endDate,
                "1234");
        AccountPerformanceReportDto reportDto = accountPerformanceReportDtoServiceDataAggregatorImpl.buildReportDto(key,
                accountPerformance, performance);
        Assert.assertNotNull(reportDto);
        Assert.assertTrue(reportDto.getColHeaders().size() == 7);
        Assert.assertTrue(reportDto.getColHeaders().get(0).equals("2014 Q1"));
        Assert.assertTrue(reportDto.getColHeaders().get(1).equals("2014 Q2"));
        Assert.assertTrue(reportDto.getColHeaders().get(2).equals("2014 Q3"));
        Assert.assertTrue(reportDto.getColHeaders().get(3).equals("2014 Q4"));
        Assert.assertTrue(reportDto.getColHeaders().get(4).equals("2015 Q1^"));

        // test net return data

        Assert.assertEquals(BigDecimal.valueOf(200000), reportDto.getNetReturnData().get(0).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(20), reportDto.getNetReturnData().get(1).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(40), reportDto.getNetReturnData().get(2).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(4000), reportDto.getNetReturnData().get(3).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(2500), reportDto.getNetReturnData().get(4).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(5000), reportDto.getNetReturnData().get(5).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(50000), reportDto.getNetReturnData().get(6).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(20), reportDto.getNetReturnData().get(7).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(-30000), reportDto.getNetReturnData().get(8).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(60000), reportDto.getNetReturnData().get(9).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(400), reportDto.getNetReturnData().get(10).getDataPeriod1());
    }

    @Test
    public void testGetYearlyPerformanceReportDto() {
        DateTime startDate = DateTime.parse("2014-01-01");
        DateTime endDate = DateTime.parse("2016-01-30");

        // Period performance data.
        Performance performance = createPerformanceTestData(startDate, endDate);

        List<Performance> q = new ArrayList<>();
        q.add(createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-12-31")));
        q.add(createPerformanceTestData(DateTime.parse("2015-01-01"), DateTime.parse("2015-12-31")));
        q.add(createPerformanceTestData(DateTime.parse("2016-01-01"), DateTime.parse("2016-01-30")));

        WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
        accountPerformance.setPeriodPerformanceData(performance);
        accountPerformance.setMonthlyPerformanceData(q);
        accountPerformance.setYearlyPerformanceData(q);
        accountPerformance.setStartDate(startDate);
        accountPerformance.setEndDate(endDate);

        AccountPerformanceKey key = new AccountPerformanceKey(EncodedString.fromPlainText("31442").toString(), startDate, endDate,
                "1234");

        AccountPerformanceReportDto reportDto = accountPerformanceReportDtoServiceDataAggregatorImpl.buildReportDto(key,
                accountPerformance, performance);
        Assert.assertNotNull(reportDto);
        Assert.assertTrue(reportDto.getColHeaders().size() == 5);
        Assert.assertTrue(reportDto.getColHeaders().get(0).equals("2014"));
        Assert.assertTrue(reportDto.getColHeaders().get(1).equals("2015"));
        Assert.assertTrue(reportDto.getColHeaders().get(2).equals("2016^"));

        // test net return data

        Assert.assertEquals(BigDecimal.valueOf(200000), reportDto.getNetReturnData().get(0).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(20), reportDto.getNetReturnData().get(1).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(40), reportDto.getNetReturnData().get(2).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(4000), reportDto.getNetReturnData().get(3).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(2500), reportDto.getNetReturnData().get(4).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(5000), reportDto.getNetReturnData().get(5).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(50000), reportDto.getNetReturnData().get(6).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(20), reportDto.getNetReturnData().get(7).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(-30000), reportDto.getNetReturnData().get(8).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(60000), reportDto.getNetReturnData().get(9).getDataPeriod1());
        Assert.assertEquals(BigDecimal.valueOf(400), reportDto.getNetReturnData().get(10).getDataPeriod1());    }

    @Test
    public final void testGetYearlyPerformanceReportDtoOtherFees() {
        DateTime startDate = DateTime.parse("2014-01-01");
        DateTime endDate = DateTime.parse("2016-01-30");

        // Period performance data.
        Performance performance = createPerformanceTestData(startDate, endDate);

        List<Performance> q = new ArrayList<>();
        q.add(createPerformanceTestDataNoOtherFees(DateTime.parse("2014-01-01"), DateTime.parse("2014-12-31")));
        q.add(createPerformanceTestDataNoOtherFees(DateTime.parse("2015-01-01"), DateTime.parse("2015-12-31")));
        q.add(createPerformanceTestDataNoOtherFees(DateTime.parse("2016-01-01"), DateTime.parse("2016-01-30")));

        WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
        accountPerformance.setPeriodPerformanceData(performance);
        accountPerformance.setMonthlyPerformanceData(q);
        accountPerformance.setYearlyPerformanceData(q);
        accountPerformance.setStartDate(startDate);
        accountPerformance.setEndDate(endDate);

        AccountPerformanceKey key = new AccountPerformanceKey(EncodedString.fromPlainText("31442").toString(), startDate, endDate,
                "1234");
        AccountPerformanceReportDto reportDto = accountPerformanceReportDtoServiceDataAggregatorImpl.buildReportDto(key,
                accountPerformance, performance);

          Assert.assertNotNull(reportDto);
        Assert.assertTrue(reportDto.getColHeaders().size() == 5);
        Assert.assertTrue(reportDto.getColHeaders().get(0).equals("2014"));
        Assert.assertTrue(reportDto.getColHeaders().get(1).equals("2015"));
        Assert.assertTrue(reportDto.getColHeaders().get(2).equals("2016^"));
    }

    private Performance createPerformanceTestData(DateTime startDate, DateTime endDate) {
        PerformanceImpl p2 = new PerformanceImpl();
        p2.setBmrkRor(random());
        p2.setPerformance(random());
        p2.setCapitalGrowth(random());
        p2.setIncomeRtn(random());
        p2.setBmrkRor(random());
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
        p2.setOtherFee(BigDecimal.valueOf(-30000));

        return p2;
    }

    private Performance createPerformanceTestDataNoOtherFees(DateTime startDate, DateTime endDate) {
        PerformanceImpl p2 = new PerformanceImpl();
        p2.setBmrkRor(random());
        p2.setPerformance(random());
        p2.setCapitalGrowth(random());
        p2.setIncomeRtn(random());
        p2.setBmrkRor(random());
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
