package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.DailyLimitDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;

public interface PaymentLimitDtoService extends SubmitDtoService<AccountKey, DailyLimitDto> {
    public DailyLimitDto updateDailyLimit(DailyLimitDto keyedObject, ServiceErrors serviceErrors);
}