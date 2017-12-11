package com.bt.nextgen.core.reporting;

public enum ReportFormat {
    CURRENCY("$#,##0.00", "$"),
    LARGE_CURRENCY("$#,##0", "$"),
    LS_PRICE("$#,##0.000", "$"),
    UNITS("#,##0"),
    MANAGED_FUND_PRICE("$#,##0.0000", "$"),
    MANAGED_FUND_UNIT("#,##0.0000"),
    PERCENTAGE("#,##0.00%", "%"),
    PERCENTAGE_PRECISE("#,##0.000%", "%"),
    INTEGER("#,##0"),
    SHORT_DATE("dd MMM yyyy"),
    MEDIUM_DATE("dd MMMMM yyyy"),
    LONG_DATE("dd MMM yyyy, hh:mm a"),
    MONTH_DATE("MMM yyyy");

    private String format;
    private String symbol;

    private ReportFormat(String format) {
        this.format = format;
        symbol = "";
    }

    private ReportFormat(String format, String symbol) {
        this.format = format;
        this.symbol = symbol;
    }

    public String getFormat() {
        return format;
    }

    public String getFormatWithoutSymbol() {
        if (symbol == null) {
            return format;
        }
        return format.replaceAll(symbol, "");
    }
}
