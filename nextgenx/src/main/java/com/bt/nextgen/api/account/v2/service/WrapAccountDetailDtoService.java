package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.WrapAccountDetailDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

@Deprecated
public interface WrapAccountDetailDtoService extends FindByKeyDtoService<AccountKey, WrapAccountDetailDto>,
        UpdateDtoService<AccountKey, WrapAccountDetailDto>
{

}