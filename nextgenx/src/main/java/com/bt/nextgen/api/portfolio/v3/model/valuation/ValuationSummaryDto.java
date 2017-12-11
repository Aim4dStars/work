package com.bt.nextgen.api.portfolio.v3.model.valuation;

import ch.lambdaj.Lambda;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.btfin.panorama.service.integration.asset.AssetType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;

public class ValuationSummaryDto {
    private final AssetType assetType;
    private final List<InvestmentValuationDto> investments;
    private final BigDecimal totalBalance;
    private String thirdPartySource;

    public ValuationSummaryDto(@NotNull AssetType assetType, @NotNull BigDecimal totalBalance,
            @NotNull List<InvestmentValuationDto> investments) {
        super();
        this.assetType = assetType;
        this.totalBalance = totalBalance;
        this.investments = Collections.unmodifiableList(investments);
    }

    public String getCategoryName() {
        return assetType.getGroupDescription();
    }

    public boolean getAllAssetsExternal() {
        boolean allExternal = true;
        for (InvestmentValuationDto valuation : investments) {
            if (valuation.getExternalAsset() == null || !valuation.getExternalAsset()) {
                allExternal = false;
            }
        }
        return allExternal;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        for (InvestmentValuationDto valuation : investments) {
            if (AssetType.MANAGED_PORTFOLIO == assetType || AssetType.TAILORED_PORTFOLIO == assetType) {
                // MP total already includes income
                balance = balance.add(valuation.getBalance());
            } else {
                balance = balance.add(valuation.getBalance());
                balance = balance.add(valuation.getIncome());
            }
        }
        return balance;
    }

    public BigDecimal getInternalBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        for (InvestmentValuationDto valuation : investments) {
            if (valuation.getExternalAsset() == null || !valuation.getExternalAsset()) {
                if (AssetType.MANAGED_PORTFOLIO == assetType || AssetType.TAILORED_PORTFOLIO == assetType) {
                    // MP total already includes income
                    balance = balance.add(valuation.getBalance());
                } else {
                    balance = balance.add(valuation.getBalance());
                    balance = balance.add(valuation.getIncome());
                }
            }
        }
        return balance;
    }

    public BigDecimal getExternalBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        for (InvestmentValuationDto valuation : investments) {
            if (valuation.getExternalAsset() != null && valuation.getExternalAsset()) {
                balance = balance.add(valuation.getBalance());
            }
        }
        return balance;
    }

    public BigDecimal getPortfolioPercent() {
        return PortfolioUtils.getValuationAsPercent(getBalance(), totalBalance);
    }

    public BigDecimal getIncome() {
        BigDecimal income = BigDecimal.ZERO;
        for (InvestmentValuationDto valuation : investments) {
            income = income.add(valuation.getIncome());
        }
        return income;
    }

    public BigDecimal getIncomePercent() {
        return PortfolioUtils.getValuationAsPercent(getIncome(), totalBalance);
    }

    public BigDecimal getOutstandingCash() {
        BigDecimal outstanding = BigDecimal.ZERO;
        boolean hasOutstanding = false;
        for (InvestmentValuationDto valuation : investments) {
            if (valuation instanceof CashManagementValuationDto) {
                BigDecimal cmaOutstanding = ((CashManagementValuationDto) valuation).getOutstandingCash();
                if(cmaOutstanding != null) {
                    outstanding = outstanding.add(cmaOutstanding);
                    hasOutstanding = true;
                }
            }
        }
        if (hasOutstanding) {
            return outstanding;
        }
        return null;
    }

    public BigDecimal getOutstandingCashPercent() {
        BigDecimal outstanding = getOutstandingCash();
        if (outstanding != null) {
            return PortfolioUtils.getValuationAsPercent(outstanding, totalBalance);
        }
        return null;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public List<InvestmentValuationDto> getInvestments() {
        return Lambda.filter(Lambda.having(Lambda.on(InvestmentValuationDto.class).getIncomeOnly(), equalTo(false)), investments);
    }

    public String getThirdPartySource() {
        return thirdPartySource;
    }

    public void setThirdPartySource(String thirdPartySource) {
        this.thirdPartySource = thirdPartySource;
    }
}
