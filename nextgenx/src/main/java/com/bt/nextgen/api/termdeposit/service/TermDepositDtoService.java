package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositDetailDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;

public interface TermDepositDtoService
        extends ValidateDtoService<AccountKey, TermDepositDetailDto>, SubmitDtoService<AccountKey, TermDepositDetailDto> {
}