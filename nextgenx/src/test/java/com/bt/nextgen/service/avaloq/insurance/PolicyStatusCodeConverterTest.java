package com.bt.nextgen.service.avaloq.insurance;


import com.bt.nextgen.service.avaloq.insurance.model.PolicyStatusCode;
import com.bt.nextgen.service.avaloq.insurance.service.PolicyStatusCodeConverter;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class PolicyStatusCodeConverterTest
{
    @Test
    public void testConvertWaiverToInforceEnum()
    {
        PolicyStatusCodeConverter converter = new PolicyStatusCodeConverter();
        PolicyStatusCode code = converter.convert("Waiver");
        assertThat(PolicyStatusCode.IN_FORCE, Is.is(code));
    }

    @Test
    public void testConvertHolidayToInforceEnum()
    {
        PolicyStatusCodeConverter converter = new PolicyStatusCodeConverter();
        PolicyStatusCode code = converter.convert("Holiday");
        assertThat(PolicyStatusCode.IN_FORCE, Is.is(code));
    }

    @Test
    public void testConvertInForceToInforceEnum()
    {
        PolicyStatusCodeConverter converter = new PolicyStatusCodeConverter();
        PolicyStatusCode code = converter.convert("Cancelled");
        assertThat(PolicyStatusCode.CANCELLED, Is.is(code));
    }
}