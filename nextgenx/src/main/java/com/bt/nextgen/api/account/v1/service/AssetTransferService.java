package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.transitions.TransitionAssetDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

/**
 * Created by L069552 on 28/09/2015.
 */
@Deprecated
public interface AssetTransferService extends SearchByCriteriaDtoService<TransitionAssetDto> {
}
