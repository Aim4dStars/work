package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.AbstractJsonObjectMapperTest;
import com.bt.nextgen.api.draftaccount.model.form.IAddressForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.Address;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the AddressForm object.
 */
public class AddressFormTest extends AbstractJsonObjectMapperTest<Address> {

    private AddressForm addressForm;

    public AddressFormTest() {
        super(Address.class);
    }

    @Test
    public void standardAddress() throws IOException {
        initAddress("individual-new-1", "investors[0]/resaddress");
        assertTrue(addressForm.isComponentised());
        assertStandardAddress(addressForm, "305", "28", "West", "STREET", "NORTH SYDNEY", "NSW", "2060", "AU");
    }

    @Test
    public void internationalAddress() throws IOException {
        initAddress("individual-new-2", "investors[0]/postaladdress");
        assertFalse(addressForm.isComponentised());
        assertThat(addressForm.getAddressLine1(), is("Wawra Sr"));
        assertThat(addressForm.getAddressLine2(), is("No 23 Sss"));
        assertThat(addressForm.getCity(), is("ABBA"));
        assertThat(addressForm.getPin(), is("1234"));
        assertThat(addressForm.getCountry(), is("UM"));
    }

    private void initAddress(String resourceName, String path) throws IOException {
        this.addressForm = new AddressForm(readJsonResource(resourceName, path));
    }

    public static void assertStandardAddress(IAddressForm address, String streetNumber, String streetName,
            String streetType, String suburb, String state, String postcode, String country) {
        assertThat(address.getStreetNumber(), is(streetNumber));
        assertThat(address.getStreetName(), is(streetName));
        assertThat(address.getStreetType(), is(streetType));
        assertThat(address.getSuburb(), is(suburb));
        assertThat(address.getState(), is(state));
        assertThat(address.getPostcode(), is(postcode));
        assertThat(address.getCountry(), is(country));
    }

    public static void assertStandardAddress(IAddressForm address, String unitNumber, String streetNumber,
            String streetName, String streetType, String suburb, String state, String postcode, String country) {
        assertThat(address.getUnitNumber(), is(unitNumber));
        assertStandardAddress(address, streetNumber, streetName, streetType, suburb, state, postcode, country);
    }
}
