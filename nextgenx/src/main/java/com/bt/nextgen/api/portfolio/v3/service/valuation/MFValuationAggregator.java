package com.bt.nextgen.api.portfolio.v3.service.valuation;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.PropertyExtractor;
import com.bt.nextgen.api.account.v2.service.DistributionAccountDtoService;
import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedFundValuationDto;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedFundHolding;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("MFValuationAggregatorV3")
public class MFValuationAggregator extends AbstractValuationAggregator {

    private static final String CATEGORY_MANAGED_FUND = "Managed funds";

    @Autowired
    private DistributionAccountDtoService mfaDtoService;

    @Override
    public String getSubAccountCategory() {
        return CATEGORY_MANAGED_FUND;
    }

    public List<InvestmentValuationDto> getManagedFundValuationDtos(List<AccountHolding> subAccountHoldings, BigDecimal balance) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        List<ManagedFundValuationDto> mfList = buildManagedFundValuationDto(subAccountHoldings, balance);
        if (CollectionUtils.isNotEmpty(mfList)) {
            valuationList.addAll(mfList);
        }

        return valuationList;
    }

    private List<ManagedFundValuationDto> buildManagedFundValuationDto(List<AccountHolding> subAccountHoldings,
            BigDecimal accountBalance) {
        List<ManagedFundValuationDto> mfDtoList = new ArrayList<>();
        if (subAccountHoldings == null || CollectionUtils.isEmpty(subAccountHoldings)) {
            return mfDtoList;
        }

        for (AccountHolding mfHolding : subAccountHoldings) {
            List<InvestmentAssetDto> dtoList = getInvestmentList(accountBalance, Collections.singletonList(mfHolding));

            if (!(dtoList.isEmpty())) {
                String distributionMethod = ((ManagedFundHolding) mfHolding).getDistributionMethod() == null ? null
                        : ((ManagedFundHolding) mfHolding).getDistributionMethod().getDisplayName();

                List<DistributionMethod> availableMethods = mfaDtoService.getAvailableDistributionMethod(mfHolding.getAsset());

                List<String> methods = Lambda.convert(availableMethods, new PropertyExtractor<DistributionMethod, String>(
                        "displayName"));

                mfDtoList.add(new ManagedFundValuationDto(mfHolding, accountBalance, dtoList.get(0), distributionMethod,
                        methods, mfHolding.getExternal()));
            }
        }
        return mfDtoList;
    }

}
