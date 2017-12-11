package com.bt.nextgen.reports.account.cgt.v2;

import com.bt.nextgen.api.cgt.model.CgtSecurity;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import javax.validation.constraints.NotNull;

public class CgtSecurityData {
    private CgtSecurity cgtSecurity;

    public CgtSecurityData(@NotNull CgtSecurity cgtSecurity) {
        this.cgtSecurity = cgtSecurity;
    }

    public Object getKey() {
        return null;
    }

    public CgtSecurity getCgtSecurity() {
        return cgtSecurity;
    }

    public String getSecurityCode() {
        return cgtSecurity.getSecurityCode();
    }

    public String getSecurityName() {
        return cgtSecurity.getSecurityName();
    }

    public String getSecurityType() {
        return cgtSecurity.getSecurityType();
    }

    public String getAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cgtSecurity.getAmount());
    }

    public String getDaysHeld() {
        return ReportFormatter.format(ReportFormat.INTEGER, cgtSecurity.getDaysHeld());
    }

    public String getQuantity() {
        return ReportFormatter.format(ReportFormat.UNITS, cgtSecurity.getQuantity());
    }

    public String getDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, cgtSecurity.getDate());
    }

    public String getTaxDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, cgtSecurity.getTaxDate());
    }

    public String getTaxAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cgtSecurity.getTaxAmount());
    }

    public String getCostBase() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cgtSecurity.getCostBase());
    }

    public String getCalculatedCost() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cgtSecurity.getCalculatedCost());
    }

    public String getCostCode() {
        return cgtSecurity.getCostCode();
    }

    public String getGrossGain() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cgtSecurity.getGrossGain());
    }

    public String getCalculatedGain() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cgtSecurity.getCalculatedGain());
    }

    public String getIndexedCostBase() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cgtSecurity.getIndexedCostBase());
    }

    public String getReducedCostBase() {

        return ReportFormatter.format(ReportFormat.CURRENCY, cgtSecurity.getReducedCostBase());
    }

    public String getCostBaseGain() {
        return ReportFormatter.format(ReportFormat.CURRENCY, cgtSecurity.getCostBaseGain());
    }

}
