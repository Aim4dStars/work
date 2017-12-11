package com.bt.nextgen.service.onboarding;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.serviceops.service.ProvisionMFADeviceService;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;

import java.util.Map;

/**
 * Created by L069552 on 6/11/17.
 */
public class ProvisionMFADeviceRequestModel implements ProvisionMFADeviceRequest {

    private Map<CustomerNoAllIssuerType, String> customerIdentifiers;
    private String primaryMobileNumber;
    private String clientId;
    private String canonicalProductCode;

    @Override
    public Map<CustomerNoAllIssuerType, String> getCustomerIdentifiers() {
        return customerIdentifiers;
    }

    @Override
    public void setCustomerIdentifiers(Map<CustomerNoAllIssuerType, String> customerIdentifiers) {
        this.customerIdentifiers = customerIdentifiers;
    }

    @Override
    public String getPrimaryMobileNumber() {
        return primaryMobileNumber;
    }

    @Override
    public void setPrimaryMobileNumber(String primaryMobileNumber) {
        this.primaryMobileNumber = primaryMobileNumber;
    }

    @Override
    public String getCanonicalProductCode() {
        return canonicalProductCode;
    }

    @Override
    public void setCanonicalProductCode(String canonicalProductCode) {
        this.canonicalProductCode = canonicalProductCode;
    }
}