package com.bt.nextgen.api.modelportfolio.v2.service;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioUploadDto;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;

public interface ModelPortfolioUploadDtoService extends ValidateDtoService<ModelPortfolioKey, ModelPortfolioUploadDto>,
        SubmitDtoService<ModelPortfolioKey, ModelPortfolioUploadDto>,
        UpdateDtoService<ModelPortfolioKey, ModelPortfolioUploadDto>,
        SearchByCriteriaDtoService<ModelPortfolioUploadDto>{

}
