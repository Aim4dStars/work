package com.bt.nextgen.reports.account.allocation.sector;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.integration.asset.AssetType;

public class AllocationCsvReportData {
    private AllocationBySectorDto allocationBySectorDto;

    public AllocationCsvReportData(AllocationBySectorDto allocationBySectorDto) {
        this.allocationBySectorDto = allocationBySectorDto;
    }

    public String getExternal() {
        return allocationBySectorDto.getIsExternal() ? "External" : "Panorama";
    }

    public String getIndustrySector() {
        return ((AssetAllocationBySectorDto) allocationBySectorDto).getIndustrySectorSubSector();
    }

    public String getIndustryCategory() {
        return ((AssetAllocationBySectorDto) allocationBySectorDto).getIndustrySectorSubSectorCode();
    }

    public String getAssetClass() {
        return allocationBySectorDto.getAssetSector();
    }

    public String getAssetCode() {
        return ((AssetAllocationBySectorDto) allocationBySectorDto).getAssetCode();
    }

    public String getAssetName() {
        return allocationBySectorDto.getSource() != null ? allocationBySectorDto.getSource() + " "
                + allocationBySectorDto.getName()
                : allocationBySectorDto.getName();
    }

    public String getQuantity() {
        AssetAllocationBySectorDto sectorDto = (AssetAllocationBySectorDto) allocationBySectorDto;
        if(!allocationBySectorDto.getPending()){
            if (sectorDto.getAssetType().equals(AssetType.MANAGED_FUND.name())) {
                return ReportFormatter.format(ReportFormat.MANAGED_FUND_UNIT, sectorDto.getUnits());
            } else {
                return ReportFormatter.format(ReportFormat.UNITS, sectorDto.getUnits());
           }
        }else{
            return null;
        }
    }

    public String getBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, allocationBySectorDto.getBalance());
    }

    public String getAllocationPercentage() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, allocationBySectorDto.getAllocationPercentage());
    }

    public String getAssetSubClass() {
        AssetAllocationBySectorDto sectorDto = (AssetAllocationBySectorDto) allocationBySectorDto;
        return sectorDto.getAssetSubclass();
    }
}
