package com.bt.nextgen.reports.account.performance;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.api.performance.service.PerformanceReportUtil;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;

public class NetReturnData extends AccountPerformanceTypeData {

    protected enum DisplayMode {
        FULL,
        TRUNCATED,
        COMPACT
    }

    protected enum ReturnMode {
        ACCOUNT,
        PORTFOLIO
    }

    private DisplayMode displayMode;
    private ReturnMode returnMode;

    public NetReturnData(List<Performance> performances, PerformancePeriodType periodType, boolean includeOtherFees,
            ReturnMode returnMode, DisplayMode displayMode) {
        super("NetReturn");
        this.displayMode = displayMode;
        this.returnMode = returnMode;
        addColumnHeaders(performances, periodType);
        addTableData(performances, includeOtherFees);
    }

    private void addTableData(List<Performance> performances, boolean includeOtherFees) {
        addRow("Opening balance", PerformanceRowData.RowType.DATA,
                extract(performances, on(Performance.class).getOpeningBalance()));
        addRow("Inflows", PerformanceRowData.RowType.SUBDATA, extract(performances, on(Performance.class).getInflows()));
        addRow("Outflows", PerformanceRowData.RowType.SUBDATA, extract(performances, on(Performance.class).getOutflows()));
        addRow("Income", PerformanceRowData.RowType.SUBDATA, extract(performances, on(Performance.class).getIncome()));
        addRow("Expenses", PerformanceRowData.RowType.SUBDATA, extract(performances, on(Performance.class).getExpenses()));

        if (returnMode == ReturnMode.ACCOUNT) {
            addRow("Closing balance before fees", PerformanceRowData.RowType.DATA,
                    extract(performances, on(Performance.class).getClosingBalanceBeforeFee()));
            addRow("Market movement", PerformanceRowData.RowType.DATA, extract(performances, on(Performance.class).getMktMvt()));

            addRow("Fees", PerformanceRowData.RowType.DATA, extract(performances, on(Performance.class).getFee()));
            if (includeOtherFees) {
                addRow("Other fees", PerformanceRowData.RowType.DATA, extract(performances, on(Performance.class).getOtherFee()));
            }
            addRow("Closing balance after fees", PerformanceRowData.RowType.DATA,
                    extract(performances, on(Performance.class).getClosingBalanceAfterFee()));
            addRow("Your account $ return", PerformanceRowData.RowType.DATA,
                    extract(performances, on(Performance.class).getNetGainLoss()));
        } else if (returnMode == ReturnMode.PORTFOLIO) {
            addRow("Market movement", PerformanceRowData.RowType.DATA, extract(performances, on(Performance.class).getMktMvt()));
            addRow("Closing balance", PerformanceRowData.RowType.DATA,
                    extract(performances, on(Performance.class).getClosingBalanceAfterFee()));
            addRow("Your portfolio $ return", PerformanceRowData.RowType.TOTAL,
                    extract(performances, on(Performance.class).getNetGainLoss()));
        }

    }

    private void addRow(String description, PerformanceRowData.RowType type, List<BigDecimal> values) {
        List<String> formatted = new ArrayList<>();
        formatted.add(description);
        for (BigDecimal value : values) {
            if (value == null) {
                formatted.add(ReportFormatter.format(ReportFormat.LARGE_CURRENCY, false, value));
            } else if (displayMode == DisplayMode.TRUNCATED) {
                formatted.add(ReportFormatter.format(ReportFormat.LARGE_CURRENCY, false, value.setScale(0, RoundingMode.DOWN)));
            } else {
                if (displayMode == DisplayMode.COMPACT) {
                    BigDecimal compactValue = value.divide(BigDecimal.valueOf(1000));
                    if (compactValue.compareTo(BigDecimal.valueOf(1000)) >= 0) {
                        formatted.add(ReportFormatter.format(ReportFormat.LARGE_CURRENCY, false,
                                value.setScale(0, RoundingMode.DOWN)));
                    } else {
                        formatted.add(ReportFormatter.format(ReportFormat.CURRENCY, false, value));
                    }
                }else {
                    formatted.add(ReportFormatter.format(ReportFormat.CURRENCY, false, value));
                }
            }

        }
        addRow(new PerformanceRowData(type, formatted));
    }

    private void addColumnHeaders(List<Performance> performances, PerformancePeriodType periodType) {
        // sublist to leave room for period and inception data
        List<String> columnHeaders = PerformanceReportUtil.buildReportColumnHeaders(
                performances.subList(0, performances.size() - 2), periodType);
        if (columnHeaders != null) {
            addColumnHeader("");
            for (String header : columnHeaders) {
                addColumnHeader(header);
            }
            addColumnHeader("Period<br/>return");
            addColumnHeader("Since<br/>inception");
        }
    }

}
