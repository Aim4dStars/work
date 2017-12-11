package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.DailyLimitDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface PaymentLimitDtoService extends SubmitDtoService <AccountKey, DailyLimitDto>
{
	public DailyLimitDto updateDailyLimit(DailyLimitDto keyedObject, ServiceErrors serviceErrors);
}