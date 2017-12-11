package com.bt.nextgen.reports.account.allocation.sector;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.core.reporting.charts.BTChartTheme;
import com.bt.nextgen.core.reporting.charts.PieChartLabelGenerator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.RingPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;

import java.awt.Color;
import java.util.List;

public class AssetSectorReportChart {

    public static JFreeChart createChart(List<AllocationBySectorDto> sectorData) {

        DefaultPieDataset dataset = new DefaultPieDataset();
        if (sectorData != null) {
            for (AllocationBySectorDto sector : sectorData) {
                dataset.setValue(sector.getName(), sector.getAllocationPercentage());
            }
        }

        JFreeChart chart = ChartFactory.createRingChart(null, dataset, true, false, false);
        
        RingPlot plot = (RingPlot) chart.getPlot();
        plot.setSectionDepth(0.65);
        plot.setCircular(true);
        plot.setLabelGenerator(null);
        plot.setBackgroundPaint(null);
        plot.setShadowPaint(null);
        plot.setSectionOutlinesVisible(false);
        plot.setSeparatorsVisible(false);
        plot.setInteriorGap(0.1);

        for (AllocationBySectorDto sector : sectorData) {
            plot.setSectionOutlinePaint(sector.getName(), Color.WHITE);
        }
        
        // Use the standard report number format for labels
        plot.setLegendLabelGenerator(new PieChartLabelGenerator());
        
        LegendTitle legend = chart.getLegend();
        legend.setBorder(0, 0, 0, 0);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setVerticalAlignment(VerticalAlignment.CENTER);
        legend.setItemLabelPadding(new RectangleInsets(3, 3, 3, 40));

        // Standard colours and styles
        BTChartTheme.applyTheme(chart);

        return chart;
    }
}
