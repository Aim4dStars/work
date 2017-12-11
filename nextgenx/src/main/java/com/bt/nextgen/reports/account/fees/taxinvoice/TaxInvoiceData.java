package com.bt.nextgen.reports.account.fees.taxinvoice;

import com.bt.nextgen.api.fees.v2.model.FeeDto;
import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDetailsDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class TaxInvoiceData {
    private DateTime feeDate;
    private String description;
    private String ipsName;
    private BigDecimal feeExcludingGst;
    private BigDecimal gst;
    private BigDecimal feeIncludingGst;

    public TaxInvoiceData(DateTime feeDate, String description, BigDecimal feeExcludingGet, BigDecimal gst,
            BigDecimal feeIncludingGst) {
        this.feeDate = feeDate;
        this.description = description;
        this.feeExcludingGst = feeExcludingGet;
        this.feeIncludingGst = feeIncludingGst;
        this.gst = gst;
    }

    public TaxInvoiceData(TaxInvoiceDetailsDto invDetails) {
        this.feeDate = invDetails.getFeeDate();
        this.description = invDetails.getDescription();
        this.ipsName = invDetails.getIpsName();
        addFeeDetails(invDetails.getFeeDto());
    }

    private void addFeeDetails(FeeDto feeDto) {
        this.feeExcludingGst = BigDecimal.ZERO;
        this.gst = BigDecimal.ZERO;
        this.feeIncludingGst = BigDecimal.ZERO;
        if (feeDto != null) {
            this.feeExcludingGst = feeDto.getFeeExcludingGST();
            this.feeIncludingGst = feeDto.getFeeIncludingGST();
            this.gst = feeDto.getGst();
        }
    }

    public String getFeeDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, feeDate);
    }

    public String getDescription() {
        return description;
    }

    public String getFeeExcludingGstAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, feeExcludingGst.negate());
    }

    public String getGstAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, gst.negate());
    }

    public String getFeeIncludingGstAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, feeIncludingGst.negate());
    }

    public String getIpsName() {
        return ipsName;
    }
}
