package com.bt.nextgen.serviceops.controller;

import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by M044020 on 15/09/2017.
 */
public class ServiceOpsConverterToServiceOpModelTest {

    private IndividualDetailImpl individual;

    @Before
    public void setUp() throws Exception {
        individual = new IndividualDetailImpl();
        individual.setFirstName("Sean");
        individual.setLastName("Connell");
        individual.setFullName("Sean Connell");
        individual.setOpenDate(new DateTime(2016, 1, 1, 0, 0));
        individual.setCustomerId("2017212122");
        individual.setDateOfBirth(new DateTime(1977, 12, 25, 0, 0));
        individual.setCisId("123123122");
        individual.setWestpacCustomerNumber("23131233");
        individual.setSafiDeviceId("123123-13123-122sdffds");
        individual.setRegistrationOnline(true);
        individual.setAdviserFlag(false);
        individual.setParaPlannerFlag(false);
        individual.setPhones(getMockPhones());
        individual.setEmails(getMockEmails());
        individual.setClientKey(ClientKey.valueOf("123123AFCE"));
        individual.setAddresses(getMockAddress());
    }

    private List<Phone> getMockPhones() {
        List<Phone> phones = new ArrayList<>();
        Phone phone = new PhoneImpl(AddressKey.valueOf("12345"), AddressMedium.MOBILE_PHONE_PRIMARY, "0488888888", "61",
                "04", null, true, AddressType.ELECTRONIC);
        phones.add(phone);
        phone = new PhoneImpl(AddressKey.valueOf("12348"), AddressMedium.MOBILE_PHONE_SECONDARY, "0488888889", "61",
                "04", null, false, AddressType.ELECTRONIC);
        phones.add(phone);
        phone = new PhoneImpl(AddressKey.valueOf("12348"), AddressMedium.PERSONAL_TELEPHONE, "88888888", "61", "02",
                null, true, AddressType.ELECTRONIC);
        phones.add(phone);
        return phones;
    }

    private List<Email> getMockEmails() {
        List<Email> emails = new ArrayList<>();
        Email email = new EmailImpl(AddressKey.valueOf("12345"), AddressMedium.EMAIL_PRIMARY, "test@bt.com", null, true,
                AddressType.ELECTRONIC);
        emails.add(email);
        return emails;
    }

    private List<Address> getMockAddress() {
        List<Address> addresses = new ArrayList<>();
        addresses.add(getMockResidentialAddress());
        addresses.add(getMockPostalAddress());
        return addresses;
    }

    private Address getMockResidentialAddress() {
        AddressImpl address = new AddressImpl();
        address.setAddressKey(AddressKey.valueOf("123"));
        address.setAddressType(AddressMedium.POSTAL);
        address.setDomicile(true);
        address.setStreetNumber("200");
        address.setStreetName("Barangaroo Ave");
        address.setSuburb("Barangaroo");
        address.setCity("Sydney");
        address.setState("NSW");
        address.setCountry("Australia");
        address.setCountryAbbr("AU");
        address.setCountryCode("AU");
        return address;
    }

    private Address getMockPostalAddress() {
        AddressImpl address = new AddressImpl();
        address.setAddressKey(AddressKey.valueOf("123"));
        address.setAddressType(AddressMedium.POSTAL);
        address.setDomicile(false);
        address.setMailingAddress(true);
        address.setStreetNumber("200");
        address.setStreetName("Kent St");
        address.setSuburb("Sydney");
        address.setCity("Sydney");
        address.setState("NSW");
        address.setCountry("Australia");
        address.setCountryAbbr("AU");
        address.setCountryCode("AU");
        return address;
    }

    @Test
    public void populateWithPhones() throws Exception {
        ServiceOpsModel result = ServiceOpsConverter.toServiceOpsModel(individual);
        assertThat(result.getMobilePhones().size(), is(2));
        assertThat(result.getPhone().size(), is(1));
        assertThat(result.getEmail().size(), is(1));
        assertThat(result.getResidentialAddress().getStreet(), is("Barangaroo Ave"));
        assertThat(result.getPostalAddress().getStreet(), is("Kent St"));
    }

    @Test
    public void populateWithoutPhones() throws Exception {
        individual.setPhones(null);
        ServiceOpsModel result = ServiceOpsConverter.toServiceOpsModel(individual);
        assertThat(result.getMobilePhones().size(), is(0));
        assertThat(result.getPhone().size(), is(0));
        assertThat(result.getEmail().size(), is(1));
    }

    @Test
    public void populateWithOracleUser() throws Exception {
        individual.setCustomerId("0123213213");
        ServiceOpsModel result = ServiceOpsConverter.toServiceOpsModel(individual);
        assertThat(result.getUserId(), is("123213213"));
        assertThat(result.getGcmId(), is("123213213"));
    }
}