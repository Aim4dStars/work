package com.bt.nextgen.reports.fees.taxinvoice.recipientcreated;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.integration.fees.RCTInvoicesFee;

public class FeeDetailsReportData {
    private RCTInvoicesFee rctInvoicesFee;

    public FeeDetailsReportData(RCTInvoicesFee rctInvoicesFee) {
        this.rctInvoicesFee = rctInvoicesFee;
    }

    public String getFeeDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, rctInvoicesFee.getInvoiceDate());

    }

    public String getDescription() {
        String description = null;
        if (rctInvoicesFee.getOrderType() != null) {
            description = rctInvoicesFee.getOrderType().getDisplayName();
        }
        return description;
    }

    public String getFeeExcludingTax() {
        return ReportFormatter.format(ReportFormat.CURRENCY, rctInvoicesFee.getFeeExcludingGST());
    }

    public String getTax() {
        return ReportFormatter.format(ReportFormat.CURRENCY, rctInvoicesFee.getGST());
    }

    public String getFeeIncludingTax() {
        return ReportFormatter.format(ReportFormat.CURRENCY, rctInvoicesFee.getFeeIncludingGST());
    }

}
