package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.product.ProductKey;

@Deprecated
public interface TermDepositPresentationService {

    TermDepositPresentation getTermDepositPresentation(AccountKey accountKey,String assetId, ServiceErrors serviceErrors);
}
