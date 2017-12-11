package com.bt.nextgen.api.policy.model;

import com.bt.nextgen.core.api.model.BaseDto;

public class CommissionDto extends BaseDto {

    private String commissionType;
    private String initialPercent;
    private String commissionState;
    private String dialDown;
    private String renewalPercent;
    private String commissionSplit;

    public String getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(String commissionType) {
        this.commissionType = commissionType;
    }

    public String getInitialPercent() {
        return initialPercent;
    }

    public void setInitialPercent(String initialPercent) {
        this.initialPercent = initialPercent;
    }

    public String getCommissionState() {
        return commissionState;
    }

    public void setCommissionState(String commissionState) {
        this.commissionState = commissionState;
    }

    public String getDialDown() {
        return dialDown;
    }

    public void setDialDown(String dialDown) {
        this.dialDown = dialDown;
    }

    public String getRenewalPercent() {
        return renewalPercent;
    }

    public void setRenewalPercent(String renewalPercent) {
        this.renewalPercent = renewalPercent;
    }

    public String getCommissionSplit() {
        return commissionSplit;
    }

    public void setCommissionSplit(String commissionSplit) {
        this.commissionSplit = commissionSplit;
    }
}
