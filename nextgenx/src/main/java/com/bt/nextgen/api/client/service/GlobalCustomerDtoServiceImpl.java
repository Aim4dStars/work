package com.bt.nextgen.api.client.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.model.TaxResidenceCountriesDto;
import com.bt.nextgen.api.client.util.AddressConverter;
import com.bt.nextgen.api.client.util.ClientDetailDtoConverter;
import com.bt.nextgen.api.client.util.EmailConverter;
import com.bt.nextgen.api.client.util.GenderMapperUtil;
import com.bt.nextgen.api.client.util.PhoneConverter;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.IndividualDetails;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.integration.domain.Individual;
import com.btfin.panorama.core.security.integration.domain.Investor;
import com.btfin.panorama.service.avaloq.domain.existingclient.Client;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.selectFirst;
import static com.bt.nextgen.api.country.service.CountryDtoServiceImpl.UCM_CODE;
import static com.bt.nextgen.api.country.service.CountryDtoServiceImpl.fieldValue;
import static com.bt.nextgen.service.group.customer.groupesb.RoleType.INDIVIDUAL;
import static com.btfin.panorama.core.conversion.CodeCategory.COUNTRY;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.join;
import static org.apache.commons.lang.StringUtils.repeat;
import static org.apache.commons.lang.StringUtils.upperCase;
import static org.apache.commons.lang.WordUtils.capitalizeFully;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Implementation of the {@code GlobalCustomerDtoService} using Group ESB SVC0258.
 */
@Service
@SuppressWarnings("squid:S1200")
public class GlobalCustomerDtoServiceImpl implements GlobalCustomerDtoService {

    private static final Logger LOGGER = getLogger(GlobalCustomerDtoServiceImpl.class);

    private static final int AREA_CODE_LENGTH = 2;

    private static final String AREA_CODE_PADDING = repeat("0", AREA_CODE_LENGTH);

    private static final int COUNTRY_CODE_LENGTH = 2;

    private static final int UCM_COUNTRY_CODE_LENGTH = 3;

    public static final String BTFG_$_IM_CODE = "btfg$im_code";

    public static final String FOREIGN = "FOREIGN";

    @Autowired
    private DirectInvestorDataDtoService directInvestorDataDtoService;

    private static final Matcher<String> NON_BLANK = new LambdaMatcher<String>() {
        @Override
        protected boolean matchesSafely(String name) {
            return isNotBlank(name);
        }
    };

    private final AddressConverter addressConverter = new AddressConverter() {
        @Override
        public AddressDto convert(Address address) {
            if(address != null) {
                final AddressDto dto = super.convert(address);
                adjustCountryFields(dto);
                if(dto.isStandardAddressFormat() && isNotBlank(address.getStreetType())){
                    dto.setGcmStreetType(getStreetTypeFromGCMCode(address.getStreetType().toUpperCase()));
                }
                dto.setGcmAddress(true);
                return dto;
            }
            return null;
        }
    };

    private final EmailConverter emailConverter = new EmailConverter() {
        @Override
        public EmailDto convert(Email email) {
            final EmailDto dto = super.convert(email);
            dto.setGcmMastered(true);
            return dto;
        }
    };

    private final PhoneConverter phoneConverter = new PhoneConverter() {
        @Override
        public PhoneDto convert(Phone phone) {
            final PhoneDto dto = super.convert(phone);
            String areaCode = dto.getAreaCode();
            if (areaCode != null) {
                final int length = areaCode.length();
                if (length < AREA_CODE_LENGTH) {
                    areaCode = AREA_CODE_PADDING.substring(0, AREA_CODE_LENGTH - length) + areaCode;
                    dto.setAreaCode(areaCode);
                }
            }
            dto.setGcmPhone(true);
            return dto;
        }
    };

    @Autowired
    @Qualifier("customerDataManagementService")
    private CustomerDataManagementIntegrationService customerDataManagementService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Override
    public ClientDto find(ClientKey key, ServiceErrors errors) {
        final ClientDto existing = searchExisting(key, errors);
        return existing == null ? searchGlobal(key, errors) : existing;
    }

    private ClientDto searchExisting(ClientKey key, ServiceErrors errors) {
        final Client client = clientIntegrationService.loadClientByCISKey(key.getClientId(), errors);
        if (isValidIndividualInvestor(client)) {
            LOGGER.info("Found existing client with CIS Key {}", key.getClientId());
            return individual(client, errors);
        }
        return null;
    }

    private boolean isValidIndividualInvestor(Client client) {
        return client != null && client.getLegalForm() == InvestorType.INDIVIDUAL
                && client.getIdentityVerificationStatus() == IdentityVerificationStatus.Completed
                && isNotEmpty(((IndividualWithAccountDataImpl) client).getPrimaryMobile())
                && isNotEmpty(((IndividualWithAccountDataImpl) client).getPrimaryEmail());
    }

