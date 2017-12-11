package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

/**
 * Corporate action summary dto service interface
 * <p/>
 * Implemented by CorporateActionSummaryDtoServiceImpl
 */

public interface CorporateActionDetailsDtoService extends FindByKeyDtoService<CorporateActionDtoKey, CorporateActionDetailsBaseDto> {
}
