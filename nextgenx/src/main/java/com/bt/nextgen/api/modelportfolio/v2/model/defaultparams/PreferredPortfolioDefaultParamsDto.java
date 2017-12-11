package com.bt.nextgen.api.modelportfolio.v2.model.defaultparams;

import java.math.BigDecimal;

public class PreferredPortfolioDefaultParamsDto extends ModelPortfolioDefaultParamsDto {

    private BigDecimal defaultAssetTolerance;
    private BigDecimal minTradeAmount;

    public PreferredPortfolioDefaultParamsDto(DealerParameterKey key, BigDecimal defaultAssetTolerance,
            BigDecimal minInvestmentAmount, BigDecimal minTradeAmount) {
        super(key, minInvestmentAmount);
        this.defaultAssetTolerance = defaultAssetTolerance;
        this.minTradeAmount = minTradeAmount;
    }

    public BigDecimal getDefaultAssetTolerance() {
        return defaultAssetTolerance;
    }

    public BigDecimal getMinTradeAmount() {
        return minTradeAmount;
    }

}