    private ClientDto searchGlobal(ClientKey key, ServiceErrors errors) {
        final String cisKey = key.getClientId();
        final CustomerManagementRequest request = new CustomerManagementRequestImpl(cisKey, INDIVIDUAL, CustomerManagementOperation.values());
        final CustomerData customer = customerDataManagementService.retrieveCustomerInformation(request, singletonList("BANK_ACCOUNT"), errors);
        if (customer != null) {
            LOGGER.debug("Successfully fetched GCM customer with CIS key: {}", cisKey);
            return individual(customer, key, errors);
        } else {
            LOGGER.warn("Unable to locate GCM customer with CIS key: {}", cisKey);
            return null;
        }
    }

    private ClientDto individual(Client client, ServiceErrors errors) {
        LOGGER.debug("Loading full details of client {}", client.getClientKey().getId());
        final ClientDetail detail = clientIntegrationService.loadClientDetails(client.getClientKey(), errors);
        ClientDto clientDto = ClientDetailDtoConverter.toClientDto(detail, null, staticIntegrationService, errors);
        updateForeignRegisteredFromGCM(clientDto,client.getClientKey().getId(),((Investor) detail).getCISKey().getId(),errors);
        return clientDto;
    }

    /**
     * Update the taxdetails from GCM.
     * @param clientDto
     * @param clientId
     * @param cisKey
     * @param serviceErrors
     */
    private void updateForeignRegisteredFromGCM(ClientDto clientDto, String clientId, String cisKey, ServiceErrors serviceErrors){
        ClientUpdateKey clientUpdateKey = new ClientUpdateKey(clientId,"tax_details",cisKey,"INDIVIDUAL");
        CustomerDataDto customerDataDto = directInvestorDataDtoService.find(clientUpdateKey,serviceErrors);
        for(TaxResidenceCountriesDto taxResidenceCountriesDto : customerDataDto.getTaxResidenceCountries() ) {
            //There will be row created  with taxResidencyCountry as 'Foreign' with tin value 'Y' as registered and 'N' as not registered. This indicates the there is an overseas country for tax purpose.
            if("FOREIGN".equalsIgnoreCase(taxResidenceCountriesDto.getTaxResidenceCountry()) && (clientDto instanceof IndividualDto)) {
                if("Y".equalsIgnoreCase(taxResidenceCountriesDto.getTin())){
                    ((IndividualDto)clientDto).setIsForeignRegistered("Y");
                }
                else if("N".equalsIgnoreCase(taxResidenceCountriesDto.getTin())){
                    ((IndividualDto)clientDto).setIsForeignRegistered("N");
                }
            }
        }
    }

    private ClientDto individual(CustomerData data, ClientKey cisKey, ServiceErrors serviceErrors) {
        final IndividualDto individual = new IndividualDto();
        // NB: we do NOT invoke individual.setKey() as this entity does not (yet) exist in Avaloq.
        individual.setCisId(cisKey.getClientId());
        final AddressDto address = addressConverter.convert(data.getAddress());
        setIndividualDetails(data, individual, address);
        setIndividualTaxDetails(data, individual, serviceErrors);
        individual.setAddresses(address != null ? singletonList(address) : null);
        individual.setEmails(convert(data.getEmails(), emailConverter));
        individual.setPhones(convert(data.getPhoneNumbers(), phoneConverter));
        individual.setRegistered(false);
        return individual;
    }

    private void setIndividualTaxDetails(CustomerData data, IndividualDto individual, ServiceErrors serviceErrors) {
        if("Y".equals(individual.getIsForeignRegistered())) {
            List<TaxResidenceCountriesDto> taxResidenceCountries = CustomerDataDtoConverter.convertTaxResidenceCountryDto(data.getTaxResidenceCountries(), null, staticIntegrationService, serviceErrors);

            List<TaxResidenceCountriesDto> filteredTaxResidenceCountries = Lambda.filter(new LambdaMatcher<TaxResidenceCountriesDto>() {
                @Override
                protected boolean matchesSafely(TaxResidenceCountriesDto taxResidenceCountriesDto) {
                    return !FOREIGN.equals(taxResidenceCountriesDto.getTaxResidenceCountry());
                }
            }, taxResidenceCountries);
            individual.setTaxResidenceCountries(filteredTaxResidenceCountries);
        }
    }

