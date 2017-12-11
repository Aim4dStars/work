package com.bt.nextgen.reports.account.valuation;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.integration.base.SystemType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class SimpleValuationReportData extends AbstractValuationReportData {
    private String name;
    private BigDecimal rate;
    private BigDecimal balance;
    private BigDecimal allocationPercent;
    private Boolean externalAsset;
    private String source;
    private String thirdPartySource;

    public SimpleValuationReportData(String name, BigDecimal rate, BigDecimal balance, BigDecimal allocationPercent,
            Boolean externalAsset, String source, String thirdPartySource) {
        this.name = name;
        this.rate = rate;
        this.balance = balance;
        this.allocationPercent = allocationPercent;
        this.externalAsset = externalAsset;
        this.source = source;
        this.thirdPartySource = thirdPartySource;
    }

    public String getName() {
        return name;
    }

    public String getRate() {
        BigDecimal interestRate = null;
        if(externalAsset || SystemType.WRAP.name().equals(thirdPartySource)){
            return ReportFormatter.format(ReportFormat.PERCENTAGE, interestRate);
        }
        else {
            interestRate = rate;
            if (interestRate != null) {
                return ReportFormatter.format(ReportFormat.PERCENTAGE, interestRate);
            } else {
                return "";
            }
        }
    }

    public String getBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, balance);
    }

    public String getAllocationPercent() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, allocationPercent);
    }

    public Boolean getExternalAsset() {
        return externalAsset;
    }

    public String getThirdPartySource() {
        return thirdPartySource;
    }

    public String getSource() {
        return source;
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public List<ValuationReportData> getChildren() {
        return Collections.emptyList();
    }
}
