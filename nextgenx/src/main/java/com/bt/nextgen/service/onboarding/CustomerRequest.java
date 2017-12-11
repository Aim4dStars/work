package com.bt.nextgen.service.onboarding;

import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;

import java.util.Map;

/**
 * Created by L069552 on 6/11/17.
 */
public interface CustomerRequest {

    Map<CustomerNoAllIssuerType, String> getCustomerIdentifiers();

    void setCustomerIdentifiers(Map<CustomerNoAllIssuerType, String> customerIdentifiers);
}