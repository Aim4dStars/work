package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.model.*;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.util.ReferenceNumberFormatter;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


@Service
@Transactional(value = "springJpaTransactionManager")
public class ClientApplicationDtoConverterService {

    public static final int BIG_N = 1048576;
    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;


    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplicationDtoConverterService.class);

    /**
     * Convert a repository Bean to an onboarding application bean
     *
     * @param account
     * @param serviceErrors
     * @return ClientApplicationDto - onboarding application dto
     */
    public ClientApplicationDto convertToDto(ClientApplication account, ServiceErrors serviceErrors) {
        ClientApplicationDto clientApplicationDto = getMinimalClientApplicationDto(account);
        clientApplicationDto.setLastModified(account.getLastModifiedAt());
        clientApplicationDto.setReferenceNumber(ReferenceNumberFormatter.formatReferenceNumber(account.getId()));

        String lastModifiedId = account.getLastModifiedId();
        if (!StringUtils.isEmpty(lastModifiedId)) {
            clientApplicationDto.setLastModifiedByName(getLastModifiedByName(lastModifiedId, account.getClientApplicationForm().isDirectAccount(), serviceErrors));
        }
        setAdviserDetails(serviceErrors, clientApplicationDto, account.getAdviserPositionId());

        final Product product = productIntegrationService.getProductDetail(ProductKey.valueOf(account.getProductId()), new FailFastErrorsImpl());
        LOGGER.info(LoggingConstants.ONBOARDING + " ProductId: {} - Product: {}", account.getProductId(), product);

        clientApplicationDto.setProductName(null != product ? product.getProductName(): null);
        clientApplicationDto.setProductId(EncodedString.fromPlainText(account.getProductId()).toString());
        return clientApplicationDto;
    }

    private ClientApplicationDto getMinimalClientApplicationDto(ClientApplication account) {
        final ClientApplicationKey key = new ClientApplicationKey(account.getId());
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl(key);
        final String accountFormDataJsonString = JsonSanitizer.sanitize(account.getFormData()); //fix Fortify issue by sanitizing JSON string
        if (accountFormDataJsonString != null) {
            LOGGER.info(LoggingConstants.ONBOARDING + " formData.length: {} - formData JSON String: {}", accountFormDataJsonString.length(), accountFormDataJsonString);
            if (accountFormDataJsonString.length() > BIG_N) {
                LOGGER.error(LoggingConstants.ONBOARDING + " rehydrating the formData seems messed up, nothing should be this big!");
                throw new IllegalStateException("the formData seems messed up, nothing should be this big");
            }
            try {
                Map<String, Object> formData = objectMapper.readValue(accountFormDataJsonString, new TypeReference<LinkedHashMap<String, Object>>() {});
                clientApplicationDto.setFormData(formData);
                if (clientApplicationDto.isJsonSchemaSupported()) { //using the upgraded JSON model based on JSON Schema v1
                    if (clientApplicationDto.isDirectApplication()) {
                        clientApplicationDto = new ClientApplicationDtoDirectImpl(key, objectMapper.readValue(accountFormDataJsonString, DirectClientApplicationFormData.class));
                    } else {
                        clientApplicationDto = new ClientApplicationDtoAdvisedImpl(key, objectMapper.readValue(accountFormDataJsonString, OnboardingApplicationFormData.class));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        clientApplicationDto.setStatus(account.getStatus());
        return clientApplicationDto;
    }

    /**
     * this function returns basic formdata, it doesn't fetch any broker or product information.
     * @param account
     * @return
     */
    public ClientApplicationDto convertToMinimalDto(ClientApplication account){
        return getMinimalClientApplicationDto(account);
    }

    private void setAdviserDetails(ServiceErrors serviceErrors, ClientApplicationDto clientApplicationDto, String adviserPositionId) {
        clientApplicationDto.setAdviserId(EncodedString.fromPlainText(adviserPositionId).toString());
        if (adviserPositionId != null && (!adviserPositionId.isEmpty())) {
            clientApplicationDto.setAdviserName(getBrokerNameByPositionId(adviserPositionId, serviceErrors));
        }
    }

    private String getBrokerNameByPositionId(String positionId, ServiceErrors serviceErrors) {
        try {
            BrokerUser adviser = brokerIntegrationService.getAdviserBrokerUser(BrokerKey.valueOf(positionId), serviceErrors);
            return getFormatedName(adviser);
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("Can not find adviser name", ex);
            return "";
        }
    }

    private String getLastModifiedByName(String lastModifiedId, boolean isDirect, ServiceErrors serviceErrors) {
        if (!StringUtils.isEmpty(lastModifiedId)) {
            if (isDirect) {
                // Returning id again since a WPL user cannot invoke cached clients to retrieve its name. It fails due to STS policy.
                return lastModifiedId;
            } else {
                return getLastModifiedForAdvisedAccount(lastModifiedId, serviceErrors);

            }
        }
        return "";
    }

    private String getLastModifiedForAdvisedAccount(String lastModifiedId, ServiceErrors serviceErrors) {
        BrokerUser brokerUser = brokerIntegrationService.getBrokerUser(UserKey.valueOf(lastModifiedId), serviceErrors);
        if (brokerUser == null) {
            LOGGER.warn(String.format("Null BrokerUser (from gcmId = %s)", lastModifiedId));
            return "";
        }
        return getFormatedName(brokerUser);
    }

    private String getFormatedName(Client client) {
        return String.format("%s, %s",
                client.getLastName(),
                client.getFirstName());
    }

    void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

}
