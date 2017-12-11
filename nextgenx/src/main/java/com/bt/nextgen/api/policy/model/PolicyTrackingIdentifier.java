package com.bt.nextgen.api.policy.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class PolicyTrackingIdentifier extends BaseDto implements KeyedDto<CustomerKey> {

    private CustomerKey key;

    @Override
    public CustomerKey getKey() {
        return key;
    }

    public void setKey(CustomerKey key) {
        this.key = key;
    }
}
