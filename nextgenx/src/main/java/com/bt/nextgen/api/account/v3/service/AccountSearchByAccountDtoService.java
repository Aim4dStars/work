package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByKeyedCriteriaDtoService;

public interface AccountSearchByAccountDtoService extends SearchByKeyedCriteriaDtoService<AccountKey, AccountDto>, SearchByKeyDtoService<AccountKey, AccountDto>
{
}
