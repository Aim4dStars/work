package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Corporate action dto service interface with metdadata
 * <p/>
 * Implemented by CorporateActionDtoServiceImpl
 */

public interface CorporateActionListWithMetadataDtoService extends FindByKeyDtoService<CorporateActionListDtoKey, CorporateActionListDto> {
    CorporateActionListDto searchCommon(CorporateActionListDtoKey key, ServiceErrors serviceErrors);
}
