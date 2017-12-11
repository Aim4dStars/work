package com.bt.nextgen.api.portfolio.v3.service.allocation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.HoldingAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.TermDepositAggregatedAssetAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;

@Component("SectorAssetAllocationBuilderV3")
class SectorAssetAllocationBuilder {

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    public AllocationBySectorDto buildIncomeAsset(Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap,
            BigDecimal balance) {
        List<AllocationBySectorDto> incomes = new ArrayList<>();
        List<HoldingSource> incomeHoldings = new ArrayList<>();
        for (List<HoldingSource> holdings : assetHoldingsMap.values()) {
            for (HoldingSource holding : holdings) {
                if (BigDecimal.ZERO.compareTo(holding.getIncome()) != 0) {
                    incomeHoldings.add(holding);
                }
            }
        }
        incomes.addAll(getIncomeHoldingAllocations(incomeHoldings, balance));
        Collections.sort(incomes, new AllocationComparator());
        return new AssetAllocationBySectorDto("Income accrued", incomes);
    }

    public List<HoldingAllocationBySectorDto> getIncomeHoldingAllocations(List<HoldingSource> incomeHoldings, BigDecimal balance) {
        List<HoldingAllocationBySectorDto> incomeHoldingAllocations = new ArrayList<>();
        Group<HoldingSource> holdingGroups = Lambda.group(incomeHoldings, Lambda.by(Lambda.on(HoldingSource.class).getSource()));
        for (Group<HoldingSource> holdingGroup : holdingGroups.subgroups()) {
            List<HoldingSource> groupedHoldings = holdingGroup.findAll();
            incomeHoldingAllocations.add(new HoldingAllocationBySectorDto(groupedHoldings, balance, true));
        }
        return incomeHoldingAllocations;
    }
    
    public AssetAllocationBySectorDto buildAssetAllocation(AccountKey accountKey, List<HoldingSource> holdings,
            BigDecimal balance) {
        Asset asset = holdings.get(0).getAsset();
        boolean pending = holdings.get(0).isPending();
        List<AllocationBySectorDto> allocations = new ArrayList<>();
        Group<HoldingSource> holdingGroups = Lambda.group(holdings, Lambda.by(Lambda.on(HoldingSource.class).getSource()));

        for (Group<HoldingSource> holdingGroup : holdingGroups.subgroups()) {

            HoldingAllocationBySectorDto assetAllocation = new HoldingAllocationBySectorDto(holdingGroup.findAll(), balance,
                    false);
            allocations.add(assetAllocation);
        }

        List<AllocationBySectorDto> sortedAllocations = Lambda.sort(allocations,
                Lambda.on(AllocationBySectorDto.class).getName().toLowerCase());

        if (AssetType.TERM_DEPOSIT.equals(asset.getAssetType()) && (asset instanceof TermDepositAsset)) {
            return new TermDepositAggregatedAssetAllocationBySectorDto((TermDepositAsset) asset, sortedAllocations,
                    termDepositPresentationService.getTermDepositPresentation(accountKey, asset.getAssetId(),
                            new ServiceErrorsImpl()));
        } else {
            return new AssetAllocationBySectorDto(asset, pending, sortedAllocations);
        }
    }

}
