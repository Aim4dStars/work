package com.bt.nextgen.api.portfolio.v3.service.allocation;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AggregatedAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("SectorAggregatorV3")
class SectorAggregator extends AbstractAllocationAggregator {

    @Autowired
    private SectorAssetAllocationBuilder assetBuilder;

    private static final String ALLOCATION = "Allocation";

    public AggregatedAllocationBySectorDto aggregateAllocations(AccountKey accountKey, List<SubAccountValuation> valuations,
            BigDecimal balance) {
        Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap = new HashMap<>();
        for (SubAccountValuation valuation : valuations) {
            addValuationToMap(assetHoldingsMap, valuation);
        }
        List<AllocationBySectorDto> sectorAllocs = buildSectorAllocations(accountKey, assetHoldingsMap, balance);
        addIncomeAsset(sectorAllocs, assetHoldingsMap, balance);
        return new AggregatedAllocationBySectorDto(ALLOCATION, sectorAllocs);
    }

    private Map<String, List<AllocationBySectorDto>> buildSectorAllocationMap(AccountKey accountKey,
            Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap,
            BigDecimal balance) {
        Map<String, List<AllocationBySectorDto>> allocationMap = new HashMap<>();

        for (List<HoldingSource> assetHoldings : assetHoldingsMap.values()) {
            if (!isIncomeOnly(assetHoldings)) {
                AssetAllocationBySectorDto assetAllocation = assetBuilder
                        .buildAssetAllocation(accountKey, assetHoldings, balance);
                String sector = assetAllocation.getAssetSector();
                List<AllocationBySectorDto> sectorAssets = allocationMap.get(sector);
                if (sectorAssets == null) {
                    sectorAssets = new ArrayList<>();
                    allocationMap.put(sector, sectorAssets);
                }
                sectorAssets.add(assetAllocation);
            }
        }
        return allocationMap;
    }

    private boolean isIncomeOnly(List<HoldingSource> holdings) {
        for (HoldingSource holding : holdings) {
            if (!holding.isIncomeOnly()) {
                return false;
            }
        }
        return true;
    }

    private List<AllocationBySectorDto> buildSectorAllocations(AccountKey accountKey,
            Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap,
            BigDecimal balance) {
        Map<String, List<AllocationBySectorDto>> sectorMap = buildSectorAllocationMap(accountKey, assetHoldingsMap, balance);

        List<AllocationBySectorDto> sectorAllocations = new ArrayList<>();
        for (Map.Entry<String, List<AllocationBySectorDto>> sector : sectorMap.entrySet()) {
            List<AllocationBySectorDto> sectorAllocs = sector.getValue();
            Collections.sort(sectorAllocs, new AllocationComparator());
            AggregatedAllocationBySectorDto sectorAllocation = new AggregatedAllocationBySectorDto(sector.getKey(), sectorAllocs);
            sectorAllocations.add(sectorAllocation);
        }
        Collections.sort(sectorAllocations, new SectorComparator());
        return sectorAllocations;
    }

    private void addIncomeAsset(List<AllocationBySectorDto> sectorAllocs,
            Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap,
            BigDecimal balance) {
        AllocationBySectorDto income = assetBuilder.buildIncomeAsset(assetHoldingsMap, balance);
        if (!BigDecimal.ZERO.equals(income.getBalance())) {
            for (AllocationBySectorDto sectorAlloc : sectorAllocs) {
                if (AssetClass.CASH.getDescription().equals(sectorAlloc.getName())) {
                    // income goes last
                    ((AggregatedAllocationBySectorDto) sectorAlloc).getAllocations().add(income);
                    break;
                }
            }
        }
    }

    protected AllocationGroupKey getGroupingKey(AccountHolding holding) {
        return new AllocationGroupKey(holding.getAsset().getAssetId(), holding.getReferenceAsset() != null,
                holding.getExternal());
    }
}
