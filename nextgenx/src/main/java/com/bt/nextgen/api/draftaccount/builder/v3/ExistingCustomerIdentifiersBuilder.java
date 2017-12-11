package com.bt.nextgen.api.draftaccount.builder.v3;

import ns.btfin_com.party.v3_0.CustomerIdentifier;
import org.springframework.stereotype.Service;

import static ns.btfin_com.party.v3_0.CustomerNoAllIssuerType.WESTPAC;

import static com.btfin.panorama.onboarding.helper.PartyHelper.customerIdentifier;

@Service
class ExistingCustomerIdentifiersBuilder extends CustomerIdentifiersBuilder {

    public CustomerIdentifier buildCustomerIdentifierWithCustomerNumber(String customerNumber) {
        return customerIdentifier(customerNumber, WESTPAC);
    }
}
