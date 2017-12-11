package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;

public interface PayeeDtoService extends SubmitDtoService<AccountKey, PaymentDto>, ValidateDtoService<AccountKey, PaymentDto> {
}
