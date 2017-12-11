package com.bt.nextgen.api.corporateaction.v1.service;


import java.util.List;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;

public interface ImCorporateActionPortfolioModelDtoService {
    List<ImCorporateActionPortfolioModelDto> toCorporateActionPortfolioModelDto(CorporateActionContext context,
                                                                                List<CorporateActionAccount> accounts,
                                                                                CorporateActionSavedDetails savedDetails,
                                                                                ServiceErrors serviceErrors);
}
