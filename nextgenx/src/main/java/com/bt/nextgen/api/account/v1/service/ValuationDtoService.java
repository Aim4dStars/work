package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.api.account.v1.model.ValuationDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface ValuationDtoService extends FindByKeyDtoService <DatedAccountKey, ValuationDto>
{

}
