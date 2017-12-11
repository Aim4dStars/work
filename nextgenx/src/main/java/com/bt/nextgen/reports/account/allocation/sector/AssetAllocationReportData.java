package com.bt.nextgen.reports.account.allocation.sector;

import java.util.List;

public class AssetAllocationReportData {

    private List<AssetSectorReportData> sectorList;

    public AssetAllocationReportData(List<AssetSectorReportData> sectorList) {
        this.sectorList = sectorList;
    }

    public List<AssetSectorReportData> getSectorList() {
        return sectorList;
    }
}
