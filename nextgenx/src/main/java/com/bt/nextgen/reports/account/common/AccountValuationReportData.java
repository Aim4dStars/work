package com.bt.nextgen.reports.account.common;

import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.math.BigDecimal;

public class AccountValuationReportData {

    private BigDecimal internalBalance;
    private BigDecimal externalBalance;
    private BigDecimal totalBalance;

    public AccountValuationReportData(ValuationDto valuationData) {
        this.internalBalance = valuationData.getInternalBalance();
        this.externalBalance = valuationData.getExternalBalance();
        this.totalBalance = valuationData.getBalance();
    }

    public String getInternalBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, true, internalBalance);
    }

    public String getExternalBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, true, externalBalance);
    }

    public String getTotalBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, true, totalBalance);
    }
}
