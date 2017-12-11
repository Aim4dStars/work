package com.bt.nextgen.reports.fees.taxinvoice.recipientcreated;

import net.sf.jasperreports.engine.Renderable;

import org.joda.time.DateTime;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class FooterReportData {
    private DateTime generationDate;
    private Renderable iconBusinessSupport;
    private Renderable footerBackgroundPortrait;
    private String businessSupportContact;
    private String businessSupportName;
    private String reportTitle;
    private String reportInformation;

    public FooterReportData(Renderable iconBusinessSupport, Renderable footerBackgroundPortrait, String businessSupportContact,
            String businessSupportName, String reportTitle, String reportInformation) {
        this.generationDate = new DateTime();
        this.iconBusinessSupport = iconBusinessSupport;
        this.footerBackgroundPortrait = footerBackgroundPortrait;
        this.businessSupportContact = businessSupportContact;
        this.businessSupportName = businessSupportName;
        this.reportTitle = reportTitle;
        this.reportInformation = reportInformation;
    }

    public String getReportGeneration() {
        return reportTitle + " created " + ReportFormatter.format(ReportFormat.LONG_DATE, generationDate);
    }

    public String getReportInformation() {
        return reportInformation;
    }

    public Renderable getIconBusinessSupport() {
        return iconBusinessSupport;
    }

    public Renderable getFooterBackgroundPortrait() {
        return footerBackgroundPortrait;
    }

    public String getBusinessSupportName() {
        return businessSupportName;
    }

    public String getBusinessSupportContact() {
        return businessSupportContact;
    }


}
