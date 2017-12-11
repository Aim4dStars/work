package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.core.api.dto.FilterableDtoService;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface AccountDtoService extends SearchByCriteriaDtoService<AccountDto>, FindAllDtoService<AccountDto>,
        FilterableDtoService<AccountDto>
{

}