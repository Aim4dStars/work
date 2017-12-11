package com.bt.nextgen.core.reporting.charts;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;

import java.math.BigDecimal;
import java.text.AttributedString;

public class PieChartLabelGenerator extends StandardPieSectionLabelGenerator {

    @Override
    public String generateSectionLabel(PieDataset dataset, Comparable key) {
        String label = null;
        if (dataset != null) {
            BigDecimal value = (BigDecimal) dataset.getValue(key);
            String formattedValue = ReportFormatter.format(ReportFormat.PERCENTAGE, true, value);
            label = key.toString() + " - " + formattedValue;
        }
        return label;
    }

    @Override
    public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
        return null;
    }
}