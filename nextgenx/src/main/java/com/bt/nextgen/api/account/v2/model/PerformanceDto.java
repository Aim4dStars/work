package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.KeyedDto;

@Deprecated
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
