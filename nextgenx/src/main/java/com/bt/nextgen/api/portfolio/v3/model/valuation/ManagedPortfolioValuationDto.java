package com.bt.nextgen.api.portfolio.v3.model.valuation;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioStatus;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;

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
    private final Boolean hasPending;
    private final Boolean pendingSellDown;
    private final Boolean pendingClosure;
    private final List<InvestmentAssetDto> investmentAssets;
    private final String name;
    private final Boolean tailorMade;
    private final IncomePreference incomePreference;

    public ManagedPortfolioValuationDto(ManagedPortfolioAccountValuation mpAccount, Map<String, BigDecimal> balances,
            BigDecimal accountBalance, Boolean hasPending, List<InvestmentAssetDto> investments, boolean externalAsset) {

        super(mpAccount.getSubAccountKey() == null ? null
                : EncodedString.fromPlainText(mpAccount.getSubAccountKey().getId()).toString(), balances.get("balance"),
                mpAccount.getAvailableBalance(), accountBalance, balances.get("totalInterest"), null, externalAsset, false);

        this.assetId = mpAccount.getAsset() != null ? mpAccount.getAsset().getAssetId() : "";
        this.assetCode = mpAccount.getAsset() != null ? mpAccount.getAsset().getAssetCode() : "";
        this.tailorMade = mpAccount.getAsset() != null && mpAccount.getAsset().getAssetType() == AssetType.TAILORED_PORTFOLIO;
        this.yield = mpAccount.getYield();
        this.cost = balances.get("averageCost");
        this.capgainDollar = balances.get("estimatedGain");
        this.dividend = balances.get("dividend");
        this.distribution = balances.get("distribution");
        this.interestPaid = balances.get("interestPaid");
        this.incomePercent = balances.get("incomePercent");
        this.hasPending = hasPending;
        this.pendingSellDown = mpAccount.getHasPendingSellDown();
        this.pendingClosure = mpAccount.getStatus() == ManagedPortfolioStatus.PENDING_CLOSURE;
        this.investmentAssets = investments;
        this.name = mpAccount.getAsset() == null ? "" : mpAccount.getAsset().getAssetName();
        this.incomePreference = mpAccount.getIncomePreference();
    }

    public ManagedPortfolioValuationDto(AccountHolding holding, Map<String, BigDecimal> balances, Boolean hasPending,
            Boolean pendingSellDown, List<InvestmentAssetDto> investments, boolean externalAsset) {

        super(null, balances.get("balance"), holding.getAvailableBalance(), balances.get("accountBalance"), balances
                .get("totalInterest"),
                null, externalAsset, holding.getIncomeOnly());

        this.assetId = holding.getAsset() != null ? holding.getAsset().getAssetId() : "";
        this.assetCode = holding.getAsset() != null ? holding.getAsset().getAssetCode() : "";
        this.tailorMade = false;
        this.yield = holding.getYield();
        this.cost = balances.get("averageCost");
        this.capgainDollar = balances.get("estimatedGain");
        this.dividend = balances.get("dividend");
        this.distribution = balances.get("distribution");
        this.hasPending = hasPending;
        this.pendingSellDown = pendingSellDown;
        this.pendingClosure = false;
        this.interestPaid = balances.get("interestPaid");
        this.incomePercent = balances.get("incomePercent");
        this.investmentAssets = investments;
        this.name = holding.getAsset() == null ? "" : holding.getAsset().getAssetName();
        this.incomePreference = null;
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
        if (cost != null && !cost.equals(BigDecimal.ZERO)) {
            return PortfolioUtils.getValuationAsPercent(capgainDollar, cost);
        }
        return null;
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

    public Boolean getPendingSellDown() {
        return pendingSellDown;
    }

    public Boolean getPendingClosure() {
        return pendingClosure;
    }

    public List<InvestmentAssetDto> getInvestmentAssets() {
        return Lambda
                .filter(Lambda.having(Lambda.on(InvestmentAssetDto.class).getIncomeOnly(), equalTo(false)), investmentAssets);
    }

    @Override
    public String getName() {
        return name;
    }

    public Boolean getTailorMade() {
        return tailorMade;
    }

    @Override
    public String getCategoryName() {
        return AssetType.MANAGED_PORTFOLIO.getGroupDescription();
    }

    public IncomePreference getIncomePreference() {
        return incomePreference;
    }
}
