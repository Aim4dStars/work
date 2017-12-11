package com.bt.nextgen.reports.account.allocation.sector;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class AllocationReportData {

    private static final String SEPARATOR = " &#183 ";

    private String name;
    private String code;
    private String assetType;
    private String source;
    private BigDecimal units;
    private BigDecimal balance;
    private BigDecimal allocationPercentage;
    private Boolean isPending;
    private Boolean isExternal;

    public AllocationReportData(AssetAllocationBySectorDto allocation) {
        this.name = allocation.getName();
        this.code = allocation.getAssetCode();
        this.assetType = allocation.getAssetType();
        this.source = allocation.getSource();
        this.units = allocation.getUnits();
        this.balance = allocation.getBalance();
        this.allocationPercentage = allocation.getAllocationPercentage();
        this.isPending = allocation.getPending();
        this.isExternal = allocation.getIsExternal();
    }

    public String getExternalAssetTitle() {
        StringBuilder title = new StringBuilder();

        if (StringUtils.isNotBlank(source)) {
            title.append(source);
            title.append("<br/>");
        }

        if (StringUtils.isNotBlank(code)) {
            title.append(code);
            title.append(SEPARATOR);
        }

        title.append(name);

        return title.toString();
    }

    public String getAssetTitle() {
        StringBuilder title = new StringBuilder();

        if (StringUtils.isNotBlank(code)) {
            title.append("<b>");
            title.append(code);
            title.append("</b>");
            title.append(SEPARATOR);
        }

        title.append(name);

        return title.toString();
    }

    public String getUnits() {
        if (AssetType.MANAGED_FUND.name().equals(assetType)) {
            if (isPending) {
                return "-";
            }
            return ReportFormatter.format(ReportFormat.MANAGED_FUND_UNIT, false, units);
        }
        return ReportFormatter.format(ReportFormat.UNITS, false, units);
    }

    public String getBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, true, balance);
    }

    public String getAllocationPercentage() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, true, allocationPercentage);
    }

    public Boolean getIsExternal() {
        return isExternal;
    }
}

