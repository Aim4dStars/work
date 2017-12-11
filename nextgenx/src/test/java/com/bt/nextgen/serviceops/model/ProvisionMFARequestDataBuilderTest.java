package com.bt.nextgen.serviceops.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

/**
 * Created by L069552 on 14/11/17.
 */
public class ProvisionMFARequestDataBuilderTest {

    @Test
    public void testBuilder(){

        ProvisionMFARequestDataBuilder.ProvisionMFABuilder provisionMFABuilder = new ProvisionMFARequestDataBuilder.ProvisionMFABuilder();
        provisionMFABuilder.withCanonicalProductName("abc");
        ProvisionMFARequestData provisionMFARequestData = provisionMFABuilder.withCanonicalProductName("abc").withCISKey("12345678966").withCustomerNumber("12345677")
                .withGcmId("201654478").withPrimaryMobileNumber("0564444667").collect();
        assertNotNull(provisionMFARequestData);
        assertThat(provisionMFARequestData.getPrimaryMobileNumber(),is("0564444667"));
        assertThat(provisionMFARequestData.getGcmId(),is("201654478"));
    }
}