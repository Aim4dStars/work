package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface WrapAccountDtoService extends FindByKeyDtoService<AccountKey, AccountDto>
{

}