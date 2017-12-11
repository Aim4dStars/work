package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.AvailableCashDto;

import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface AvailableCashDtoService extends FindByKeyDtoService <AccountKey, AvailableCashDto>
{

}