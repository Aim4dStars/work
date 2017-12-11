package com.bt.nextgen.api.account.v2.model;

import ch.lambdaj.Lambda;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsEqual.*;

@Deprecated
public class ManagedPortfolioValuationDto extends AbstractInvestmentValuationDto {

    private final String assetCode;
    private final String assetId;
    private final BigDecimal yield;
    private final BigDecimal cost;
    private final BigDecimal capgainDollar;
    private final BigDecimal dividend;
    private final BigDecimal distribution;
    private final BigDecimal interestPaid;
    private final BigDecimal incomePercent;
    private final AssetType assetType;
    private final Boolean hasPending;
    private final List<InvestmentAssetDto> investmentAssets;
    private final String name;

    public ManagedPortfolioValuationDto(ManagedPortfolioAccountValuation mpAccount, Map<String, BigDecimal> balances,
            BigDecimal portfolioPercent, Boolean hasPending, List<InvestmentAssetDto> investments, boolean externalAsset) {
        super(mpAccount.getSubAccountKey() == null ? null
                : EncodedString.fromPlainText(mpAccount.getSubAccountKey().getId())
                .toString(), balances.get("balance"), mpAccount.getAvailableBalance(), portfolioPercent, balances
                .get("totalInterest"), null, externalAsset, false);
        this.assetId = mpAccount.getAsset() != null ? mpAccount.getAsset().getAssetId() : "";
        this.assetCode = mpAccount.getAsset() != null ? mpAccount.getAsset().getAssetCode() : "";
        this.assetType = mpAccount.getAsset() != null ? mpAccount.getAsset().getAssetType() : AssetType.MANAGED_PORTFOLIO;
        this.yield = mpAccount.getYield();
        this.cost = balances.get("averageCost");
        this.capgainDollar = balances.get("estimatedGain");
        this.dividend = balances.get("dividend");
        this.distribution = balances.get("distribution");
        this.hasPending = hasPending;
        this.interestPaid = balances.get("interestPaid");
        this.incomePercent = balances.get("incomePercent");
        this.investmentAssets = investments;
        this.name = mpAccount.getAsset() == null ? "" : mpAccount.getAsset().getAssetName();
    }

    public ManagedPortfolioValuationDto(AccountHolding holding, Map<String, BigDecimal> balances, BigDecimal portfolioPercent,
            Boolean hasPending, List<InvestmentAssetDto> investments, boolean externalAsset) {
        super(null, balances.get("balance"), holding.getAvailableBalance(), portfolioPercent, balances.get("totalInterest"),
                null, externalAsset, holding.getIncomeOnly());

        this.assetId = holding.getAsset() != null ? holding.getAsset().getAssetId() : "";
        this.assetCode = holding.getAsset() != null ? holding.getAsset().getAssetCode() : "";
        this.assetType = AssetType.MANAGED_PORTFOLIO;
        this.yield = holding.getYield();
        this.cost = balances.get("averageCost");
        this.capgainDollar = balances.get("estimatedGain");
        this.dividend = balances.get("dividend");
        this.distribution = balances.get("distribution");
        this.hasPending = hasPending;
        this.interestPaid = balances.get("interestPaid");
        this.incomePercent = balances.get("incomePercent");
        this.investmentAssets = investments;
        this.name = holding.getAsset() == null ? "" : holding.getAsset().getAssetName();
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
        return Lambda.filter(Lambda.having(Lambda.on(InvestmentAssetDto.class).getIncomeOnly(), equalTo(false)),
                investmentAssets);
    }


    public String getName() {
        return name;
    }

    @Override
    public String getCategoryName() {
        return assetType.getGroupDescription();
    }
}
