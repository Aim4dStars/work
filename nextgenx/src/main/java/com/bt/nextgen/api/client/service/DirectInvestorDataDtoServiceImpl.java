package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.avaloq.matchtfn.MatchTFNIntegrationService;
import com.bt.nextgen.service.gesb.arrangementreporting.v2.RetrieveTFNService;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementOperation;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequestImpl;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.btfin.panorama.service.avaloq.domain.existingclient.Client;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Service to retrieve direct investor details from GCM
 */
@Component
public class DirectInvestorDataDtoServiceImpl implements DirectInvestorDataDtoService {

    @Autowired
    @Qualifier("customerDataManagementService")
    private CustomerDataManagementIntegrationService customerDataManagementIntegrationService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private RetrieveTFNService retrieveTFNService;

    @Autowired
    private MatchTFNIntegrationService matchTFNIntegrationService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    private static final String ADDRESS = "ADDRESS";
    private static final String EMAIL = "EMAIL";
    private static final String BANK_ACCOUNT = "BANK_ACCOUNT";
    private static final String INDIVIDUAL_DETAILS = "INDIVIDUAL_DETAILS";
    private static final String TAX_DETAILS = "TAX_DETAILS";
    private static final String TFN = "TFN";

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectInvestorDataDtoServiceImpl.class);

    private boolean isSvc0610FeatureEnabled() {
        return featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("svc0610v2.enabled");
    }

    @Override
    public CustomerDataDto find(ClientUpdateKey key, ServiceErrors serviceErrors) {
        final CustomerDataDto dto = new CustomerDataDto();
        dto.setKey(key);
        LOGGER.debug("Starting retrieve of direct investor details from GCM and ABS.");
        final CustomerManagementRequest req = createCustomerManagementRequest(key);
        final String[] types = key.getUpdateType().toUpperCase().split(",");
        final CustomerData custData = setOperationTypes(types, req, serviceErrors);
        convertToDto(dto, custData, types, serviceErrors);

        boolean tfnExists = Arrays.asList(types).contains(TFN);
        String superCheckTfn = null;
        String westpacTfn = null;

        if (tfnExists) {

            // Will have a new service to retrieve the super check TFN until then mocking the value
            for (String type : types) {
                superCheckTfn = type.contains("DUMMY_TFN") ? type.substring(type.indexOf("=") + 1, type.length()) : null;
            }
            ;

            // Retrieve from Westpac through 610 inCase not present in SuperCheck
            if (isBlank(superCheckTfn) && isSvc0610FeatureEnabled()) {
                LOGGER.debug("Start retrieving TFN for direct investor from GESB (svc610)");
                try {
                    westpacTfn = retrieveTFNService.getTFN(key.getCisId(), serviceErrors);
                }  catch(Exception e){
                    westpacTfn = null;
                    LOGGER.error("Exception in retrieving a TFN from svc0610 ",e);
                }
                LOGGER.debug("Finished retrieving TFN for direct investor from GESB (svc610)");
            }

            //Get the details from Existing Panorama if it exists
            if(StringUtils.isNotBlank(key.getClientId())){
                retrieveExistingPanoramaDetails(key.getCisId(),dto, superCheckTfn, serviceErrors);
            }

            // Set the TFN values
            if (isNotBlank(superCheckTfn)) {
                dto.setHasSuperCheck(true);
                dto.setTfn(superCheckTfn);
            } else {
                dto.setHasSuperCheck(false);
                dto.setTfn(westpacTfn);
            }
        }


        LOGGER.debug("Finishing retrieve of direct investor details from GCM and ABS.");
        return dto;
    }

    /**
     * Used to retrieve the Panorama details from existing customer, hasDirectSuperAccounts, hasTfN and hasTfnMatched
     */
    private void retrieveExistingPanoramaDetails(String cisid, CustomerDataDto dto, String superCheckTfn, ServiceErrors serviceErrors) {

        LOGGER.debug(">> Start retrieving Panorama Details for direct investor from Avaloq for CIS: {}", cisid);

         final Client client = clientIntegrationService.loadClientByCISKey(cisid, serviceErrors);

            // Check if client exists in ABS
            if (client != null) {
                IndividualWithAccountDataImpl existingIndividual = (IndividualWithAccountDataImpl) client;
                PanoramaCustomerDto panoramaDetails = new PanoramaCustomerDto();
                panoramaDetails.setHasDirectSuperAccount(existingIndividual.hasDirectSuperAccount());
                panoramaDetails.setHasDirectPensionAccount(existingIndividual.hasDirectPensionAccount());
                panoramaDetails.setHasTfn(existingIndividual.getHasTfn());
                String person_id = client.getClientKey().getId();
                LOGGER.info("Found Existing Account in ABS with CIS Key: {}, Person Id:{}, panoramaDetails: {}", cisid, person_id, panoramaDetails);

                // If TFN co-exists in ABS then match with SuperCheck TFN
                if (isNotBlank(person_id) && existingIndividual.getHasTfn() && isNotBlank(superCheckTfn)) {
                    boolean tfnMatchedResult = matchTFNIntegrationService.doMatchTFN(person_id, superCheckTfn, serviceErrors);
                    panoramaDetails.setHasTfnMatched(tfnMatchedResult);
                    LOGGER.info("Check if TFN:{} exists for the ABS Person Id:{} and Matched result:{}", superCheckTfn, person_id, tfnMatchedResult);
                }

                if(!existingIndividual.getHasTfn() && existingIndividual.hasAnyPensionAccount()) {
                    panoramaDetails.setHasPensionExemptionReason(true);
                }

                //Save the panorama details
                dto.setPanoramaDetails(panoramaDetails);

        }
        LOGGER.debug(">> Finished retrieving Panorama Details for direct investor from Avaloq");
    }

    /**
     * CIS Key for a direct investor is taken from SAML token
     *
     * @param key
     * @return
     */
    private CustomerManagementRequest createCustomerManagementRequest(ClientUpdateKey key) {
        final CustomerManagementRequest request = new CustomerManagementRequestImpl();
        request.setCISKey(CISKey.valueOf(key.getCisId()));
        request.setInvolvedPartyRoleType(RoleType.INDIVIDUAL);
        return request;
    }

    /**
     * This supports retrieving multiple operation types in the one request
     *
     * @param operationTypes
     * @param req
     * @param serviceErrors
     * @return
     */
    private CustomerData setOperationTypes(String[] operationTypes, CustomerManagementRequest req, ServiceErrors serviceErrors) {
        final List<CustomerManagementOperation> operations = new ArrayList<>();
        for (String operation : operationTypes) {
            switch (operation) {
                case ADDRESS:
                    operations.add(CustomerManagementOperation.ADDRESS_UPDATE);
                    break;
                case EMAIL:
                    operations.add(CustomerManagementOperation.CONTACT_DETAILS_UPDATE);
                    break;
                case BANK_ACCOUNT:
                    operations.add(CustomerManagementOperation.ARRANGEMENTS);
                    break;
                case INDIVIDUAL_DETAILS:
                    operations.add(CustomerManagementOperation.INDIVIDUAL_DETAILS);
                    break;
                case TAX_DETAILS:
                    operations.add(CustomerManagementOperation.TAX_RESIDENCE_COUNTRY_UPDATE);
                    break;
                case TFN:
                    break;//do nothing as TFN is used to call svc610 only
                default:
                    LOGGER.error("Operation not found for: {}", operation);
                    break;
            }
        }
        req.setOperationTypes(operations);
        return customerDataManagementIntegrationService.retrieveCustomerInformation(req, Arrays.asList(operationTypes), serviceErrors);
    }

    private void convertToDto(CustomerDataDto dto, CustomerData data, String[] operationTypes, ServiceErrors serviceErrors) {
        for (String type : operationTypes) {
            switch (type) {
                case ADDRESS:
                    if (data.getAddress() != null) {
                        dto.setAddress(CustomerDataDtoConverter.populateAddress(data.getAddress()));
                        dto.getAddress().setAddressType(CustomerDataDtoConverter.setDtoAddressType(ClientType.N));
                    }
                    break;
                case EMAIL:
                    final List<EmailDto> emailDtoList = CustomerDataDtoConverter.getEmailDtos(data);
                    dto.setEmails(emailDtoList);
                    final List<PhoneDto> phoneDtoList = CustomerDataDtoConverter.getMaskedAustralianMobileNumbers(data.getPhoneNumbers());
                    dto.setPhones(phoneDtoList);
                    break;
                case BANK_ACCOUNT:
                    final List<BankAccountDto> bankAccounts = CustomerDataDtoConverter.getBankAccountsDto(data);
                    dto.setBankAccounts(bankAccounts);
                    break;
                case INDIVIDUAL_DETAILS:
                    dto.setIndividualDetails(data.getIndividualDetails());
                    break;
                case TAX_DETAILS:
                    dto.setTaxResidenceCountries(CustomerDataDtoConverter.convertTaxResidenceCountryDto(data.getTaxResidenceCountries(), null, staticIntegrationService, serviceErrors));
                    break;
                case TFN:
                    break;//do nothing as TFN is used to call svc610 only
                default:
                    LOGGER.error("Operation not found for: {}", type);
                    break;
            }
        }
    }

}
