package com.bt.nextgen.api.pushnotification.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

/**
 * Dto for Push notification API
 */

public class PushSubscriptionDto extends BaseDto implements KeyedDto<PushSubscriptionDtoKey> {

    private PushSubscriptionDtoKey key;
    private String platform;
    private String subscriptionAction;

    public PushSubscriptionDto() {
    }

    /**
     * @param key
     * @param platform
     * @param subscriptionAction
     */
    public PushSubscriptionDto(PushSubscriptionDtoKey key, String platform, String subscriptionAction) {
        this.key = key;
        this.platform = platform;
        this.subscriptionAction = subscriptionAction;
    }

    @Override
    public PushSubscriptionDtoKey getKey() {
        return key;
    }

    public void setKey(PushSubscriptionDtoKey key) {
        this.key = key;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSubscriptionAction() {
        return subscriptionAction;
    }

    public void setSubscriptionAction(String subscriptionAction) {
        this.subscriptionAction = subscriptionAction;
    }
}
