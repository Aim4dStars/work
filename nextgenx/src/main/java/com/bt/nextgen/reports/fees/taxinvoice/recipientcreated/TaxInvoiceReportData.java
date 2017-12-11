package com.bt.nextgen.reports.fees.taxinvoice.recipientcreated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ch.lambdaj.Lambda;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.avaloq.fees.RCTInvoices;
import com.bt.nextgen.service.integration.fees.RCTInvoicesFee;

public class TaxInvoiceReportData {
    private List<RCTInvoices> rctInvoices;
    private List<FeeDetailsReportData> rctInvoiceFees = new ArrayList<>();

    public TaxInvoiceReportData(List<RCTInvoices> rctInvoices, List<RCTInvoicesFee> rctInvoicesFees) {
        this.rctInvoices = rctInvoices;
        for(RCTInvoicesFee rctInvoicesFee: rctInvoicesFees){
            rctInvoiceFees.add(new FeeDetailsReportData(rctInvoicesFee));
        }
    }

    public String getSupplierName() {
        return rctInvoices.get(0).getSupplier();
    }

    public String getSupplierABN() {
        String supplierABN = null;
        if (rctInvoices.get(0).getSupplierABN() != null) {
            supplierABN = "ABN: " + rctInvoices.get(0).getSupplierABN();
        }
        return supplierABN;
    }

    public String getSupplierAddress() {
        return rctInvoices.get(0).getSupplierAddress();
    }

    public String getSupplyDescription() {
        return "Super Advice fees";
    }

    public String getRecipientName() {
        return rctInvoices.get(0).getRecipient();
    }

    public String getRecipientABN() {
        String recipientABN = null;
        if (rctInvoices.get(0).getRecipientABN() != null) {
            recipientABN = "ABN: " + rctInvoices.get(0).getRecipientABN();
        }
        return recipientABN;
    }

    public String getRecipientAddress() {
        return rctInvoices.get(0).getRecipientAddress();
    }

    public List<FeeDetailsReportData> getRCTInvoiceFees(){
        return rctInvoiceFees;
    }

    public String getTotalDescription() {
        return "Total";
    }

    public String getTotalFeeExcludingTax() {
        BigDecimal totalFeeExcludingTax = Lambda.sum(rctInvoices, Lambda.on(RCTInvoices.class).getFeeExcludingGST());
        return ReportFormatter.format(ReportFormat.CURRENCY, totalFeeExcludingTax);
    }

    public String getTotalFeeIncludingTax() {
        BigDecimal totalFeeIncludingTax = Lambda.sum(rctInvoices, Lambda.on(RCTInvoices.class).getFeeIncludingGST());
        return ReportFormatter.format(ReportFormat.CURRENCY, totalFeeIncludingTax);
    }

    public String getTotalTax() {
        BigDecimal totalGST = Lambda.sum(rctInvoices, Lambda.on(RCTInvoices.class).getGST());
        return ReportFormatter.format(ReportFormat.CURRENCY, totalGST);
    }
}
