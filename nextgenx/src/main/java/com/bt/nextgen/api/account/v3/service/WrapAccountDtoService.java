package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface WrapAccountDtoService extends FindByKeyDtoService<AccountKey, AccountDto> {

}