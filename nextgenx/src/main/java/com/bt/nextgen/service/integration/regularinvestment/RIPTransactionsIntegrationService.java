package com.bt.nextgen.service.integration.regularinvestment;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;

import java.util.List;

public interface RIPTransactionsIntegrationService {

    public List<RegularInvestmentTransaction> loadRegularInvestments(AccountKey accountKey, ServiceErrors serviceErrors);

}
