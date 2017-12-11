package com.bt.nextgen.reports.account.investmentorders.ordercapture;

import java.math.BigDecimal;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class SlidingScaleTierData {
    private BigDecimal lowerBound;
    private BigDecimal upperBound;
    private BigDecimal rate;

    public SlidingScaleTierData(BigDecimal lowerBound, BigDecimal upperBound, BigDecimal rate) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.rate = rate;
    }

    public String getTier() {
        if (upperBound != null) {
            return ReportFormatter.format(ReportFormat.LARGE_CURRENCY, lowerBound) + " - "
                + ReportFormatter.format(ReportFormat.LARGE_CURRENCY, upperBound);
        } else {
            return ReportFormatter.format(ReportFormat.LARGE_CURRENCY, lowerBound) + " and higher";
        }
    }

    public String getRate() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, rate);
    }
}
