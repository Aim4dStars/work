package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.BglDataDto;
import com.bt.nextgen.api.account.v1.model.DateRangeAccountKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface BglDtoService extends FindByKeyDtoService <DateRangeAccountKey, BglDataDto>
{

}
