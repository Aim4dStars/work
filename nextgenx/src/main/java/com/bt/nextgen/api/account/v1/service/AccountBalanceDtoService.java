package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountBalanceDto;
import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface AccountBalanceDtoService extends FindByKeyDtoService<AccountKey, AccountBalanceDto> {
}
