package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.util.EmailFilterUtil;
import com.bt.nextgen.api.client.util.PhoneFilterUtil;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.AlternateNameImpl;
import com.bt.nextgen.service.avaloq.account.AlternateNameType;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.PensionExemptionReason;
import com.bt.nextgen.service.integration.domain.PersonDetail;
import com.bt.nextgen.service.integration.domain.Phone;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.filter;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Service
@Transactional
@SuppressWarnings("squid:S1200")
public class InvestorDtoConverterForPersonDetail {

    private static final String AUSTRALIA = "Australia";

    public static final String VERIFIED = "Verified";

    @Autowired
    private AddressDtoConverter addressDtoConverter;

    @Autowired
    private EmailDtoConverter emailDtoConverter;

    @Autowired
    private PhoneDtoConverter phoneDtoConverter;

    @Autowired
    private CRSTaxDetailHelperService crsTaxDetailHelperService;

    private static final Logger logger = LoggerFactory.getLogger(InvestorDtoConverterForPersonDetail.class);

    /*
        This  method is for unit test cases.
     */
    public void setCrsTaxDetailHelperService(CRSTaxDetailHelperService crsTaxDetailHelperService) {
        this.crsTaxDetailHelperService = crsTaxDetailHelperService;
    }

    public static final Map<PersonRelationship, InvestorRole> personRolesMap;


    static {
        personRolesMap = new HashMap<>();
        personRolesMap.put(PersonRelationship.DIRECTOR, InvestorRole.Director);
        personRolesMap.put(PersonRelationship.TRUSTEE, InvestorRole.Trustee);
        personRolesMap.put(PersonRelationship.SIGNATORY, InvestorRole.Signatory);
        personRolesMap.put(PersonRelationship.SECRETARY, InvestorRole.Secretary);
        personRolesMap.put(PersonRelationship.AO, InvestorRole.Owner);
        personRolesMap.put(PersonRelationship.BENEFICIARY, InvestorRole.Beneficiary);
        personRolesMap.put(PersonRelationship.MBR, InvestorRole.Member);
        personRolesMap.put(PersonRelationship.SHAREHLD, InvestorRole.Shareholder);
        personRolesMap.put(PersonRelationship.BENEF_OWNER, InvestorRole.BeneficialOwner);
        personRolesMap.put(PersonRelationship.CONTROLLER_OF_TRUST, InvestorRole.ControllerOfTrust);
    }

    public InvestorDto convertFromPersonDetail(PersonDetail person, AccountSubType accountSubType,Map<String,Boolean> existingPersonCISKeysWithOverseasDetail) {
        IndividualDto investorDto = new IndividualDto();

        investorDto.setTitle(person.getTitle());

        List<String> names = filter(new LambdaMatcher<String>() {
            @Override
            protected boolean matchesSafely(String item) {
                return item != null && !item.isEmpty();
            }
        }, Arrays.asList(person.getFirstName(), person.getMiddleName(), person.getLastName()));

        investorDto.setFullName(StringUtils.join(names, ' '));
        investorDto.setFirstName(person.getFirstName());
        investorDto.setLastName(person.getLastName());
        investorDto.setPreferredName(person.getPreferredName());
        setFormerName(investorDto, person);
        investorDto.setDateOfBirth(person.getDateOfBirth() != null ? ApiFormatter.asShortDate(person.getDateOfBirth().toGregorianCalendar().getTime()) : null);
        investorDto.setGender(person.getGender() != null ? person.getGender().getName() : null);
        investorDto.setResiCountryforTax(person.getResiCountryForTax());
        investorDto.setTfnProvided(person.getTfnProvided());
        investorDto.setExemptionReason(getExemptionReason(person, accountSubType));
        investorDto.setPrimaryRole(person.getPrimaryRole());
        investorDto.setAddresses(getAddresses(person.getAddresses()));
        investorDto.setEmails(getEmails(person.getEmails()));
        investorDto.setPhones(getPhones(person.getPhones()));
        investorDto.setPersonRoles(getPersonRoles(person));
        //If a person's gender or date of birth is not set, then IDV wouldn't have been done.
        //E.g Additional beneficiary or member.
        if (person.getDateOfBirth()!= null && person.getGender() != null) {
            investorDto.setIdvs(VERIFIED); //already submitted
        }
        setPlaceOfBirthDetails(investorDto, person);

        if (null != person.getClientKey()) {
            investorDto.setKey(new com.bt.nextgen.api.client.model.ClientKey(EncodedString.fromPlainTextUsingTL(person.getClientKey().getId()).toString()));
        }

        boolean isExistingUser = null != person.getCISKey() && null != existingPersonCISKeysWithOverseasDetail
        && !existingPersonCISKeysWithOverseasDetail.isEmpty() && existingPersonCISKeysWithOverseasDetail.containsKey(person.getCISKey().getId());
        String cisKeyValue = isExistingUser ? person.getCISKey().getId() : null;
        logger.info("Populating CRS overseas data for CIS Key :: {} with value :: {}", cisKeyValue,existingPersonCISKeysWithOverseasDetail.get(cisKeyValue));
        crsTaxDetailHelperService.populateCRSTaxDetailsForIndividual(person,investorDto,isExistingUser,existingPersonCISKeysWithOverseasDetail,cisKeyValue);
        return investorDto;
    }

