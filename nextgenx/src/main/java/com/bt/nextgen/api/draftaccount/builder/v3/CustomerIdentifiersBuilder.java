package com.bt.nextgen.api.draftaccount.builder.v3;

import ns.btfin_com.party.v3_0.CustomerIdentifier;
import ns.btfin_com.party.v3_0.CustomerIdentifiers;
import org.springframework.stereotype.Service;

import static com.btfin.panorama.onboarding.helper.PartyHelper.customerIdentifier;
import static com.btfin.panorama.onboarding.helper.PartyHelper.customerIdentifiers;
import static ns.btfin_com.party.v3_0.CustomerNoAllIssuerType.BT_PANORAMA;
import static ns.btfin_com.party.v3_0.CustomerNoAllIssuerType.WESTPAC;
import static ns.btfin_com.party.v3_0.CustomerNoAllIssuerType.WESTPAC_LEGACY;

@Service
public class CustomerIdentifiersBuilder {

    public CustomerIdentifier buildCustomerIdentifierWithGcmId(String gcmId) {
        return customerIdentifier(gcmId, BT_PANORAMA);
    }

    public CustomerIdentifier buildCustomerIdentifierWithCisKey(String cisKey) {
        return customerIdentifier(cisKey, WESTPAC_LEGACY);
    }

    public CustomerIdentifiers buildCustomerIdentifiersWithGcmId(String gcmId) {
        return customerIdentifiers(buildCustomerIdentifierWithGcmId(gcmId));
    }

    public CustomerIdentifiers buildCustomerIdentifiersWithCisKey(String cisKey) {
        return customerIdentifiers(buildCustomerIdentifierWithCisKey(cisKey));
    }

    public CustomerIdentifiers buildCustomerIdentifiersWithCisKeyAndZNumber(String cisKey, String customerNumber) {
        final CustomerIdentifier cisKeyIdentifier = buildCustomerIdentifierWithCisKey(cisKey);
        final CustomerIdentifier zNumberIdentifier = customerIdentifier(customerNumber, WESTPAC);
        return customerIdentifiers(cisKeyIdentifier, zNumberIdentifier);
    }
}
