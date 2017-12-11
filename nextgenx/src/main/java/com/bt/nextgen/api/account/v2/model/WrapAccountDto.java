package com.bt.nextgen.api.account.v2.model;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

@Deprecated
public class WrapAccountDto extends BaseDto implements KeyedDto<AccountKey> {
    private AccountKey key;

    private DateTime accountStartDate;

    private DateTime closureDate;

    public WrapAccountDto() {
    }

    public WrapAccountDto(AccountKey key, DateTime accountStartDate, DateTime closureDate) {
        this.key = key;
        this.accountStartDate = accountStartDate;
        this.closureDate = closureDate;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }

    public DateTime getAccountStartDate() {
        return accountStartDate;
    }

    public DateTime getClosureDate() {
        return closureDate;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }
}
