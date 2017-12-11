package com.bt.nextgen.api.account.v2.service.allocation;

import com.bt.nextgen.api.account.v2.model.allocation.exposure.AllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AssetAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.HoldingAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.TermDepositAssetAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetAllocation;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Deprecated
@Component
class ExposureAssetAllocationBuilder {

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    private Map<String, BigDecimal> getCategoryMap(Asset asset, Map<AssetKey, AssetAllocation> assetAllocations) {

        Map<String, BigDecimal> categoriesMap = new HashMap<>();
        for (AssetClass category : AssetClass.values()) {
            categoriesMap.put(category.name(), BigDecimal.ZERO);
        }

        AssetAllocation assetAllocation = null;
        if (asset != null) {
            assetAllocation = assetAllocations.get(AssetKey.valueOf(asset.getAssetId()));
        }
        if (assetAllocation == null || assetAllocation.getAllocations() == null) {
            AssetClass assetClass = asset == null || asset.getAssetClass() == null ? AssetClass.CASH : asset.getAssetClass();
            if (categoriesMap.containsKey(assetClass.name())) {
                categoriesMap.put(assetClass.name(), BigDecimal.ONE);
            }
        } else {
            Map<AssetClass, BigDecimal> storedAllocations = assetAllocation.getAllocations();
            for (Entry<AssetClass, BigDecimal> entry : storedAllocations.entrySet()) {
                AssetClass assetClass = entry.getKey();
                // Don't explode if an unknown asset class comes in
                if (assetClass == null) {
                    assetClass = AssetClass.OTHER;
                }
                categoriesMap.put(assetClass.name(), entry.getValue());
            }
        }
        return categoriesMap;
    }

    public AllocationByExposureDto buildIncomeAsset(Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap,
            Map<AssetKey, AssetAllocation> assetAllocations, BigDecimal balance) {
        List<AllocationByExposureDto> incomes = new ArrayList<>();
        for (List<HoldingSource> holdings : assetHoldingsMap.values()) {
            for (HoldingSource holding : holdings) {
                if (BigDecimal.ZERO.compareTo(holding.getIncome()) != 0) {
                    Asset holdingAsset = holding.getSource();
                    Map<String, BigDecimal> categoryMap = getCategoryMap(null, assetAllocations);
                    HoldingAllocationByExposureDto holdingAlloc = new HoldingAllocationByExposureDto(holdingAsset, balance,
                            holding.getIncome(), holding.isExternal(), holding.getExternalSource(), categoryMap);

                    incomes.add(holdingAlloc);
                }
            }
        }

        Collections.sort(incomes, new AllocationComparator());

        return new AssetAllocationByExposureDto("Income accrued", incomes);
    }

    public AssetAllocationByExposureDto buildAssetAllocation(AccountKey accountKey, List<HoldingSource> holdings,
            Map<AssetKey, AssetAllocation> assetAllocations, BigDecimal balance, ServiceErrors serviceErrors) {
        Asset asset = holdings.get(0).getAsset();
        Map<String, BigDecimal> categoryMap = getCategoryMap(asset, assetAllocations);
        List<AllocationByExposureDto> allocations = new ArrayList<>();
        for (HoldingSource holding : holdings) {
            Asset sourceAsset = holding.getSource();
            HoldingAllocationByExposureDto holdingAlloc = new HoldingAllocationByExposureDto(sourceAsset, balance,
                    holding.getMarketValue(), holding.isExternal(), holding.getExternalSource(), categoryMap);

            allocations.add(holdingAlloc);
        }

        Collections.sort(allocations, new AllocationComparator());

        if (AssetType.TERM_DEPOSIT.equals(asset.getAssetType()) && (asset instanceof TermDepositAsset)) {
            TermDepositPresentation tdPres = termDepositPresentationService.getTermDepositPresentation(accountKey,
                    asset.getAssetId(), serviceErrors);
            return new TermDepositAssetAllocationByExposureDto(asset, allocations, tdPres);
        } else {
            return new AssetAllocationByExposureDto(asset, allocations);
        }
    }
}
