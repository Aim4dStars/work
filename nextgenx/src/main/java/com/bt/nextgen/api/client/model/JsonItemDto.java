package com.bt.nextgen.api.client.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * Created by L075208 on 12/07/2017.
 */
public class JsonItemDto extends BaseDto {

    private String value;

    public JsonItemDto(String value) {
        this.value = value;
    }

    @JsonRawValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}