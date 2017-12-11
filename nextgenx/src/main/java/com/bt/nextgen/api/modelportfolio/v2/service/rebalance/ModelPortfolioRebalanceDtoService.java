package com.bt.nextgen.api.modelportfolio.v2.service.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;

public interface ModelPortfolioRebalanceDtoService
        extends FindAllDtoService<ModelPortfolioRebalanceDto>, SubmitDtoService<ModelPortfolioKey, ModelPortfolioRebalanceDto> {

}
