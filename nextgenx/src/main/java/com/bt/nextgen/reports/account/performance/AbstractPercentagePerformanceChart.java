package com.bt.nextgen.reports.account.performance;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

public class AbstractPercentagePerformanceChart {

    public AbstractPercentagePerformanceChart() {
        super();
    }

    private BigDecimal getDataRange(XYPlot plot) {
        double min = 100d;
        double max = -100d;
        for (int i = 0; i < plot.getDatasetCount(); i++) {
            XYDataset ds = plot.getDataset(i);
            for (int j = 0; j < ds.getSeriesCount(); j++) {
                for (int k = 0; k < ds.getItemCount(j); k++) {
                    double value = ds.getYValue(j, k);
                    if (value < min) {
                        min = value;
                    }
                    if (value > max) {
                        max = value;
                    }
                }
            }
        }
        return BigDecimal.valueOf(max - min);
    }

    DecimalFormat getRangeFormat(XYPlot plot) {
        StringBuilder formatString = new StringBuilder();
        BigDecimal range = getDataRange(plot);
        int decimals = ((int) (Math.floor(Math.log10(range.doubleValue())) - 1)) * -1;
        if (decimals <= 0) {
            return new DecimalFormat("0'%'");
        } else {
            for (int i = 0; i < decimals; i++) {
                formatString = formatString.append("0");
            }
            return new DecimalFormat("0." + formatString.toString() + "'%'");
        }
    }

}