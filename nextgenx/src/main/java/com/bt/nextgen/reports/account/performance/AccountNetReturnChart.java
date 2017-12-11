package com.bt.nextgen.reports.account.performance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.VerticalAlignment;

import com.bt.nextgen.api.performance.model.AccountNetReturnChartDto;
import com.bt.nextgen.core.reporting.charts.BTChartThemeV2;

public class AccountNetReturnChart {
    public JFreeChart createChart(AccountNetReturnChartDto chartDto, String[] seriesNames) {
        JFreeChart chart = ChartFactory.createLineChart(null, null, null,
                createDataset(seriesNames[0], chartDto.getColHeaders(), chartDto.getClosingBalances()),
                PlotOrientation.VERTICAL, true, false, false);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        plot.setDataset(1, createDataset(seriesNames[1], chartDto.getColHeaders(), chartDto.getNetReturns()));
        plot.mapDatasetToRangeAxis(1, 1);

        plot.setRangeGridlineStroke(new BasicStroke(1.0f));
        plot.setDomainGridlinesVisible(false);

        LineAndShapeRenderer closingBalanceRenderer = (LineAndShapeRenderer) plot.getRenderer();
        closingBalanceRenderer.setSeriesPaint(0, BTChartThemeV2.PAINT_PLOT_LINE_COLORS.get(0));
        closingBalanceRenderer.setSeriesStroke(0, BTChartThemeV2.STROKE_LINE);

        BarRenderer barRenderer = new BarRenderer();
        barRenderer.setMaximumBarWidth(0.1D);
        barRenderer.setSeriesPaint(0, BTChartThemeV2.PAINT_BAR);
        barRenderer.setShadowVisible(false);
        plot.setRenderer(1, barRenderer);

        LegendItemCollection legendItemCollection = getLegendItemCollection();
        plot.setFixedLegendItems(legendItemCollection);

        CategoryAxis domainAxis = plot.getDomainAxis();
        NumberAxis closingBalanceAxis = (NumberAxis) plot.getRangeAxis();
        NumberAxis accountReturnAxis = new NumberAxis();
        BTChartThemeV2.applyTheme(chart);
        plot.setRangeAxis(1, accountReturnAxis);
        
        configureChartAxis(domainAxis, closingBalanceAxis, accountReturnAxis);
        configureChartLegend(chart);

        return chart;
    }

    private void configureChartAxis(CategoryAxis domainAxis, NumberAxis closingBalanceAxis, NumberAxis accountReturnAxis) {

        // Domain axis
        domainAxis.setTickMarksVisible(false);
        domainAxis.setAxisLineVisible(false);
        domainAxis.setTickLabelFont(new Font("Helvetica", Font.PLAIN, 8));

        // Range axis
        DecimalFormat rangeFormat = new DecimalFormat("#,###");
        closingBalanceAxis.setNumberFormatOverride(rangeFormat);
        closingBalanceAxis.setAxisLineVisible(false);
        closingBalanceAxis.setTickMarksVisible(false);
        closingBalanceAxis.setUpperMargin(0.05);
        closingBalanceAxis.setLowerMargin(0.20);
        closingBalanceAxis.setTickLabelFont(new Font("Helvetica", Font.PLAIN, 8));
        closingBalanceAxis.setTickLabelPaint(new Color(0x404040));

        accountReturnAxis.setNumberFormatOverride(rangeFormat);
        accountReturnAxis.setAxisLineVisible(false);
        accountReturnAxis.setTickMarksVisible(false);
        accountReturnAxis.setUpperMargin(0.05);
        accountReturnAxis.setLowerMargin(0.20);
        accountReturnAxis.setTickLabelFont(new Font("Helvetica", Font.PLAIN, 8));
        accountReturnAxis.setTickLabelPaint(new Color(0x404040));

    }

    public LegendItemCollection getLegendItemCollection() {
        LegendItemCollection legendItemCollection = new LegendItemCollection();
        // Adding a custom legend for Accumulated performance
        Color accPerformanceColor = BTChartThemeV2.PAINT_PLOT_LINE_COLORS.get(0);
        Ellipse2D accPeformanceCircle = new Ellipse2D.Float(0, 0, 10, 10);
        LegendItem accPerformanceItem = new LegendItem("Closing balance after fees", "Closing balance after fees", "", "",
                accPeformanceCircle,
                accPerformanceColor);
        legendItemCollection.add(accPerformanceItem);

        // Adding a custom legend for Total return
        Color totalReturnColor = BTChartThemeV2.PAINT_BAR;
        Ellipse2D totalReturnCircle = new Ellipse2D.Float(0, 0, 10, 10);
        LegendItem totalReturnItem = new LegendItem("Your account $ return", "Your account $ return", "", "", totalReturnCircle,
                totalReturnColor);
        legendItemCollection.add(totalReturnItem);
        return legendItemCollection;
    }

    private void configureChartLegend(JFreeChart chart) {
        LegendTitle legend = chart.getLegend();

        legend.setBorder(0, 0, 0, 0);
        legend.setVerticalAlignment(VerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(HorizontalAlignment.LEFT);
        legend.setMargin(10, 40, 10, 40);
        legend.setItemFont(new Font("Helvetica", Font.PLAIN, 8));
    }

    private CategoryDataset createDataset(String seriesId, List<String> colHeaders, List<BigDecimal> values) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < colHeaders.size(); i++) {
            if (i < values.size()) {
                if (values.get(i) != null) {
                    dataset.addValue(values.get(i).doubleValue(), seriesId, colHeaders.get(i));
                } else {
                    dataset.addValue(null, seriesId, colHeaders.get(i));
                }
            } 
            else {
                dataset.addValue(0D, seriesId, colHeaders.get(i));
            }
        }

        return dataset;
    }
}
