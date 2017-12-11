package com.bt.nextgen.api.account.v2.service.valuation;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.PropertyExtractor;
import com.bt.nextgen.api.account.v2.model.InvestmentAssetDto;
import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v2.model.ShareValuationDto;
import com.bt.nextgen.api.account.v2.service.DistributionAccountDtoService;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ShareAccountValuation;
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
public class ShareValuationAggregator extends AbstractValuationAggregator {
    private static final String CATEGORY_SHARE = "Listed securities";

    @Autowired
    private DistributionAccountDtoService distributionDtoService;

    public String getSubAccountCategory() {
        return CATEGORY_SHARE;
    }

    public List<InvestmentValuationDto> getShareValuationDtos(SubAccountValuation subAccount, BigDecimal balance) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        ShareAccountValuation shareVal = (ShareAccountValuation) subAccount;
        List<ShareValuationDto> shareList = buildShareValuationDtos(shareVal, balance);
        if (CollectionUtils.isNotEmpty(shareList)) {
            valuationList.addAll(shareList);
        }

        return valuationList;
    }

    private List<ShareValuationDto> buildShareValuationDtos(ShareAccountValuation shareAccount, BigDecimal accountBalance) {
        List<ShareValuationDto> shareDtoList = new ArrayList<>();
        if (shareAccount == null || CollectionUtils.isEmpty(shareAccount.getHoldings())) {
            return shareDtoList;
        }

        for (AccountHolding shareHolding : shareAccount.getHoldings()) {

            BigDecimal balance = shareHolding.getMarketValue();
            List<InvestmentAssetDto> dtoList = getInvestmentList(balance, Collections.singletonList(shareHolding));

            if (!(dtoList.isEmpty())) {
                BigDecimal portfolioPercent = PortfolioUtils.getValuationAsPercent(balance, accountBalance);

                List<DistributionMethod> availableMethods = distributionDtoService.getAvailableDistributionMethod(shareHolding
                        .getAsset());

                List<String> methods = Lambda.convert(availableMethods, new PropertyExtractor<DistributionMethod, String>(
                        "displayName"));

                shareDtoList.add(new ShareValuationDto(shareHolding, portfolioPercent, dtoList.get(0), methods, shareHolding
                        .getExternal()));

            }
        }
        return shareDtoList;
    }

}
