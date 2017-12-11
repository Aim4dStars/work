package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.core.api.dto.SearchOneByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

public interface WrapAccountDetailDtoService extends SearchOneByCriteriaDtoService<WrapAccountDetailDto>,
        UpdateDtoService<AccountKey, WrapAccountDetailDto>
{

}