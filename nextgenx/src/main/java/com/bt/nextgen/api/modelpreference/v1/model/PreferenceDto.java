package com.bt.nextgen.api.modelpreference.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

public class PreferenceDto extends BaseDto {
    private String issuerId;
    private String issuerName;
    private String preference;
    private DateTime effectiveDate;

    public PreferenceDto() {

    }

    public PreferenceDto(String issuerId, String issuerName, String preference, DateTime effectiveDate) {
        super();
        this.issuerId = issuerId;
        this.issuerName = issuerName;
        this.preference = preference;
        this.effectiveDate = effectiveDate;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(String issuerId) {
        this.issuerId = issuerId;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public DateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(DateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

}
