package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import ns.btfin_com.party.v3_0.AlternateNameType;
import ns.btfin_com.party.v3_0.IndividualType;
import ns.btfin_com.party.v3_0.PartyDetailType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAIntInvolvedPartyDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAInvolvedPartyDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAContactsType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailAddressType;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static java.util.Collections.singletonList;
import static java.util.Arrays.asList;


@RunWith(MockitoJUnitRunner.class)
public class POAPartyDetailsTypeBuilderTest {

    @Mock
    private ContactsTypeBuilder contactsTypeBuilder;

    private POAPartyDetailsTypeBuilder intermediaryPartyDetails;

    @Mock
    private BrokerUser intermediary;

    private IndividualDto individualDto;

    @Before
    public void setUp() throws Exception {
        intermediaryPartyDetails = new POAPartyDetailsTypeBuilder(contactsTypeBuilder);
        when(intermediary.getBankReferenceId()).thenReturn("GCM_ID");
        when(intermediary.getFirstName()).thenReturn("John");
        when(intermediary.getLastName()).thenReturn("Doe");
        Phone phone = new PhoneImpl();
        Email email = new EmailImpl();
        when(intermediary.getPhones()).thenReturn(singletonList(phone));
        when(intermediary.getEmails()).thenReturn(singletonList(email));

        individualDto = new IndividualDto();
        individualDto.setGcmId("GCM_ID");
        individualDto.setLastName("Doe");
        individualDto.setFirstName("John");
        individualDto.setPreferredName("Johnny");
        individualDto.setPhones(singletonList(new PhoneDto()));
        individualDto.setEmails(singletonList(new EmailDto()));

    }

    @Test
    public void shouldSetThePartyDetailsContainingTheIntermediaryDetails() throws Exception {
        POAIntInvolvedPartyDetailsType intermediaryParty = intermediaryPartyDetails.createIntermediaryDetails(this.intermediary);
        PartyDetailType partyDetails = intermediaryParty.getPartyDetails();
        IndividualType individual = partyDetails.getIndividual();
        assertThat(individual.getGivenName(), is("John"));
        assertThat(individual.getLastName(), is("Doe"));
    }

    @Test
    public void shouldSetTheContactsGivenThePrimaryPhone() throws Exception {
        Phone phone1 = createPhone("+61222222", AddressMedium.BUSINESS_TELEPHONE);
        Phone phone2 = createPhone("0456789456", AddressMedium.MOBILE_PHONE_PRIMARY);

        when(intermediary.getPhones()).thenReturn(asList(phone1, phone2));
        POAIntInvolvedPartyDetailsType intermediaryDetails = intermediaryPartyDetails.createIntermediaryDetails(intermediary);
        List<ContactType> contacts = intermediaryDetails.getContacts().getContact();
        assertThat(contacts.size(), is(1));
        assertThat(contacts.get(0).getContactDetail().getContactNumber().getNonStandardContactNumber(), is("+61222222"));
    }

    @Test
    public void shouldSetTheEmailAddressesGivenThePrimaryEmailAddress() throws Exception {
        Email email1 = createEmail("test1@test.test", AddressMedium.EMAIL_PRIMARY);
        Email email2 = createEmail("test2@test.test", AddressMedium.EMAIL_ADDRESS_SECONDARY);

        when(intermediary.getEmails()).thenReturn(asList(email1, email2));
        POAIntInvolvedPartyDetailsType intermediaryDetails = intermediaryPartyDetails.createIntermediaryDetails(intermediary);
        List<EmailAddressType> emails = intermediaryDetails.getEmailAddresses().getEmailAddress();
        assertThat(emails.size(), is(1));
        assertThat(emails.get(0).getEmailAddressDetail().getValue().getEmailAddress(), is("test1@test.test"));
    }

    @Test
    public void shouldSetThePartyDetailsContainingTheIndividualPersonalDetails() throws Exception {
        POAInvolvedPartyDetailsType investorDetails = intermediaryPartyDetails.createInvestorDetails(individualDto);

        PartyDetailType partyDetails = investorDetails.getPartyDetails();
        IndividualType individual = partyDetails.getIndividual();

        assertThat(individual.getGivenName(), is("John"));
        assertThat(individual.getLastName(), is("Doe"));
        AlternateNameType preferredName = individual.getAlternateName().get(0);
        assertThat(preferredName.getName(), is("Johnny"));
        assertTrue(preferredName.isPreferred());
    }

    @Test
    public void shouldSetTheContactsGivenThePrimaryPhone2() throws Exception {
        PhoneDto phoneDto1 = createPhoneDto("0456789456", AddressMedium.MOBILE_PHONE_PRIMARY);
        PhoneDto phoneDto2 = createPhoneDto("+6124567891", AddressMedium.BUSINESS_TELEPHONE);
        individualDto.setPhones(asList(phoneDto1, phoneDto2));

        POAContactsType mockContactType = mock(POAContactsType.class);
        when(contactsTypeBuilder.buildContactsType(eq(phoneDto1.getNumber()), eq(ContactNumberTypeCode.MOBILE))).thenReturn(mockContactType);

        POAInvolvedPartyDetailsType investorDetails = intermediaryPartyDetails.createInvestorDetails(individualDto);

        assertThat(investorDetails.getContacts(), Matchers.<ns.btfin_com.sharedservices.common.contact.v1_1.ContactsType>is(mockContactType));
    }

    @Test
    public void shouldSetTheEmailAddressesGivenThePrimaryEmailAddress2() throws Exception {
        EmailDto email1 = getEmailDto("test1@test.test", AddressMedium.EMAIL_PRIMARY);
        EmailDto email2 = getEmailDto("test2@test.test", AddressMedium.EMAIL_ADDRESS_SECONDARY);
        individualDto.setEmails(asList(email1, email2));
        POAInvolvedPartyDetailsType investorDetails = intermediaryPartyDetails.createInvestorDetails(individualDto);
        List<EmailAddressType> emails = investorDetails.getEmailAddresses().getEmailAddress();
        assertThat(emails.size(), is(1));
        assertThat(emails.get(0).getEmailAddressDetail().getValue().getEmailAddress(), is("test1@test.test"));
    }

    private Email createEmail(String emailAddress, AddressMedium addressMedium) {
        EmailImpl email = new EmailImpl();
        email.setEmail(emailAddress);
        email.setType(addressMedium);
        return email;
    }

    private Phone createPhone(String number, AddressMedium addressMedium) {
        PhoneImpl phone = new PhoneImpl();
        phone.setType(addressMedium);
        phone.setNumber(number);
        return phone;
    }

    private EmailDto getEmailDto(String email, AddressMedium emailPrimary) {
        EmailDto email1 = new EmailDto();
        email1.setEmail(email);
        email1.setEmailType(emailPrimary.getAddressType());
        return email1;
    }

    private PhoneDto createPhoneDto(String number, AddressMedium addressMedium) {
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setPhoneType(addressMedium.getAddressType());
        phoneDto.setNumber(number);
        return phoneDto;
    }
}
