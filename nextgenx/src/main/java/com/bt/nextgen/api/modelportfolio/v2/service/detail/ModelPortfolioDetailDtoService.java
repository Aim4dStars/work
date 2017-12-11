package com.bt.nextgen.api.modelportfolio.v2.service.detail;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelPortfolioDetailDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;

public interface ModelPortfolioDetailDtoService extends ValidateDtoService<ModelPortfolioKey, ModelPortfolioDetailDto>,
        SubmitDtoService<ModelPortfolioKey, ModelPortfolioDetailDto>,
        FindByKeyDtoService<ModelPortfolioKey, ModelPortfolioDetailDto>,
        UpdateDtoService<ModelPortfolioKey, ModelPortfolioDetailDto> {

}
