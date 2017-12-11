package com.bt.nextgen.api.modelportfolio.v2.model.defaultparams;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.math.BigDecimal;

public class ModelPortfolioDefaultParamsDto extends BaseDto implements KeyedDto<DealerParameterKey> {

    private DealerParameterKey key;
    private BigDecimal minInvestmentAmount;

    public ModelPortfolioDefaultParamsDto(DealerParameterKey key) {
        super();
        this.key = key;
    }

    public ModelPortfolioDefaultParamsDto(DealerParameterKey key, BigDecimal minInvestmentAmount) {
        this(key);
        this.minInvestmentAmount = minInvestmentAmount;
    }

    public DealerParameterKey getKey() {
        return key;
    }

    public BigDecimal getMinInvestmentAmount() {
        return minInvestmentAmount;
    }

}
