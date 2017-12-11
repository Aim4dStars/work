package com.bt.nextgen.api.fees.v2.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.fees.v2.model.AssetMappedAccountTransactionFeesDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface TransactionFeeDtoService extends FindByKeyDtoService<AccountKey, AssetMappedAccountTransactionFeesDto> {

}
