package com.bt.nextgen.api.portfolio.v3.service;

import com.bt.nextgen.api.portfolio.v3.service.performance.PerformanceReportUtil;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PerformanceReportUtilTest {

    private WrapAccountPerformanceImpl accountPerformance;

    @Before
    public void setUp() throws Exception {
        accountPerformance = new WrapAccountPerformanceImpl();
        List<Performance> dailyPerformanceData = new ArrayList<>();
        List<Performance> weeklyPerformanceData = new ArrayList<>();
        List<Performance> monthlyPerformanceData = new ArrayList<>();
        List<Performance> quarterlyPerformanceData = new ArrayList<>();
        List<Performance> yearlyPerformanceData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PerformanceImpl dailyPerformance = new PerformanceImpl();
            dailyPerformanceData.add(dailyPerformance);
        }
        for (int i = 0; i < 4; i++) {
            PerformanceImpl weeklyPerformance = new PerformanceImpl();
            weeklyPerformanceData.add(weeklyPerformance);
        }
        for (int i = 0; i < 3; i++) {
            PerformanceImpl monthlyPerformance = new PerformanceImpl();
            monthlyPerformanceData.add(monthlyPerformance);
        }
        for (int i = 0; i < 2; i++) {
            PerformanceImpl quarterlyPerformance = new PerformanceImpl();
            quarterlyPerformanceData.add(quarterlyPerformance);
        }
        yearlyPerformanceData.add(new PerformanceImpl());
        accountPerformance.setQuarterlyPerformanceData(quarterlyPerformanceData);
        accountPerformance.setMonthlyPerformanceData(monthlyPerformanceData);
        accountPerformance.setDailyPerformanceData(dailyPerformanceData);
        accountPerformance.setWeeklyPerformanceData(weeklyPerformanceData);
        accountPerformance.setYearlyPerformanceData(yearlyPerformanceData);
    }

    @Test
    public void testIsCompleteMonth() {
        DateTime start = DateTime.parse("2015-07-01");
        DateTime end = DateTime.parse("2015-07-31");

        boolean isCompleteMonth = PerformanceReportUtil.isCompleteMonth(start, end);
        Assert.assertTrue(isCompleteMonth);
    }

    @Test
    public void testIsCompleteWeek() {
        DateTime start = DateTime.parse("2015-07-06");
        DateTime end = DateTime.parse("2015-07-12");

        boolean isCompleteWeek = PerformanceReportUtil.isCompleteWeek(start, end);
        Assert.assertTrue(isCompleteWeek);
    }

    @Test
    public void testIsCompleteYear() {
        DateTime start = DateTime.parse("2014-01-01");
        DateTime end = DateTime.parse("2014-12-31");

        boolean isCompleteYear = PerformanceReportUtil.isCompleteYear(start, end);
        Assert.assertTrue(isCompleteYear);
    }

    @Test
    public void testIsCompleteQuarter() {
        DateTime start = DateTime.parse("2015-04-01");
        DateTime end = DateTime.parse("2015-06-30");

        boolean isCompleteQuarter = PerformanceReportUtil.isCompleteQuarter(start, end);
        Assert.assertTrue(isCompleteQuarter);
    }

    @Test
    public void testGetAccountPerformancePeriodForBarGraph_Weekly() {

        WrapAccountPerformanceImpl performance = new WrapAccountPerformanceImpl();
        performance.setWeeklyPerformanceData(new ArrayList<Performance>());

        PerformancePeriodType periodType = PerformanceReportUtil.getAccountPerformancePeriodForBarGraph(performance);
        Assert.assertEquals(periodType, PerformancePeriodType.WEEKLY);
    }

    @Test
    public void testGetAccountPerformancePeriodForBarGraph_Monthly() {

        WrapAccountPerformanceImpl performance = new WrapAccountPerformanceImpl();
        performance.setMonthlyPerformanceData(new ArrayList<Performance>());

        PerformancePeriodType periodType = PerformanceReportUtil.getAccountPerformancePeriodForBarGraph(performance);
        Assert.assertEquals(periodType, PerformancePeriodType.MONTHLY);
    }

    @Test
    public void testGetAccountPerformancePeriodForBarGraph_Quarterly() {

        WrapAccountPerformanceImpl performance = new WrapAccountPerformanceImpl();
        performance.setQuarterlyPerformanceData(new ArrayList<Performance>());

        PerformancePeriodType periodType = PerformanceReportUtil.getAccountPerformancePeriodForBarGraph(performance);
        Assert.assertEquals(periodType, PerformancePeriodType.QUARTERLY);
    }

    @Test
    public void testGetAccountPerformancePeriodForBarGraph_Yearly() {

        WrapAccountPerformanceImpl performance = new WrapAccountPerformanceImpl();
        performance.setYearlyPerformanceData(new ArrayList<Performance>());

        PerformancePeriodType periodType = PerformanceReportUtil.getAccountPerformancePeriodForBarGraph(performance);
        Assert.assertEquals(periodType, PerformancePeriodType.YEARLY);
    }

    @Test
    public void testGetAccountPerformancePeriodForLineGraph_Daily() {

        WrapAccountPerformanceImpl performance = new WrapAccountPerformanceImpl();
        performance.setDailyPerformanceData(new ArrayList<Performance>());

        PerformancePeriodType periodType = PerformanceReportUtil.getAccountPerformancePeriodForLineGraph(performance);
        Assert.assertEquals(periodType, PerformancePeriodType.DAILY);
    }

    @Test
    public void testGetAccountPerformancePeriodForLineGraph_Weekly() {

        WrapAccountPerformanceImpl performance = new WrapAccountPerformanceImpl();
        performance.setWeeklyPerformanceData(new ArrayList<Performance>());

        PerformancePeriodType periodType = PerformanceReportUtil.getAccountPerformancePeriodForLineGraph(performance);
        Assert.assertEquals(periodType, PerformancePeriodType.WEEKLY);
    }

    @Test
    public void testGetAccountPerformancePeriodForLineGraph_Monthly() {

        WrapAccountPerformanceImpl performance = new WrapAccountPerformanceImpl();
        performance.setMonthlyPerformanceData(new ArrayList<Performance>());

        PerformancePeriodType periodType = PerformanceReportUtil.getAccountPerformancePeriodForLineGraph(performance);
        Assert.assertEquals(periodType, PerformancePeriodType.MONTHLY);
    }

    @Test
    public void testGetPerformanceData() {
        List<Performance> monthlyPerformanceData = PerformanceReportUtil.getPerformanceData(PerformancePeriodType.MONTHLY,
                accountPerformance);
        Assert.assertEquals(3, monthlyPerformanceData.size());
        List<Performance> weeklyPerformanceData = PerformanceReportUtil.getPerformanceData(PerformancePeriodType.WEEKLY,
                accountPerformance);
        Assert.assertEquals(4, weeklyPerformanceData.size());
        List<Performance> dailyPerformanceData = PerformanceReportUtil.getPerformanceData(PerformancePeriodType.DAILY,
                accountPerformance);
        Assert.assertEquals(5, dailyPerformanceData.size());
        List<Performance> quarterlyPerformanceData = PerformanceReportUtil.getPerformanceData(PerformancePeriodType.QUARTERLY,
                accountPerformance);
        Assert.assertEquals(2, quarterlyPerformanceData.size());
        List<Performance> yearlyPerformanceData = PerformanceReportUtil.getPerformanceData(PerformancePeriodType.YEARLY,
                accountPerformance);
        Assert.assertEquals(1, yearlyPerformanceData.size());
    }

    @Test
    public void testGetPeriodFirstDay() {
        Assert.assertEquals(DateTime.parse("2015-05-01"),
                PerformanceReportUtil.getPeriodFirstDay(PerformancePeriodType.MONTHLY, DateTime.parse("2015-05-31")));
        Assert.assertEquals(DateTime.parse("2015-05-25"),
                PerformanceReportUtil.getPeriodFirstDay(PerformancePeriodType.WEEKLY, DateTime.parse("2015-05-31")));
        Assert.assertEquals(DateTime.parse("2015-05-31"),
                PerformanceReportUtil.getPeriodFirstDay(PerformancePeriodType.DAILY, DateTime.parse("2015-05-31")));
        Assert.assertEquals(DateTime.parse("2015-04-01"),
                PerformanceReportUtil.getPeriodFirstDay(PerformancePeriodType.QUARTERLY, DateTime.parse("2015-05-30")));
        Assert.assertEquals(DateTime.parse("2010-01-01"),
                PerformanceReportUtil.getPeriodFirstDay(PerformancePeriodType.YEARLY, DateTime.parse("2010-05-31")));
    }
}
