package com.bt.nextgen.api.account.v2.service.valuation;

import com.bt.nextgen.api.account.v2.model.InvestmentAssetDto;
import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v2.model.ManagedPortfolioValuationDto;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Deprecated
@Component
public class MPValuationAggregator extends AbstractValuationAggregator {

    private static final String CATEGORY_MANAGED_PORTFOLIO = "Managed portfolios";

    public String getSubAccountCategory() {
        return CATEGORY_MANAGED_PORTFOLIO;
    }

    public List<InvestmentValuationDto> getManagedPortfolioValuationDto(SubAccountValuation subAccount, BigDecimal accountBalance) {
        List<InvestmentValuationDto> result = new ArrayList<>();
        if (subAccount.getHoldings().get(0).getExternal()) {
            result.addAll(getExternalManagedPortfolioValuationDto(subAccount, accountBalance));
        } else {
            result.add(getInternalManagedPortfolioValuationDto(subAccount, accountBalance));
        }
        return result;
    }

    private List<InvestmentValuationDto> getExternalManagedPortfolioValuationDto(SubAccountValuation subAccount,
            BigDecimal accountBalance) {
        List<InvestmentValuationDto> valuations = new ArrayList<>();
        for (AccountHolding holding : subAccount.getHoldings()) {

            Map<String, BigDecimal> balances = new HashMap<>();
            balances.put("balance", holding.getMarketValue());
            balances.put("totalInterest", BigDecimal.ZERO);

            ManagedPortfolioValuationDto valuation = new ManagedPortfolioValuationDto(holding, balances,
                    PortfolioUtils.getValuationAsPercent(holding.getBalance(), accountBalance), false,
                    new ArrayList<InvestmentAssetDto>(), true);
            valuations.add(valuation);
        }
        return valuations;
    }

    private InvestmentValuationDto getInternalManagedPortfolioValuationDto(SubAccountValuation subAccount,
            BigDecimal accountBalance) {
        ManagedPortfolioAccountValuation managedPortfolioAccount = (ManagedPortfolioAccountValuation) subAccount;

        Map<String, BigDecimal> balances = getManagedPortfolioBalances(managedPortfolioAccount);
        Boolean hasPending = hasPending(managedPortfolioAccount.getHoldings());

        List<InvestmentAssetDto> dtoList = getInvestmentList(balances.get("balance"), managedPortfolioAccount.getHoldings());

        ManagedPortfolioValuationDto valuation = new ManagedPortfolioValuationDto(managedPortfolioAccount, balances,
                PortfolioUtils.getValuationAsPercent(balances.get("balance"), accountBalance), hasPending, dtoList, false);
        return valuation;
    }

    private Map<String, BigDecimal> getManagedPortfolioBalances(ManagedPortfolioAccountValuation managedPortfolioAccount) {

        BigDecimal interestPaid = BigDecimal.ZERO;
        BigDecimal dividend = BigDecimal.ZERO;
        BigDecimal distribution = BigDecimal.ZERO;

        for (AccountHolding holding : managedPortfolioAccount.getHoldings()) {
            AssetType assetType = holding.getAsset().getAssetType();
            BigDecimal interestEarned = holding.getAccruedIncome();
            if (interestEarned == null) {
                continue;
            }
            if (AssetType.CASH == assetType) {
                interestPaid = interestPaid.add(interestEarned);
            } else if (AssetType.SHARE == assetType) {
                dividend = dividend.add(interestEarned);
            } else if (AssetType.MANAGED_FUND == assetType) {
                distribution = distribution.add(interestEarned);
            }
        }

        BigDecimal totalInterest = interestPaid.add(dividend).add(distribution);
        BigDecimal balance = managedPortfolioAccount.getMarketValue().add(totalInterest);
        BigDecimal averageCost = managedPortfolioAccount.getCost();
        BigDecimal incomePercent = PortfolioUtils.getValuationAsPercent(totalInterest, balance);
        BigDecimal estimatedGain = balance.subtract(averageCost);

        Map<String, BigDecimal> balances = new LinkedHashMap<>();

        balances.put("balance", balance);
        balances.put("averageCost", averageCost);
        balances.put("estimatedGain", estimatedGain);
        balances.put("interestPaid", interestPaid);
        balances.put("dividend", dividend);
        balances.put("distribution", distribution);
        balances.put("totalInterest", totalInterest);
        balances.put("incomePercent", incomePercent);

        return balances;
    }

    private boolean hasPending(List<AccountHolding> holdings) {
        for (AccountHolding holding : holdings) {
            if (holding.getHasPending()) {
                return true;
            }
        }
        return false;
    }
}
