package com.bt.nextgen.api.account.v2.model;

import ch.lambdaj.Lambda;
import com.btfin.panorama.service.integration.asset.AssetType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;

@Deprecated
public class ValuationSummaryDto {
    private final AssetType assetType;
    private final boolean allAssetsExternal;
    private final BigDecimal portfolioPercent;
    private final BigDecimal income;
    private final BigDecimal incomePercent;
    private final List<InvestmentValuationDto> investments;


    public ValuationSummaryDto(AssetType assetType, boolean allAssetsExternal, BigDecimal portfolioPercent, BigDecimal income,
            BigDecimal incomePercent, List<InvestmentValuationDto> investments) {
        super();
        this.assetType = assetType;
        this.allAssetsExternal = allAssetsExternal;
        this.portfolioPercent = portfolioPercent;
        this.income = income;
        this.incomePercent = incomePercent;
        this.investments = investments == null ? null : Collections.unmodifiableList(investments);
    }

    public String getCategoryName() {
        return assetType.getGroupDescription();
    }

    public boolean getAllAssetsExternal() {
        return allAssetsExternal;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (investments != null) {
            for (InvestmentValuationDto valuation : investments) {
                if (AssetType.MANAGED_PORTFOLIO == assetType) {
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

    public BigDecimal getInternalBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (investments != null) {
            for (InvestmentValuationDto valuation : investments) {
                if (valuation.getExternalAsset() == null || !valuation.getExternalAsset()) {
                    if (AssetType.MANAGED_PORTFOLIO == assetType) {
                        // MP total already includes income
                        balance = balance.add(valuation.getBalance());
                    } else {
                        balance = balance.add(valuation.getBalance());
                        balance = balance.add(valuation.getIncome());
                    }
                }
            }
        }
        return balance;
    }

    public BigDecimal getExternalBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (investments != null) {
            for (InvestmentValuationDto valuation : investments) {
                if (valuation.getExternalAsset() != null && valuation.getExternalAsset()) {
                    balance = balance.add(valuation.getBalance());
                }
            }
        }
        return balance;
    }

    public BigDecimal getPortfolioPercent() {
        return portfolioPercent;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public BigDecimal getIncomePercent() {
        return incomePercent;
    }

    public String getAssetType() {
        return assetType.name();
    }

    public List<InvestmentValuationDto> getInvestments() {
        return Lambda.filter(Lambda.having(Lambda.on(InvestmentValuationDto.class).getIncomeOnly(), equalTo(false)), investments);
    }

}
