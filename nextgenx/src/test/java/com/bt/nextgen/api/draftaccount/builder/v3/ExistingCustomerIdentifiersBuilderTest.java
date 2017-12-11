package com.bt.nextgen.api.draftaccount.builder.v3;

import ns.btfin_com.party.v3_0.CustomerIdentifier;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.party.v3_0.CustomerNumberIdentifier;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExistingCustomerIdentifiersBuilderTest {

    ExistingCustomerIdentifiersBuilder existingCustomerIdentifiersBuilder = new ExistingCustomerIdentifiersBuilder();

    @Test
    public void shouldBuildACustomerIdentifiersWithTheCisKey() throws Exception {

        String cisKey = "123456";
        CustomerIdentifier existingCustomerIdentifier = existingCustomerIdentifiersBuilder.buildCustomerIdentifierWithCisKey(cisKey);
        CustomerNumberIdentifier customerNumberIdentifier = existingCustomerIdentifier.getCustomerNumberIdentifier();

        assertThat(customerNumberIdentifier.getCustomerNumber(), is(cisKey));
        assertThat(customerNumberIdentifier.getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC_LEGACY));
    }

    @Test
    public void shouldBuildACustomerIdentifiersWithGCMId() throws Exception {

        String gcmId = "123456";
        CustomerIdentifier existingCustomerIdentifier = existingCustomerIdentifiersBuilder.buildCustomerIdentifierWithGcmId(gcmId);
        CustomerNumberIdentifier customerNumberIdentifier = existingCustomerIdentifier.getCustomerNumberIdentifier();

        assertThat(customerNumberIdentifier.getCustomerNumber(), is(gcmId));
        assertThat(customerNumberIdentifier.getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.BT_PANORAMA));
    }

    @Test
    public void shouldBuildACustomerIdentifiersWithCustomerNumber() throws Exception {

        String customerNumber = "123456";
        CustomerIdentifier existingCustomerIdentifier = existingCustomerIdentifiersBuilder.buildCustomerIdentifierWithCustomerNumber(customerNumber);
        CustomerNumberIdentifier customerNumberIdentifier = existingCustomerIdentifier.getCustomerNumberIdentifier();

        assertThat(customerNumberIdentifier.getCustomerNumber(), is(customerNumber));
        assertThat(customerNumberIdentifier.getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC));
    }
}