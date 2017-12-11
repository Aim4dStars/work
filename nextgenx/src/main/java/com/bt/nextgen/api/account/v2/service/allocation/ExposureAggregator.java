package com.bt.nextgen.api.account.v2.service.allocation;

import com.bt.nextgen.api.account.v2.model.allocation.exposure.AggregateAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AssetAllocationByExposureDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetAllocation;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetKey;
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

@Deprecated
@Component
class ExposureAggregator extends AbstractAllocationAggregator {

    @Autowired
    private ExposureAssetAllocationBuilder assetBuilder;

    private static final String ALLOCATION = "Allocation";

    public AggregateAllocationByExposureDto aggregateAllocations(AccountKey accountKey, List<SubAccountValuation> valuations,
            Map<AssetKey, AssetAllocation> assetAllocations, BigDecimal balance, ServiceErrors serviceErrors) {
        Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap = new HashMap<>();
        for (SubAccountValuation valuation : valuations) {
            addValuationToMap(assetHoldingsMap, valuation);
        }

        List<AllocationByExposureDto> expAllocs = buildExposureAllocations(accountKey, assetHoldingsMap, assetAllocations,
                balance, serviceErrors);
        addIncomeAsset(expAllocs, assetHoldingsMap, assetAllocations, balance);

        return new AggregateAllocationByExposureDto(ALLOCATION, expAllocs);
    }

    private List<AllocationByExposureDto> buildExposureAllocations(AccountKey accountKey,
            Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap, Map<AssetKey, AssetAllocation> assetAllocations,
            BigDecimal balance, ServiceErrors serviceErrors) {
        Map<String, List<AllocationByExposureDto>> sectorMap = buildSectorAllocationMap(accountKey, assetHoldingsMap,
                assetAllocations, balance, serviceErrors);

        List<AllocationByExposureDto> exposureAllocations = new ArrayList<>();
        for (Map.Entry<String, List<AllocationByExposureDto>> sector : sectorMap.entrySet()) {
            List<AllocationByExposureDto> sectorAllocs = sector.getValue();
            Collections.sort(sectorAllocs, new AllocationComparator());
            AggregateAllocationByExposureDto sectorAllocation = new AggregateAllocationByExposureDto(sector.getKey(),
                    sectorAllocs);
            exposureAllocations.add(sectorAllocation);
        }

        Collections.sort(exposureAllocations, new SectorComparator());
        return exposureAllocations;
    }

    private Map<String, List<AllocationByExposureDto>> buildSectorAllocationMap(AccountKey accountKey,
            Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap, Map<AssetKey, AssetAllocation> assetAllocations,
            BigDecimal balance, ServiceErrors serviceErrors) {
        Map<String, List<AllocationByExposureDto>> allocationMap = new HashMap<>();

        for (List<HoldingSource> assetHoldings : assetHoldingsMap.values()) {
            AssetAllocationByExposureDto assetAllocation = assetBuilder.buildAssetAllocation(accountKey, assetHoldings,
                    assetAllocations,
                    balance, serviceErrors);
            String sector = assetAllocation.getAssetSector();
            List<AllocationByExposureDto> sectorAssets = allocationMap.get(sector);
            if (sectorAssets == null) {
                sectorAssets = new ArrayList<>();
                allocationMap.put(sector, sectorAssets);
            }
            sectorAssets.add(assetAllocation);
        }
        return allocationMap;
    }
    

    private void addIncomeAsset(List<AllocationByExposureDto> exposures,
            Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap,
            Map<AssetKey, AssetAllocation> assetAllocations, BigDecimal balance) {
        AllocationByExposureDto incomeExposure = assetBuilder.buildIncomeAsset(assetHoldingsMap, assetAllocations,
                balance);
        for (AllocationByExposureDto exposure : exposures) {
            if (AssetClass.CASH.getDescription().equals(exposure.getName())) {
                // income goes last
                ((AggregateAllocationByExposureDto) exposure).getAllocations().add(incomeExposure);
                break;
            }
        }
    }

    protected AllocationGroupKey getGroupingKey(AccountHolding holding) {
        return new AllocationGroupKey(holding.getAsset().getAssetId(), true, holding.getExternal());
    }
}
