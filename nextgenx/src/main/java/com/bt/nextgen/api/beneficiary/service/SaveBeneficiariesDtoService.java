package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryTrxnDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;

/**
 * Super beneficiaries - save/update service
 */
public interface SaveBeneficiariesDtoService extends SubmitDtoService<AccountKey, BeneficiaryTrxnDto> {
}
