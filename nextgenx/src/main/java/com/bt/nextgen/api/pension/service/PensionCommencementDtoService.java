package com.bt.nextgen.api.pension.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.pension.model.PensionTrxnDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;

/**
 * Created by L067218 on 12/09/2016.
 */

/**
 * Super Pension Commencement service
 */
public interface PensionCommencementDtoService extends SubmitDtoService<AccountKey, PensionTrxnDto> {
}
