package com.bt.nextgen.reports.account.allocation.sector;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class AllocationGroupReportData {

    private String groupName;
    private Boolean displayGroupDetails;
    private List<AllocationBySectorDto> groupAllocations;

    public AllocationGroupReportData(String groupName, Boolean displayGroupDetails, List<AllocationBySectorDto> groupAllocations) {
        this.groupName = groupName;
        this.displayGroupDetails = displayGroupDetails;
        this.groupAllocations = groupAllocations;
    }

    public String getGroupName() {
        return groupName;
    }

    public Boolean getDisplayGroupDetails() {
        return displayGroupDetails;
    }

    public String getGroupBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        for (AllocationBySectorDto allocation : groupAllocations) {
            balance = balance.add(allocation.getBalance());
        }
        return ReportFormatter.format(ReportFormat.CURRENCY, true, balance);
    }

    public String getGroupAllocationPercentage() {
        BigDecimal percentage = BigDecimal.ZERO;
        for (AllocationBySectorDto allocation : groupAllocations) {
            percentage = percentage.add(allocation.getAllocationPercentage());
        }
        return ReportFormatter.format(ReportFormat.PERCENTAGE, true, percentage);
    }

    public List<AllocationReportData> getAllocations() {
        List<AllocationReportData> holdingList = new ArrayList<>();
        for (AllocationBySectorDto allocation : groupAllocations) {
            holdingList.add(new AllocationReportData((AssetAllocationBySectorDto) allocation));
        }
        return holdingList;
    }
}
