package com.bt.nextgen.api.account.v1.model;


import com.bt.nextgen.account.api.model.InvestmentValuationDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
public class ManagedFundValuationDto extends InvestmentValuationDto {
    private final InvestmentAssetDto investmentAsset;

    private final Boolean pendingSellDown;

    private final String distributionMethod;

    private final List<String> availableMethods;

    // suppressed as this class will be deprecated imminently
    @SuppressWarnings("squid:S00107")
    public ManagedFundValuationDto(String subAccountId, String name, ValuationSummaryDto summaryDto, BigDecimal availableBalance,
            InvestmentAssetDto investmentAsset, Boolean pendingSellDown, String distributionMethod, List<String> availableMethods) {
        super(subAccountId, name, summaryDto.getBalance(), availableBalance, summaryDto.getPortfolioPercent(), summaryDto
                .getIncome());
        this.investmentAsset = investmentAsset;
        this.pendingSellDown = pendingSellDown;
        this.distributionMethod = distributionMethod;
        this.availableMethods = availableMethods;
    }

    public InvestmentAssetDto getInvestmentAsset() {
        return investmentAsset;
    }

    public Boolean getPendingSellDown() {
        return pendingSellDown;
    }

    public String getDistributionMethod() {
        return distributionMethod;
    }

    public List<String> getAvailableMethods() {
        return availableMethods;
    }

}
