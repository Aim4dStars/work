package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.client.model.*;
import com.bt.nextgen.api.client.util.ClientDetailDtoConverter;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.InvestorDetailImpl;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerDataImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.*;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.domain.Investor;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import static com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation.ADDRESS_UPDATE;
import static com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation.CONTACT_DETAILS_UPDATE;
import static com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation.PREFERRED_NAME_UPDATE;
import static com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation.TAX_RESIDENCE_COUNTRY_UPDATE;
import static java.util.Collections.singletonList;

@SuppressWarnings("squid:S1200")
@Component
public class CustomerDataDtoServiceImpl implements CustomerDataDtoService {

    private static final String PREFERRED_NAME = "PREFERRED_NAME";
    private static final String ADDRESS = "ADDRESS";
    private static final String EMAILS = "EMAILS";
    private static final String REGISTRATION_STATE = "REGISTRATION_STATE";
    private static final String BANK_ACCOUNT = "BANK_ACCOUNT";
    private static final String INDIVIDUAL_DETAILS = "INDIVIDUAL_DETAILS";
    private static final String TAX_RESIDENCE_COUNTRY = "TAX_RESIDENCE_COUNTRY";
    private static final String TIN = "TIN";
    private static final Logger logger = LoggerFactory.getLogger(CustomerDataDtoServiceImpl.class);

    @Autowired
    private CmsService cmsService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    @Qualifier("customerDataManagementService")
    private CustomerDataManagementIntegrationService customerDataManagementIntegrationService;

    @Autowired
    @Qualifier("customerManagementService")
    private CustomerManagementIntegrationService customerManagementIntegrationService;

    @Autowired
    @Qualifier("addressManagementService")
    private CustomerDataManagementIntegrationService customerAddressManagementIntegrationService;

    @Autowired
    @Qualifier("preferredNameManagementService")
    private CustomerDataManagementIntegrationService customerPreferredNameManagementIntegrationService;
    @Autowired
    @Qualifier("contactDetailsManagementService")
    private CustomerDataManagementIntegrationService customerContactDetailsManagementIntegrationService;
    @Autowired
    @Qualifier("regStateManagementService")
    private CustomerDataManagementIntegrationService customerRegStateManagementIntegrationService;
    @Autowired
    @Qualifier("taxResiCountryManagementV11Service")
    private CustomerDataManagementIntegrationService customerTaxResidenceCountryIntegrationService;
    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Override
    public CustomerDataDto find(ClientUpdateKey key, ServiceErrors serviceErrors) {
        CustomerDataDto dto = new CustomerDataDto();
        dto.setKey(key);
        CustomerManagementRequest req = new CustomerManagementRequestImpl();
        String clientId = new EncodedString(key.getClientId()).plainText();
        Client client = getClient(ClientKey.valueOf(clientId), serviceErrors);

        //tactical solution for the June release to set brand silo from person_det call
        setBrandSiloFromPersonDetCallForESBIntercep((ClientDetail) client);

        req.setCISKey(((Investor) client).getCISKey());
        ClientType clientType = getClientType(key, client);

        req.setInvolvedPartyRoleType(getRoleType(clientType));
        final CustomerData custData = setOperationType(key.getUpdateType().toUpperCase(), req, serviceErrors);
        convertToDto(dto, custData, key.getUpdateType().toUpperCase(), client, serviceErrors);
        return dto;
    }

