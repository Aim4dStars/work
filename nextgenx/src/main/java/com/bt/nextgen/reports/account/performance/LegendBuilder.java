package com.bt.nextgen.reports.account.performance;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;

import com.bt.nextgen.core.reporting.charts.BTChartThemeV2;

public class LegendBuilder {
    private LegendItemCollection legendItemCollection = new LegendItemCollection();

    public void addCircleLegend(String legendName, Color legendColor, boolean fill) {
        Ellipse2D legendCircle = new Ellipse2D.Float(0, 0, 10, 10);
        if (fill) {
            legendItemCollection.add(new LegendItem(legendName, legendName, "", "", legendCircle, BTChartThemeV2.FILL_COLOR,
                    new BasicStroke(0.5f), legendColor));
        } else {
            legendItemCollection.add(new LegendItem(legendName, legendName, "", "", legendCircle, legendColor));
        }
    }

    public void addLineLegend(Color legendColor) {
        Line2D line = new Line2D.Float(40, 0, 40, 20);
        legendItemCollection.add(new LegendItem("", "", "", "", line, legendColor));
    }

    public LegendItemCollection getLegendItemCollection() {
        return legendItemCollection;
    }
}
