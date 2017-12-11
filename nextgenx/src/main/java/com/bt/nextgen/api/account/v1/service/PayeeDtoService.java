package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.PaymentDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface PayeeDtoService extends SubmitDtoService <AccountKey, PaymentDto>,ValidateDtoService <AccountKey, PaymentDto>
{

	public PaymentDto addPayee(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors);
}
