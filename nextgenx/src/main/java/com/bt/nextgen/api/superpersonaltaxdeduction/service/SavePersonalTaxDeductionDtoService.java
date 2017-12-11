package com.bt.nextgen.api.superpersonaltaxdeduction.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Super Personal Tax Deduction Capture and Vary Details - Submit
 */
public interface SavePersonalTaxDeductionDtoService extends SubmitDtoService<AccountKey, PersonalTaxDeductionNoticeTrxnDto> {
}
