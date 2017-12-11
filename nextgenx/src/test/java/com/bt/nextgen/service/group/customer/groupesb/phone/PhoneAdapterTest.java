package com.bt.nextgen.service.group.customer.groupesb.phone;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.TelephoneAddress;
import com.bt.nextgen.service.group.customer.groupesb.phone.v7.PhoneAdapterV7;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Tests for the {@code PhoneAdapter} class.
 */
public class PhoneAdapterTest {

    private PhoneAdapter adapter;

    private PhoneAddressContactMethod phone;

    @Before
    public void initPhoneAndAdapter() {
        phone = new PhoneAddressContactMethod();
        phone.setHasAddress(new TelephoneAddress());
        adapter = new PhoneAdapterV7(phone);
    }

    @Test
    public void getCountryCode() {
        phone.getHasAddress().setCountryCode("61");
        assertThat(adapter.getCountryCode(), is("61"));
    }

    @Test
    public void getCountryCodeErasesLeadingPlusSign() {
        phone.getHasAddress().setCountryCode("+61");
        assertThat(adapter.getCountryCode(), is("61"));
    }

    @Test
    public void getCountryCodeReturnsNullForBlank() {
        phone.getHasAddress().setCountryCode("");
        assertThat(adapter.getCountryCode(), is(nullValue()));
    }

    /**
     * TODO: <i>Why</i> is it necessary to remove the leading zero from the area code?!
     */
    @Test
    public void getAreaCodeErasesLeadingZero() {
        phone.getHasAddress().setAreaCode("03");
        assertThat(adapter.getAreaCode(), is("3"));
    }

    @Test
    public void getAreaCodeReturnsNullForBlank() {
        phone.getHasAddress().setAreaCode("");
        assertThat(adapter.getAreaCode(), is(nullValue()));
    }

    @Test
    public void getNumber() {
        phone.getHasAddress().setLocalNumber("98776621");
        assertThat(adapter.getNumber(), is("98776621"));
    }

    @Test
    public void getNumberReturnsNullForBlank() {
        phone.getHasAddress().setLocalNumber("");
        assertThat(adapter.getNumber(), is(nullValue()));
    }

    @Test
    public void getTypeForMobile() {
        phone.setContactMedium("mobile");
        assertThat(adapter.getType(), is(AddressMedium.MOBILE_PHONE_SECONDARY));
    }

    @Test
    public void getTypeForWorkPhone() {
        phone.setContactMedium("phone");
        phone.setUsage("WRK");
        assertThat(adapter.getType(), is(AddressMedium.BUSINESS_TELEPHONE));
    }

    @Test
    public void getTypeForHomePhone() {
        phone.setContactMedium("phone");
        phone.setUsage("HOM");
        assertThat(adapter.getType(), is(AddressMedium.PERSONAL_TELEPHONE));
    }
}