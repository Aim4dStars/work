package com.bt.nextgen.api.marketdata.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;

public interface UserTierService {
    String getUserTier(AccountKey accountKey);

    boolean isShareEnabled();
}
