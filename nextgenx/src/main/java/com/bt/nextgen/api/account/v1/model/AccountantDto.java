package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.api.broker.model.BrokerDto;

/**
 * @deprecated Use V2
 */
@Deprecated
public class AccountantDto extends BrokerDto {
    private String accountingSoftware;
    private String externalAssetsFeedState;
    public String getAccountingSoftware() {
        return accountingSoftware;
    }

    public void setAccountingSoftware(String accountingSoftware) {
        this.accountingSoftware = accountingSoftware;
    }

    public String getExternalAssetsFeedState() {
        return externalAssetsFeedState;
    }

    public void setExternalAssetsFeedState(String externalAssetsFeedState) {
        this.externalAssetsFeedState = externalAssetsFeedState;
    }
}
