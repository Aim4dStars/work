package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.model.form.ClientApplicationFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.ExtendedPersonDetailsFormFactory;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.InvolvedPartyDetailsType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberUsageTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailAddressType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailUsageTypeCode;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;

public class ContactDetailsBuilderTest extends AbstractJsonReaderTest {

    private ContactDetailsBuilder contactDetailsBuilder = new ContactDetailsBuilder();

    @Test
    public void shouldBuildContactDetailsFromInvestorDetailsMap() throws IOException {
        String investorDetailsJson = "{\n" +
                "        \"mobile\": {\n" +
                "            \"value\": \"0412345678\"\n" +
                "        },\n" +
                "        \"email\": {\n" +
                "            \"value\": \"john.doe@smith.com\"\n" +
                "        },\n" +
                "        \"secondaryemail\": {\n" +
                "            \"value\": \"jd@smith.com\"\n" +
                "        },\n" +
                "        \"homenumber\": {\n" +
                "            \"label\": \"homeNumber\",\n" +
                "            \"areacode\": \"04\",\n" +
                "            \"countrycode\": \"61\",\n" +
                "            \"value\": \"123456\"\n" +
                "        },\n" +
                "        \"secondarymobile\": {\n" +
                "            \"value\": \"63123456\"\n" +
                "        },\n" +
                "        \"worknumber\": {\n" +
                "            \"label\": \"workNumber\",\n" +
                "            \"areacode\": \"02\",\n" +
                "            \"countrycode\": \"61\",\n" +
                "            \"value\": \"12345678\"\n" +
                "        }," +
                "        \"preferredcontact\": \"secondaryemail\"\n" +
                "    }";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> investorDetailsMap = mapper.readValue(investorDetailsJson, new TypeReference<Map<String, Object>>() {
        });

        InvolvedPartyDetailsType investorDetails = new InvolvedPartyDetailsType();
        contactDetailsBuilder.populateContactDetailsField(investorDetails, ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(investorDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertNotNull(investorDetails.getEmailAddresses());
        List<EmailAddressType> emailAddresses = investorDetails.getEmailAddresses().getEmailAddress();
        assertThat(emailAddresses, hasSize(2));
        assertEquals(emailAddresses.get(0).getEmailAddressDetail().getValue().getEmailAddress(), "john.doe@smith.com");
        assertEquals(emailAddresses.get(0).getEmailAddressDetail().getValue().getEmailUsage(), Arrays.asList(EmailUsageTypeCode.ALL_HOURS));
        assertEquals(emailAddresses.get(1).getEmailAddressDetail().getValue().getEmailAddress(), "jd@smith.com");
        assertEquals(emailAddresses.get(1).getEmailAddressDetail().getValue().getEmailUsage(), Arrays.asList(EmailUsageTypeCode.AFTER_HOURS, EmailUsageTypeCode.PREFERRED));


        assertNotNull(investorDetails.getContacts());
        List<ContactType> contacts = investorDetails.getContacts().getContact();
        assertThat(contacts, hasSize(2));
        assertThat(contacts.get(0).getContactDetail().getContactNumberUsage(), hasSize(1));
        assertEquals(contacts.get(0).getContactDetail().getContactNumberUsage().get(0), ContactNumberUsageTypeCode.ALL_HOURS);
        assertEquals(contacts.get(0).getContactDetail().getContactNumberType(), ContactNumberTypeCode.MOBILE);
        assertEquals(contacts.get(0).getContactDetail().getContactNumber().getStandardContactNumber().getAreaCode(), "4");
        assertEquals(contacts.get(0).getContactDetail().getContactNumber().getStandardContactNumber().getSubscriberNumber(), "12345678");

        assertThat(contacts.get(1).getContactDetail().getContactNumberUsage(), hasSize(1));
        assertEquals(contacts.get(1).getContactDetail().getContactNumberUsage().get(0), ContactNumberUsageTypeCode.AFTER_HOURS);
        assertEquals(contacts.get(1).getContactDetail().getContactNumberType(), ContactNumberTypeCode.MOBILE);
        assertEquals(contacts.get(1).getContactDetail().getContactNumber().getStandardContactNumber().getSubscriberNumber(), "63123456");

        // NOTE: HOME AND WORK NUMBERS HAVE MOVED TO INVESTOR DETAILS
    }

    @Test
    public void shouldBuildContactDetailsFromInvestorDetailsMapWithMobileAsPreferredContact() throws IOException {
        String investorDetailsJson = "{\n" +
                "        \"mobile\": {\n" +
                "            \"value\": \"0412345678\"\n" +
                "        },\n" +
                "        \"email\": {\n" +
                "            \"value\": \"john.doe@smith.com\"\n" +
                "        },\n" +
                "        \"preferredcontact\": \"mobile\"\n" +
                "    }";

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> investorDetailsMap = mapper.readValue(investorDetailsJson, new TypeReference<Map<String, Object>>() {
        });

        InvolvedPartyDetailsType investorDetails = new InvolvedPartyDetailsType();
        contactDetailsBuilder.populateContactDetailsField(investorDetails, ExtendedPersonDetailsFormFactory.getNewExtendedPersonDetailsForm(investorDetailsMap, false, PaymentAuthorityEnum.NOPAYMENTS, true));
        assertNotNull(investorDetails.getEmailAddresses());
        List<EmailAddressType> emailAddresses = investorDetails.getEmailAddresses().getEmailAddress();
        assertThat(emailAddresses, hasSize(1));
        assertEquals(emailAddresses.get(0).getEmailAddressDetail().getValue().getEmailAddress(), "john.doe@smith.com");
        assertEquals(emailAddresses.get(0).getEmailAddressDetail().getValue().getEmailUsage(), Arrays.asList(EmailUsageTypeCode.ALL_HOURS));

        assertNotNull(investorDetails.getContacts());
        List<ContactType> contacts = investorDetails.getContacts().getContact();
        assertThat(contacts, hasSize(1));
        ContactDetailType contactDetail = contacts.get(0).getContactDetail();
        assertThat(contactDetail.getContactNumberUsage(), hasSize(2));
        assertEquals(contactDetail.getContactNumberUsage().get(0), ContactNumberUsageTypeCode.ALL_HOURS);
        assertEquals(contactDetail.getContactNumberUsage().get(1), ContactNumberUsageTypeCode.PREFERRED);
        assertEquals(contactDetail.getContactNumberType(), ContactNumberTypeCode.MOBILE);
        assertEquals(contactDetail.getContactNumber().getStandardContactNumber().getAreaCode(), "4");
        assertEquals(contactDetail.getContactNumber().getStandardContactNumber().getSubscriberNumber(), "12345678");
    }

    @Test
    public void shouldBuildContactDetailsWithOtherAsPreferredContact() throws IOException {

        InvolvedPartyDetailsType investorDetails = new InvolvedPartyDetailsType();
        IClientApplicationForm formdata = ClientApplicationFormFactory.getNewClientApplicationForm(readJsonFromFile("client_application_gcmret_individual_otherphone.json"));
        contactDetailsBuilder.populateContactDetailsField(investorDetails, formdata.getInvestors().get(0));
        assertNotNull(investorDetails.getContacts());
        List<ContactType> contacts = investorDetails.getContacts().getContact();
        assertThat(contacts, hasSize(2));
        assertThat(contacts.get(1).getContactDetail().getContactNumberUsage().get(0),is(ContactNumberUsageTypeCode.OTHER));
        assertThat(contacts.get(1).getContactDetail().getContactNumberUsage().get(1),is(ContactNumberUsageTypeCode.PREFERRED));
        assertThat(contacts.get(1).getContactDetail().getContactNumber().getStandardContactNumber().getAreaCode(),is("2"));
        assertThat(contacts.get(1).getContactDetail().getContactNumber().getStandardContactNumber().getSubscriberNumber(),is("58962666"));
    }
}
