package com.bt.nextgen.api.subscriptions.model;

import com.bt.nextgen.api.draftaccount.model.TransitionStateDto;
import com.google.common.base.Objects;

/**
 * WorkFlow status for Subscriptions
 */
public class WorkFlowStatusDto extends TransitionStateDto {
    private String status;
    private String helpId;

    public WorkFlowStatusDto() {
        super(null, null);
    }

    public WorkFlowStatusDto(String state, String date) {
        super(state, date);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHelpId() {
        return helpId;
    }

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("state", getState())
                .add("date ", getDate())
                .add("status", status)
                .toString();
    }
}