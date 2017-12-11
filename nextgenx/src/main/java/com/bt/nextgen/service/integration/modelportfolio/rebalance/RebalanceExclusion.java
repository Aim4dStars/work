package com.bt.nextgen.service.integration.modelportfolio.rebalance;

import com.bt.nextgen.service.integration.account.AccountKey;

public interface RebalanceExclusion {
    public AccountKey getAccountKey();

    public String getExclusionReason();

    public boolean getIncluded();
}
