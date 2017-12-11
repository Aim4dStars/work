package com.bt.nextgen.serviceops.model;

import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;

import java.util.Map;

/**
 * Created by L069552 on 6/11/17.
 */
public class ProvisionMFARequestData {


    private String primaryMobileNumber;
    private String role;
    private String canonicalProductName;
    private String gcmId;
    private String cisKey;
    private String customerNumber;


    public String getPrimaryMobileNumber() {
        return primaryMobileNumber;
    }

    public void setPrimaryMobileNumber(String primaryMobileNumber) {
        this.primaryMobileNumber = primaryMobileNumber;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCanonicalProductName() {
        return canonicalProductName;
    }

    public void setCanonicalProductName(String canonicalProductName) {
        this.canonicalProductName = canonicalProductName;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public void setCisKey(String cisKey) {
        this.cisKey = cisKey;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }
}