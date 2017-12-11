package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.AccountSubscriptionDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

public interface AccountSubscriptionDtoService extends UpdateDtoService<AccountKey, AccountSubscriptionDto>,
        FindByKeyDtoService<AccountKey, AccountSubscriptionDto> {
}
