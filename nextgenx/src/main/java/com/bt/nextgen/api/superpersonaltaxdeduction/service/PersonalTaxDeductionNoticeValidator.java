package com.bt.nextgen.api.superpersonaltaxdeduction.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.core.api.dto.ValidateDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Validator for super tax deduction notices.
 */
public interface PersonalTaxDeductionNoticeValidator extends ValidateDtoService<AccountKey, PersonalTaxDeductionNoticeTrxnDto> {
}
