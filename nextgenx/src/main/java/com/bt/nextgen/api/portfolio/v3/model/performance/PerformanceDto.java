package com.bt.nextgen.api.portfolio.v3.model.performance;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.KeyedDto;

public class PerformanceDto extends PerformanceBaseDto implements KeyedDto<AccountKey> {
    private AccountKey key;

    public PerformanceDto() {
        super();
    }

    @Override
    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

}
