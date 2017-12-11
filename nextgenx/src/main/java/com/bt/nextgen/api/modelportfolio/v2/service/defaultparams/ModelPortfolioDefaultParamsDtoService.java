package com.bt.nextgen.api.modelportfolio.v2.service.defaultparams;

import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.DealerParameterKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioDefaultParamsDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface ModelPortfolioDefaultParamsDtoService extends
        FindByKeyDtoService<DealerParameterKey, ModelPortfolioDefaultParamsDto> {

}
