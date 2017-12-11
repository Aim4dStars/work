package com.bt.nextgen.api.client.v2.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.ClientTxnDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.service.ClientUpdateCategory;
import com.bt.nextgen.api.client.util.ClientAccountUtil;
import com.bt.nextgen.api.client.v2.model.ClientDto;
import com.bt.nextgen.api.client.v2.util.ClientDetailDtoConverter;
import com.bt.nextgen.api.client.v2.util.ClientFilterUtil;
import com.bt.nextgen.api.client.v2.util.SearchFilter;
import com.bt.nextgen.api.client.validation.ClientDetailsDtoErrorMapper;
import com.bt.nextgen.api.draftaccount.builder.AddressStreetTypeMapper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.exception.NotAllowedException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.client.ClientDetailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.InvestorDetailImpl;
import com.bt.nextgen.service.avaloq.domain.RegisteredEntityImpl;
import com.bt.nextgen.service.gesb.locationmanagement.PostalAddress;
import com.bt.nextgen.service.gesb.locationmanagement.v1.LocationManagementIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.group.customer.groupesb.phone.CustomerPhone;
import com.bt.nextgen.service.group.customer.groupesb.phone.PhoneAction;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.btfin.panorama.core.security.integration.domain.Individual;
import com.btfin.panorama.core.security.integration.domain.IndividualDetail;
import com.btfin.panorama.core.security.integration.domain.Investor;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.domain.RegisteredEntity;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bt.nextgen.api.client.service.ClientUpdateCategory.ADDRESS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Service("ClientListDtoServiceV2")
@SuppressWarnings({"squid:S1200", "squid:S1160"})
public class ClientListDtoServiceImpl implements ClientListDtoService {
    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;
    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;
    @Autowired
    private ProductIntegrationService productIntegrationService;
    @Autowired
    private ClientDetailsDtoErrorMapper clientDetailsDtoErrorMapper;
    @Autowired
    private CustomerLoginManagementIntegrationService userInformationService;
    @Autowired
    @Qualifier("preferredNameManagementService")
    private CustomerDataManagementIntegrationService customerPreferredNameManagementIntegrationService;
    @Autowired
    @Qualifier("addressManagementService")
    private CustomerDataManagementIntegrationService customerAddressManagementIntegrationService;
    @Autowired
    @Qualifier("contactDetailsManagementService")
    private CustomerDataManagementIntegrationService customerContactDetailsManagementIntegrationService;
    @Autowired
    private BrokerIntegrationService brokerIntegrationService;
    @Autowired
    private CmsService cmsService;
    @Autowired
    @Qualifier("regStateManagementService")
    private CustomerDataManagementIntegrationService customerRegStateManagementIntegrationService;
    @Autowired
    private AddressStreetTypeMapper streetTypeMapper;
    @Autowired
    private LocationManagementIntegrationService addressService;


    private static final Logger logger = LoggerFactory.getLogger(ClientListDtoServiceImpl.class);
    protected static final String RESIDENTIAL = "residential";
    private static final String POSTAL = "postal";
    private static final Boolean newAddressValidation = Properties.getSafeBoolean("feature.onboardingClientMaintenanceAddressRedesign");



    @Override
    public List<ClientIdentificationDto> findAll(ServiceErrors serviceErrors) {
        logger.info("Method to retrieve all Clients");
        final ClientFilterUtil filterUtil = new ClientFilterUtil(clientIntegrationService, accountService,
                productIntegrationService, brokerIntegrationService);
        final List<ClientIdentificationDto> clientList = filterUtil.findAll(serviceErrors);
        logger.info("Clients List retrieved and cached");
        return clientList;
    }


