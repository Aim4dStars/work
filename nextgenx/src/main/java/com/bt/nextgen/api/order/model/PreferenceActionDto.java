package com.bt.nextgen.api.order.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import com.bt.nextgen.service.integration.order.PreferenceAction;
import com.fasterxml.jackson.annotation.JsonView;

public class PreferenceActionDto extends BaseDto {

    @JsonView(JsonViews.Write.class)
    private String issuerId;

    @JsonView(JsonViews.Read.class)
    private String issuerName;

    @JsonView(JsonViews.Write.class)
    private Preference preference;

    @JsonView(JsonViews.Write.class)
    private PreferenceAction action;


    public PreferenceActionDto() {
        super();
    }

    public PreferenceActionDto(String issuerId, String issuerName, Preference preference, PreferenceAction action) {
        super();
        this.issuerId = issuerId;
        this.preference = preference;
        this.issuerName = issuerName;
        this.action = action;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getIssuerId() {
        return issuerId;
    }


    public Preference getPreference() {
        return preference;
    }

    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public PreferenceAction getAction() {
        return action;
    }

    public void setAction(PreferenceAction action) {
        this.action = action;
    }

}
