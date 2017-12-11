package com.bt.nextgen.api.portfolio.v3.service.allocation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;

import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AssetAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.HoldingAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.TermDepositAssetAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetAllocation;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.btfin.panorama.service.integration.asset.AssetType;

@Component("ExposureAssetAllocationBuilderV3")
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
                categoriesMap.put(assetClass.getExposureCategory().name(), BigDecimal.ONE);
            }
        } else {
            Map<AssetClass, BigDecimal> storedAllocations = assetAllocation.getAllocations();
            for (Entry<AssetClass, BigDecimal> entry : storedAllocations.entrySet()) {
                AssetClass assetClass = entry.getKey();
                // Don't explode if an unknown asset class comes in
                if (assetClass == null) {
                    assetClass = AssetClass.OTHER;
                }
                if (categoriesMap.get(assetClass.getExposureCategory().name()) == null) {
                    categoriesMap.put(assetClass.getExposureCategory().name(), entry.getValue());
                } else {
                    categoriesMap.put(assetClass.getExposureCategory().name(),
                            categoriesMap.get(assetClass.getExposureCategory().name()).add(entry.getValue()));
                }

            }
        }
        return categoriesMap;
    }

    public AllocationByExposureDto buildIncomeAsset(Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap,
            Map<AssetKey, AssetAllocation> assetAllocations, BigDecimal balance) {
        List<AllocationByExposureDto> incomes = new ArrayList<>();
        List<HoldingSource> incomeHoldings = new ArrayList<>();
        for (List<HoldingSource> holdings : assetHoldingsMap.values()) {
            for (HoldingSource holding : holdings) {
                if (BigDecimal.ZERO.compareTo(holding.getIncome()) != 0) {
                    incomeHoldings.add(holding);
                }
            }
        }
        Map<String, BigDecimal> categoryMap = getCategoryMap(null, assetAllocations);
        incomes.addAll(getIncomeHoldingAllocations(incomeHoldings, balance, categoryMap));
        Collections.sort(incomes, new AllocationComparator());

        return new AssetAllocationByExposureDto("Income accrued", incomes);
    }

    public List<HoldingAllocationByExposureDto> getIncomeHoldingAllocations(List<HoldingSource> incomeHoldings,
            BigDecimal balance, Map<String, BigDecimal> categoryMap) {
        List<HoldingAllocationByExposureDto> incomeHoldingAllocations = new ArrayList<>();
        Group<HoldingSource> holdingGroups = Lambda.group(incomeHoldings, Lambda.by(Lambda.on(HoldingSource.class).getSource()));
        for (Group<HoldingSource> holdingGroup : holdingGroups.subgroups()) {
            List<HoldingSource> groupedHoldings = holdingGroup.findAll();
            incomeHoldingAllocations.add(new HoldingAllocationByExposureDto(groupedHoldings, balance, categoryMap, true));
        }
        return incomeHoldingAllocations;
    }

    public AssetAllocationByExposureDto buildAssetAllocation(AccountKey accountKey, List<HoldingSource> holdings,
            Map<AssetKey, AssetAllocation> assetAllocations, BigDecimal balance, ServiceErrors serviceErrors) {
        Asset asset = holdings.get(0).getAsset();
        Map<String, BigDecimal> categoryMap = getCategoryMap(asset, assetAllocations);
        List<AllocationByExposureDto> allocations = new ArrayList<>();
        Group<HoldingSource> holdingGroups = Lambda.group(holdings, Lambda.by(Lambda.on(HoldingSource.class).getSource()));
        for (Group<HoldingSource> holdingGroup : holdingGroups.subgroups()) {
            HoldingAllocationByExposureDto holdingAlloc = new HoldingAllocationByExposureDto(holdingGroup.findAll(), balance,
 categoryMap,
                    false);

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
