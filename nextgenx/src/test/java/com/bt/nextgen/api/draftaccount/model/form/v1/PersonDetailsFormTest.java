package com.bt.nextgen.api.draftaccount.model.form.v1;

import java.io.IOException;

import org.junit.Test;

import com.bt.nextgen.api.draftaccount.model.AbstractJsonObjectMapperTest;
import com.bt.nextgen.api.draftaccount.model.form.IContactValue;
import com.bt.nextgen.api.draftaccount.model.form.IIdentityVerificationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;
import com.bt.nextgen.service.integration.domain.Gender;

import static com.bt.nextgen.api.draftaccount.model.form.v1.AddressFormTest.assertStandardAddress;
import static com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil.date;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@code PersonDetailsForm}.
 */
public class PersonDetailsFormTest extends AbstractJsonObjectMapperTest<Customer> {

    private IPersonDetailsForm person;

    public PersonDetailsFormTest() {
        super(Customer.class);
    }

    @Test
    public void billyBastard() throws IOException {
        initPerson("company-new-1", "directors[0]");
        assertThat(person.getTitle(), is("REV"));
        assertThat(person.getFirstName(), is("Billy"));
        assertThat(person.getMiddleName(), is("Bob"));
        assertThat(person.getLastName(), is("Bastard"));
        assertThat(person.getPreferredName(), is("William"));
        assertThat(person.getAlternateName(), is("Bill"));
        assertThat(person.getFormerName(), is("Wilhelm"));
        assertThat(person.getDateOfBirth(), is("15/07/1978"));
        assertThat(person.getDateOfBirthAsCalendar(), is(date(1978, 7, 15)));
        assertThat(person.getGender(), is(Gender.MALE));
        assertTrue(person.hasMobile());
        assertContact(person.getMobile(), "0413665998", false);
        assertTrue(person.hasSecondaryMobileNumber());
        assertContact(person.getSecondaryMobile(), "0418990887", false);
        assertTrue(person.hasEmail());
        assertContact(person.getEmail(), "billy.bob.bastard@gmail.com", false);
        assertFalse(person.hasSecondaryEmailAddress());
        assertStandardAddress(person.getResidentialAddress(), "12", "Schwank", "SERVICE_WAY", "SYDNEY", "NSW", "2000", "AU");
        assertStandardAddress(person.getPostalAddress(), "12", "Schwank", "SERVICE_WAY", "SYDNEY", "NSW", "2000", "AU");
        assertTrue(person.hasWorkNumber());
        assertPhoneNumber(person.getWorkNumber(), "61", "02", "99847754", true);
        assertFalse(person.hasHomeNumber());
        assertNull(person.getHomeNumber());
    }

    @Test
    public void belindaBastard() throws IOException {
        initPerson("company-new-1", "directors[1]");
        assertThat(person.getTitle(), is("SISTER"));
        assertThat(person.getFirstName(), is("Belinda"));
        assertThat(person.getMiddleName(), is("Breaking"));
        assertThat(person.getLastName(), is("Bastard"));
        assertThat(person.getPreferredName(), is("Bella"));
        assertThat(person.getAlternateName(), is("Belle"));
        assertThat(person.getFormerName(), is("Blend"));
        assertThat(person.getDateOfBirth(), is("15/08/1977"));
        assertThat(person.getDateOfBirthAsCalendar(), is(date(1977, 8, 15)));
        assertThat(person.getGender(), is(Gender.FEMALE));
        assertContact(person.getMobile(), "0411777898", false);
        assertContact(person.getEmail(), "belinda.breaking.bastard@gmail.com", false);
        assertContact(person.getSecondaryEmailContact(), "bella.bastard@test.com", true);
        assertStandardAddress(person.getResidentialAddress(), "84", "Pinchworth", "PARADE", "SYDNEY", "NSW", "2000", "AU");
        assertStandardAddress(person.getPostalAddress(), "84", "Pinchworth", "PARADE", "SYDNEY", "NSW", "2000", "AU");
        assertTrue(person.hasWorkNumber());
        assertPhoneNumber(person.getWorkNumber(), "61", "02", "33215544", false);
        assertFalse(person.hasHomeNumber());
        assertNull(person.getHomeNumber());
    }

    @Test
    public void testIfOtherNumberIsSetAsPreferredContact() throws IOException {
        initPerson("company-new-1", "directors[2]");
        assertThat(person.getFirstName(), is("Beverly"));
        assertTrue(person.hasOtherNumber());
        assertPhoneNumber(person.getOtherNumber(), "61", "02", "04234233", true);
    }

    @Test
    public void identityVerification() throws IOException {
        initPerson("company-new-1", "directors[0]");
        IIdentityVerificationForm form = person.getIdentityVerificationForm();
        assertThat(form.hasPhotoDocuments(), is(false));
        assertThat(form.hasInternationalDocuments(), is(false));
        assertThat(form.hasNonPhotoDocuments(), is(true));
        assertThat(form.getNonPhotoDocuments().getIdentityDocuments().size(), is(2));

    }

    private void initPerson(String resourceName, String jsonPath) throws IOException {
        final Customer customer = readJsonResource(resourceName, jsonPath);
        this.person = new PersonDetailsForm(1, customer);
    }

    public static void assertPhoneNumber(IContactValue contact, String countryCode, String areaCode, String subscriber, boolean preferred) {
        assertThat(contact.getCountryCode(), is(countryCode));
        assertThat(contact.getAreaCode(), is(areaCode));
        assertThat(contact.getValue(), is(subscriber));
        assertThat(contact.isPreferredContact(), is(preferred));
    }

    public static void assertContact(IContactValue contact, String value, boolean preferred) {
        assertThat(contact.getValue(), is(value));
        assertThat(contact.isPreferredContact(), is(preferred));
    }

    @Test
    public void testGetExemptionReason() throws IOException {
        initPerson("joint-new-2", "investors[0]");
        assertThat(person.getExemptionReason(), is("fin_busi_provid"));

        initPerson("joint-new-2", "investors[1]");
        assertThat(person.getExemptionReason(), is("No Exemption"));
    }
}