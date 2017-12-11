package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractValuationAggregator {

    protected abstract String getSubAccountCategory();

    protected List<InvestmentAssetDto> getInvestmentList(BigDecimal mpBalance, List<AccountHolding> holdings) {

        List<InvestmentAssetDto> investments = new ArrayList<>();

        for (AccountHolding holding : holdings) {
            InvestmentAssetDto investment = new InvestmentAssetDto(holding, mpBalance);
            investments.add(investment);
        }

        Collections.sort(investments, investmentListComparator);

        return investments;
    }

    protected static final Comparator<InvestmentAssetDto> investmentListComparator = new Comparator<InvestmentAssetDto>() {

        @Override
        public int compare(InvestmentAssetDto o1, InvestmentAssetDto o2) {
            // Sort investment list into alphabetic order, with CASH type assets first
            String name1 = o1.getAssetName() == null ? "" : o1.getAssetName().toLowerCase();
            String name2 = o2.getAssetName() == null ? "" : o2.getAssetName().toLowerCase();

            // If one asset is CASH, that one comes first
            if (AssetType.CASH.name().equals(o1.getAssetType()) && !AssetType.CASH.name().equals(o2.getAssetType())) {
                return -1;
            } else if (!AssetType.CASH.name().equals(o1.getAssetType()) && AssetType.CASH.name().equals(o2.getAssetType())) {
                return 1;
            } else {
                return name1.compareTo(name2);
            }
        }
    };

}
