package com.bt.nextgen.api.account.v3.model;

import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class AccountSearchDto extends BaseDto implements KeyedDto<AccountSearchKey> {
    private AccountSearchKey key;
    private String displayName;
    private List<AccountDto> accounts;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<AccountDto> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountDto> accounts) {
        this.accounts = accounts;
    }

    @Override
    public AccountSearchKey getKey() {
        return key;
    }

    public void setKey(AccountSearchKey key) {
        this.key = key;
    }
}
