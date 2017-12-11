package com.bt.nextgen.api.smsf.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;

import com.bt.nextgen.api.smsf.model.ExternalAssetTrxnDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;

/**
 * Smsf external assets - save/update service
 */
public interface SaveExternalAssetsService extends SubmitDtoService <AccountKey, ExternalAssetTrxnDto>{

}
