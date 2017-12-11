package com.bt.nextgen.api.user.v1.model;

public class TermsAndConditionsDtoKey {
    private String userId;
    private String termAndConditions;
    private Integer version;

    public TermsAndConditionsDtoKey(String userId, String termAndConditions, Integer version) {
        super();
        this.userId = userId;
        this.termAndConditions = termAndConditions;
        this.version = version;
    }

    public String getUserId() {
        return userId;
    }

    public String getTermAndConditions() {
        return termAndConditions;
    }

    public Integer getVersion() {
        return version;
    }

}
