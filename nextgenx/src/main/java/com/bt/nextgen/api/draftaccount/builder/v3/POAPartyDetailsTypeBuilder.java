package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import ns.btfin_com.party.v3_0.AlternateNameType;
import ns.btfin_com.party.v3_0.IndividualType;
import ns.btfin_com.party.v3_0.PartyDetailType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAIntInvolvedPartyDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAInvolvedPartyDetailsType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static com.btfin.panorama.onboarding.helper.EmailHelper.emailAddresses;
import static com.btfin.panorama.onboarding.helper.EmailHelper.emailAddress;
import static com.btfin.panorama.onboarding.helper.ContactHelper.contacts;
import static com.btfin.panorama.onboarding.helper.ContactHelper.nonStandardPhone;

@Component("POAPartyDetailsTypeBuilder")
public class POAPartyDetailsTypeBuilder {

    private final ContactsTypeBuilder contactsTypeBuilder;

    public POAPartyDetailsTypeBuilder(ContactsTypeBuilder contactsTypeBuilder) {
        this.contactsTypeBuilder = contactsTypeBuilder;
    }

    public POAPartyDetailsTypeBuilder() {
        this(new ContactsTypeBuilder());
    }

    public POAInvolvedPartyDetailsType createInvestorDetails(IndividualDto individualDto) {
        POAInvolvedPartyDetailsType investorDetails = new POAInvolvedPartyDetailsType();
        investorDetails.setPartyDetails(getPartyDetail(individualDto));
        investorDetails.setContacts(contactsTypeBuilder.buildContactsType(getPrimaryContactNumber(individualDto.getPhones()), ContactNumberTypeCode.MOBILE));
        investorDetails.setEmailAddresses(emailAddresses(emailAddress(getPrimaryEmailAddress(individualDto.getEmails()))));
        return investorDetails;
    }

    public POAIntInvolvedPartyDetailsType createIntermediaryDetails(BrokerUser brokerUser) {
        POAIntInvolvedPartyDetailsType intermediaryDetails = new POAIntInvolvedPartyDetailsType();
        intermediaryDetails.setEmailAddresses(emailAddresses(emailAddress(getPrimaryEmail(brokerUser.getEmails()))));
        intermediaryDetails.setContacts(contacts(nonStandardPhone(getPrimaryPhone(brokerUser.getPhones()))));
        intermediaryDetails.setPartyDetails(getPartyDetail(brokerUser));
        return intermediaryDetails;
    }

    private PartyDetailType getPartyDetail(BrokerUser brokerUser) {
        PartyDetailType partyDetails = new PartyDetailType();
        partyDetails.setIndividual(getIndividualType(brokerUser.getFirstName(), brokerUser.getLastName()));
        return partyDetails;
    }

    private PartyDetailType getPartyDetail(IndividualDto individualDto) {
        PartyDetailType partyDetails = new PartyDetailType();
        partyDetails.setIndividual(getIndividualType(individualDto));
        return partyDetails;
    }

    private IndividualType getIndividualType(String firstName, String lastName) {
        IndividualType individual = new IndividualType();
        individual.setGivenName(firstName);
        individual.setLastName(lastName);
        return individual;
    }

    private IndividualType getIndividualType(IndividualDto individualDto) {
        final IndividualType individual = getIndividualType(individualDto.getFirstName(), individualDto.getLastName());
        final String preferredName = individualDto.getPreferredName();
        if (hasText(preferredName)) {
            AlternateNameType preferred = new AlternateNameType();
            preferred.setName(individualDto.getPreferredName());
            preferred.setPreferred(true);
            individual.getAlternateName().add(preferred);
        }
        return individual;
    }

    private String getPrimaryPhone(List<Phone> phones) {
        for (Phone phone : phones) {
            if (AddressMedium.BUSINESS_TELEPHONE.equals(phone.getType())) {
                return phone.getNumber();
            }
        }
        return "";
    }

    private String getPrimaryEmail(List<Email> emails) {
        for (Email email : emails) {
            if (AddressMedium.EMAIL_PRIMARY.equals(email.getType())) {
                return email.getEmail();
            }
        }
        return null;
    }

    private String getPrimaryEmailAddress(List<EmailDto> emails) {
        for (EmailDto email : emails) {
            if (AddressMedium.EMAIL_PRIMARY.getAddressType().equals(email.getEmailType())) {
                return email.getEmail();
            }
        }
        return null;
    }

    private String getPrimaryContactNumber(List<PhoneDto> phones) {
        for (PhoneDto phone : phones) {
            if (AddressMedium.MOBILE_PHONE_PRIMARY.getAddressType().equals(phone.getPhoneType())) {
                return phone.getNumber();
            }
        }
        return null;
    }
}
