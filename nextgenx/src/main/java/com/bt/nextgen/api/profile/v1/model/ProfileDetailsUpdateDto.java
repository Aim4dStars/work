package com.bt.nextgen.api.profile.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class ProfileDetailsUpdateDto extends BaseDto implements KeyedDto<String>
{
    private String key;

    public ProfileDetailsUpdateDto(String key)
    {
        this.key = key;
    }

    @Override
    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }
}
