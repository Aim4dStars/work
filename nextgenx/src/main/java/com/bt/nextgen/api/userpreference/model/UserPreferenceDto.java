package com.bt.nextgen.api.userpreference.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class UserPreferenceDto extends BaseDto implements KeyedDto<UserPreferenceDtoKey> {
    private UserPreferenceDtoKey key;
    private String value;

    public UserPreferenceDto() {
    }

    public UserPreferenceDto(String userType, String preferenceId, String value) {
        this.key = new UserPreferenceDtoKey(userType, preferenceId);
        this.value = value;
    }

    public void setKey(UserPreferenceDtoKey key) {
        this.key = key;
    }

    @Override public UserPreferenceDtoKey getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
