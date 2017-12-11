package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;

import java.util.List;

public class ModelPortfolioRebalanceDetailDto extends ModelDto {

    private Integer totalAccountsCount;
    private List<ModelPortfolioRebalanceAccountDto> rebalanceAccounts;

    public ModelPortfolioRebalanceDetailDto(ModelPortfolioKey key, String modelName, String modelCode,
            Integer totalAccountsCount, List<ModelPortfolioRebalanceAccountDto> rebalanceAccounts) {
        super(key, modelName, modelCode);
        this.totalAccountsCount = totalAccountsCount;
        this.rebalanceAccounts = rebalanceAccounts;
    }

    public Integer getTotalAccountsCount() {
        return totalAccountsCount;
    }

    public List<ModelPortfolioRebalanceAccountDto> getRebalanceAccounts() {
        return rebalanceAccounts;
    }
}

