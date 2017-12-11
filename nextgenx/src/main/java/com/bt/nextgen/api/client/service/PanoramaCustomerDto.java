package com.bt.nextgen.api.client.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Existing Panorama customer details to be used in CustomerDataDto (GCM dto) by association only.
 *
 * Created by M040398 (Florin.Adochiei@btfinancialgroup.com) on 20/07/2017.
 */
public class PanoramaCustomerDto {
    private boolean hasTfn;
    private boolean hasDirectSuperAccount;
    private boolean hasDirectPensionAccount;
    private boolean hasTfnMatched; // Indicates whether the Super check TFN is present in ABS
    private boolean hasPensionExemptionReason;

    @JsonProperty(value="hasPensionExemptionReason")
    public boolean getHasPensionExemptionReason() {
        return hasPensionExemptionReason;
    }

    public void setHasPensionExemptionReason(boolean hasPensionExemptionReason) {
        this.hasPensionExemptionReason = hasPensionExemptionReason;
    }

    @JsonProperty(value="hasTfn")
    public boolean getHasTfn() {
        return hasTfn;
    }

    public void setHasTfn(boolean hasTfn) {
        this.hasTfn = hasTfn;
    }

    @JsonProperty(value="hasDirectSuperAccount")
    public boolean getHasDirectSuperAccount() {
        return hasDirectSuperAccount;
    }

    public void setHasDirectSuperAccount(boolean hasDirectSuperAccount) {
        this.hasDirectSuperAccount = hasDirectSuperAccount;
    }
    @JsonProperty(value="hasTfnMatched")
    public boolean getHasTfnMatched() {
        return hasTfnMatched;
    }

    public void setHasTfnMatched(boolean hasTfnMatched) {
        this.hasTfnMatched = hasTfnMatched;
    }

    @JsonProperty(value="hasDirectPensionAccount")
    public boolean getHasDirectPensionAccount() {
        return hasDirectPensionAccount;
    }

    public void setHasDirectPensionAccount(boolean hasDirectPensionAccount) {
        this.hasDirectPensionAccount = hasDirectPensionAccount;
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this).
                append("hasTfn", hasTfn).
                append("hasTfnMatched", hasTfnMatched).
                append("hasDirectSuperAccount", hasDirectSuperAccount).
                append("hasDirectPensionAccount", hasDirectPensionAccount).
                append("hasPensionExemptionReason", hasPensionExemptionReason).
                toString();
    }
}
