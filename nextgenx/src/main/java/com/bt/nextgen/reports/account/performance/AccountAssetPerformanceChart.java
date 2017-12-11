package com.bt.nextgen.reports.account.performance;

import java.awt.BasicStroke;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.VerticalAlignment;
import org.joda.time.DateTime;

import com.bt.nextgen.api.portfolio.v3.model.DateValueDto;
import com.bt.nextgen.api.portfolio.v3.model.DatedAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountBenchmarkPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountPerformanceDto;
import com.bt.nextgen.api.portfolio.v3.model.performance.PerformanceSummaryDto;
import com.bt.nextgen.core.reporting.charts.BTChartThemeV2;
import com.bt.nextgen.core.reporting.charts.MappedDateAxis;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;

@SuppressWarnings({ "squid:S1172", "squid:S1188", "squid:S1200", "squid:S881" })
public class AccountAssetPerformanceChart extends AbstractPercentagePerformanceChart {
    private AccountPerformanceDto accountPerformanceDto;
    private PerformanceSummaryDto<DatedAccountKey> inceptionDataDto;
    private List<AccountBenchmarkPerformanceDto> benchmarks;

    public AccountAssetPerformanceChart(AccountPerformanceDto accountPerformanceDto,
            PerformanceSummaryDto<DatedAccountKey> inceptionDataDto, List<AccountBenchmarkPerformanceDto> benchmarks) {
        this.accountPerformanceDto = accountPerformanceDto;
        this.inceptionDataDto = inceptionDataDto;
        this.benchmarks = benchmarks;
    }

    public JFreeChart createChart() {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(null, null, null, null);
        XYPlot plot = chart.getXYPlot();
        int series = 0;

        createCumulativeReturn(plot, series++);
        for (AccountBenchmarkPerformanceDto benchmark : benchmarks) {
            createBenchmarkReturn(benchmark, plot, series++);
        }

        createTotalReturn(plot, series++);

        LegendItemCollection legendItemCollection = getLegendItemCollection(benchmarks);
        plot.setFixedLegendItems(legendItemCollection);
        configureChartAxis(plot);
        configureChartLegend(chart);
        BTChartThemeV2.applyTheme(chart);

        return chart;
    }

    public LegendItemCollection getLegendItemCollection(List<AccountBenchmarkPerformanceDto> benchmarks) {
        LegendBuilder legendBuilder = new LegendBuilder();
        legendBuilder.addCircleLegend("Accumulated performance", BTChartThemeV2.PAINT_PLOT_LINE_COLORS.get(0), false);
        legendBuilder.addCircleLegend("Total return", BTChartThemeV2.PAINT_BAR, false);

        if (!benchmarks.isEmpty()) {
            legendBuilder.addLineLegend(BTChartThemeV2.SEPARATOR);
        }

        for (int index = 0; index < benchmarks.size(); index++) {
            legendBuilder.addCircleLegend(benchmarks.get(index).getBenchmarkName(),
                    BTChartThemeV2.PAINT_PLOT_LINE_COLORS.get(index + 1), true);
        }

        return legendBuilder.getLegendItemCollection();
    }

    private void createTotalReturn(XYPlot plot, int series) {
        TimeSeries timeSeries = new TimeSeries("Total return");

        for (DateValueDto dto : accountPerformanceDto.getPeriodPerformance()) {
            timeSeries.addOrUpdate(createGraphDate(accountPerformanceDto.getSummaryPeriodType(), dto),
                    percentValue(dto.getValue()));
        }

        plot.setDataset(series, new TimeSeriesCollection(timeSeries));

        XYBarRenderer renderer = new XYBarRenderer(accountPerformanceDto.getPeriodPerformance().size());
        renderer.setSeriesPaint(0, BTChartThemeV2.PAINT_BAR);
        renderer.setShadowVisible(false);
        renderer.setMargin(0.6);
        plot.setRenderer(series, renderer);
    }

    private void createCumulativeReturn(XYPlot plot, int series) {
        TimeSeries timeSeries = new TimeSeries("Accumulated performance");
        for (DateValueDto dto : accountPerformanceDto.getCumulativePerformance()) {
            timeSeries.add(createGraphDate(accountPerformanceDto.getDetailedPeriodType(), dto), percentValue(dto.getValue()));
        }
        plot.setDataset(series, new TimeSeriesCollection(timeSeries));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(series, BTChartThemeV2.PAINT_PLOT_LINE_COLORS.get(series));
        renderer.setSeriesStroke(series, BTChartThemeV2.STROKE_LINE);
        plot.setRenderer(series, renderer);
    }

