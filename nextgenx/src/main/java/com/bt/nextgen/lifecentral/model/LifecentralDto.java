package com.bt.nextgen.lifecentral.model;

import com.bt.nextgen.core.api.model.BaseDto;

/**
 * Created by M041926 on 5/08/2016.
 */
public class LifecentralDto extends BaseDto {

    private String lifeCentralUrl;

    private String eamLifeCentralUrl;

    public String getEamLifeCentralUrl() {
        return eamLifeCentralUrl;
    }

    public void setEamLifeCentralUrl(String eamLifeCentralUrl) {
        this.eamLifeCentralUrl = eamLifeCentralUrl;
    }

    public String getLifeCentralUrl() {
        return lifeCentralUrl;
    }

    public void setLifeCentralUrl(String lifeCentralUrl) {
        this.lifeCentralUrl = lifeCentralUrl;
    }
}
