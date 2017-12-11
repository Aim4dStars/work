package com.bt.nextgen.api.client.util;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.AddressKey;
import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.ClientTxnDto;
import com.bt.nextgen.api.client.model.CompanyDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.model.RegisteredEntityDto;
import com.bt.nextgen.api.client.model.RegisteredStateDto;
import com.bt.nextgen.api.client.model.SmsfDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.api.client.model.TrustDto;
import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.builder.AddressStreetTypeMapper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.btfin.panorama.core.security.avaloq.accountactivation.FormerName;
import com.bt.nextgen.service.avaloq.client.ClientDetailImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.group.customer.groupesb.TaxResidenceCountry;
import com.bt.nextgen.service.group.customer.groupesb.address.CustomerAddress;
import com.bt.nextgen.service.group.customer.groupesb.email.CustomerEmail;
import com.bt.nextgen.service.group.customer.groupesb.phone.PhoneAction;
import com.bt.nextgen.service.group.customer.groupesb.state.CustomerRegisteredState;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.PensionAccountDetail;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.AddressType;
import com.bt.nextgen.service.integration.domain.Company;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.btfin.panorama.core.security.integration.domain.IndividualDetail;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.domain.RegisteredEntity;
import com.bt.nextgen.service.integration.domain.Smsf;
import com.bt.nextgen.service.integration.domain.Trust;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.conversion.CodeCategory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.btfin.panorama.core.security.encryption.EncodedString.fromPlainText;
import static com.btfin.panorama.core.security.encryption.EncodedString.fromPlainTextUsingTL;
import static java.util.Collections.emptyList;
import static java.util.Collections.sort;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.WordUtils.capitalizeFully;
import static org.springframework.util.StringUtils.hasText;

@SuppressWarnings({"findbugs:SE_COMPARATOR_SHOULD_BE_SERIALIZABLE", "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck", "checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.ExecutableStatementCountCheck", "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.JavaNCSSCheck", "squid:MethodCyclomaticComplexity"})
public class ClientDetailDtoConverter {

