package com.bt.nextgen.api.option.v1.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.KeyedDto;

public class AccountOptionsDto extends OptionsDto implements KeyedDto<AccountKey> {
    private final AccountKey key;

    public AccountOptionsDto(AccountKey key) {
        this.key = key;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }
}
