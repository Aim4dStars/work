package com.bt.nextgen.api.draftaccount.builder.v3;

import ns.btfin_com.party.v3_0.CustomerIdentifiers;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.party.v3_0.CustomerNumberIdentifier;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CustomerIdentifiersBuilderTest {

    private CustomerIdentifiersBuilder customerIdentifiersBuilder = new CustomerIdentifiersBuilder();

    @Test
    public void shouldBuildACustomerIdentifiersWithTheGcmId() throws Exception {

        String gcmId = "123456";
        CustomerIdentifiers customerIdentifiers = customerIdentifiersBuilder.buildCustomerIdentifiersWithGcmId(gcmId);
        CustomerNumberIdentifier customerNumberIdentifier = customerIdentifiers.getCustomerIdentifier().get(0).getCustomerNumberIdentifier();

        assertThat(customerNumberIdentifier.getCustomerNumber(), is(gcmId));
        assertThat(customerNumberIdentifier.getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.BT_PANORAMA));
    }

    @Test
    public void shouldBuildACustomerIdentifiersWithTheCisKey() throws Exception {

        String cisKey = "123456";
        CustomerIdentifiers customerIdentifiers = customerIdentifiersBuilder.buildCustomerIdentifiersWithCisKey(cisKey);
        CustomerNumberIdentifier customerNumberIdentifier = customerIdentifiers.getCustomerIdentifier().get(0).getCustomerNumberIdentifier();

        assertThat(customerNumberIdentifier.getCustomerNumber(), is(cisKey));
        assertThat(customerNumberIdentifier.getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC_LEGACY));
    }

    @Test
    public void shouldBuildACustomerIdentifiersWithTheCisKeyAndZNumber() throws Exception {

        String cisKey = "123456";
        String customerNumber = "654321";
        CustomerIdentifiers customerIdentifiers = customerIdentifiersBuilder.buildCustomerIdentifiersWithCisKeyAndZNumber(cisKey, customerNumber);

        CustomerNumberIdentifier customerNumberIdentifierCISKey = customerIdentifiers.getCustomerIdentifier().get(0).getCustomerNumberIdentifier();
        CustomerNumberIdentifier customerNumberIdentifierZNumber = customerIdentifiers.getCustomerIdentifier().get(1).getCustomerNumberIdentifier();

        assertThat(customerNumberIdentifierCISKey.getCustomerNumber(), is(cisKey));
        assertThat(customerNumberIdentifierCISKey.getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC_LEGACY));

        assertThat(customerNumberIdentifierZNumber.getCustomerNumber(), is(customerNumber));
        assertThat(customerNumberIdentifierZNumber.getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.WESTPAC));
    }
}
