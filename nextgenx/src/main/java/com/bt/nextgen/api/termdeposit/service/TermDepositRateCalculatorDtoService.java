package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.termdeposit.model.Badge;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDto;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositRateDetails;
import com.bt.nextgen.core.api.dto.FindByPartialKeyDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;

import java.util.List;
import java.util.Set;

/**
 * Created by l079353 on 10/07/2017.
 */
public interface TermDepositRateCalculatorDtoService
        extends FindByPartialKeyDtoService<TermDepositCalculatorKey, TermDepositCalculatorDto> {
    TermDepositRateDetails getTermDepositInterestRatesWithBadges(TermDepositCalculatorKey key,
                 Set<Badge> badges,ServiceErrors serviceErrors);

}
