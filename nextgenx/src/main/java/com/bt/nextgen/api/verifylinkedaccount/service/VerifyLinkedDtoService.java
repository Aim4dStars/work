package com.bt.nextgen.api.verifylinkedaccount.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.verifylinkedaccount.model.LinkedAccountDetailsDto;
import com.bt.nextgen.api.verifylinkedaccount.model.VerificationLinkedAccountStatusDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Created by l078480 on 22/08/2017.
 */
public interface VerifyLinkedDtoService extends SubmitDtoService<AccountKey, LinkedAccountDetailsDto>{

}
