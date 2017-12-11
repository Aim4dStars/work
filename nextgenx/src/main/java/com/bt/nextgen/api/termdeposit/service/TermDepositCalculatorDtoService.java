package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDto;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.core.api.dto.FindByPartialKeyDtoService;

public interface TermDepositCalculatorDtoService
        extends FindByPartialKeyDtoService<TermDepositCalculatorKey, TermDepositCalculatorDto> {
    String getTermDepositRatesAsCsv(String brand, String type, String productId);
}
