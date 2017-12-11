package com.bt.nextgen.api.account.v1.service;

import java.util.List;
import java.util.Map;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.PaymentDto;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface PaymentDtoService extends SearchByCriteriaDtoService<PaymentDto>, ValidateDtoService<AccountKey, PaymentDto>,
        SubmitDtoService<AccountKey, PaymentDto> {

    public List<PaymentDto> loadPayees(String accountId, String model, ServiceErrors serviceErrors);

    public PaymentDto validatePayment(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors);

    public Map<String, PaymentDto> loadPaymentReciepts();
}
