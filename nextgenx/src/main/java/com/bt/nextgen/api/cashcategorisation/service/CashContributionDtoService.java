package com.bt.nextgen.api.cashcategorisation.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Supporting interface to retrieve and persist member contributions on a cash transaction
 */
public interface CashContributionDtoService extends SubmitDtoService<AccountKey, CategorisableCashTransactionDto>
{

}
