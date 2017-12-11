package com.bt.nextgen.api.modelpreference.v1.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class AccountPreferencesDto extends BaseDto implements KeyedDto<AccountKey> {
    private AccountKey accountKey;
    private List<PreferenceDto> preferences;
    private List<SubaccountPreferencesDto> subAccounts;

    public AccountPreferencesDto(AccountKey accountKey, List<SubaccountPreferencesDto> subaccounts,
            List<PreferenceDto> preferences) {
        super();
        this.accountKey = accountKey;
        this.preferences = preferences;
        this.subAccounts = subaccounts;
    }

    @Override
    public AccountKey getKey() {
        return accountKey;
    }

    public List<PreferenceDto> getPreferences() {
        return preferences;
    }

    public List<SubaccountPreferencesDto> getSubAccounts() {
        return subAccounts;
    }

}
