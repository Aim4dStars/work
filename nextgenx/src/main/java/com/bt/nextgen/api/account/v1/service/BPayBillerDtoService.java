package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.BillerKey;
import com.bt.nextgen.api.account.v1.model.BpayBillerDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface BPayBillerDtoService extends FindAllDtoService<BpayBillerDto>,
    ValidateDtoService<BillerKey, BpayBillerDto> {
}
