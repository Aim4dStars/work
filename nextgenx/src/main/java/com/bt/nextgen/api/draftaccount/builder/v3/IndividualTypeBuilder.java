package com.bt.nextgen.api.draftaccount.builder.v3;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IContactValue;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Gender;
import ns.btfin_com.identityverification.v1_1.PartyIdentificationStatusType;
import ns.btfin_com.party.v3_0.GenderTypeCode;
import ns.btfin_com.party.v3_0.HomePhoneNumberType;
import ns.btfin_com.party.v3_0.IndividualType;
import ns.btfin_com.party.v3_0.PartyIdentificationInformationsIndType;
import ns.btfin_com.party.v3_0.PurposeOfBusinessRelationshipIndType;
import ns.btfin_com.party.v3_0.WorkPhoneNumberType;
import ns.btfin_com.sharedservices.common.address.v3_0.RegisteredResidentialAddressDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactDetailIncUsageType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberType;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberTypeCode;
import ns.btfin_com.sharedservices.common.contact.v1_1.ContactNumberUsageTypeCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static com.bt.nextgen.api.draftaccount.builder.v3.ContactsTypeBuilder.standardContactNumber;
import static com.btfin.panorama.onboarding.helper.PartyHelper.individual;
import static org.springframework.util.StringUtils.hasText;

@Service
@SuppressWarnings("squid:S1200")
public class IndividualTypeBuilder {

    public static final String BTFG_$_IM_CODE = "btfg$im_code";

    @Autowired
    private AddressTypeBuilder addressTypeBuilder;

    @Autowired
    private PartyIdentificationInformationBuilder partyIdentificationInformationBuilder;

    @Autowired
    private IDVPerformedByIntermediaryTypeBuilder idvPerformedByIntermediaryTypeBuilder;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Autowired
    protected PurposeOfBusinessRelationshipTypeBuilder purposeOfBusinessRelationshipTypeBuilder;

    @Autowired
    private AddressV2CacheService addressV2CacheService;

    static GenderTypeCode genderTypeCode(Gender gender) {
        if (gender != null) {
            switch (gender) {
                case FEMALE:
                    return GenderTypeCode.FEMALE;
                case MALE:
                    return GenderTypeCode.MALE;
                default:
                    return GenderTypeCode.OTHER_RESPONSE;
            }
        }
        return null;
    }

    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck")
    public IndividualType getIndividualType(IPersonDetailsForm person, BrokerUser adviser, Broker dealer, IAccountSettingsForm accountSettings, ServiceErrors serviceErrors) {
        String title = person.getTitle();
        if (hasText(title)) {
            title = gcmTitle(title);
        }
        String[] middleNames = {person.getMiddleName()};
        if (!hasText(middleNames[0])) {
            middleNames = null;
        }
        final IndividualType individual = individual(title, person.getFirstName(), middleNames, person.getLastName(),
                genderTypeCode(person.getGender()), person.getDateOfBirthAsCalendar(), person.getPreferredName(),
                person.getAlternateName());
        setFormerNameAndPlaceOfBirth(person, individual);

        if (!person.isGcmRetrievedPerson()) {
            if (person.hasResidentialAddress()) {
                individual.setResidentialAddress(addressTypeBuilder.getAddressType(person.getResidentialAddress(),
                        new RegisteredResidentialAddressDetailType(), serviceErrors));
            }
        } else {
            individual.setResidentialAddress(addressTypeBuilder.getAddressType(person.getGCMRetAddresses(),
                new RegisteredResidentialAddressDetailType(), true, serviceErrors));
        }

        if (person.hasHomeNumber()) {
            final IContactValue home = person.getHomeNumber();
            individual.setHomePhoneNumber(homePhone(standardContactNumber(home), home.isPreferredContact()));
        }

        if (person.hasWorkNumber()) {
            final IContactValue work = person.getWorkNumber();
            individual.setWorkPhoneNumber(workPhone(standardContactNumber(work), work.isPreferredContact()));
        }

        final PartyIdentificationInformationsIndType identification = partyIdentificationInformationBuilder.getPartyIdentificationInformation(person);
        if (identification != null) {
            individual.setPartyIdentificationInformations(identification);
            individual.setPartyIdentificationPerformedBy(idvPerformedByIntermediaryTypeBuilder.intermediary(adviser, dealer));
        }

        setPartyIdentificationStatus(person, individual);

        final PurposeOfBusinessRelationshipIndType purpose = purposeOfBusinessRelationshipTypeBuilder.purpose(person, accountSettings);
        if (purpose != null) {
            individual.setPurposeOfBusinessRelationship(purpose);
        }
        return individual;
    }