    private String getExemptionReason(PersonDetail person, AccountSubType accountSubType) {
        if(AccountSubType.PENSION == accountSubType && null!=person.getPensionExemptionReason()) {
            PensionExemptionReason pensionExemptionReason = PensionExemptionReason.getPensionExemptionReason(person.getPensionExemptionReason().toString());
            return pensionExemptionReason.getValue();
        }
        else if(AccountSubType.PENSION != accountSubType && null != person.getExemptionReason()) {
            return person.getExemptionReason().getValue();
        }

        return "";
    }

    private void setPlaceOfBirthDetails(IndividualDto investorDto, PersonDetail person) {
        investorDto.setPlaceOfBirthCountry(person.getBirthCountry());
        investorDto.setPlaceOfBirthSuburb(person.getBirthSuburb());

        if (AUSTRALIA.equalsIgnoreCase(person.getBirthCountry())) {
            investorDto.setPlaceOfBirthState(person.getBirthStateDomestic());
        } else {
            investorDto.setPlaceOfBirthState(person.getBirthStateInternational());
        }
    }

    private void setFormerName(IndividualDto investorDto, PersonDetail person) {
        List<AlternateNameImpl> alternameList = person.getAlternateNameList();
        if(!isEmpty(alternameList)){
            AlternateNameImpl name = Lambda.selectFirst(alternameList, Lambda.having(Lambda.on(AlternateNameImpl.class).getAlternateNameType(), Matchers.is(AlternateNameType.FormerName)));
            if(name!=null && StringUtils.isNotBlank(name.getFullName())){
                investorDto.setFormerName(name.getFullName());
            }
        }
    }

    public List<InvestorRole> getPersonRoles(PersonDetail person) {

        Set<InvestorRole> personRoles = new HashSet<>();
        if (person.getPrimaryRole() != null) {
            personRoles.add(personRolesMap.get(person.getPrimaryRole()));
        }

        if (person.isBeneficiary()) {
            personRoles.add(InvestorRole.Beneficiary);
        }
        if (person.isMember()) {
            personRoles.add(InvestorRole.Member);
        }
        if (person.isShareholder()) {
            personRoles.add(InvestorRole.Shareholder);
        }
        if (person.isBeneficialOwner() && !person.isShareholder()){
            personRoles.add(InvestorRole.BeneficialOwner);
        }
        if (person.isControllerOfTrust()) {
            personRoles.add(InvestorRole.ControllerOfTrust);
        }
        if(person.isSecretary()){
            personRoles.add(InvestorRole.Secretary);
        }

        return new ArrayList<>(personRoles);
    }

    private List<PhoneDto> getPhones(List<Phone> phones) {
        if (phones == null || phones.isEmpty()){
            return new ArrayList<>();
        }
        List<PhoneDto> phoneDtos = new ArrayList<>();
        List<Phone> dedupFilteredPhones = new PhoneFilterUtil().filterDuplicates(phones);
        for (Phone phone : dedupFilteredPhones) {
            phoneDtos.add(phoneDtoConverter.getPhoneDto(phone));
        }
        return phoneDtos;
    }

    private List<EmailDto> getEmails(List<Email> emails) {
        if (emails == null || emails.isEmpty()){
            return new ArrayList<>();
        }
        List<EmailDto> emailDtos = new ArrayList<>();
        List<Email> dedupFilteredEmails = new EmailFilterUtil().filterDuplicates(emails);
        for (Email email : dedupFilteredEmails) {
            emailDtos.add(emailDtoConverter.getEmailDto(email));
        }
        return emailDtos;
    }

    private List<AddressDto> getAddresses(List<Address> addresses) {
        List<AddressDto> addressDtos = new LinkedList<>();
        for (Address address : addresses) {
            addressDtos.add(addressDtoConverter.getAddressDto(address));
        }
        return addressDtos;
    }





}
