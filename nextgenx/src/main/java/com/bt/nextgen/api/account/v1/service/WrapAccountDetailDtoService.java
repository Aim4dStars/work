package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.WrapAccountDetailDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface WrapAccountDetailDtoService extends FindByKeyDtoService<AccountKey, WrapAccountDetailDto>,
        UpdateDtoService<AccountKey, WrapAccountDetailDto>
{

}