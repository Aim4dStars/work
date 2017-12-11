package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.account.api.model.InvestmentValuationDto;
import com.bt.nextgen.service.avaloq.PortfolioUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
public class ManagedPortfolioValuationDto extends InvestmentValuationDto {
    private final String assetCode;
    private final String assetId;
    private final BigDecimal yield;
    private final BigDecimal cost;
    private final BigDecimal capgainDollar;
    private final BigDecimal dividend;
    private final BigDecimal distribution;
    private final BigDecimal interestPaid;
    private final BigDecimal incomePercent;
    private final Boolean hasPending;
    private final List<InvestmentAssetDto> investmentAssets;

    // Resolved number of method arguments in v2.
    @SuppressWarnings({ "squid:S00107" })
    public ManagedPortfolioValuationDto(String subAccountId, String assetId, String name, String assetCode, BigDecimal yield,
            BigDecimal balance, BigDecimal availableBalance, BigDecimal portfolioPercent, BigDecimal cost,
            BigDecimal capgainDollar, BigDecimal interestPaid, BigDecimal dividend, BigDecimal distribution, Boolean hasPending,
            BigDecimal incomePercent, List<InvestmentAssetDto> investments) {
        super(subAccountId, name, balance, availableBalance, portfolioPercent, interestPaid.add(dividend).add(distribution));
        this.assetId = assetId;
        this.assetCode = assetCode;
        this.yield = yield;
        this.cost = cost;
        this.capgainDollar = capgainDollar;
        this.dividend = dividend;
        this.distribution = distribution;
        this.hasPending = hasPending;
        this.interestPaid = interestPaid;
        this.incomePercent = incomePercent;
        this.investmentAssets = investments;
    }

    public String getAssetId() {
        return assetId;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getCapgainDollar() {
        return capgainDollar;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public BigDecimal getCapgainPercent() {
        return PortfolioUtils.getValuationAsPercent(capgainDollar, cost);
    }

    public BigDecimal getDividend() {
        return dividend;
    }

    public BigDecimal getDistribution() {
        return distribution;
    }

    public BigDecimal getInterestPaid() {
        return interestPaid;
    }

    public BigDecimal getIncomePercent() {
        return incomePercent;
    }

    public Boolean getHasPending() {
        return hasPending;
    }

    public List<InvestmentAssetDto> getInvestmentAssets() {
        return investmentAssets;
    }
}
