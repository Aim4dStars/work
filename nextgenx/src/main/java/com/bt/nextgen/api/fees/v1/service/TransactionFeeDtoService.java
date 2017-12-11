package com.bt.nextgen.api.fees.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.fees.v1.model.AssetMappedAccountTransactionFeesDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface TransactionFeeDtoService extends FindByKeyDtoService<AccountKey, AssetMappedAccountTransactionFeesDto> {

}
