package com.bt.nextgen.reports.account.allocation.sector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AggregatedAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;

@Service
public class AssetSectorReportDataConverter {

    public AssetAllocationReportData getReportData(AllocationGroupType allocationGroupType, List<AllocationBySectorDto> sectorList) {
        List<AssetSectorReportData> sectorReportData = getAssetSectors(allocationGroupType, sectorList);
        return new AssetAllocationReportData(sectorReportData);
    }

    public List<AssetSectorReportData> getAssetSectors(AllocationGroupType allocationGroupType,
            List<AllocationBySectorDto> sectorList) {
        List<AssetSectorReportData> assetClassList = new ArrayList<>();

        if (sectorList != null) {
            for (AllocationBySectorDto sector : sectorList) {
                Boolean showAllocationGroups = (AllocationGroupType.INDUSTRY_SUB_SECTOR == allocationGroupType && sectorAllowsGroups(sector
                        .getName())) || (AllocationGroupType.ASSET_SUB_CLASS == allocationGroupType);
                List<AllocationBySectorDto> allocationList = ((AggregatedAllocationBySectorDto) sector).getAllocations();
                List<AllocationGroupReportData> allocationGroups = getAllocationGroups(showAllocationGroups, allocationGroupType,
                        allocationList);

                assetClassList.add(new AssetSectorReportData(sector, allocationGroups));
            }
        }
        return assetClassList;
    }

    private Boolean sectorAllowsGroups(String sectorName) {
        return "Australian Shares".equalsIgnoreCase(sectorName) || "Australian Property".equalsIgnoreCase(sectorName);
    }

    private List<AllocationGroupReportData> getAllocationGroups(Boolean showAllocationGroups,
            AllocationGroupType allocationGroupType,
            List<AllocationBySectorDto> allocationList) {
        List<AllocationGroupReportData> allocationGroupList = new ArrayList<>();

        if (allocationList != null) {
            if (showAllocationGroups) {
                Map<String, List<AllocationBySectorDto>> groupedAllocations = groupAllocations(allocationList,
                        allocationGroupType);

                for (String key : groupedAllocations.keySet()) {
                    List<AllocationBySectorDto> group = groupedAllocations.get(key);
                    boolean displayGroupDetail = key != null ? true : false;
                    AllocationGroupReportData allocationGroup = new AllocationGroupReportData(key, displayGroupDetail, group);
                    allocationGroupList.add(allocationGroup);
                }
            } else {
                allocationGroupList.add(new AllocationGroupReportData(null, false, allocationList));
            }
        }
        return allocationGroupList;
    }

    private Map<String, List<AllocationBySectorDto>> groupAllocations(List<AllocationBySectorDto> allocationList,
            AllocationGroupType allocationGroupType) {
        Map<String, List<AllocationBySectorDto>> groupMap = new HashMap<>();
        String allocationGroup = null;
        for (AllocationBySectorDto allocation : allocationList) {
            if (AllocationGroupType.INDUSTRY_SUB_SECTOR == allocationGroupType) {
                allocationGroup = ((AssetAllocationBySectorDto) allocation).getIndustrySectorSubSector();
            } else {
                allocationGroup = ((AssetAllocationBySectorDto) allocation).getAssetSubclass();
            }

            if (!groupMap.containsKey(allocationGroup)) {
                List<AllocationBySectorDto> groupAllocations = new ArrayList<>();
                groupAllocations.add(allocation);
                groupMap.put(allocationGroup, groupAllocations);
            } else {
                groupMap.get(allocationGroup).add(allocation);
            }
        }
        return groupMap;
    }
}