    /**
     * This method sets brandsilo in request attribute, which will be used to set it in ESBHeaderInterceptor.
     */
    private void setBrandSiloFromPersonDetCallForESBIntercep(ClientDetail clientDetail) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        if (request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO) == null) {
            request.setAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO, clientDetail.getBrandSiloId());
        }
    }
    @Override
    public CustomerRawData retrieve(ClientUpdateKey key, String silo, String[] operationTypes, ServiceErrors serviceErrors) {
        CustomerDataDto dto = new CustomerDataDto();
        dto.setKey(key);
        CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setCISKey(CISKey.valueOf(key.getCisId()));
        request.setInvolvedPartyRoleType(RoleType.valueOf(key.getClientType()));
        CustomerRawData customerData = setOperationTypes(operationTypes, request, serviceErrors);

        return customerData;
    }

    private ClientType getClientType(ClientUpdateKey key, Client client) {
        if (key.getClientType() != null) {
            return "INDIVIDUAL".equals(key.getClientType()) ? ClientType.N : ClientType.L;
        }
        else {
            return client.getClientType();
        }
    }

    private RoleType getRoleType(ClientType clientType) {
        switch (clientType) {
            case N:
                return RoleType.INDIVIDUAL;
            case L:
                return RoleType.ORGANISATION;
            default:
                return null;
        }
    }

    private CustomerRawData setOperationTypes(String[] operationTypes, CustomerManagementRequest req, ServiceErrors serviceErrors) {
        final List<CustomerManagementOperation> operations = new ArrayList<>();
        for (String operation : operationTypes) {
            switch (operation) {
                case ADDRESS:
                    operations.add(CustomerManagementOperation.ADDRESS_UPDATE);
                    break;
                case EMAILS:
                    operations.add(CustomerManagementOperation.CONTACT_DETAILS_UPDATE);
                    break;
                case BANK_ACCOUNT:
                    operations.add(CustomerManagementOperation.ARRANGEMENTS);
                    break;
                case INDIVIDUAL_DETAILS:
                    operations.add(CustomerManagementOperation.INDIVIDUAL_DETAILS);
                    break;
                case REGISTRATION_STATE:
                    operations.add(CustomerManagementOperation.REGISTRATION_STATE);
                    break;
                case PREFERRED_NAME:
                    operations.add(CustomerManagementOperation.PREFERRED_NAME_UPDATE);
                    break;
                default:
                    break;
            }
        }
        req.setOperationTypes(operations);
        return customerManagementIntegrationService.retrieveCustomerRawInformation(req, Arrays.asList(operationTypes), serviceErrors);
    }

    private CustomerData setOperationType(String operationType, CustomerManagementRequest req, ServiceErrors serviceErrors) {
        final CustomerData custData = new CustomerDataImpl();
        final CustomerInformationRetriever retriever;
        switch (operationType) {
            case PREFERRED_NAME:
                retriever = new CustomerPreferredNameRetriever();
                break;
            case ADDRESS:
                retriever = new CustomerAddressRetriever();
                break;
            case EMAILS:
                retriever = new CustomerContactDetailsRetriever();
                break;
            case REGISTRATION_STATE:
                retriever = new CustomerRegisteredStateRetriever();
                break;
            case TAX_RESIDENCE_COUNTRY:
                retriever = new CustomerTaxResidenceCountryRetriever();
                break;
            default:
                retriever = null;
                break;
        }
        if (retriever != null) {
            retriever.retrieve(req, serviceErrors, custData);
        }
        return custData;
    }

    private void convertToDto(CustomerDataDto dto, CustomerData data, String operationType, Client client, ServiceErrors serviceErrors) {
        switch (operationType) {
            case PREFERRED_NAME:
                dto.setPreferredName(data.getPreferredName());
                break;
            case ADDRESS:
                if (data.getAddress() != null) {
                    dto.setAddress(CustomerDataDtoConverter.populateAddress(data.getAddress()));
                    dto.getAddress().setAddressType(CustomerDataDtoConverter.setDtoAddressType(client.getClientType()));
                }
                break;
            case EMAILS:
                List<EmailDto> emailDtoList = CustomerDataDtoConverter.getEmailDtos(data);
                dto.setEmails(emailDtoList);
                List<PhoneDto> phoneDtoList = CustomerDataDtoConverter.getPhoneDtos(data.getPhoneNumbers());
                dto.setPhones(phoneDtoList);
                break;
            case REGISTRATION_STATE:
                final RegisteredStateDto registeredStateDto = CustomerDataDtoConverter.getRegisteredStateDto(data);
                dto.setRegisteredStateDto(registeredStateDto);
                break;
            case BANK_ACCOUNT:
                final List<BankAccountDto> bankAccounts = CustomerDataDtoConverter.getBankAccountsDto(data);
                dto.setBankAccounts(bankAccounts);
                break;
            case INDIVIDUAL_DETAILS:
                dto.setIndividualDetails(data.getIndividualDetails());
                break;
            case TAX_RESIDENCE_COUNTRY:
                convertTaxResidenciesCountries(dto, data, client, serviceErrors);
                break;
            default:
                break;
        }
    }

    private void convertTaxResidenciesCountries(CustomerDataDto dto, CustomerData data, Client client, ServiceErrors serviceErrors) {
        if (null != client) {
            InvestorDetailImpl investorDetail = (InvestorDetailImpl) client;

            dto.setTaxResidenceCountries(CustomerDataDtoConverter.convertTaxResidenceCountryDto(data.getTaxResidenceCountries(),
                    investorDetail.getResiCountryCodeForTax(),
                    staticIntegrationService, serviceErrors));
        } else {
            dto.setTaxResidenceCountries(CustomerDataDtoConverter.convertTaxResidenceCountryDto(data.getTaxResidenceCountries(),
                    null,
                    staticIntegrationService, serviceErrors));
        }
    }

    /**
     * Update customer data in ABS and/or GCM depending on the updateType attribute
     *
     * @param customerDataDto
     * @param serviceErrors
     * @return null - if errors occurred or CustomerDataDto object if update was successful
     */
    @Override
    public CustomerDataDto update(CustomerDataDto customerDataDto, ServiceErrors serviceErrors) {
        final String updateType = customerDataDto.getKey().getUpdateType().toUpperCase();
        switch (updateType) {
            case EMAILS:
                logger.info("Sending GCM update emails/phones for customer: {}", customerDataDto.getCisKey());
                boolean gcmUpdateSuccess = invokeGCMContactDetailsUpdate(customerDataDto, serviceErrors);
                if (!gcmUpdateSuccess) {
                    return null;
                }
                break;
            case TIN: //update tax residence country in GCM
                logger.info("Sending GCM update TIN for customer: {}", customerDataDto.getCisKey());
                invokeGCMTaxResidentCountriesUpdate(customerDataDto, serviceErrors);
                break;
            default:
                break;
        }
        logger.info("Customer Record updated successfully for: {}", customerDataDto.getCisKey());
        return customerDataDto;
    }

    private CustomerManagementRequest buildGcmCustomerManagementRequest(String cisKeyString) {
        final CISKey cisKey = CISKey.valueOf(cisKeyString);
        CustomerManagementRequest gcmCustomerManagementRequest = new CustomerManagementRequestImpl();
        gcmCustomerManagementRequest.setCISKey(cisKey);
        gcmCustomerManagementRequest.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        return gcmCustomerManagementRequest;
    }

    private CustomerData buildGcmCustomerData(String cisKeyString) {
        CustomerData gcmCustomerData = new CustomerDataImpl();
        gcmCustomerData.setRequest(buildGcmCustomerManagementRequest(cisKeyString));
        return gcmCustomerData;
    }

    private boolean invokeGCMContactDetailsUpdate(CustomerDataDto customerDataDto, ServiceErrors serviceErrors) {
        CustomerData customerData = buildGcmCustomerData(customerDataDto.getCisKey());
        boolean isCustomerDataUpdated;
        customerData.getRequest().setOperationTypes(singletonList(CustomerManagementOperation.CONTACT_DETAILS_UPDATE));
        customerData.setEmails(CustomerDataDtoConverter.getEmailListForGcmUpdate(customerDataDto.getEmails()));
        customerData.setPhoneNumbers(CustomerDataDtoConverter.getPhoneListForGcmUpdate(customerDataDto.getPhones()));

        isCustomerDataUpdated = customerContactDetailsManagementIntegrationService.updateCustomerInformation(customerData, serviceErrors);

        if (!isCustomerDataUpdated) {
            logger.error("GCM update of phones/emails has failed for cisKey: {}", customerDataDto.getCisKey());
            final ServiceError error = new ServiceErrorImpl(cmsService.getContent("Err.IP-0369"));
            serviceErrors.addError(error);
        }
        convertToDto(customerDataDto, customerData, EMAILS, null, serviceErrors);
        return isCustomerDataUpdated;
    }

    private boolean invokeGCMTaxResidentCountriesUpdate(CustomerDataDto customerDataDto, ServiceErrors serviceErrors) {
        CustomerData customerData = buildGcmCustomerData(customerDataDto.getCisKey());
        boolean isCustomerDataUpdated;
        customerData.getRequest().setOperationTypes(singletonList(CustomerManagementOperation.TAX_RESIDENCE_COUNTRY_UPDATE));
        customerData.setTaxResidenceCountries(ClientDetailDtoConverter.setTaxResidenceCountriesForUpdate(customerDataDto.getTaxResidenceCountries(), staticIntegrationService, serviceErrors));
        isCustomerDataUpdated = customerTaxResidenceCountryIntegrationService.updateCustomerInformation(customerData, serviceErrors);
        if (!isCustomerDataUpdated) {
            final ServiceError error = new ServiceErrorImpl(cmsService.getContent("Err.IP-0369"));
            serviceErrors.addError(error);
        }
        convertToDto(customerDataDto, customerData, TAX_RESIDENCE_COUNTRY, null, serviceErrors);
        return isCustomerDataUpdated;
    }

    private Client getClient(ClientKey clientKey, ServiceErrors serviceErrors) {
        return clientIntegrationService.loadClientDetails(clientKey, serviceErrors);
    }

    @SuppressWarnings("squid:S00118")
    private abstract class CustomerInformationRetriever {

        private final CustomerDataManagementIntegrationService service;

        private final CustomerManagementOperation operation;

        CustomerInformationRetriever(CustomerDataManagementIntegrationService service, CustomerManagementOperation operation) {
            this.service = service;
            this.operation = operation;
        }

        void retrieve(CustomerManagementRequest request, ServiceErrors errors, CustomerData customer) {
            request.setOperationTypes(singletonList(operation));
            final CustomerData data = service.retrieveCustomerInformation(request, null, errors);
            if (data != null) {
                copy(data, customer);
            }
        }

        abstract void copy(@Nonnull CustomerData from, CustomerData to);
    }

    private class CustomerPreferredNameRetriever extends CustomerInformationRetriever {
        CustomerPreferredNameRetriever() {
            super(customerPreferredNameManagementIntegrationService, PREFERRED_NAME_UPDATE);
        }

        @Override
        void copy(@Nonnull CustomerData from, CustomerData to) {
            to.setPreferredName(from.getPreferredName());
        }
    }

    private class CustomerAddressRetriever extends CustomerInformationRetriever {
        CustomerAddressRetriever() {
            super(customerAddressManagementIntegrationService, ADDRESS_UPDATE);
        }

        @Override
        void copy(@Nonnull CustomerData from, CustomerData to) {
            to.setAddress(from.getAddress());
        }
    }

    private class CustomerTaxResidenceCountryRetriever extends CustomerInformationRetriever {
        CustomerTaxResidenceCountryRetriever() {
            super(customerTaxResidenceCountryIntegrationService, TAX_RESIDENCE_COUNTRY_UPDATE);
        }

        @Override
        void copy(@Nonnull CustomerData from, CustomerData to) {
            to.setTaxResidenceCountries(from.getTaxResidenceCountries());
        }
    }

    private class CustomerContactDetailsRetriever extends CustomerInformationRetriever {
        CustomerContactDetailsRetriever() {
            super(customerContactDetailsManagementIntegrationService, CONTACT_DETAILS_UPDATE);
        }

        @Override
        void copy(@Nonnull CustomerData from, CustomerData to) {
            to.setEmails(from.getEmails());
            to.setPhoneNumbers(from.getPhoneNumbers());
        }
    }


    private class CustomerRegisteredStateRetriever extends CustomerInformationRetriever {
        CustomerRegisteredStateRetriever() {
            super(customerRegStateManagementIntegrationService, CustomerManagementOperation.REGISTRATION_STATE);
        }

        @Override
        void copy(@Nonnull CustomerData from, CustomerData to) {
            to.setRegisteredState(from.getRegisteredState());
        }
    }
}