    private static WorkPhoneNumberType workPhone(ContactNumberType number, ContactNumberUsageTypeCode... usages) {
        EnumSet usageSet = EnumSet.of(ContactNumberUsageTypeCode.WORK);
        usageSet.addAll(Arrays.asList(usages));
        return (WorkPhoneNumberType)phone(number, usageSet, new WorkPhoneNumberType());
    }

    private static WorkPhoneNumberType workPhone(ContactNumberType number, boolean preferred) {
        return preferred?workPhone(number, new ContactNumberUsageTypeCode[]{ContactNumberUsageTypeCode.PREFERRED}):workPhone(number, new ContactNumberUsageTypeCode[0]);
    }

    private static HomePhoneNumberType homePhone(ContactNumberType number, ContactNumberUsageTypeCode... usages) {
        EnumSet usageSet = EnumSet.of(ContactNumberUsageTypeCode.HOME);
        usageSet.addAll(Arrays.asList(usages));
        return (HomePhoneNumberType)phone(number, usageSet, new HomePhoneNumberType());
    }

    private static HomePhoneNumberType homePhone(ContactNumberType number, boolean preferred) {
        return preferred?homePhone(number, new ContactNumberUsageTypeCode[]{ContactNumberUsageTypeCode.PREFERRED}):homePhone(number, new ContactNumberUsageTypeCode[0]);
    }

    private static <P extends ContactDetailIncUsageType> P phone(ContactNumberType number, Set<ContactNumberUsageTypeCode> usages, P instance) {
        instance.setContactNumber(number);
        instance.setContactNumberType(ContactNumberTypeCode.PHONE);
        instance.getContactNumberUsage().addAll(usages);
        return instance;
    }

    private void setFormerNameAndPlaceOfBirth(IPersonDetailsForm person, IndividualType individual) {

        if (StringUtils.isNotBlank(person.getFormerName())) {
            individual.setFormerName(person.getFormerName());
        }

        if (null != person.getPlaceOfBirth()) {
            individual.setCountryOfBirth(person.getPlaceOfBirth().getCountryOfBirth());
            individual.setStateOfBirth(person.getPlaceOfBirth().getStateOfBirth());
            individual.setCityOfBirth(person.getPlaceOfBirth().getCityOfBirth());
        }
    }

    private void setPartyIdentificationStatus(IPersonDetailsForm person, IndividualType individual) {
        if (person.isIdVerified()) {
            individual.setPartyIdentificationStatus(PartyIdentificationStatusType.YES);
        }
    }

    private String gcmTitle(final String title) {
        //US15627 Tactical fix till GCM starts accepting DR instead of Dr.
        //US20684 Tactical fix since GCM return Dr. but Avaloq static codes expect DR
        if("DR".equals(title) || "Dr.".equals(title)){
            return "Dr.";
        }

        Code code = staticIntegrationService.loadCodeByUserId(CodeCategory.PERSON_TITLE, title, new ServiceErrorsImpl());
        if (code == null) {
            // For some titles the Avaloq user id and the canonical codes don't match. Hence need to look up the code via BTFG_$_IM_CODE.
            // The below search just makes sure that the title exists Static codes.
            Collection<Code> codes = staticIntegrationService.loadCodes(CodeCategory.PERSON_TITLE, new ServiceErrorsImpl());
            code = Lambda.selectFirst(codes, new LambdaMatcher<Code>() {
                @Override
                protected boolean matchesSafely(Code code) {
                    return code.getField(BTFG_$_IM_CODE).getValue().equals(title);
                }
            });
        }
        return code.getField(BTFG_$_IM_CODE).getValue();
    }
}
