package com.bt.nextgen.core.reporting;

import net.sf.jasperreports.engine.JasperPrint;

public class JasperPrintResult {
    private ReportIdentity reportIdentity;
    private JasperPrint jasperPrint;

    public JasperPrintResult(ReportIdentity reportIdentity, JasperPrint jasperPrint) {
        this.reportIdentity = reportIdentity;
        this.jasperPrint = jasperPrint;
    }

    public ReportIdentity getReportIdentity() {
        return reportIdentity;
    }

    public JasperPrint getJasperPrint() {
        return jasperPrint;
    }
}
