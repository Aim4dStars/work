package com.bt.nextgen.api.option.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.option.v1.model.AccountOptionsDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface AccountOptionDtoService
 extends FindByKeyDtoService<AccountKey, AccountOptionsDto>
{

}
