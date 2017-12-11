package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.OtherAssetValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.QuantisedAssetValuationDto;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("OtherValuationAggregatorV3")
public class OtherValuationAggregator extends AbstractValuationAggregator {

    private static final String CATEGORY_OTHER = "Other assets";

    public String getSubAccountCategory() {
        return CATEGORY_OTHER;
    }

    public List<InvestmentValuationDto> getOtherValuationDto(List<AccountHolding> subAccountHoldings, BigDecimal balance,
            boolean quantised) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        List<InvestmentValuationDto> mfList = buildValuationDtos(subAccountHoldings, balance, quantised);
        if (CollectionUtils.isNotEmpty(mfList)) {
            valuationList.addAll(mfList);
        }

        return valuationList;
    }

    private List<InvestmentValuationDto> buildValuationDtos(List<AccountHolding> subAccountHoldings, BigDecimal accountBalance,
            boolean quantised) {
        List<InvestmentValuationDto> dtoList = new ArrayList<>();
        if (subAccountHoldings == null || CollectionUtils.isEmpty(subAccountHoldings)) {
            return dtoList;
        }

        for (AccountHolding holding : subAccountHoldings) {
            BigDecimal balance = holding.getMarketValue();
            List<InvestmentAssetDto> assetList = getInvestmentList(balance, Collections.singletonList(holding));

            if (quantised) {
                dtoList.add(
                        new QuantisedAssetValuationDto(holding, assetList.get(0), balance, holding.getUnits(), accountBalance));
            } else {
                dtoList.add(new OtherAssetValuationDto(holding, assetList.get(0), balance, accountBalance));
            }
        }
        return dtoList;
    }
}
