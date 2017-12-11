package com.bt.nextgen.api.trading.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class TradeAssetTypeDto extends BaseDto implements KeyedDto<String> {

    private final String value;
    private final String label;

    public TradeAssetTypeDto(String value, String label) {
        this.value = value;
        this.label = label;
    }

    @Override
    public String getKey() {
        return getValue();
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
