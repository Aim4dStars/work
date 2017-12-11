package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;

import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

@Deprecated
public interface AccountBalanceDtoService extends FindByKeyDtoService<AccountKey, AccountBalanceDto>,
        SearchByCriteriaDtoService<AccountBalanceDto> {
}
