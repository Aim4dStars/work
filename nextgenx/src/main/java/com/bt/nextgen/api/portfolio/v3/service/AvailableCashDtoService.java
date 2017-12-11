package com.bt.nextgen.api.portfolio.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.portfolio.v3.model.AvailableCashDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface AvailableCashDtoService extends FindByKeyDtoService <AccountKey, AvailableCashDto>
{

}