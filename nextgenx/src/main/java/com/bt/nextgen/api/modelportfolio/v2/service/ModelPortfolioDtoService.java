package com.bt.nextgen.api.modelportfolio.v2.service;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentData;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;

public interface ModelPortfolioDtoService extends FindAllDtoService<ModelPortfolioDto>,
        FindByKeyDtoService<ModelPortfolioKey, ModelPortfolioDto> {
    public FinancialDocumentData loadMonthlyModelDocument(String modelId, FinancialDocumentType financialDocumentType);
}
