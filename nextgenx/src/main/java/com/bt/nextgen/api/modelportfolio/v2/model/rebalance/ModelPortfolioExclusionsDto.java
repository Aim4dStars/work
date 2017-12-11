package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class ModelPortfolioExclusionsDto extends BaseDto implements KeyedDto<ModelPortfolioKey> {
    private ModelPortfolioKey key;
    private List<ModelPortfolioExclusionDto> exclusions;

    public ModelPortfolioExclusionsDto(ModelPortfolioKey key, List<ModelPortfolioExclusionDto> exclusions) {
        super();
        this.key = key;
        this.exclusions = exclusions;
    }

    public ModelPortfolioKey getKey() {
        return key;
    }

    public List<ModelPortfolioExclusionDto> getExclusions() {
        return exclusions;
    }
}
