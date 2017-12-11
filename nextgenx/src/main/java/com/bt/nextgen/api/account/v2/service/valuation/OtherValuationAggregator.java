package com.bt.nextgen.api.account.v2.service.valuation;

import com.bt.nextgen.api.account.v2.model.InvestmentAssetDto;
import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v2.model.OtherAssetValuationDto;
import com.bt.nextgen.api.account.v2.model.QuantisedAssetValuationDto;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
@Component
public class OtherValuationAggregator extends AbstractValuationAggregator {

    private static final String CATEGORY_OTHER = "Other assets";

    public String getSubAccountCategory() {
        return CATEGORY_OTHER;
    }

    public List<InvestmentValuationDto> getOtherValuationDto(SubAccountValuation subAccount, BigDecimal balance, boolean quantised) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        List<InvestmentValuationDto> mfList = buildValuationDtos(subAccount, balance, quantised);
        if (CollectionUtils.isNotEmpty(mfList)) {
            valuationList.addAll(mfList);
        }

        return valuationList;
    }

    private List<InvestmentValuationDto> buildValuationDtos(SubAccountValuation subAccount, BigDecimal accountBalance,
            boolean quantised) {
        List<InvestmentValuationDto> dtoList = new ArrayList<>();
        if (subAccount == null || CollectionUtils.isEmpty(subAccount.getHoldings())) {
            return dtoList;
        }


        for (AccountHolding holding : subAccount.getHoldings()) {
            BigDecimal balance = holding.getMarketValue();
            BigDecimal portfolioPercent = PortfolioUtils.getValuationAsPercent(balance, accountBalance);

            List<InvestmentAssetDto> assetList = getInvestmentList(balance, Collections.singletonList(holding));

            if (assetList.isEmpty()) {
                if (quantised) {
                    dtoList.add(new QuantisedAssetValuationDto(holding, null, balance, holding.getUnits(), portfolioPercent));
                } else {
                    dtoList.add(new OtherAssetValuationDto(holding, null, balance, portfolioPercent));
                }
            } else {
                if (quantised) {
                    dtoList.add(new QuantisedAssetValuationDto(holding, assetList.get(0), balance, holding.getUnits(),
                            portfolioPercent));
                } else {
                    dtoList.add(new OtherAssetValuationDto(holding, assetList.get(0), balance, portfolioPercent));
                }
            }
        }
        return dtoList;
    }
}
