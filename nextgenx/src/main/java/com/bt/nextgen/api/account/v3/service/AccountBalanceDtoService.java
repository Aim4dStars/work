package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

public interface AccountBalanceDtoService extends FindByKeyDtoService<AccountKey, AccountBalanceDto>,
        SearchByCriteriaDtoService<AccountBalanceDto> {
}
