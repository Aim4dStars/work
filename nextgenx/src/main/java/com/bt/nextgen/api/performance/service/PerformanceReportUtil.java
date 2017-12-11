package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceReportUtil {

    public static String INCOMPLETE_PREFIX = "^";

    public static boolean isCompleteWeek(DateTime start, DateTime end) {
        if (Days.daysBetween(start, end).getDays() >= 7) {
            return false;
        }

        DateTime wk1 = PerformanceReportUtil.getStartOfDay(start.dayOfWeek().withMinimumValue());
        DateTime wk2 = PerformanceReportUtil.getEndOfDay(start.dayOfWeek().withMaximumValue());
        return (Days.daysBetween(wk1, start).getDays() == 0 && Days.daysBetween(wk2, end).getDays() == 0);
    }

    public static boolean isCompleteMonth(DateTime start, DateTime end) {
        if (Months.monthsBetween(start, end).getMonths() > 1) {
            return false;
        }

        DateTime m1 = getStartOfDay(start.dayOfMonth().withMinimumValue());
        DateTime m2 = getEndOfDay(start.dayOfMonth().withMaximumValue());
        return (Days.daysBetween(m1, start).getDays() == 0 && Days.daysBetween(m2, end).getDays() == 0);
    }

    public static boolean isCompleteYear(DateTime start, DateTime end) {
        if (Years.yearsBetween(start, end).getYears() > 1) {
            return false;
        }

        DateTime y1 = getStartOfDay(start.monthOfYear().withMinimumValue().dayOfMonth().withMinimumValue());
        DateTime y2 = getEndOfDay(start.monthOfYear().withMaximumValue().dayOfMonth().withMaximumValue());
        return (Days.daysBetween(y1, start).getDays() == 0 && Days.daysBetween(y2, end).getDays() == 0);
    }

    public static boolean isCompleteQuarter(DateTime start, DateTime end) {
        int quarter = getCurrentQuarter(start);
        DateTime startOfQuarter = getQuarterFirstDay(quarter, start);
        DateTime endOfQuarter = getEndOfDay(startOfQuarter.plusMonths(2).dayOfMonth().withMaximumValue());

        return (Days.daysBetween(startOfQuarter, start).getDays() == 0 && Days.daysBetween(endOfQuarter, end).getDays() == 0);
    }

    public static int getCurrentQuarter(DateTime dateTime) {
        return ((dateTime.monthOfYear().get() - 1) / 3) + 1;
    }

    public static DateTime getQuarterFirstDay(int quarter, DateTime specDate) {
        int month = ((quarter - 1) * 3) + 1;
        DateTime datetime = new DateTime(specDate.year().get(), month, specDate.dayOfMonth().get(), 0, 0);
        return getStartOfDay(datetime.withMonthOfYear(month).dayOfMonth().withMinimumValue());
    }

    public static DateTime getPeriodFirstDay(PerformancePeriodType periodType, DateTime dateTime) {
        DateTime periodStart;

        if (periodType.equals(PerformancePeriodType.WEEKLY)) {
            periodStart = dateTime.withDayOfWeek(1);
        } else if (periodType.equals(PerformancePeriodType.MONTHLY)) {
            periodStart = dateTime.withDayOfMonth(1);
        } else if (periodType.equals(PerformancePeriodType.QUARTERLY)) {
            int currentQuarter = getCurrentQuarter(dateTime);
            periodStart = getQuarterFirstDay(currentQuarter, dateTime);
        } else if (periodType.equals(PerformancePeriodType.YEARLY)) {
            periodStart = dateTime.withDayOfYear(1);
        } else {
            periodStart = dateTime;
        }

        return periodStart;
    }

    public static DateTime getStartOfDay(DateTime dateTime) {
        return dateTime.hourOfDay().withMinimumValue().secondOfDay().withMinimumValue();
    }

    public static DateTime getEndOfDay(DateTime dateTime) {
        return dateTime.hourOfDay().withMaximumValue().secondOfDay().withMaximumValue();
    }

    public static Map<String, PerformancePeriodType> getPerformancePeriod(PeriodicPerformance performance) {
        Boolean hasDaily = performance.getDailyPerformanceData() != null;
        Boolean hasWeekly = performance.getWeeklyPerformanceData() != null;
        Boolean hasMonthly = performance.getMonthlyPerformanceData() != null;
        Boolean hasQuarterly = performance.getQuarterlyPerformanceData() != null;
        Boolean hasYearly = performance.getYearlyPerformanceData() != null;

        List<Boolean> periodOptions = Arrays.asList(hasDaily, hasWeekly, hasMonthly, hasQuarterly, hasYearly);

        int mostDetailed = periodOptions.indexOf(Boolean.TRUE);
        int leastDetailed = periodOptions.lastIndexOf(Boolean.TRUE);
        PerformancePeriodType detailedPeriodType = mostDetailed != -1 ? PerformancePeriodType.values()[mostDetailed] : null;
        PerformancePeriodType summaryPeriodType = leastDetailed != -1 ? PerformancePeriodType.values()[leastDetailed] : null;

        Map<String, PerformancePeriodType> periodTypes = new HashMap<>();
        periodTypes.put("detailedPeriodType", detailedPeriodType);
        periodTypes.put("summaryPeriodType", summaryPeriodType);

        return periodTypes;
    }

    public static List<String> buildReportColumnHeaders(List<Performance> tablePerformanceData, PerformancePeriodType periodType) {
        List<String> columnHeaders = new ArrayList<>();

        int count = 1;
        for (Performance perf : tablePerformanceData) {
            DateTime start = perf.getPeriodSop();
            DateTime end = perf.getPeriodEop();

            String header = "";
            switch (periodType) {
            case WEEKLY:
                // WK1, WK2 etc
                header = "WK" + count;
                if (!PerformanceReportUtil.isCompleteWeek(start, end)) {
                    header += PerformanceReportUtil.INCOMPLETE_PREFIX;
                }
                break;
            case MONTHLY:
                // Jan 14, Feb 14 etc
                header = start.monthOfYear().getAsShortText();
                header += (" " + start.year().getAsText().substring(2, 4));
                if (!PerformanceReportUtil.isCompleteMonth(start, end)) {
                    header += PerformanceReportUtil.INCOMPLETE_PREFIX;
                }
                break;
            case QUARTERLY:
                // Q1 yy, Q2 yy etc
                int quarter = PerformanceReportUtil.getCurrentQuarter(start);
                header = start.year().getAsText();
                header += " Q" + (quarter);
                if (!PerformanceReportUtil.isCompleteQuarter(start, end)) {
                    header += PerformanceReportUtil.INCOMPLETE_PREFIX;
                }
                break;
            case YEARLY:
                // YYYY
                header = start.year().getAsText();
                if (!PerformanceReportUtil.isCompleteYear(start, end)) {
                    header += PerformanceReportUtil.INCOMPLETE_PREFIX;
                }
                break;
            default:
                header = "-";
                break;
            }
            columnHeaders.add(header);
            count++;
        }

        return columnHeaders;
    }

    public static List<Performance> getPerformanceData(PerformancePeriodType periodType, PeriodicPerformance performance) {
        List<Performance> result = new ArrayList<>();

        if (periodType == null) {
            return new ArrayList<>();
        }

        switch (periodType) {
        case DAILY:
            result = performance.getDailyPerformanceData();
            break;
        case MONTHLY:
            result = performance.getMonthlyPerformanceData();
            break;
        case QUARTERLY:
            result = performance.getQuarterlyPerformanceData();
            break;
        case WEEKLY:
            result = performance.getWeeklyPerformanceData();
            break;
        case YEARLY:
            result = performance.getYearlyPerformanceData();
            break;
        default:
            break;
        }

        return result;
    }
}
