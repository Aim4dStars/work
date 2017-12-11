package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountDto;
import com.bt.nextgen.core.api.dto.FilterableDtoService;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

@Deprecated
public interface AccountDtoService extends SearchByCriteriaDtoService<AccountDto>, FindAllDtoService<AccountDto>,
        FilterableDtoService<AccountDto>
{

}