package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountSearchDto;
import com.bt.nextgen.api.account.v3.model.AccountSearchKey;
import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByKeyedCriteriaDtoService;

public interface AccountSearchByClientDtoService extends SearchByKeyedCriteriaDtoService<AccountSearchKey, AccountSearchDto>, SearchByKeyDtoService<AccountSearchKey, AccountSearchDto>
{
}