    private void setIndividualDetails(CustomerData data, IndividualDto individual, AddressDto address) {
        final IndividualDetails details = data.getIndividualDetails();
        individual.setTitle(getTitle(details));
        individual.setGcmTitleLabel(gcmTitleLabel(getTitle(details)));
        individual.setFirstName(capitalizeFully(details.getFirstName()));
        if (!isEmpty(details.getMiddleNames())) {
            individual.setMiddleName(capitalizeFully(details.getMiddleNames().get(0)));
        }
        individual.setLastName(capitalizeFully(details.getLastName()));
        individual.setFullName(capitalizeFully(fullName(individual.getFirstName(), individual.getMiddleName(), individual.getLastName())));
        individual.setPreferredName(capitalizeFully(data.getPreferredName()));
        individual.setGender(capitalizeFully(GenderMapperUtil.getGenderFromGCMGenderCode(details.getGender())));
        individual.setDateOfBirth(details.getDateOfBirth());
        individual.setIdVerified(details.getIdVerified());
        individual.setInvestorType(InvestorType.INDIVIDUAL.getId());
        if(null!=address) {
            individual.setState(address.getState());
            individual.setCountry(address.getCountry());
        }
        individual.setUserName(details.getUserName());
        individual.setIsForeignRegistered(details.getIsForeignRegistered());
    }

    private String getTitle(IndividualDetails details) {
        return "Dr.".equals(details.getTitle()) ? "DR" : details.getTitle();
    }

    /**
     * The <pre>&lt;country&gt;</pre> node coming back from the GCM service might actually be a country <i>code</i>,
     * rather than a full-blown country name. If this is the case, adjust the country fields in the provided address
     * DTO to contain the proper values, looking up country codes from Avaloq static data.
     * @param address the address object which might need adjusting.
     */
    private void adjustCountryFields(AddressDto address) {
        String country = address.getCountry();
        String countryCode = address.getCountryCode();
        if (isBlank(countryCode)) {
            countryCode = country;
        }
        if (isNotBlank(countryCode) && (isBlank(country) || country.length() <= UCM_COUNTRY_CODE_LENGTH)) {
            countryCode = upperCase(countryCode);
            final ServiceErrors errors = new ServiceErrorsImpl();
            final Code code;
            switch (countryCode.length()) {
                case COUNTRY_CODE_LENGTH:
                    code = staticIntegrationService.loadCodeByUserId(COUNTRY, countryCode, errors);
                    break;
                case UCM_COUNTRY_CODE_LENGTH:
                    final Collection<Code> countries = staticIntegrationService.loadCodes(COUNTRY, errors);
                    code = selectFirst(countries, new WithUcmCode(countryCode));
                    break;
                default:
                    code = null;
                    break;
            }
            if (code != null) {
                countryCode = code.getUserId();
                country = code.getName();
            }
        }
        address.setCountryCode(countryCode);
        address.setCountry(country);
    }

    private String getTitleFromCanonicalCode(final CodeCategory category, final String value) {
        Collection<Code> codes = staticIntegrationService.loadCodes(category, new ServiceErrorsImpl());
        Code code = Lambda.selectFirst(codes, new LambdaMatcher<Code>() {
            @Override
            protected boolean matchesSafely(Code code) {
                return code.getField(BTFG_$_IM_CODE).getValue().equals(value);
            }
        });
        return code.getName();
    }

    private String getStreetTypeFromGCMCode(final String streetType) {
        return getTitleFromCanonicalCode(CodeCategory.ADDRESS_STREET_TYPE, streetType);
    }

    private String gcmTitleLabel(final String title) {
        return getTitleFromCanonicalCode(CodeCategory.PERSON_TITLE, title);
    }

    /**
     * Generate a full name by joining all the individual names in order, with a space between each name.
     *
     * @param names names to be joined together to form the full name. Blank/null items are allowed, and ignored.
     * @return the space-delimited full name.
     */
    public static String fullName(String... names) {
        return join(filter(NON_BLANK, names), ' ');
    }

    /**
     * Lambda matcher to check the CIS key of the list of existing (Avaloq) clients.
     */
    static final class WithCisKey extends LambdaMatcher<Client> {

        private final String cisKey;

        WithCisKey(String cisKey) {
            this.cisKey = cisKey;
        }

        @Override
        protected boolean matchesSafely(Client client) {
            final CISKey key = ((Individual) client).getCISKey();
            LOGGER.trace("Testing required CIS key {} against {}", cisKey, key == null ? "<null>" : key.getId());
            return key != null && cisKey.equals(key.getId());
        }
    }

    static final class WithUcmCode extends LambdaMatcher<Code> {

        private final String ucmCode;

        WithUcmCode(String ucmCode) {
            this.ucmCode = ucmCode;
        }

        @Override
        protected boolean matchesSafely(Code country) {
            return ucmCode.equalsIgnoreCase(fieldValue(country, UCM_CODE));
        }
    }
}
