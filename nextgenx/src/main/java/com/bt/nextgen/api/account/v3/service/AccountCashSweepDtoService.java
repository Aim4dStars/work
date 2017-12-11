package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountCashSweepDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

/**
 * Interface for the cash sweep service
 */
public interface AccountCashSweepDtoService extends UpdateDtoService<AccountKey, AccountCashSweepDto>,
        FindByKeyDtoService<AccountKey, AccountCashSweepDto> {
}
