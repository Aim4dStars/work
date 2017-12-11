package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.api.movemoney.v2.model.BillerKey;
import com.bt.nextgen.api.movemoney.v2.model.BpayBillerDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;

public interface BPayBillerDtoService extends FindAllDtoService<BpayBillerDto>, ValidateDtoService<BillerKey, BpayBillerDto> {
}
