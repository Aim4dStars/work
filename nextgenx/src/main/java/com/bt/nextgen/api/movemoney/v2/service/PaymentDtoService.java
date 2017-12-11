package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.core.api.dto.SearchByKeyedCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;

public interface PaymentDtoService extends SearchByKeyedCriteriaDtoService<AccountKey, PaymentDto>,
        ValidateDtoService<AccountKey, PaymentDto>, SubmitDtoService<AccountKey, PaymentDto>,
        UpdateDtoService<AccountKey, PaymentDto>{

}
