package com.bt.nextgen.api.fees.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.taxinvoice.TaxAdviserDetails;

public class TaxInvoiceDto extends BaseDto {

    private String startDate;

    private String endDate;

    private String dealerGroupName;

    private String dealerGroupABN;

    private TaxAdviserDetails adviserDetails;

    private String reportTypeTitle;

    public String getReportTypeTitle() {
        return reportTypeTitle;
    }

    public void setReportTypeTitle(String reportTypeTitle) {
        this.reportTypeTitle = reportTypeTitle;
    }

    public String getDealerGroupName() {
        return dealerGroupName;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

    public String getDealerGroupABN() {
        return dealerGroupABN;
    }

    public void setDealerGroupABN(String dealerGroupABN) {
        this.dealerGroupABN = dealerGroupABN;
    }

    public TaxAdviserDetails getAdviserDetails() {
        return adviserDetails;
    }

    public void setAdviserDetails(TaxAdviserDetails adviserDetails) {
        this.adviserDetails = adviserDetails;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}