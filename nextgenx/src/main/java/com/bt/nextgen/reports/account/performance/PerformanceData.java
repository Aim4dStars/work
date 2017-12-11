package com.bt.nextgen.reports.account.performance;

import com.bt.nextgen.api.performance.service.PerformanceReportUtil;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;

public class PerformanceData extends AccountPerformanceTypeData {

    protected enum DisplayMode {
        NORMAL,
        PRECISE
    }

    private DisplayMode displayMode;

    public PerformanceData(List<Performance> performances, PerformancePeriodType periodType, DisplayMode displayMode,
            boolean includeBenchmark) {
        super("Performance");
        this.displayMode = displayMode;
        addColumnHeaders(performances, periodType);
        addTableData(performances, includeBenchmark);
    }

    private void addTableData(List<Performance> performances, boolean includeBenchmark) {
        addRow("Total performance", PerformanceRowData.RowType.DATA,
                extract(performances, on(Performance.class).getPerformance()));
        addRow("Capital return", PerformanceRowData.RowType.SUBDATA,
                extract(performances, on(Performance.class).getCapitalGrowth()));
        addRow("Income return", PerformanceRowData.RowType.SUBDATA, extract(performances, on(Performance.class).getIncomeRtn()));
        if (includeBenchmark) {
            addRow("Selected benchmark", PerformanceRowData.RowType.DATA,
                    extract(performances, on(Performance.class).getBmrkRor()));
            addRow("Active return", PerformanceRowData.RowType.TOTAL,
                    extract(performances, on(Performance.class).getActiveRor()));
        }
    }

    private void addRow(String description, PerformanceRowData.RowType type, List<BigDecimal> values) {
        List<String> formatted = new ArrayList<>();
        formatted.add(description);
        for (BigDecimal value : values) {
            if (value == null) {
                formatted.add(ReportFormatter.format(ReportFormat.PERCENTAGE, value));
            } else {
                if (displayMode == DisplayMode.PRECISE) {
                    formatted.add(ReportFormatter.format(ReportFormat.PERCENTAGE_PRECISE, value.divide(BigDecimal.valueOf(100))));
                } else {
                    formatted.add(ReportFormatter.format(ReportFormat.PERCENTAGE, value.divide(BigDecimal.valueOf(100))));
                }

            }
        }
        addRow(new PerformanceRowData(type, formatted));
    }

    private void addColumnHeaders(List<Performance> performances, PerformancePeriodType periodType) {
        // sublist to leave room for period and inception data
        List<String> columnHeaders = PerformanceReportUtil
                .buildReportColumnHeaders(performances.subList(0, performances.size() - 2), periodType);
        if (columnHeaders != null) {
            addColumnHeader("Your account returns");
            for (String header : columnHeaders) {
                addColumnHeader(header);
            }
            addColumnHeader("Period<br/>return");
            addColumnHeader("Since<br/>inception");
        }
    }

}
