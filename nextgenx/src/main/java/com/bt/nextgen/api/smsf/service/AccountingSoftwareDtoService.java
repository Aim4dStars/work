package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.smsf.model.AccountingSoftwareDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

/**
 * Created by L062329 on 12/06/2015.
 */
public interface AccountingSoftwareDtoService extends UpdateDtoService<AccountKey,AccountingSoftwareDto>,
        FindByKeyDtoService<AccountKey, AccountingSoftwareDto> {
}
