package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.trading.v1.model.AvailableAssetInfoDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface AvailableAssetInfoDtoService extends FindByKeyDtoService<AccountKey, AvailableAssetInfoDto> {

}
