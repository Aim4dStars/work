package com.bt.nextgen.api.portfolio.v3.service.valuation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.PropertyExtractor;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ShareValuationDto;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;

@Component("ShareValuationAggregatorV3")
public class ShareValuationAggregator extends AbstractValuationAggregator {
    private static final String CATEGORY_SHARE = "Listed securities";

    public String getSubAccountCategory() {
        return CATEGORY_SHARE;
    }

    public List<InvestmentValuationDto> getShareValuationDtos(List<AccountHolding> subAccountHoldings, BigDecimal balance) {
        List<InvestmentValuationDto> valuationList = new ArrayList<>();

        List<ShareValuationDto> shareList = buildShareValuationDtos(subAccountHoldings, balance);
        if (CollectionUtils.isNotEmpty(shareList)) {
            valuationList.addAll(shareList);
        }

        return valuationList;
    }

    private List<ShareValuationDto> buildShareValuationDtos(List<AccountHolding> subAccountHoldings, BigDecimal accountBalance) {
        List<ShareValuationDto> shareDtoList = new ArrayList<>();
        if (subAccountHoldings == null || CollectionUtils.isEmpty(subAccountHoldings)) {
            return shareDtoList;
        }

        List<List<AccountHolding>> groupedAccountHoldings = getGroupedAccountHoldings(subAccountHoldings);

        List<String> methods = Lambda.convert(DistributionMethod.values(), new PropertyExtractor<DistributionMethod, String>(
                "displayName"));

        for (List<AccountHolding> shareHoldings : groupedAccountHoldings) {
            List<InvestmentAssetDto> dtoList = null;
            dtoList = getInvestmentList(accountBalance, shareHoldings);
            shareDtoList.add(new ShareValuationDto(shareHoldings, accountBalance, dtoList, methods));

        }
        return shareDtoList;
    }

    private List<List<AccountHolding>> getGroupedAccountHoldings(List<AccountHolding> subAccountHoldings) {
        Map<Pair<String, Boolean>, List<AccountHolding>> accountHoldingsMap = new HashMap<>();
        for (AccountHolding accountHolding : subAccountHoldings) {
            Pair<String, Boolean> key = new ImmutablePair<>(accountHolding.getAsset().getAssetId(), accountHolding.getExternal());
            if (accountHoldingsMap.containsKey(key)) {
                accountHoldingsMap.get(key).add(accountHolding);
            } else {
                List<AccountHolding> accountHoldings = new ArrayList<>();
                accountHoldings.add(accountHolding);
                accountHoldingsMap.put(key, accountHoldings);
            }
        }
        return new ArrayList<>(accountHoldingsMap.values());
    }

}
