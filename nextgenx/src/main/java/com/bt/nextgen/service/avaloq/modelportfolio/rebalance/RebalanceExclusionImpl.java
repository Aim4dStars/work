package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceExclusion;

public class RebalanceExclusionImpl implements RebalanceExclusion {
    private AccountKey accountKey;
    private String exclusionReason;
    private boolean included;

    public RebalanceExclusionImpl(AccountKey accountKey, boolean included, String exclusionReason) {
        super();
        this.accountKey = accountKey;
        this.included = included;
        this.exclusionReason = exclusionReason;
    }

    @Override
    public AccountKey getAccountKey() {
        return accountKey;
    }

    @Override
    public String getExclusionReason() {
        return exclusionReason;
    }

    @Override
    public boolean getIncluded() {
        return included;
    }

}
