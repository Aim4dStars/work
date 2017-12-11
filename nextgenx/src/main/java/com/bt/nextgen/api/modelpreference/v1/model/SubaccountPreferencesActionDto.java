package com.bt.nextgen.api.modelpreference.v1.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.order.model.PreferenceActionDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class SubaccountPreferencesActionDto extends BaseDto implements KeyedDto<AccountKey> {
    private AccountKey subaccountKey;
    private List<PreferenceActionDto> preferences;


    public SubaccountPreferencesActionDto(AccountKey subaccountKey, List<PreferenceActionDto> preferences) {
        super();
        this.subaccountKey = subaccountKey;
        this.preferences = preferences;
    }

    @Override
    public AccountKey getKey() {
        return subaccountKey;
    }

    public List<PreferenceActionDto> getPreferences() {
        return preferences;
    }

}
