package com.bt.nextgen.api.account.v2.service.valuation;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.PropertyExtractor;
import com.bt.nextgen.api.account.v2.model.InvestmentAssetDto;
import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v2.model.ManagedFundValuationDto;
import com.bt.nextgen.api.account.v2.service.DistributionAccountDtoService;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedFundHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
@Component
public class MFValuationAggregator extends AbstractValuationAggregator {

    private static final String CATEGORY_MANAGED_FUND = "Managed funds";

    @Autowired
    private DistributionAccountDtoService mfaDtoService;

    @Override
    public String getSubAccountCategory() {
        return CATEGORY_MANAGED_FUND;
    }

    public List<InvestmentValuationDto> getManagedFundValuationDtos(SubAccountValuation subAccount, BigDecimal balance) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        List<ManagedFundValuationDto> mfList = buildManagedFundValuationDto(subAccount, balance);
        if (CollectionUtils.isNotEmpty(mfList)) {
            valuationList.addAll(mfList);
        }

        return valuationList;
    }

    private List<ManagedFundValuationDto> buildManagedFundValuationDto(SubAccountValuation mfAccount, BigDecimal accountBalance) {
        List<ManagedFundValuationDto> mfDtoList = new ArrayList<>();
        if (mfAccount == null || CollectionUtils.isEmpty(mfAccount.getHoldings())) {
            return mfDtoList;
        }

        for (AccountHolding mfHolding : mfAccount.getHoldings()) {

            BigDecimal balance = mfHolding.getMarketValue();
            List<InvestmentAssetDto> dtoList = getInvestmentList(balance, Collections.singletonList(mfHolding));

            if (!(dtoList.isEmpty())) {
                BigDecimal portfolioPercent = PortfolioUtils.getValuationAsPercent(balance, accountBalance);

                String distributionMethod = ((ManagedFundHolding) mfHolding).getDistributionMethod() == null ? null
                        : ((ManagedFundHolding) mfHolding).getDistributionMethod().getDisplayName();

                List<DistributionMethod> availableMethods = mfaDtoService.getAvailableDistributionMethod(mfHolding.getAsset());

                List<String> methods = Lambda.convert(availableMethods, new PropertyExtractor<DistributionMethod, String>(
                        "displayName"));

                mfDtoList.add(new ManagedFundValuationDto(mfHolding, portfolioPercent, dtoList.get(0), distributionMethod,
                        methods, mfHolding.getExternal()));
            }
        }
        return mfDtoList;
    }

}
