package com.bt.nextgen.api.modelportfolio.v2.model.defaultparams;

import java.math.BigDecimal;

public class TailoredPortfolioDefaultParamsDto extends ModelPortfolioDefaultParamsDto {

    private BigDecimal minimumCashAllocationPercentage;

    public TailoredPortfolioDefaultParamsDto(DealerParameterKey key, BigDecimal minInvestmentAmount,
            BigDecimal minimumCashAllocationPercentage) {
        super(key, minInvestmentAmount);
        this.minimumCashAllocationPercentage = minimumCashAllocationPercentage;
    }

    public BigDecimal getMinimumCashAllocationPercentage() {
        return minimumCashAllocationPercentage;
    }

}
