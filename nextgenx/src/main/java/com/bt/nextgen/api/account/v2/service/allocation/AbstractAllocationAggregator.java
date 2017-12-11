package com.bt.nextgen.api.account.v2.service.allocation;

import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Deprecated
public abstract class AbstractAllocationAggregator {

    public AbstractAllocationAggregator() {
        super();
    }

    protected void addValuationToMap(Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap, SubAccountValuation valuation) {

        for (AccountHolding holding : valuation.getHoldings()) {
            Asset asset = holding.getAsset();
            HoldingSource holdingSource = new HoldingSource(asset, holding, valuation);

            AllocationGroupKey groupKey = getGroupingKey(holding);
            List<HoldingSource> assetHoldings = assetHoldingsMap.get(groupKey);
            if (assetHoldings == null) {
                assetHoldings = new ArrayList<>();
                assetHoldingsMap.put(groupKey, assetHoldings);
            }
            assetHoldings.add(holdingSource);
        }
    }
    
    protected abstract AllocationGroupKey getGroupingKey(AccountHolding holding);

}

