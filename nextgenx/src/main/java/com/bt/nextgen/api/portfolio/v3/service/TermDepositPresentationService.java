package com.bt.nextgen.api.portfolio.v3.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.account.AccountKey;

public interface TermDepositPresentationService {

    TermDepositPresentation getTermDepositPresentation(AccountKey accountKey, String assetId, ServiceErrors serviceErrors);
}
