package com.bt.nextgen.api.client.util;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.NonStandardPostalAddress;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.group.customer.groupesb.address.CustomerAddress;
import com.bt.nextgen.service.group.customer.groupesb.address.v7.AddressAdapterV7;
import com.bt.nextgen.service.group.customer.groupesb.address.v7.InternationalAddressV7Adapter;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the AddressConverter.
 */
public class AddressConverterTest {

    @Autowired
    private AddressConverter converter;

    @Before
    public void initConverter() {
        converter = new AddressConverter();
    }

    @Test
    public void convertAustralianStreetAddress() throws Exception {
        AddressDto address = converter.convert(streetAddress());
        assertEquals("445221154", address.getAddressKey().getAddressId());
        assertEquals("WorkSMART", address.getBuilding());
        assertEquals("10", address.getFloor());
        assertEquals("150", address.getStreetNumber());
        assertEquals("Collins", address.getStreetName());
        assertEquals("Street", address.getStreetType());
        assertEquals("Melbourne", address.getSuburb());
        assertEquals("Victoria", address.getState());
        assertEquals("VIC", address.getStateAbbr());
        assertEquals("btfg$au_vic", address.getStateCode());
        assertEquals("3000", address.getPostcode());
        assertEquals("Australia", address.getCountry());
        assertEquals("au", address.getCountryCode());
        assertTrue(address.isDomicile());
    }

    @Test
    public void convertAustralianPoBoxAddress() throws Exception {
        AddressDto address = converter.convert(poBoxAddress());
        assertEquals("9908789", address.getAddressKey().getAddressId());
        assertEquals("72", address.getPoBox());
        assertEquals("Post Office Box", address.getPoBoxPrefix());
        assertEquals("Highett", address.getSuburb());
        assertEquals("Victoria", address.getState());
        assertEquals("VIC", address.getStateAbbr());
        assertEquals("btfg$au_vic", address.getStateCode());
        assertEquals("3190", address.getPostcode());
        assertEquals("Australia", address.getCountry());
        assertEquals("au", address.getCountryCode());
        assertTrue(address.isMailingAddress());
    }

    @Test
    public void convertInternationalAddress() throws Exception {
        AddressDto address = converter.convert(internationalAddress());
        assertEquals("3320012", address.getAddressKey().getAddressId());
        assertEquals("61 The Cut", address.getAddressLine1());
        assertEquals("London", address.getAddressLine2());
        assertEquals("SE1 8LL", address.getAddressLine3());
        assertEquals("United Kingdom", address.getCountry());
        assertEquals("GB", address.getCountryCode());
    }

    private Address streetAddress() {
        final AddressImpl address = new AddressImpl();
        address.setAddressKey(AddressKey.valueOf("445221154"));
        address.setBuilding("WorkSMART");
        address.setFloor("10");
        address.setStreetNumber("150");
        address.setStreetName("Collins");
        address.setStreetType("Street");
        address.setSuburb("Melbourne");
        address.setState("Victoria");
        address.setStateAbbr("VIC");
        address.setStateCode("btfg$au_vic");
        address.setPostCode("3000");
        address.setCountry("Australia");
        address.setCountryCode("au");
        address.setDomicile(true);
        return address;
    }

    private Address poBoxAddress() {
        final AddressImpl address = new AddressImpl();
        address.setAddressKey(AddressKey.valueOf("9908789"));
        address.setPoBox("72");
        address.setPoBoxPrefix("Post Office Box");
        address.setSuburb("Highett");
        address.setState("Victoria");
        address.setStateAbbr("VIC");
        address.setStateCode("btfg$au_vic");
        address.setPostCode("3190");
        address.setCountry("Australia");
        address.setCountryCode("au");
        address.setMailingAddress(true);
        return address;
    }

    /**
     * Dummy up an international address instance.
     * @return international address.
     */
    private Address internationalAddress() {
        final CustomerAddress international = new CustomerAddress();
        international.setAddressKey(AddressKey.valueOf("3320012"));
        international.setAddressLine1("61 The Cut");
        international.setAddressLine2("London");
        international.setAddressLine3("SE1 8LL");
        international.setCountryCode("GB");
        international.setCountryName("United Kingdom");
        return international;
    }

    private Address nonStandarAddress(){
        NonStandardPostalAddress nonStandardPostalAddress = new NonStandardPostalAddress();
        nonStandardPostalAddress.setAddressLine1("61 The Cut");
        nonStandardPostalAddress.setAddressLine2("Hyderabad");
        nonStandardPostalAddress.setAddressLine3("SE1 8LL");
        nonStandardPostalAddress.setCountry("IN");
        AddressAdapterV7 addressAdapter = new InternationalAddressV7Adapter(nonStandardPostalAddress);
        return addressAdapter;
    }

    @Test
    public void convertNonStandardAddress() throws Exception {
        AddressDto address = converter.convert(nonStandarAddress());
        assertEquals("61 The Cut", address.getAddressLine1());
        assertEquals("Hyderabad", address.getAddressLine2());
        assertEquals("SE1 8LL", address.getAddressLine3());
        assertEquals("IN", address.getCountry());
    }
}