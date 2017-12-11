package com.bt.nextgen.api.saml.model;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * Created by F030695 on 20/01/2016.
 */
public class SamlTokenRefreshDto extends BaseDto {

    private boolean refreshed;

    public SamlTokenRefreshDto(boolean refreshed) {
        this.refreshed = refreshed;
    }

    public boolean isRefreshed() {
        return refreshed;
    }

    public void setRefreshed(boolean refreshed) {
        this.refreshed = refreshed;
    }
}
