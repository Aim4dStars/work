package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.core.api.dto.FilterableDtoService;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

public interface AccountDtoService extends SearchByCriteriaDtoService<AccountDto>, FindAllDtoService<AccountDto>,
        FilterableDtoService<AccountDto>
{

}