    @Override
    public List<ClientIdentificationDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        final ClientFilterUtil filterUtil = new ClientFilterUtil(clientIntegrationService, accountService, productIntegrationService,
                brokerIntegrationService);
        final List<ClientIdentificationDto> clientList = filterUtil.search(criteriaList, serviceErrors);
        final SearchFilter searchFilter = new SearchFilter(criteriaList);
        if (searchFilter.isSearch()) {
            return Lambda.filter(searchFilter, clientList);
        }
        return clientList;
    }

    /**
     * Retrieves and sets the related accounts of the clientkey
     *      Retrieves the owner client key (parent client key) of the accounts for which input clientkey is an associated person
     *      and then gets all the accounts of the clientkeys(input clientkey and parent clientkeys if any)
     * @param clientKey
     * @param clientDto
     * @param serviceErrors
     */
    private void getAllRelatedAccount(com.bt.nextgen.service.integration.userinformation.ClientKey clientKey, ClientDto clientDto, ServiceErrors serviceErrors) {
        final Map<com.bt.nextgen.service.integration.userinformation.ClientKey, Client> clientMap = clientIntegrationService.loadClientMap(serviceErrors);
        Client client = clientMap.get(clientKey);
        if (client.getLegalForm().equals(InvestorType.INDIVIDUAL)) {
            final Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
            ClientAccountUtil clientAccountUtil = new ClientAccountUtil(clientMap, accountMap);
            Set<com.bt.nextgen.service.integration.userinformation.ClientKey> clientsRelatedAccountOwners = clientAccountUtil.getAssociatedPersonsRelatedAccountsOwner(clientKey);
            clientsRelatedAccountOwners.add(clientKey); //including the client
            final Collection<WrapAccount> wrapAccounts = clientAccountUtil.getAllActiveLinkedAccounts(clientsRelatedAccountOwners);
            ClientDetailDtoConverter.setClientDtoAccounts(clientDto, wrapAccounts);
        }
    }

    @Override
    public ClientDto find(ClientKey key, ServiceErrors serviceErrors) {
        final String clientId = new EncodedString(key.getClientId()).plainText();
        com.bt.nextgen.service.integration.userinformation.ClientKey clientKey = com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf(clientId);
        final ClientDetail clientModel = clientIntegrationService.loadClientDetails(clientKey, serviceErrors);
        // we don't care about personal tfns
        final ClientDto clientDto = ClientDetailDtoConverter.toClientDto(clientModel, null);
        getAllRelatedAccount(clientKey, clientDto, serviceErrors);
        if (clientModel instanceof IndividualDetail) {
            final String gcmId = ((Investor) clientModel).getGcmId();
            clientDto.setUserName(userInformationService.getCustomerUserName(gcmId, new ServiceErrorsImpl()));
        }
        return clientDto;
    }


    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1141", "squid:S1696",
            "checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.ExecutableStatementCountCheck"})
    @Override
    public ClientIdentificationDto update(ClientIdentificationDto trxDto, ServiceErrors serviceErrors) {
        if (!(trxDto instanceof ClientTxnDto)) {
            throw new NotAllowedException("Incompatible Parameter Type ");
        }
        final ClientTxnDto clientDto = (ClientTxnDto) trxDto;
        ClientUpdateCategory updateInfo;
        InvestorDetail investorDetail;
        ClientTxnDto clientResponseDto;

        try {
            updateInfo = ClientUpdateCategory.getConstant(clientDto.getUpdatedAttribute());
            final InvestorType investorType = InvestorType.valueOf(clientDto.getInvestorTypeUpdated());
            investorDetail = new IndividualDetailImpl();
            clientResponseDto = new ClientTxnDto();

            if (!InvestorType.INDIVIDUAL.equals(investorType)) {
                investorDetail = new RegisteredEntityImpl();
            }

            if(newAddressValidation && updateInfo.equals(ADDRESS)) {
                resolveNewQasAddress(clientDto.getAddresses(), serviceErrors);
            }

            if (isGcmData(clientDto, investorType)) {
                final boolean status = updateCustomerData(clientDto, updateInfo, serviceErrors);
                if (!status) {
                    final ServiceError error = new ServiceErrorImpl(cmsService.getContent("Err.IP-0369"));
                    serviceErrors.addError(error);
                }
            }
            else if (ClientUpdateCategory.EMAILS.equals(updateInfo)) {
                logger.info("Sending Avaloq/GCM update for email and phone of client {}", clientDto.getKey().getClientId());

                final List<PhoneDto> phoneListFromUI = clientDto.getPhones();
                ClientDetailDtoConverter.createPhoneListForAvaloqUpdate(clientDto);

                final List<EmailDto> emailListFromUI = clientDto.getEmails();
                ClientDetailDtoConverter.filterEmails(clientDto);

                convertToInvestorDetail(trxDto, clientDto, investorDetail);

                final ClientTxnDto response = updateInvestorDetailsAtABS(serviceErrors, clientDto, ClientUpdateCategory.CONTACT, investorDetail);
                if (response.getEmails().isEmpty() && response.getPhones().isEmpty()) {
                    logger.warn("Avaloq update for phone/email has failed for client {}", clientDto.getKey().getClientId());
                    final ServiceError error = new ServiceErrorImpl();
                    error.setReason("Error updating Phone/email in avaloq for client");
                    serviceErrors.addError(error);
                    return response;
                }

                ClientDetailDtoConverter.filterData(clientDto, phoneListFromUI, emailListFromUI);
                final boolean status = updateCustomerData(clientDto, updateInfo, serviceErrors);
                handleServiceErrors(clientDto, serviceErrors);
                logger.info("GCM update status of email/phone for user {} is {}", clientDto.getKey().getClientId(), status);
            }
            else {
                logger.info("GCM not enabled : Sending avaloq update");
                convertToInvestorDetail(trxDto, clientDto, investorDetail);
                clientResponseDto = updateInvestorDetailsAtABS(serviceErrors, clientDto, updateInfo, investorDetail);
            }
            return clientResponseDto;
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            final ServiceError error = new ServiceErrorImpl();
            error.setException(e);
            error.setReason("Error getting the metadata from the report");
            serviceErrors.addError(error);
        }
        return null;
    }

    private void handleServiceErrors(ClientTxnDto clientResponseDto, ServiceErrors serviceErrors) {
        final List<ValidationError> validation = new ArrayList<>();
        for (ServiceError err : serviceErrors.getErrorList()) {
            validation.add(new ValidationError(null, err.getErrorCode(), ValidationError.ErrorType.ERROR));

        }
        clientResponseDto.setWarnings(clientDetailsDtoErrorMapper.map(validation));

    }

    private ClientTxnDto updateInvestorDetailsAtABS(ServiceErrors serviceErrors, ClientTxnDto clientDto,
                                                    ClientUpdateCategory updateInfo, InvestorDetail investorDetail) {
        ClientTxnDto clientResponseDto;
        final InvestorDetail detail = (InvestorDetail) clientIntegrationService.update(investorDetail, updateInfo, serviceErrors);
        clientResponseDto = toUpdateClientDto(updateInfo, detail);
        clientResponseDto.setKey(clientDto.getKey());
        return clientResponseDto;
    }

    private void convertToInvestorDetail(ClientIdentificationDto trxDto, ClientTxnDto clientDto, InvestorDetail investorDetail)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ((ClientDetailImpl) investorDetail).setClientKey(com.bt.nextgen.service.integration.userinformation.
                ClientKey.valueOf(new EncodedString(trxDto.getKey()
                .getClientId()).plainText()));

        final Map<String, Object> map = PropertyUtils.describe(clientDto);
        final Set<String> set = map.keySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            final String propertyName = (String) iterator.next();
            final Object property = PropertyUtils.getProperty(clientDto, propertyName);
            toDomainModel(property, propertyName, investorDetail);
        }
    }

    private boolean isGcmData(ClientTxnDto clientDto, InvestorType investorType) {
        switch (ClientUpdateCategory.getConstant(clientDto.getUpdatedAttribute())) {
            case PREFERRED_NAME:
            case REGISTRATION_STATE:
                return true;
            case ADDRESS:
                for (Iterator iterator = clientDto.getAddresses().iterator(); iterator.hasNext(); ) {
                    final AddressDto dto = (AddressDto) iterator.next();
                    if (dto.isGcmAddress()) {
                        return retrieveAddrFromGcm(investorType, dto.getAddressType());
                    }
                }
            default:
                return false;
        }
    }

    private boolean retrieveAddrFromGcm(InvestorType investorType, String addrType) {
        switch (investorType) {
            case INDIVIDUAL:
            case TRUST:
            case SMSF:
                if (addrType.equalsIgnoreCase(RESIDENTIAL)) {
                    return true;
                }
            case COMPANY:
                if (addrType.equalsIgnoreCase(POSTAL)) {
                    return true;
                }
            default:
                return false;
        }
    }

    private boolean updateCustomerData(ClientTxnDto clientDto, ClientUpdateCategory updateInfo, ServiceErrors serviceErrors) {
        final CustomerData customerData = new CustomerDataImpl();
        final CustomerManagementRequest request = new CustomerManagementRequestImpl();
        final List<CustomerManagementOperation> operations = new ArrayList<CustomerManagementOperation>();
        Boolean status = false;

        if (clientDto.getCisKey() == null || clientDto.getInvestorTypeUpdated() == null) {
            final Client client = clientIntegrationService.loadClient(com.bt.nextgen.service.integration.userinformation.
                            ClientKey.valueOf(new EncodedString(clientDto.getKey().getClientId()).plainText()),
                    serviceErrors);
            if (client.getClientType() == null) {
                return false;
            }
            else {
                createCustomerManagermentReqFromClient(client, request);
            }
        }
        //Both Ciskey and Investor Type cannot be null. So no null check required
        else {
            if ("INDIVIDUAL".equals(clientDto.getInvestorTypeUpdated())) {
                request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
            }
            else {
                request.setInvolvedPartyRoleType(RoleType.ORGANISATION);
            }
            request.setCISKey(CISKey.valueOf(clientDto.getCisKey()));
        }

        // tactical solution to call person_det to fetch the brandsilo for the investor to be sent in the ESB header
        setBrandSiloFromPersonDetCallForESBIntercep(clientDto, serviceErrors);

        switch (updateInfo) {
            case PREFERRED_NAME:
                customerData.setPreferredName(clientDto.getPreferredName());
                setOperation(request, operations, CustomerManagementOperation.PREFERRED_NAME_UPDATE);
                customerData.setRequest(request);
                status = customerPreferredNameManagementIntegrationService.updateCustomerInformation(customerData, serviceErrors);
                break;
            case ADDRESS:
                setOperation(request, operations, CustomerManagementOperation.ADDRESS_UPDATE);
                customerData.setAddress(ClientDetailDtoConverter.setAddressForUpdate(clientDto.getAddresses(), streetTypeMapper));
                customerData.setRequest(request);
                status = customerAddressManagementIntegrationService.updateCustomerInformation(customerData, serviceErrors);
                break;
            case EMAILS:
                setOperation(request, operations, CustomerManagementOperation.CONTACT_DETAILS_UPDATE);
                createCustomerData(request, clientDto, customerData);
                status = customerContactDetailsManagementIntegrationService.updateCustomerInformation(customerData, serviceErrors);
                break;
            case REGISTRATION_STATE:
                setOperation(request, operations, CustomerManagementOperation.REGISTRATION_STATE);
                customerData.setRegisteredState(ClientDetailDtoConverter.convertToRegistrationStateModel(clientDto.getRegisteredStateDto()));
                customerData.setRequest(request);
                status = customerRegStateManagementIntegrationService.updateCustomerInformation(customerData, serviceErrors);
                break;
            default:
                break;
        }
        return status;
    }

    /**
     * This method sets brandsilo in request attribute, which will be used to set it in ESBHeaderInterceptor.
     */
    private void setBrandSiloFromPersonDetCallForESBIntercep(ClientTxnDto clientDto, ServiceErrors serviceErrors) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        final String clientId = new EncodedString(clientDto.getKey().getClientId()).plainText();
        final com.bt.nextgen.service.integration.userinformation.ClientKey clientKey = com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf(clientId);
        final ClientDetail investorDetail = clientIntegrationService.loadClientDetails(clientKey, serviceErrors);

        if (request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO) == null) {
            request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, investorDetail.getBrandSiloId());
        }
    }


    private void resolveNewQasAddress(List<AddressDto> addresses, ServiceErrors serviceErrors) {

        for (AddressDto addressDto : addresses) {
            if (!addressDto.isInternationalAddress() && isNotBlank(addressDto.getAddressIdentifier())) {
                PostalAddress addressResponse = addressService.retrievePostalAddress(addressDto.getAddressIdentifier(), serviceErrors);
                if(null!=addressResponse) {
                    addressDto.setSuburb(addressResponse.getSuburb());
                    addressDto.setState(addressResponse.getState());
                    addressDto.setCountry(addressResponse.getCountry());
                    addressDto.setBuilding(addressResponse.getBuilding());
                    addressDto.setStreetName(addressResponse.getStreetName());
                    addressDto.setStreetNumber(addressResponse.getStreetNumber());
                    addressDto.setStreetType(addressResponse.getStreetType());
                    addressDto.setPostcode(addressResponse.getPostCode());
                    addressDto.setFloor(addressResponse.getFloor());
                    addressDto.setUnitNumber(addressResponse.getUnit());
                } else {
                    serviceErrors.addError(new ServiceErrorImpl("Error in resolving the new QAS Address from the Service"));
                }
            }
        }
    }

    private void createCustomerData(CustomerManagementRequest request, ClientTxnDto clientDto, CustomerData customerData) {
        customerData.setRequest(request);
        customerData.setEmails(ClientDetailDtoConverter.setEmailListForUpdate(clientDto.getEmails()));
        customerData.setPhoneNumbers(convertPhoneDtoToPhone(clientDto.getPhones()));
    }

    private void setOperation(CustomerManagementRequest request, List<CustomerManagementOperation> operations,
                              CustomerManagementOperation operation) {
        operations.add(operation);
        request.setOperationTypes(operations);
    }

    @SuppressWarnings("unchecked")
    public InvestorDetail toDomainModel(Object propertyValue, String propertyName, InvestorDetail investorDetail)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (propertyName != null && propertyValue != null) {
            if (propertyValue instanceof Collection) {
                final ClientUpdateCategory updateInfo = ClientUpdateCategory.getConstant(propertyName);
                List domainList = null;
                if (updateInfo != null) {
                    final Collection collection = (Collection) propertyValue;
                    switch (updateInfo) {
                        case ADDRESSES:
                            domainList = ClientDetailDtoConverter.setAddressListForUpdate(collection);
                            break;
                        case PHONES:
                            domainList = ClientDetailDtoConverter.setPhoneListForUpdate(collection);
                            break;
                        case EMAILS:
                            domainList = ClientDetailDtoConverter.setEmailListForUpdate(collection);
                            break;
                        default:
                            break;
                    }
                }
                BeanUtils.setProperty(investorDetail, propertyName, domainList);
            }
            else {
                BeanUtils.setProperty(investorDetail, propertyName, propertyValue);
            }
        }
        return investorDetail;
    }

    private ClientTxnDto toUpdateClientDto(ClientUpdateCategory updateInfo, InvestorDetail investorDetailImpl) {
        final ClientTxnDto clientResponseDto = new ClientTxnDto();
        clientResponseDto.setModificationSeq(investorDetailImpl.getModificationSeq());
        switch (updateInfo) {
            case PREFERRED_NAME:
                clientResponseDto.setPreferredName(((IndividualDetail) investorDetailImpl).getPreferredName());
                break;

            case COMPANY_NAME:
                clientResponseDto.setFullName(investorDetailImpl.getFullName());
                break;

            case GST:
                clientResponseDto.setRegistrationForGst(((RegisteredEntity) investorDetailImpl).isRegistrationForGst());
                break;

            case REGISTRATION_STATE:
                clientResponseDto.setRegistrationStateCode(((RegisteredEntityImpl) investorDetailImpl).getRegistrationStateCode());
                clientResponseDto.setRegistrationState(((RegisteredEntityImpl) investorDetailImpl).getRegistrationState());
                break;

            case TFN:
                setTfnStatus(clientResponseDto, investorDetailImpl);
                break;

            case CONTACT:
            case ADDRESS:
                setAddresses(clientResponseDto, investorDetailImpl);
                break;
            default:
                break;

        }
        clientResponseDto.setWarnings(clientDetailsDtoErrorMapper.map(((InvestorDetailImpl) investorDetailImpl).getWarnings()));
        return clientResponseDto;
    }

    private void setTfnStatus(ClientTxnDto clientResponseDto, InvestorDetail investorDetailImpl) {
        if (investorDetailImpl.getTfn() != null) {
            clientResponseDto.setTfnProvided(true);
        }
        clientResponseDto.setTfnExemptId(investorDetailImpl.getTfnExemptId());
        clientResponseDto.setSaTfnExemptId(investorDetailImpl.getSaTfnExemptId());
        if (investorDetailImpl.getExemptionReason() != null) {
            clientResponseDto.setExemptionReason(investorDetailImpl.getExemptionReason().getValue());
        }
    }

    private void setAddresses(ClientTxnDto clientResponseDto, InvestorDetail investorDetailImpl) {
        final List<AddressDto> addressDtoList = new ArrayList<AddressDto>();
        final List<PhoneDto> phoneDtoList = new ArrayList<PhoneDto>();
        final List<EmailDto> emailDtoList = new ArrayList<EmailDto>();
        ClientDetailDtoConverter.setAddressDtoList(investorDetailImpl,
                addressDtoList,
                phoneDtoList,
                emailDtoList);
        clientResponseDto.setAddresses(addressDtoList);
        clientResponseDto.setEmails(emailDtoList);
        clientResponseDto.setPhones(phoneDtoList);
    }

    @Override
    public List<ClientIdentificationDto> getFilteredValue(String queryString,
                                                          List<ApiSearchCriteria> filterCriteria,
                                                          ServiceErrors serviceErrors) {
        final ClientFilterUtil filterUtil = new ClientFilterUtil(clientIntegrationService, accountService,
                productIntegrationService, brokerIntegrationService);
        return filterUtil.getFilteredValue(filterCriteria, serviceErrors);
    }

    /**
     * This method removes the duplicate emails coming from ABS for Your Details page
     *
     * @param emails TODO REMOVE this once the new de-dupe is done
     */
    public void removeDuplicateEmails(List<EmailDto> emails) {

        final List<EmailDto> emailsTemp = new ArrayList<EmailDto>();
        final List<EmailDto> emailsSecondaryList = new ArrayList<EmailDto>();
        EmailDto emailDtoTemp = null;
        EmailDto finalEmailDto = null;
        int traverseCounter = 0;
        final Set<String> fileteredEmails = new HashSet<String>();

        Collections.sort(emails, new Comparator<EmailDto>() {
            @Override
            public int compare(EmailDto email1, EmailDto email2) {
                return email1.getEmail().compareTo(email2.getEmail());
            }
        });

        filterPrimaryValuesEmails(emails, emailsSecondaryList);

        for (EmailDto emailDto : emailsSecondaryList) {

            if (traverseCounter < (emailsSecondaryList.size() - 1)) {

                emailDtoTemp = emailsSecondaryList.get(traverseCounter + 1);

                if (emailDto.getEmail().equalsIgnoreCase(emailDtoTemp.getEmail())) {

                    finalEmailDto = processEmailDto(emailDto, emailDtoTemp);
                    fileteredEmails.add(finalEmailDto.getEmail());


                }
                else {
                    if (!fileteredEmails.contains(emailDto.getEmail())) {
                        fileteredEmails.add(emailDto.getEmail());
                        emailsTemp.add(emailDto);
                    }

                    if (finalEmailDto != null) {
                        emailsTemp.add(finalEmailDto);
                    }
                    finalEmailDto = null;

                }
            }
            else {

                if (!fileteredEmails.contains(emailDto.getEmail())) {
                    fileteredEmails.add(emailDto.getEmail());
                    emailsTemp.add(emailDto);
                }
            }
            traverseCounter++;
        }

        if (finalEmailDto != null) {
            emailsTemp.add(finalEmailDto);
        }

        addPrimaryValuesEmails(emailsTemp, emails);

        emails.clear();
        emails.addAll(emailsTemp);

    }

    private EmailDto processEmailDto(EmailDto emailDto, EmailDto emailDtoTemp) {

        if ("Primary".equalsIgnoreCase(emailDto.getEmailType())) {

            if (emailDtoTemp.isPreferred()) {
                emailDto.setPreferred(emailDtoTemp.isPreferred());
            }

            return emailDto;

        }
        else if ("Primary".equalsIgnoreCase(emailDtoTemp.getEmailType())) {

            if (emailDto.isPreferred()) {
                emailDtoTemp.setPreferred(emailDto.isPreferred());
            }
            return emailDtoTemp;
        }
        else {

            if (emailDtoTemp.isPreferred()) {
                emailDto.setPreferred(emailDtoTemp.isPreferred());
            }

            return emailDto;
        }

    }

    /**
     * This method removes the duplicate phones coming from ABS for Your Details page
     * TODO Remove this when the new de-dupe is done
     *
     * @param phones
     */
    public void removeDuplicatePhones(List<PhoneDto> phones) {

        final List<PhoneDto> phonesTemp = new ArrayList<PhoneDto>();
        final List<PhoneDto> phonesSecondaryList = new ArrayList<PhoneDto>();

        final Map<String, List<PhoneDto>> mapPhones = new HashMap<String, List<PhoneDto>>();


        Collections.sort(phones, new Comparator<PhoneDto>() {
            @Override
            public int compare(PhoneDto phone1, PhoneDto phone2) {
                return phone1.getNumber().compareTo(phone2.getNumber());
            }
        });

        Collections.sort(phones, new Comparator<PhoneDto>() {
            @Override
            public int compare(PhoneDto phone1, PhoneDto phone2) {
                return phone1.getPhoneType().compareTo(phone2.getPhoneType());
            }
        });

        filterPrimaryValues(phones, phonesSecondaryList);

        for (PhoneDto phoneDto : phonesSecondaryList) {

            if (phoneDto != null && phoneDto.getPhoneType() != null) {

                if (mapPhones.get(phoneDto.getPhoneType()) == null) {
                    List<PhoneDto> listPhones = new ArrayList<PhoneDto>();
                    listPhones.add(phoneDto);
                    mapPhones.put(phoneDto.getPhoneType(), listPhones);
                }
                else {
                    mapPhones.get(phoneDto.getPhoneType()).add(phoneDto);
                }
            }

        }

        final Iterator<List<PhoneDto>> itrPhoneDto = mapPhones.values().iterator();
        while (itrPhoneDto.hasNext()) {

            final List<PhoneDto> listPhones = itrPhoneDto.next();

            processPhoneList(listPhones, phonesTemp);


        }


        addPrimaryValues(phonesTemp, phones);
        phones.clear();
        phones.addAll(phonesTemp);

    }

    private void processPhoneList(List<PhoneDto> listPhones, List<PhoneDto> phonesTemp) {

        PhoneDto finalPhoneDtoTemp = null;
        int traverseCounter = 0;
        PhoneDto phoneDtoTemp;

        for (PhoneDto phoneDto : listPhones) {

            if (traverseCounter < (listPhones.size() - 1)) {

                phoneDtoTemp = listPhones.get(traverseCounter + 1);

                if (phoneDto.getNumber().equalsIgnoreCase(phoneDtoTemp.getNumber())) {

                    finalPhoneDtoTemp = processPhoneDto(phoneDto, phoneDtoTemp);

                }
                else {

                    if (finalPhoneDtoTemp == null) {

                        phonesTemp.add(phoneDto);

                    }
                    else {

                        phonesTemp.add(finalPhoneDtoTemp);
                        finalPhoneDtoTemp = null;
                    }

                }
            }
            else {

                if (finalPhoneDtoTemp != null && finalPhoneDtoTemp.getNumber().equalsIgnoreCase(phoneDto.getNumber())) {

                    phonesTemp.add(finalPhoneDtoTemp);
                }
                else {
                    phonesTemp.add(phoneDto);
                }

            }
            traverseCounter++;


        }

    }


    private PhoneDto processPhoneDto(PhoneDto phoneDto, PhoneDto phoneDtoTemp) {


        if (phoneDto.isPreferred()) {

            phoneDtoTemp.setPreferred(phoneDtoTemp.isPreferred());
        }

        return phoneDtoTemp;


    }

    private void filterPrimaryValues(List<PhoneDto> phones, List<PhoneDto> secondaryValues) {

        for (PhoneDto phonesDto : phones) {

            if (!"Primary".equalsIgnoreCase(phonesDto.getPhoneType())) {
                secondaryValues.add(phonesDto);
            }
        }
    }


    private void filterPrimaryValuesEmails(List<EmailDto> emails, List<EmailDto> secondaryValues) {

        for (EmailDto emailsDto : emails) {

            if (!"Primary".equalsIgnoreCase(emailsDto.getEmailType())) {
                secondaryValues.add(emailsDto);
            }
        }
    }


    private void addPrimaryValues(List<PhoneDto> phonesTemp, List<PhoneDto> phones) {

        for (PhoneDto phone : phones) {

            if ("Primary".equalsIgnoreCase(phone.getPhoneType())) {

                phonesTemp.add(phone);

            }
        }


    }

    private void addPrimaryValuesEmails(List<EmailDto> emailsTemp, List<EmailDto> mails) {

        for (EmailDto email : mails) {

            if ("Primary".equalsIgnoreCase(email.getEmailType())) {


                emailsTemp.add(email);


            }
        }

    }


    private CustomerManagementRequest createCustomerManagermentReqFromClient(Client client, CustomerManagementRequest request) {

        switch (client.getClientType()) {
            case N:
                request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
                break;
            case L:
                request.setInvolvedPartyRoleType(RoleType.ORGANISATION);
                break;
            default:
                logger.error("Client Type is neither Individual or Organisation - {}", client.getClientType());
        }
        request.setCISKey(((Individual) client).getCISKey());
        return request;
    }

    private List<Phone> convertPhoneDtoToPhone(List<PhoneDto> phoneList) {
        final List<Phone> phones = new ArrayList<>();

        //Transform the full telephone number to country code, area code and number to be sent to gcm
        ClientDetailDtoConverter.parseFullNumberListToGCMNumberFormat(phoneList);
        for (PhoneDto phone : phoneList) {
            if (phone.isGcmPhone() && StringUtils.isNotBlank(phone.getRequestedAction())) {
                final CustomerPhone customerPhone = new CustomerPhone();
                customerPhone.setAreaCode(phone.getAreaCode());
                customerPhone.setCountryCode(phone.getCountryCode());
                customerPhone.setNumber(phone.getNumber());
                customerPhone.setModificationSeq(phone.getModificationSeq());
                customerPhone.setType(AddressMedium.getAddressMediumByAddressType(phone.getPhoneType()));
                customerPhone.setGcm(phone.isGcmPhone());
                customerPhone.setAction(PhoneAction.fromString(phone.getRequestedAction()));
                phones.add(customerPhone);
            }
        }

        return phones;
    }
}
