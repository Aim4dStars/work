package com.bt.nextgen.api.account.v2.service.allocation;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v2.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.account.v2.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.api.account.v2.model.allocation.sector.HoldingAllocationBySectorDto;
import com.bt.nextgen.api.account.v2.model.allocation.sector.TermDepositAggregatedAssetAllocationBySectorDto;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Deprecated
@Component
class SectorAssetAllocationBuilder {

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;


    public AllocationBySectorDto buildIncomeAsset(Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap,
            BigDecimal balance) {
        List<AllocationBySectorDto> incomes = new ArrayList<>();
        for (List<HoldingSource> holdings : assetHoldingsMap.values()) {
            for (HoldingSource holding : holdings) {
                if (BigDecimal.ZERO.compareTo(holding.getIncome()) != 0) {
                    Asset holdingAsset = holding.getSource();
                    HoldingAllocationBySectorDto holdingAlloc = new HoldingAllocationBySectorDto(holdingAsset,
                            holding.getExternalSource(), holding.getIncome(), null,
                            PortfolioUtils.getValuationAsPercent(holding.getIncome(), balance), false, holding.isExternal());
                    incomes.add(holdingAlloc);
                }
            }
        }

        Collections.sort(incomes, new AllocationComparator());
        return new AssetAllocationBySectorDto("Income accrued", incomes);
    }

    public AssetAllocationBySectorDto buildAssetAllocation(AccountKey accountKey, List<HoldingSource> holdings, BigDecimal balance) {
        Asset asset = holdings.get(0).getAsset();
        boolean pending = holdings.get(0).isPending();
        List<AllocationBySectorDto> allocations = new ArrayList<>();
        for (HoldingSource holding : holdings) {
            BigDecimal holdingBalance = holding.getMarketValue();

            BigDecimal holdingUnits = holding.getUnits();
            if (holding.isPending() || holding.getAsset().getAssetType() == AssetType.CASH
                    || holding.getAsset().getAssetType() == AssetType.TERM_DEPOSIT) {
                // Quantity not applicable to prepayments, cash or TD
                holdingUnits = null;
            }

            BigDecimal holdingPercent = BigDecimal.ZERO;
            if (!(BigDecimal.ZERO.compareTo(balance) == 0)) {
                holdingPercent = holdingBalance.divide(balance, 8, RoundingMode.HALF_UP);
            }
            HoldingAllocationBySectorDto assetAllocation = new HoldingAllocationBySectorDto(holding.getSource(),
                    holding.getExternalSource(), holdingBalance, holdingUnits, holdingPercent, holding.isPending(),
                    holding.isExternal());
            allocations.add(assetAllocation);
        }

        List<AllocationBySectorDto> sortedAllocations = Lambda.sort(allocations, Lambda.on(AllocationBySectorDto.class).getName()
                .toLowerCase());

        if (AssetType.TERM_DEPOSIT.equals(asset.getAssetType()) && (asset instanceof TermDepositAsset)) {
            return new TermDepositAggregatedAssetAllocationBySectorDto((TermDepositAsset) asset, sortedAllocations,
                    termDepositPresentationService.getTermDepositPresentation(accountKey, asset.getAssetId(),
                            new ServiceErrorsImpl()));
        } else {
            return new AssetAllocationBySectorDto(asset, pending, sortedAllocations);
        }
    }
}
