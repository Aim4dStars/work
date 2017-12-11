package com.bt.nextgen.reports.account.allocation.sector;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class AssetSectorReportData {

    private String sectorName;
    private BigDecimal sectorBalance;
    private BigDecimal sectorAllocationPercentage;
    private List<AllocationGroupReportData> allocationGroups;

    public AssetSectorReportData(AllocationBySectorDto allocation, List<AllocationGroupReportData> allocationGroups) {
        this.sectorName = allocation.getName();
        this.sectorBalance = allocation.getBalance();
        this.sectorAllocationPercentage = allocation.getAllocationPercentage();
        this.allocationGroups = allocationGroups;
    }

    public String getSectorName() {
        return sectorName;
    }

    public String getSectorBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, true, sectorBalance);
    }

    public String getSectorAllocationPercentage() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, true, sectorAllocationPercentage);
    }

    public Boolean getIsCashSector() {
        return "Cash".equalsIgnoreCase(sectorName);
    }

    public List<AllocationGroupReportData> getAllocationGroups() {
        return allocationGroups;
    }
}
