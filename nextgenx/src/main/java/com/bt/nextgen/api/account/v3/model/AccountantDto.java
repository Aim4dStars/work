package com.bt.nextgen.api.account.v3.model;

import com.bt.nextgen.api.broker.model.BrokerDto;

public class AccountantDto extends BrokerDto {
    private String accountingSoftware;
    private String accountingSoftwareDisplayName;
    private String externalAssetsFeedState;

    public String getAccountingSoftware() {
        return accountingSoftware;
    }

    public void setAccountingSoftware(String accountingSoftware) {
        this.accountingSoftware = accountingSoftware;
    }

    public String getAccountingSoftwareDisplayName() {
        return accountingSoftwareDisplayName;
    }

    public void setAccountingSoftwareDisplayName(String accountingSoftwareDisplayName) {
        this.accountingSoftwareDisplayName = accountingSoftwareDisplayName;
    }

    public String getExternalAssetsFeedState() {
        return externalAssetsFeedState;
    }

    public void setExternalAssetsFeedState(String externalAssetsFeedState) {
        this.externalAssetsFeedState = externalAssetsFeedState;
    }
}