    private static final String AUSTRALIA = "Australia";
    private static final String AU = "Au";

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDetailDtoConverter.class);

    private static final Comparator<ClientDto> BY_LAST_THEN_FIRST_NAME = new Comparator<ClientDto>() {

        @Override
        public int compare(ClientDto clientDto1, ClientDto clientDto2) {
            if (clientDto1 != null && clientDto2 != null) {
                return lastThenFirstName(clientDto1).compareTo(lastThenFirstName(clientDto2));
            }
            else if (clientDto1 != null) {
                return 1;
            }
            else {
                return -1;
            }
        }
    };

    private static final Comparator<ClientDto> BY_LAST_NAME = new Comparator<ClientDto>() {
        @Override
        public int compare(ClientDto o1, ClientDto o2) {
            return (o1.getLastName().compareTo(o2.getLastName()));
        }
    };

    private static final String AUSTRALIA_COUNTRY_CODE = "61";
    private static final String TRUNK_ACCESS_CODE = "0";

    private static final String AU_MOBILE_REGEX = "^0(4|5)[0-9]*|^(4|5)[0-9]*";
    private static final String AU_LANDINE_REGEX = "^0(1|2|3|7|8)[0-9]*|^(1|2|3|7|8)[0-9]*";
    private static final String AU_TOLLFREE_REGEX = "^(1300|1800)[0-9]*";
    private static final String FOREIGN_COUNTRY_TYPE = "FOREIGN";

    public static com.bt.nextgen.api.client.model.ClientKey convertClientKey(com.bt.nextgen.service.integration.userinformation.ClientIdentifier client) {
        return new com.bt.nextgen.api.client.model.ClientKey(fromPlainTextUsingTL(client.getClientKey().getId()).toString());
    }

    public static AddressKey convertAddressKey(com.bt.nextgen.service.integration.domain.AddressKey key) {
        return new AddressKey(fromPlainText(key.getId()).toString());
    }

    private static String lastThenFirstName(ClientDto client) {
        final StringBuilder fullName = new StringBuilder();
        final String[] names = {client.getLastName(), client.getFirstName()};
        for (String name : names) {
            if (name != null) {
                fullName.append(name.toUpperCase());
            }
        }
        return fullName.toString();
    }

    /**
     * @param fullAssociatedPersons if null, no attempt is made to adjust SMSF trustee's TFN exemption status if they don't have accounts.
     */
    @SuppressWarnings("squid:S1157")
    public static ClientDto toClientDto(final ClientDetail clientModel, @Nullable final List<AssociatedPerson> fullAssociatedPersons, StaticIntegrationService staticIntegrationService, final ServiceErrors serviceErrors) {
        InvestorDto clientDto = new InvestorDto();
        if (clientModel instanceof IndividualDetail) {
            clientDto = new IndividualDto();
            toIndividualDto((IndividualDetail) clientModel, (IndividualDto) clientDto, serviceErrors);
        }
        else if (clientModel instanceof Company) {
            clientDto = new CompanyDto();
            toRegisteredEntityDto((RegisteredEntity) clientModel, (RegisteredEntityDto) clientDto);
            toCompanyDto((Company) clientModel, (CompanyDto) clientDto, staticIntegrationService, serviceErrors);
        }
        else if (clientModel instanceof Trust) {
            clientDto = new TrustDto();
            toRegisteredEntityDto((RegisteredEntity) clientModel, (RegisteredEntityDto) clientDto);
            toTrustDto((Trust) clientModel, (TrustDto) clientDto, staticIntegrationService, serviceErrors);
        }
        else if (clientModel instanceof Smsf) {
            clientDto = new SmsfDto();
            ClientDetailDtoConverter.toRegisteredEntityDto((RegisteredEntity) clientModel, (RegisteredEntityDto) clientDto);
            ClientDetailDtoConverter.toSmsfDto((Smsf) clientModel, (SmsfDto) clientDto, fullAssociatedPersons, staticIntegrationService, serviceErrors);
        }
        if (clientModel != null) {
            toInvestorDto((InvestorDetail) clientModel, (InvestorDto) clientDto, serviceErrors);
            if (!(clientModel instanceof IndividualDetail) && clientModel.getFullName() != null) {
                clientDto.setFullName(clientModel.getFullName().toUpperCase().equals(clientModel.getFullName()) ? capitalizeFully(clientModel.getFullName()) : clientModel.getFullName());
            }

            clientDto.setPhones(getPhoneDtoList(clientModel, serviceErrors));
            clientDto.setEmails(getEmailDtoList(clientModel, serviceErrors));
            clientDto.setAddresses(getAddressDtoList(clientModel, serviceErrors));
            clientDto.setKey(convertClientKey(clientModel));
            if(CollectionUtils.isNotEmpty( ((ClientDetailImpl) clientModel).getTaxResidenceCountries())){
             clientDto.setTaxResidenceCountries(ClientTxnDtoConverter.convertTaxResidenceCountryDto(
                    ((ClientDetailImpl) clientModel).getTaxResidenceCountries(),staticIntegrationService ,serviceErrors));
            }

        }
        return clientDto;
    }

    /**
     * Convert InvestorModel to InvestorDto
     *
     * @param investorDetailModel
     * @param investorDto
     * @param serviceErrors
     */
    public static void toInvestorDto(InvestorDetail investorDetailModel, InvestorDto investorDto, ServiceErrors serviceErrors) {

        investorDto.setPersonRoles(getPersonRolesForDisplay(investorDetailModel.getPersonRoles()));
        investorDto.setSafiDeviceId(investorDetailModel.getSafiDeviceId());
        if (null != investorDetailModel.getExemptionReason()) {
            investorDto.setExemptionReason(investorDetailModel.getExemptionReason().getValue());
        }
        if (null != investorDetailModel.getPensionExemptionReason()) {
            investorDto.setPensionExemptionReason(investorDetailModel.getPensionExemptionReason());
        }
        if (null != investorDetailModel.getSaTfnExemptId()) {
            investorDto.setSaTfnExemptId(investorDetailModel.getSaTfnExemptId());
        }
        investorDto.setTfnProvided(investorDetailModel.getTfnProvided());
        investorDto.setModificationSeq(investorDetailModel.getModificationSeq());
        investorDto.setGcmId(investorDetailModel.getGcmId());
        investorDto.setOpenDate(ApiFormatter.asShortDate(investorDetailModel.getOpenDate()));
        investorDto.setAnzsicId(investorDetailModel.getAnzsicId());
        investorDto.setIndustry(investorDetailModel.getIndustry());
        investorDto.setInvestorType(investorDetailModel.getInvestorType().name());
        investorDto.setTfnExemptId(investorDetailModel.getTfnExemptId());
        if (null != investorDetailModel.getCISKey()) {
            investorDto.setCisId(investorDetailModel.getCISKey().getId());
        }
        investorDto.setResiCountryforTax(investorDetailModel.getResiCountryForTax());
        investorDto.setResiCountryCodeForTax(investorDetailModel.getResiCountryCodeForTax());

    }

    public static void toRegisteredEntityDto(RegisteredEntity registeredEntityModel, RegisteredEntityDto registeredEntityDto) {
        registeredEntityDto.setAbn(registeredEntityModel.getAbn());
        registeredEntityDto.setRegistrationDate(registeredEntityModel.getRegistrationDate());
        registeredEntityDto.setRegistrationForGst(registeredEntityModel.isRegistrationForGst());
        registeredEntityDto.setRegistrationState(registeredEntityModel.getRegistrationState());
        registeredEntityDto.setRegistrationStateCode(registeredEntityModel.getRegistrationStateCode());
    }

    /**
     * Convert IndividualModel to IndividualDto
     *
     * @param individualModel
     * @param individualDto
     * @param serviceErrors
     */
    @SuppressWarnings("squid:S1157")
    public static void toIndividualDto(IndividualDetail individualModel, IndividualDto individualDto, ServiceErrors serviceErrors) {
        individualDto.setUserName(individualModel.getUserName());
        final String title = individualModel.getTitle();
        if (null != title) {
            individualDto.setTitle(title);
            individualDto.setGcmTitleLabel(title);
        }
        individualDto.setResiCountryforTax(individualModel.getResiCountryForTax());
        individualDto.setResiCountryCodeForTax(individualModel.getResiCountryCodeForTax());
        individualDto.setPreferredName(capitalizeFully(individualModel.getPreferredName()));
        individualDto.setFirstName(capitalizeFully(individualModel.getFirstName()));
        individualDto.setMiddleName(capitalizeFully(individualModel.getMiddleName()));
        individualDto.setLastName(capitalizeFully(individualModel.getLastName()));
        List<String> names = Lambda.filter(new LambdaMatcher<String>() {
            @Override
            protected boolean matchesSafely(String item) {
                return item != null && !item.isEmpty();
            }
        }, Arrays.asList(individualModel.getFirstName(), individualModel.getMiddleName(), individualModel.getLastName()));

        //Capitalizing fullName only when the name is all Upper Case
        String fullName = StringUtils.join(names, ' ');
        individualDto.setFullName(fullName.toUpperCase().equals(fullName) ? capitalizeFully(fullName) : fullName);

        final Gender gender = individualModel.getGender();
        if (null != gender) {
            individualDto.setGender(gender.getName());
        }
        individualDto.setDateOfBirth(ApiFormatter.asShortDate(individualModel.getDateOfBirth()));
        individualDto.setAge(individualModel.getAge());
        final IdentityVerificationStatus idvStatus = individualModel.getIdVerificationStatus();
        if (null != idvStatus) {
            individualDto.setIdvs(idvStatus.getId());
            individualDto.setIdVerified(idvStatus == IdentityVerificationStatus.Completed);
        }
        individualDto.setFormerName(getFormerName(individualModel.getFormerNames()));
        setPlaceOfBirthDetails(individualDto, individualModel);
    }

    /**
     * Get first former name in the list
     *
     * @param formerNames
     *
     * @return
     */
    private static String getFormerName(List<String> formerNames) {
        if (CollectionUtils.isNotEmpty(formerNames)) {
            return formerNames.get(0);
        }
        return null;
    }

    private static void setPlaceOfBirthDetails(IndividualDto individualDto, IndividualDetail individualModel) {
        individualDto.setPlaceOfBirthCountry(individualModel.getBirthCountry());
        individualDto.setPlaceOfBirthSuburb(individualModel.getBirthSuburb());

        //Country comes in either full name or code depending on which service was called
        if (AU.equalsIgnoreCase(individualModel.getBirthCountry())
                || AUSTRALIA.equalsIgnoreCase(individualModel.getBirthCountry())) {
            individualDto.setPlaceOfBirthState(individualModel.getBirthStateDomestic());
        }
        else {
            individualDto.setPlaceOfBirthState(individualModel.getBirthStateInternational());
        }
    }

    /**
     * Convert CompanyModel to CompanyDto
     *
     * @param companyModel
     * @param companyDto
     * @param serviceErrors
     */
    public static void toCompanyDto(Company companyModel, CompanyDto companyDto, StaticIntegrationService staticIntegrationService, ServiceErrors serviceErrors) {
        companyDto.setAcn(companyModel.getAcn());
        companyDto.setAsicName(capitalizeFully(companyModel.getAsicName()));
        companyDto.setResiCountryforTax(companyModel.getResiCountryForTax());
        if (companyModel.getIdVerificationStatus() != null) {
            companyDto.setIdvs(companyModel.getIdVerificationStatus().toString());
        }

        List<InvestorDto> linkedClients = setTrusted(companyModel, companyDto, staticIntegrationService, serviceErrors);
        sort(linkedClients, BY_LAST_THEN_FIRST_NAME);

        companyDto.setLinkedClients(linkedClients);
    }

    /**
     * Convert smsfModel to SmsfDto.
     *
     * @param fullAssociatedPersons if null, no attempt is made to adjust SMSF trustee's TFN exemption status if they don't have accounts.
     */
    public static void toSmsfDto(final Smsf smsfModel, final SmsfDto smsfDto, final @Nullable List<AssociatedPerson> fullAssociatedPersons, StaticIntegrationService staticIntegrationService, final ServiceErrors serviceErrors) {
        List<InvestorDto> linkedClients = setTrusted(smsfModel, smsfDto, staticIntegrationService, serviceErrors);
        sort(linkedClients, BY_LAST_THEN_FIRST_NAME);
        smsfDto.setLinkedClients(linkedClients);

        if (fullAssociatedPersons != null) {
            adjustLinkedClients(linkedClients, fullAssociatedPersons);
        }

        if (smsfModel.getIdVerificationStatus() != null) {
            smsfDto.setIdvs(smsfModel.getIdVerificationStatus().toString());
        }
    }

    private static void adjustLinkedClients(final List<InvestorDto> linkedClients, @Nonnull final List<AssociatedPerson> fullAssociatedPersons) {
        for (InvestorDto linkedClient : linkedClients) {
            String linkedClientClientId = EncodedString.toPlainText(linkedClient.getKey().getClientId());
            for (AssociatedPerson fullAssociatedPerson : fullAssociatedPersons) {
                String associatedPersonClientId = fullAssociatedPerson.getClientKey().getId();
                if (linkedClientClientId != null && associatedPersonClientId != null && linkedClientClientId.equals(associatedPersonClientId)) {
                    adjustTfnFieldFor(linkedClient, linkedClientClientId, fullAssociatedPerson);
                    if (linkedClient instanceof IndividualDto) {
                        IndividualDto linkedIndividualClient = (IndividualDto) linkedClient;
                        applyFormerNameFor(linkedIndividualClient, fullAssociatedPerson);
                        applyPlaceOfBirthFor(linkedIndividualClient, fullAssociatedPerson);
                    }
                }
            }
        }
    }

    private static void adjustTfnFieldFor(final InvestorDto linkedClient, final String linkedClientClientId, final AssociatedPerson fullAssociatedPerson) {
        boolean existingTfnProvided = linkedClient.isTfnProvided();
        boolean tfnEntered = fullAssociatedPerson.hasTFNEntered();
        if (!existingTfnProvided && tfnEntered) {
            linkedClient.setTfnProvided(true); // UI logic uses this to determine TFN status for trustees / directors
            LOGGER.info(LoggingConstants.ONBOARDING + " linkedClient ClientId=" + linkedClientClientId + ", overriding it's InvestorDto.tfnProvided with AssociatedPerson.person_has_tfn derived tfnEntered=" + tfnEntered);
        }
        linkedClient.setTfnEntered(tfnEntered); // DEBUG: placed in JSON for reference but not used (at this time)
        linkedClient.setTfnExemptId(fullAssociatedPerson.getTfnExemptId());
    }

    private static void applyFormerNameFor(final IndividualDto linkedIndividualClient, final AssociatedPerson fullAssociatedPerson) {
        List<FormerName> formerNames = fullAssociatedPerson.getFormerNames();
        String immediatelyPriorFormerName = null;
        if (CollectionUtils.isNotEmpty(formerNames)) {
            immediatelyPriorFormerName = formerNames.get(0).getFormerName();
        }
        linkedIndividualClient.setFormerName(immediatelyPriorFormerName);
    }

    private static void applyPlaceOfBirthFor(final IndividualDto linkedIndividualClient, final AssociatedPerson fullAssociatedPerson) {
        linkedIndividualClient.setPlaceOfBirthCountry(fullAssociatedPerson.getBirthCountry());
        linkedIndividualClient.setPlaceOfBirthSuburb(fullAssociatedPerson.getBirthSuburb());

        boolean isAustralianBirthPlace = AUSTRALIA.equalsIgnoreCase(fullAssociatedPerson.getBirthCountry());
        if (isAustralianBirthPlace) {
            linkedIndividualClient.setPlaceOfBirthState(fullAssociatedPerson.getBirthStateDomestic());
        }
        else {
            linkedIndividualClient.setPlaceOfBirthState(fullAssociatedPerson.getBirthStateInternational());
        }
    }

    private static List<InvestorDto> setTrusted(RegisteredEntity regisModel, RegisteredEntityDto regisEntDto, StaticIntegrationService staticIntegrationService, ServiceErrors serviceErrors) {
        List<ClientDetail> smsfTrustees = null;
        boolean firstCompany = true;

        if (regisModel instanceof Smsf) {
            smsfTrustees = ((Smsf) regisModel).getTrustees();
        }
        else if (regisModel instanceof Trust) {
            smsfTrustees = ((Trust) regisModel).getTrustees();
        }

        List<InvestorDetail> linkedClientModelList = regisModel.getLinkedClients();

        Map<ClientKey, IndividualDto> linkedClientDtoMap = new HashMap<ClientKey, IndividualDto>();

        if (linkedClientModelList != null) {
            for (InvestorDetail linkedClientModel : linkedClientModelList) {
                setInvestorLinkedClient((IndividualDetail) linkedClientModel, linkedClientDtoMap, serviceErrors);
            }
        }

        if (smsfTrustees != null) {
            for (ClientDetail smsfTrustee : smsfTrustees) {
                if (firstCompany && (smsfTrustee instanceof Company)) {
                    CompanyDto companyDto = new CompanyDto();
                    getCorporateCompanyDetails(smsfTrustee, companyDto, staticIntegrationService, serviceErrors);
                    if (regisEntDto instanceof SmsfDto) {
                        ((SmsfDto) regisEntDto).setCompany(companyDto);
                    }
                    else {
                        ((TrustDto) regisEntDto).setCompany(companyDto);
                    }

                    firstCompany = false;
                }
                else {
                    IndividualDetail linkedClientModel = (IndividualDetail) smsfTrustee;
                    setInvestorLinkedClient(linkedClientModel, linkedClientDtoMap, serviceErrors);

                }
            }
        }
        List<InvestorDto> linkedClientDtoList = new ArrayList<InvestorDto>(linkedClientDtoMap.values());
        sort(linkedClientDtoList, BY_LAST_NAME);
        return linkedClientDtoList;
    }

    public static void setInvestorLinkedClient(IndividualDetail linkedClientModel, Map<ClientKey, IndividualDto> linkedClientDtoMap, ServiceErrors serviceErrors) {
        IndividualDto linkedClientDto = null;
        linkedClientDto = linkedClientDtoMap.get(linkedClientModel.getClientKey());

        if (linkedClientDto == null) {
            linkedClientDto = new IndividualDto();
            toIndividualDto(linkedClientModel, linkedClientDto, serviceErrors);
            setIndividualDtoAddress(linkedClientModel, linkedClientDto, serviceErrors);
        }
        else {
            linkedClientDto = linkedClientDtoMap.get(linkedClientModel.getClientKey());
        }
        if (linkedClientModel.getPersonRoles() != null) {
            linkedClientDto.setPersonRoles(getPersonRolesForDisplay(linkedClientModel.getPersonRoles()));
        }
        linkedClientDto.setKey(convertClientKey(linkedClientModel));
        linkedClientDtoMap.put(linkedClientModel.getClientKey(), linkedClientDto);
    }

    private static void setIndividualDtoAddress(IndividualDetail linkedClientModel, IndividualDto linkedClientDto, ServiceErrors serviceErrors) {
        linkedClientDto.setPhones(getPhoneDtoList(linkedClientModel, serviceErrors));
        linkedClientDto.setEmails(getEmailDtoList(linkedClientModel, serviceErrors));
        linkedClientDto.setAddresses(getAddressDtoList(linkedClientModel, serviceErrors));
    }

    /**
     * Convert trustModel to TrustDto
     *
     * @param trustModel
     * @param trustDto
     * @param serviceErrors
     */
    public static void toTrustDto(Trust trustModel, TrustDto trustDto, StaticIntegrationService staticIntegrationService, ServiceErrors serviceErrors) {
        trustDto.setLegEstFund(trustModel.getLegEstFund());
        trustDto.setTrustType(trustModel.getTrustType().getTrustTypeValue());
        setTrustDescription(trustModel, trustDto);
        switch (trustModel.getTrustType()) {
            case REGI_MIS:
                trustDto.setArsn(trustModel.getArsn());
                break;
            case REGU_TRUST:
                trustDto.setLicencingNumber(trustModel.getLicencingNumber());
                break;
            default:
                break;
        }
        trustDto.setTrustReguName(capitalizeFully(trustModel.getTrustReguName()));
        trustDto.setTrustMemberClass(capitalizeFully(trustModel.getTrustMemberClass()));
        trustDto.setBusinessName(capitalizeFully(trustModel.getAsicName()));

        List<InvestorDto> linkedClients = setTrusted(trustModel, trustDto, staticIntegrationService, serviceErrors);
        sort(linkedClients, BY_LAST_THEN_FIRST_NAME);
        trustDto.setLinkedClients(linkedClients);
        List<IndividualDto> beneficiaries = getBeneficiaries(trustModel.getBeneficiaries(), serviceErrors);
        sort(beneficiaries, BY_LAST_THEN_FIRST_NAME);
        trustDto.setBeneficiaries(beneficiaries);
        if (trustModel.getIdVerificationStatus() != null) {
            trustDto.setIdvs(trustModel.getIdVerificationStatus().toString());
        }
    }

    private static void setTrustDescription(Trust trustModel, TrustDto trustDto) {
        if (trustModel.getTrustTypeDesc() != null) {
            trustDto.setTrustTypeDesc(trustModel.getTrustTypeDesc().getTrustTypeDescValue());
            if (trustModel.getBusinessClassificationDesc() != null) {
                trustDto.setBusinessClassificationDesc(trustModel.getTrustTypeDesc().getTrustTypeDescValue() + " - " + trustModel.getBusinessClassificationDesc());
            }
            else {
                trustDto.setBusinessClassificationDesc(trustModel.getTrustTypeDesc().getTrustTypeDescValue());
            }
        }
    }

    /**
     * Convert AddressModel to AddressDto
     *
     * @param addressModel
     * @param addressDto
     * @param serviceErrors
     */
    public static void toAddressDto(Address addressModel, AddressDto addressDto, ServiceErrors serviceErrors) {
        addressDto.setUnitNumber(addressModel.getUnit());
        addressDto.setSuburb(capitalizeFully(addressModel.getSuburb()));
        addressDto.setStreetType(addressModel.getStreetType());
        addressDto.setStreetTypeUserId(addressModel.getStreetTypeUserId());
        addressDto.setStreetNumber(addressModel.getStreetNumber());
        if (addressModel.getStreetName() != null) {
            addressDto.setStreetName(capitalizeFully(addressModel.getStreetName()));
        }
        else {
            addressDto.setPoBoxPrefix(addressModel.getPoBoxPrefix());
            addressDto.setPoBox(addressModel.getPoBox());
        }
        addressDto.setStateCode(addressModel.getStateCode());
        String state = addressModel.getState() != null ? addressModel.getState() : addressModel.getStateOther();
        addressDto.setState(state);
        addressDto.setStateAbbr(addressModel.getStateAbbr());
        addressDto.setPostcode(addressModel.getPostCode());
        addressDto.setModificationSeq(addressModel.getModificationSeq());
        addressDto.setFloor(addressModel.getFloor());
        addressDto.setDomicile(addressModel.isDomicile());
        addressDto.setMailingAddress(addressModel.isMailingAddress());
        addressDto.setCountryCode(addressModel.getCountryCode());
        addressDto.setCountry(capitalizeFully(addressModel.getCountry()));
        addressDto.setCountryAbbr(addressModel.getCountryAbbr());
        addressDto.setCity(capitalizeFully(addressModel.getCity()));
        addressDto.setCareOf(capitalizeFully(addressModel.getCareOf()));
        addressDto.setBuilding(capitalizeFully(addressModel.getBuilding()));
        if (addressModel.getAddressType() != null) {
            addressDto.setAddressType(addressModel.getAddressType().getAddressType());
        }
        if (addressModel.getAddressKey() != null) {
            addressDto.setAddressKey(convertAddressKey(addressModel.getAddressKey()));
        }
    }

    /**
     * Convert PhoneModel to PhoneDto
     *
     * @param phoneModel
     * @param phoneDto
     * @param serviceErrors
     */
    public static void toPhoneDto(Phone phoneModel, PhoneDto phoneDto, ServiceErrors serviceErrors) {
        phoneDto.setAreaCode(phoneModel.getAreaCode());
        phoneDto.setCountryCode(phoneModel.getCountryCode());
        phoneDto.setModificationSeq(phoneModel.getModificationSeq());
        phoneDto.setNumber(phoneModel.getNumber());
        phoneDto.setPreferred(phoneModel.isPreferred());
        phoneDto.setPhoneType(phoneModel.getType().getAddressType());
        phoneDto.setFullPhoneNumber(phoneModel.getNumber());
        phoneDto.setPhoneKey(convertAddressKey(phoneModel.getPhoneKey()));
    }

    /**
     * Convert EmailModel to EmailDto
     *
     * @param emailModel
     * @param emailDto
     * @param serviceErrors
     */
    public static void toEmailDto(Email emailModel, EmailDto emailDto, ServiceErrors serviceErrors) {
        emailDto.setEmail(emailModel.getEmail());
        emailDto.setModificationSeq(emailModel.getModificationSeq());
        emailDto.setPreferred(emailModel.isPreferred());
        emailDto.setEmailType(emailModel.getType().getAddressType());
        emailDto.setEmailKey(convertAddressKey(emailModel.getEmailKey()));
    }

    /**
     * To get Beneficiaries details for trust
     *
     * @param beneficiaries
     * @param serviceErrors
     *
     * @return
     */
    public static List<IndividualDto> getBeneficiaries(List<InvestorDetail> beneficiaries, ServiceErrors serviceErrors) {
        List<IndividualDto> beneficiariesDto = emptyList();
        if (!isEmpty(beneficiaries)) {
            beneficiariesDto = new ArrayList<>(beneficiaries.size());
            for (InvestorDetail individualDetail : beneficiaries) {
                final IndividualDto individualDto = new IndividualDto();
                final String title = ((IndividualDetail) individualDetail).getTitle();
                if (null != title) {
                    individualDto.setTitle(title);
                }
                individualDto.setFirstName(capitalizeFully(individualDetail.getFirstName()));
                individualDto.setLastName(capitalizeFully(individualDetail.getLastName()));
                if (((IndividualDetail) individualDetail).getGender() != null) {
                    individualDto.setGender(capitalizeFully(((IndividualDetail) individualDetail).getGender().getName()));
                }
                if (((IndividualDetail) individualDetail).getDateOfBirth() != null) {
                    individualDto.setDateOfBirth(ApiFormatter.asShortDate(((IndividualDetail) individualDetail).getDateOfBirth()));
                }
                if (hasText(((IndividualDetail) individualDetail).getMiddleName())) {
                    individualDto.setMiddleName(capitalizeFully(((IndividualDetail) individualDetail).getMiddleName()));
                }
                AddressDto addressDto = new AddressDto();
                if (!isEmpty(individualDetail.getAddresses())) {
                    toAddressDto(individualDetail.getAddresses().get(0), addressDto, serviceErrors);
                }
                individualDto.setAddresses(Arrays.asList(addressDto));
                individualDto.setPersonRoles(getPersonRolesForDisplay(individualDetail.getPersonRoles()));
                individualDto.setKey(convertClientKey(individualDetail));
                if (null != individualDetail.getIdVerificationStatus()) {
                    individualDto.setIdvs(individualDetail.getIdVerificationStatus().getId());
                }
                beneficiariesDto.add(individualDto);
            }
            sort(beneficiariesDto, BY_LAST_NAME);
        }
        return beneficiariesDto;
    }

    private static List<InvestorRole> getPersonRolesForDisplay(List<InvestorRole> personRoles) {
        if (personRoles != null && !personRoles.isEmpty() && personRoles.contains(InvestorRole.Shareholder) && personRoles.contains(InvestorRole.BeneficialOwner)) {
            personRoles.remove(InvestorRole.BeneficialOwner);
        }
        return personRoles;
    }

    public static void getCorporateCompanyDetails(ClientDetail client, CompanyDto companyDto, StaticIntegrationService staticIntegrationService, ServiceErrors serviceErrors) {
        companyDto.setAcn(((Company) client).getAcn());
        companyDto.setAsicName(capitalizeFully(((Company) client).getAsicName()));
        toInvestorDto((InvestorDetail) client, companyDto, serviceErrors);
        toRegisteredEntityDto((RegisteredEntity) client, companyDto);
        companyDto.setKey(convertClientKey(client));
        companyDto.setFullName(capitalizeFully(((Company) client).getAsicName()));
        List<AddressDto> addressDtos = new ArrayList<AddressDto>();
        for (Address addressModel : client.getAddresses()) {
            AddressDto addressDto = new AddressDto();
            toAddressDto(addressModel, addressDto, serviceErrors);
            addressDtos.add(addressDto);
        }
        companyDto.setAddresses(addressDtos);
        if (client instanceof RegisteredEntity && ((RegisteredEntity) client).getIdVerificationStatus() != null) {
            companyDto.setIdvs(((RegisteredEntity) client).getIdVerificationStatus().toString());
        }
        companyDto.setOccupierName(getOccupierNameFromAddress(client.getAddresses()));
        companyDto.setTaxResidenceCountries(ClientTxnDtoConverter.convertTaxResidenceCountryDto(
                ((ClientDetailImpl) client).getTaxResidenceCountries(), staticIntegrationService, serviceErrors));

    }

    private static String getOccupierNameFromAddress(List<Address> addresses) {
        for (Address address : addresses) {
            if (StringUtils.isNotBlank(address.getOccupierName())) {
                return address.getOccupierName();
            }
        }
        return null;
    }

    public static void toAddressModel(AddressImpl addressModel, AddressDto addressDto) {
        //TODO PoBoxPrefix and State Others
        addressModel.setModificationSeq(addressDto.getModificationSeq());
        if (addressDto.getAddressKey() != null) {
            addressModel.setAddressKey((com.bt.nextgen.service.integration.domain.AddressKey.valueOf(new EncodedString(addressDto.getAddressKey().getAddressId()).plainText())));
        }

        addressModel.setBuilding(addressDto.getBuilding());
        addressModel.setCity(addressDto.getCity());
        addressModel.setCountry(addressDto.getCountry());
        addressModel.setCountryCode(addressDto.getCountryCode());
        addressModel.setFloor(addressDto.getFloor());
        addressModel.setMailingAddress(addressDto.isMailingAddress());
        addressModel.setPoBox(addressDto.getPoBox());
        addressModel.setPoBoxPrefix(addressDto.getPoBoxPrefix());
        addressModel.setPostCode(addressDto.getPostcode());
        addressModel.setState(addressDto.getState());
        addressModel.setStateCode(addressDto.getStateCode());
        addressModel.setStreetName(addressDto.getStreetName());
        addressModel.setStreetNumber(addressDto.getStreetNumber());
        addressModel.setStreetType(addressDto.getStreetType());
        addressModel.setSuburb(addressDto.getSuburb());
        addressModel.setUnit(addressDto.getUnitNumber());
        addressModel.setPostAddress(AddressType.POSTAL);
        AddressMedium addressType = AddressMedium.getAddressMediumByAddressType(addressDto.getAddressType());
        addressModel.setAddressType(addressType);
    }

    public static void toPhoneModel(PhoneImpl phoneModel, PhoneDto phoneDto) {
        phoneModel.setAreaCode(phoneDto.getAreaCode());
        phoneModel.setCountryCode(phoneDto.getCountryCode());
        phoneModel.setModificationSeq(phoneDto.getModificationSeq());

        if (isNotBlank(phoneDto.getFullPhoneNumber())) {
            phoneModel.setNumber(phoneDto.getFullPhoneNumber());
        }
        else {
            phoneModel.setNumber(phoneDto.getNumber());
        }

        phoneModel.setPreferred(phoneDto.isPreferred());
        final AddressMedium addressType;
        if (AddressMedium.MOBILE_PHONE_PRIMARY.getAddressType().equals(phoneDto.getPhoneType())) {
            addressType = AddressMedium.MOBILE_PHONE_PRIMARY;
        }
        else if (AddressMedium.MOBILE_PHONE_SECONDARY.getAddressType().equals(phoneDto.getPhoneType())) {
            addressType = AddressMedium.MOBILE_PHONE_SECONDARY;
        }
        else {
            addressType = AddressMedium.getAddressMediumByAddressType(phoneDto.getPhoneType());
        }
        phoneModel.setType(addressType);
        phoneModel.setCategory(AddressType.ELECTRONIC);
        if (phoneDto.getPhoneKey() != null) {
            phoneModel.setPhoneKey(com.bt.nextgen.service.integration.domain.AddressKey.valueOf(new EncodedString(phoneDto.getPhoneKey().getAddressId()).plainText()));
        }
    }

    public static void toEmailModel(Email email, EmailDto emailDto) {
        final AddressMedium addressType = AddressMedium.EMAIL_PRIMARY.getAddressType().equals(emailDto.getEmailType()) ? AddressMedium.EMAIL_PRIMARY : AddressMedium.EMAIL_ADDRESS_SECONDARY;
        if (email instanceof EmailImpl) {
            final EmailImpl emailModel = (EmailImpl) email;
            emailModel.setEmail(emailDto.getEmail());
            emailModel.setModificationSeq(emailDto.getModificationSeq());
            emailModel.setPreferred(emailDto.isPreferred());
            emailModel.setType(addressType);
            emailModel.setCategory(AddressType.ELECTRONIC);
            if (emailDto.getEmailKey() != null) {
                emailModel.setEmailKey(com.bt.nextgen.service.integration.domain.AddressKey.valueOf(new EncodedString(emailDto.getEmailKey().getAddressId()).plainText()));
            }
        }
        else if (email instanceof CustomerEmail) {
            final CustomerEmail customerEmail = (CustomerEmail) email;
            customerEmail.setEmail(emailDto.getEmail());
            customerEmail.setOldAddress(emailDto.getOldAddress());
            customerEmail.setType(addressType);
            customerEmail.setCategory(AddressType.ELECTRONIC);
            customerEmail.setModificationSeq(emailDto.getModificationSeq());
            customerEmail.setPreferred(emailDto.isPreferred());
            if (emailDto.getEmailKey() != null) {
                customerEmail.setEmailKey(com.bt.nextgen.service.integration.domain.AddressKey.valueOf(new EncodedString(emailDto.getEmailKey().getAddressId()).plainText()));
            }
            if (isNotBlank(emailDto.getEmailActionCode())) {
                customerEmail.setAction(CustomerEmail.EmailAction.fromString(emailDto.getEmailActionCode()));
            }
        }
    }

    public static List<Address> setAddressListForUpdate(Collection<AddressDto> addressDtos) {
        final List<Address> addressFormattedList = new ArrayList<>(addressDtos.size());
        for (AddressDto addressDto : addressDtos) {
            final AddressImpl address = new AddressImpl();
            toAddressModel(address, addressDto);
            addressFormattedList.add(address);
        }
        return addressFormattedList;
    }

    public static Address setAddressForUpdate(Collection<AddressDto> collection, AddressStreetTypeMapper streetTypeMapper) {
        Address address = null;
        for (AddressDto addressDto : collection) {
            if (addressDto.isGcmAddress()) {
                if (addressDto.isInternationalAddress()) {
                    final CustomerAddress customerAddress = new CustomerAddress();
                    toInternationalAddressModel(customerAddress, addressDto);
                    address = customerAddress;
                }
                else {
                    final AddressImpl addressImpl = new AddressImpl();
                    toGcmAddressModel(addressImpl, addressDto);
                    //Assigning StartedStreetType for the address update
                    addressImpl.setStreetType(streetTypeMapper.getStandardStreetType(addressDto.getStreetType()));
                    address = addressImpl;
                }
            }
        }
        return address;
    }

    private static void toGcmAddressModel(AddressImpl addressModel, AddressDto addressDto) {
        addressModel.setBuilding(addressDto.getBuilding());
        addressModel.setCity(addressDto.getCity() != null ? addressDto.getCity() : addressDto.getSuburb());
        addressModel.setCountry(addressDto.getCountry());
        addressModel.setCountryCode(addressDto.getCountryCode());
        addressModel.setFloor(addressDto.getFloor());
        addressModel.setMailingAddress(addressDto.isMailingAddress());
        addressModel.setPoBox(addressDto.getPoBox());
        addressModel.setPoBoxPrefix(addressDto.getPoBoxPrefix());
        addressModel.setPostCode(addressDto.getPostcode());
        addressModel.setState(addressDto.getState());
        addressModel.setStateCode(addressDto.getStateCode());
        addressModel.setStreetName(addressDto.getStreetName());
        addressModel.setStreetNumber(addressDto.getStreetNumber());
        addressModel.setStreetType(addressDto.getStreetType());
        addressModel.setSuburb(addressDto.getSuburb());
        addressModel.setUnit(addressDto.getUnitNumber());
        addressModel.setPostAddress(AddressType.POSTAL);
        AddressMedium addressType = AddressMedium.getAddressMediumByAddressType(addressDto.getAddressType());
        addressModel.setAddressType(addressType);
    }

    protected static void toInternationalAddressModel(CustomerAddress address, AddressDto addressDto) {
        address.setInternationalAddress(addressDto.isInternationalAddress());
        address.setAddressLine1(addressDto.getAddressLine1());
        address.setAddressLine2(addressDto.getAddressLine2());
        address.setAddressLine3(addressDto.getAddressLine3());
        address.setCity(addressDto.getCity() != null ? addressDto.getCity() : addressDto.getSuburb());
        address.setStateName(addressDto.getState() != null ? addressDto.getState() : addressDto.getStateAbbr());
        address.setCountryName(addressDto.getCountry() != null ? addressDto.getCountry() : addressDto.getCountryAbbr());
        address.setPostCode(addressDto.getPostcode());
    }

    public static void filterEmails(ClientTxnDto clientDto) {
        List<EmailDto> emailList = new ArrayList<EmailDto>();

        for (EmailDto email : clientDto.getEmails()) {
            if (isBlank(email.getEmailActionCode()) ||
                    (isNotBlank(email.getEmailActionCode()) &&
                            !CustomerEmail.EmailAction.DELETE.equals(CustomerEmail.EmailAction.fromString(email.getEmailActionCode())))) {
                emailList.add(email);
            }
        }
        clientDto.setEmails(emailList);
    }

    public static void filterData(ClientTxnDto clientDto, List<PhoneDto> phoneListFromUI, List<EmailDto> emailListFromUI) {
        final List<EmailDto> gcmEmails = new ArrayList<>();
        for (EmailDto email : emailListFromUI) {
            if (email.getGcmMastered()) {
                gcmEmails.add(email);
            }
        }
        clientDto.setEmails(gcmEmails);
        clientDto.setPhones(phoneListFromUI);
    }

    /**
     * This method would set the domain TaxResidentCountry object with relevant TIN information
     *
     * @param taxResidenceCountriesDto object of {@link TaxResidenceCountriesDto}
     *
     * @return List of {@link TaxResidenceCountry}
     */
    public static List<TaxResidenceCountry> setTaxResidenceCountriesForUpdate(Collection<TaxResidenceCountriesDto> taxResidenceCountriesDto,
                                                                              StaticIntegrationService staticIntegrationService,
                                                                              ServiceErrors serviceErrors) {
        final List<TaxResidenceCountry> countryList = new ArrayList<>();
        Code code = null;
        Field field = null;
        if (taxResidenceCountriesDto != null) {
            for (TaxResidenceCountriesDto countryDto : taxResidenceCountriesDto) {
                final TaxResidenceCountry taxResidenceCountry = new TaxResidenceCountry();
                // For FOREIGN, TIN value passed from UI should be Y or N
                taxResidenceCountry.setTin(countryDto.getTin());

                if (StringUtils.isNotBlank(countryDto.getTaxExemptionReason())) {
                    code = staticIntegrationService.loadCodeByAvaloqId(CodeCategory.TIN_EXEMPTION_REASONS, countryDto.getTaxExemptionReason(), serviceErrors);
                    field = code.getField("btfg$ucm_code");
                    taxResidenceCountry.setExemptionReason(field.getValue());
                }
                if (!FOREIGN_COUNTRY_TYPE.equalsIgnoreCase(countryDto.getTaxResidenceCountry())) {
                    code = staticIntegrationService.loadCodeByAvaloqId(CodeCategory.COUNTRY, countryDto.getTaxResidenceCountry(), serviceErrors);
                    field = code.getField("btfg$im_code");
                    taxResidenceCountry.setResidenceCountry(field.getValue());
                }
                else {
                    taxResidenceCountry.setResidenceCountry(FOREIGN_COUNTRY_TYPE);
                }
                taxResidenceCountry.setStartDate(DateTime.parse(countryDto.getStartDate()));
                if (StringUtils.isNotBlank(countryDto.getEndDate())) {
                    taxResidenceCountry.setEndDate(DateTime.parse(countryDto.getEndDate()));
                }
                taxResidenceCountry.setVersionNumber(countryDto.getVersionNumber());
                countryList.add(taxResidenceCountry);
            }
        }

        return countryList;
    }

    public static List<Email> setEmailListForUpdate(Collection<EmailDto> emailDtos) {
        final List<Email> emailFormattedList = new ArrayList<>(emailDtos.size());
        for (EmailDto emailDto : emailDtos) {
            final Email email;
            if (emailDto.getGcmMastered()) {
                email = new CustomerEmail();
            }
            else {
                email = new EmailImpl();
            }
            toEmailModel(email, emailDto);
            emailFormattedList.add(email);
        }
        return emailFormattedList;
    }

    public static List<Phone> setPhoneListForUpdate(Collection<PhoneDto> phoneDtos) {
        final List<Phone> phoneFormattedList = new ArrayList<>(phoneDtos.size());
        for (PhoneDto phoneDto : phoneDtos) {
            PhoneImpl phone = new PhoneImpl();
            toPhoneModel(phone, phoneDto);
            phoneFormattedList.add(phone);
        }
        return phoneFormattedList;
    }

    @SuppressWarnings({"squid:S138"})
    public static List<AddressDto> getAddressDtoList(ClientDetail client, ServiceErrors serviceErrors) {
        List<AddressDto> addressDtos = new ArrayList<>();
        for (Address addressModel : client.getAddresses()) {
            AddressDto addressDto = new AddressDto();
            toAddressDto(addressModel, addressDto, serviceErrors);
            addressDtos.add(addressDto);
        }
        return addressDtos;
    }

    public static List<EmailDto> getEmailDtoList(ClientDetail client, ServiceErrors serviceErrors) {
        List<EmailDto> emailDtos = new ArrayList<>();
        List<Email> emails = new EmailFilterUtil().filterDuplicates(client.getEmails());
        for (Email email : emails) {
            EmailDto emailDto = new EmailDto();
            toEmailDto(email, emailDto, serviceErrors);
            emailDtos.add(emailDto);
        }
        return emailDtos;
    }

    public static List<PhoneDto> getPhoneDtoList(ClientDetail client, ServiceErrors serviceErrors) {
        List<PhoneDto> phoneDtos = new ArrayList<>();
        List<Phone> phones = new PhoneFilterUtil().filterDuplicates(client.getPhones());
        for (Phone phone : phones) {
            PhoneDto phoneDto = new PhoneDto();
            toPhoneDto(phone, phoneDto, serviceErrors);
            phoneDtos.add(phoneDto);
        }
        return phoneDtos;
    }

    /**
     * Method to fetch the domain level Registered State object to be sent to Integration Layer
     *
     * @param registeredStateDto
     *
     * @return customerRegisteredSate
     */
    public static CustomerRegisteredState convertToRegistrationStateModel(RegisteredStateDto registeredStateDto) {
        final CustomerRegisteredState customerRegisteredState = new CustomerRegisteredState();
        if (null != registeredStateDto) {
            customerRegisteredState.setRegistrationNumber(registeredStateDto.getRegistrationNumber());
            customerRegisteredState.setRegistrationState(registeredStateDto.getRegistrationState());
            customerRegisteredState.setCountry(registeredStateDto.getCountry());
            customerRegisteredState.setRegistrationStateCode(registeredStateDto.getRegistrationStateCode());
        }
        return customerRegisteredState;
    }

    public static void parseFullNumberToGCMNumberFormat(PhoneDto phone) {
        String phoneNum = phone.getFullPhoneNumber();
        if (isNotBlank(phoneNum)) {
            final Pattern australianMobilePattern = Pattern.compile(AU_MOBILE_REGEX);
            final Pattern australianLandPattern = Pattern.compile(AU_LANDINE_REGEX);
            final Pattern tollFreeNumberPattern = Pattern.compile(AU_TOLLFREE_REGEX);

            if (phoneNum.length() > 10) {
                try {
                    if (phoneNum.startsWith(AUSTRALIA_COUNTRY_CODE)) {
                        phone.setCountryCode(AUSTRALIA_COUNTRY_CODE);
                        phoneNum = phoneNum.replaceFirst(AUSTRALIA_COUNTRY_CODE, "");

                        if (australianMobilePattern.matcher(phoneNum).find()) {
                            createContact(phone, phoneNum);
                        }
                        else if (tollFreeNumberPattern.matcher(phoneNum).find()) {
                            createTollFreeContact(phone, phoneNum);
                        }
                        else if (australianLandPattern.matcher(phoneNum).find()) {
                            createContact(phone, phoneNum);
                        }
                    }
                    else {
                        phone.setCountryCode(phoneNum.substring(0, 2));
                        phone.setAreaCode(phoneNum.substring(2, 5));
                        phone.setNumber(phoneNum.substring(5, phoneNum.length()));
                    }
                }
                catch (StringIndexOutOfBoundsException e) {
                    LOGGER.info("Entered number couldn't be parsed as the length is lesser than required {}", phoneNum, e);
                }
            }
            else {
                try {
                    if (australianMobilePattern.matcher(phoneNum).find()) {
                        createContact(phone, phoneNum);
                    }
                    else if (tollFreeNumberPattern.matcher(phoneNum).find()) {
                        createTollFreeContact(phone, phoneNum);
                    }
                    else if (australianLandPattern.matcher(phoneNum).find()) {
                        createContact(phone, phoneNum);
                    }
                    phone.setCountryCode(AUSTRALIA_COUNTRY_CODE);
                }
                catch (StringIndexOutOfBoundsException e) {
                    LOGGER.info("Entered number couldn't be parsed as the length is lesser than required {}", phoneNum, e);
                }
            }
            phone.setFullPhoneNumber(createFullTelephoneNumber(phone));
            LOGGER.debug("Entered number is parsed to be as {}", phoneNum);
        }
    }

    private static String createFullTelephoneNumber(PhoneDto phone) {
        return phone.getCountryCode() + phone.getAreaCode() + phone.getNumber();
    }

    private static PhoneDto createContact(PhoneDto phoneSend, String phoneNum) {
        if (phoneNum.startsWith("0")) {
            phoneSend.setAreaCode(phoneNum.substring(0, 2));
            phoneSend.setNumber(phoneNum.substring(2, phoneNum.length()));
        }
        else {
            phoneSend.setAreaCode(TRUNK_ACCESS_CODE + phoneNum.substring(0, 1));
            phoneSend.setNumber(phoneNum.substring(1, phoneNum.length()));
        }
        return phoneSend;
    }

    private static PhoneDto createTollFreeContact(PhoneDto phoneSend, String phoneNum) {
        phoneSend.setAreaCode(phoneNum.substring(0, 4));
        phoneSend.setNumber(phoneNum.substring(4, phoneNum.length()));
        return phoneSend;
    }

    public static void parseFullNumberListToGCMNumberFormat(List<PhoneDto> phoneList) {
        for (PhoneDto phone : phoneList) {
            parseFullNumberToGCMNumberFormat(phone);
        }
    }

    /**
     * Method to create list for avaloq update
     *
     * @param clientDto
     */
    public static void createPhoneListForAvaloqUpdate(ClientTxnDto clientDto) {
        final List<PhoneDto> absPhones = new ArrayList<>();
        //Add all phones as everything needs to be sent to avaloq - Remove the deleted ones and the empty ones
        for (PhoneDto phone : clientDto.getPhones()) {
            if (isBlank(phone.getRequestedAction()) || !PhoneAction.fromString(phone.getRequestedAction()).equals(PhoneAction.DELETE) && (isNotBlank(phone.getNumber()) || isNotBlank(phone.getFullPhoneNumber()))) {
                removeSpecialCharsAndSpaces(phone);
                parseFullNumberToGCMNumberFormat(phone);
                absPhones.add(phone);
            }
        }

        clientDto.setPhones(absPhones);
    }

    public static void removeSpecialCharsAndSpaces(PhoneDto phone) {
        if (isNotBlank(phone.getFullPhoneNumber())) {
            phone.setFullPhoneNumber(phone.getFullPhoneNumber().replaceAll("\\s", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\+", ""));
        }
    }

    public static void setClientDtoAccounts(com.bt.nextgen.api.client.model.ClientDto clientDto, Collection<WrapAccount> wrapAccounts) {
        List<AccountDto> accountList = new ArrayList<>();
        for (WrapAccount wrapAccount : wrapAccounts) {
            com.bt.nextgen.api.account.v1.model.AccountKey accountKey = new com.bt.nextgen.api.account.v1.model.AccountKey(EncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString());
            AccountDto accountDto = new AccountDto(accountKey);

            accountDto.setAccountType(wrapAccount.getAccountStructureType().name());
            if (wrapAccount instanceof PensionAccountDetail) {
                accountDto.setAccountType(PensionType.STANDARD.getLabel());
            }
            if (accountDto.getAccountType().equals(AccountStructureType.SUPER.name())) {
                accountDto.setAccountType(org.apache.commons.lang.StringUtils.capitalize(AccountStructureType.SUPER.name().toLowerCase()));
            }
            accountList.add(accountDto);
        }
        clientDto.setAccounts(accountList);
    }

}