    private void createBenchmarkReturn(AccountBenchmarkPerformanceDto benchmark, XYPlot plot, int series) {
        TimeSeries timeSeries = new TimeSeries(benchmark.getBenchmarkName());
        for (DateValueDto dto : benchmark.getBenchmarkData()) {
            timeSeries.add(createGraphDate(benchmark.getPeriodType(), dto), percentValue(dto.getValue()));
        }
        plot.setDataset(series, new TimeSeriesCollection(timeSeries));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, BTChartThemeV2.PAINT_PLOT_LINE_COLORS.get(series));
        float[] dashingPattern2 = { 2f, 0f, 2f };
        renderer.setSeriesStroke(0,
                new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dashingPattern2, 0.0f));
        plot.setRenderer(series, renderer);
    }

    private RegularTimePeriod createGraphDate(PerformancePeriodType periodType, DateValueDto dto) {
        RegularTimePeriod result;
        switch (periodType) {
            case DAILY:
                result = new Day(dto.getDate().toDate());
                break;
            case WEEKLY:
                result = new Week(dto.getDate().dayOfWeek().withMinimumValue().toDate());
                break;
            case MONTHLY:
            result = new Month(dto.getDate().dayOfMonth().withMinimumValue().toDate());
            break;
            case QUARTERLY:
            result = new Month(getStartOfQuarter(dto.getDate().dayOfMonth().withMinimumValue().plusDays(15)).plusMonths(1)
                    .toDate());
                break;
            case YEARLY:
                result = new Year(dto.getDate().dayOfYear().withMinimumValue().toDate());
                break;
            default:
                result = new Day(dto.getDate().toDate());
                break;
        }
        return result;
    }

    private DateTime getMaxDate() {
        PerformancePeriodType periodType = accountPerformanceDto.getSummaryPeriodType();

        DateTime periodEnd = accountPerformanceDto.getKey().getEndDate();

        if (PerformancePeriodType.WEEKLY.equals(periodType)) {
            periodEnd = periodEnd.dayOfWeek().withMaximumValue();
        } else if (PerformancePeriodType.MONTHLY.equals(periodType) || PerformancePeriodType.QUARTERLY.equals(periodType)) {
            periodEnd = periodEnd.dayOfMonth().withMaximumValue();
        } else if (PerformancePeriodType.YEARLY.equals(periodType)) {
            periodEnd = periodEnd.dayOfYear().withMaximumValue();
        }
        return periodEnd;
    }

    private DateTime getStartOfQuarter(DateTime dateTime) {
        int month = dateTime.getMonthOfYear() - 1;
        return dateTime.withMonthOfYear(month - (month % 3) + 1);
    }

    private static final BigDecimal percentScale = BigDecimal.valueOf(100);

    private static double percentValue(BigDecimal value) {
        return value.multiply(percentScale).doubleValue();
    }


    private void configureChartAxis(XYPlot plot) {
        // Range axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        DecimalFormat rangeFormat = getRangeFormat(plot);
        rangeAxis.setNumberFormatOverride(rangeFormat);
        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setTickMarksVisible(false);
        rangeAxis.setAxisLineStroke(BTChartThemeV2.STROKE_LINE);

        // Domain axis
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        // Replace domain tick labels with more appropriate text
        MappedDateAxis customDateAxis = new MappedDateAxis(axis.getLabel(), axis.getTimeZone(), axis.getLocale());
        customDateAxis.setLabelList(accountPerformanceDto.getColHeaders());


        // Force chart range to be the entire requested range
        customDateAxis.setRange(getMinDate().toDate(), getMaxDate().toDate());

        customDateAxis.setTickUnit(getDateTickUnit(accountPerformanceDto.getSummaryPeriodType()));
        customDateAxis.setTickMarksVisible(false);
        if (accountPerformanceDto.getSummaryPeriodType() == PerformancePeriodType.WEEKLY) {
            // start of week definitions different between joda and jfreechart
            customDateAxis.setFirstDateTick(getMinDate().plusDays(2));
        } else if (accountPerformanceDto.getSummaryPeriodType() == PerformancePeriodType.QUARTERLY) {
            customDateAxis.setFirstDateTick(getMinDate().plusMonths(1).plusDays(15));
        }
        customDateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        plot.setDomainAxis(customDateAxis);
        plot.setRangeGridlineStroke(new BasicStroke(0.5f));
        plot.setDomainGridlinesVisible(false);
    }

    private DateTime getMinDate() {
        PerformancePeriodType periodType = accountPerformanceDto.getSummaryPeriodType();
        DateTime periodStart = accountPerformanceDto.getKey().getStartDate();

        if (PerformancePeriodType.WEEKLY.equals(periodType)) {
            periodStart = periodStart.dayOfWeek().withMinimumValue();
        } else if (PerformancePeriodType.MONTHLY.equals(periodType)) {
            periodStart = periodStart.dayOfMonth().withMinimumValue();
        } else if (PerformancePeriodType.QUARTERLY.equals(periodType)) {
            periodStart = getStartOfQuarter(periodStart.dayOfMonth().withMinimumValue());
        } else if (PerformancePeriodType.YEARLY.equals(periodType)) {
            periodStart = periodStart.dayOfYear().withMinimumValue();
        }
        return periodStart;
    }

    private DateTickUnit getDateTickUnit(PerformancePeriodType periodType) {
        DateTickUnit unit = null;

        if (PerformancePeriodType.DAILY == periodType) {
            unit = new DateTickUnit(DateTickUnitType.DAY, 1);
        } else if (PerformancePeriodType.WEEKLY == periodType) {
            unit = new DateTickUnit(DateTickUnitType.DAY, 7);
        } else if (PerformancePeriodType.MONTHLY == periodType) {
            unit = new DateTickUnit(DateTickUnitType.MONTH, 1);
        } else if (PerformancePeriodType.QUARTERLY == periodType) {
            unit = new DateTickUnit(DateTickUnitType.MONTH, 3);
        } else if (PerformancePeriodType.YEARLY == periodType) {
            unit = new DateTickUnit(DateTickUnitType.YEAR, 1);
        }

        return unit;
    }


    private void configureChartLegend(JFreeChart chart) {
        LegendTitle legend = chart.getLegend();
        legend.setBorder(0, 0, 0, 0);
        legend.setVerticalAlignment(VerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(HorizontalAlignment.LEFT);
        legend.setMargin(10, 40, 10, 40);
        legend.setItemFont(new Font("Helvetica", Font.PLAIN, 8));
    }

}