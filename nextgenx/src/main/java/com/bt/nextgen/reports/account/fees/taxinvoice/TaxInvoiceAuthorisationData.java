package com.bt.nextgen.reports.account.fees.taxinvoice;

import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDetailsDto;
import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TaxInvoiceAuthorisationData {
    private String reportTitle;
    private String accountId;
    private String endDate;
    private String startDate;
    private String dealerGroupName;
    private String dealerGroupAbn;
    private BigDecimal totalFeeExcludingGst;
    private BigDecimal totalFeeIncludingGst;
    private BigDecimal totalGst;
    private List<TaxInvoiceData> taxInvoiceData;

    private static final String TAX_INVOICE_TITLE = "Tax invoice";
    private static final String ADJUSTMENT_NOTE_TITLE = "Adjustment note";

    public TaxInvoiceAuthorisationData(TaxInvoiceDto dto) {
        this.accountId = dto.getKey().getAccountId();
        this.endDate = ReportFormatter.format(ReportFormat.SHORT_DATE, dto.getKey().getEndDate());
        this.startDate = ReportFormatter.format(ReportFormat.SHORT_DATE, dto.getKey().getStartDate());

        addTaxInvoiceData(dto);
        addDynamicReportTitle();
    }

    /**
     * Constructor used for v1_TaxInvoiceData.
     * 
     * @param startDate
     * @param endDate
     * @param totalFeeExcludingGst
     * @param totalFeeIncludeGst
     * @param totalGst
     * @param taxInvoiceData
     */
    public TaxInvoiceAuthorisationData(String startDate, String endDate, BigDecimal totalFeeExcludingGst,
            BigDecimal totalFeeIncludeGst, BigDecimal totalGst, List<TaxInvoiceData> taxInvoiceData) {

        this.endDate = endDate;
        this.startDate = startDate;
        this.totalFeeExcludingGst = totalFeeExcludingGst;
        this.totalFeeIncludingGst = totalFeeIncludeGst;
        this.totalGst = totalGst;
        this.taxInvoiceData = taxInvoiceData;
        this.reportTitle = TAX_INVOICE_TITLE;
    }

    private void addTaxInvoiceData(TaxInvoiceDto dto) {
        taxInvoiceData = new ArrayList<>();

        if (dto.getTaxInvoiceDetails() != null) {
            totalFeeExcludingGst = BigDecimal.ZERO;
            totalFeeIncludingGst = BigDecimal.ZERO;
            totalGst = BigDecimal.ZERO;

            for (TaxInvoiceDetailsDto invDetails : dto.getTaxInvoiceDetails()) {
                taxInvoiceData.add(new TaxInvoiceData(invDetails));
                totalFeeExcludingGst = totalFeeExcludingGst.add(invDetails.getFeeDto().getFeeExcludingGST());
                totalFeeIncludingGst = totalFeeIncludingGst.add(invDetails.getFeeDto().getFeeIncludingGST());
                totalGst = totalGst.add(invDetails.getFeeDto().getGst());

                dealerGroupAbn = invDetails.getAbn();
                dealerGroupName = invDetails.getInvestmentManagerName();
            }
        }
    }

    private void addDynamicReportTitle() {
        if (totalFeeIncludingGst != null && totalFeeIncludingGst.negate().compareTo(BigDecimal.ZERO) < 0) {
            this.reportTitle = ADJUSTMENT_NOTE_TITLE;
        } else {
            this.reportTitle = TAX_INVOICE_TITLE;
        }
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public List<TaxInvoiceData> getTaxInvoiceData() {
        return taxInvoiceData;
    }

    public String getTotalFeeExcludingGstAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, totalFeeExcludingGst.negate());
    }

    public String getTotalFeeIncludingGstAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, totalFeeIncludingGst.negate());
    }

    public String getTotalGstAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, totalGst.negate());
    }

    public String getDealerGroupName() {
        return dealerGroupName;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

    public String getDealerGroupAbn() {
        return dealerGroupAbn;
    }

    public void setDealerGroupAbn(String dealerGroupAbn) {
        this.dealerGroupAbn = dealerGroupAbn;
    }
}

