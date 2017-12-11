package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ContactValue;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;
import com.bt.nextgen.core.repository.CisKeyClientApplication;
import com.bt.nextgen.core.repository.CisKeyClientApplicationRepositoryImpl;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.DateTimeService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(value = "springJpaTransactionManager")
public class DirectOnboardingDtoServiceImpl implements DirectOnboardingDtoService {

    public static final String ADVISER_ID_PROPERTY = "direct.adviser.internalId";
    public static final String ADVISER_ID_PROPERTY_SUPER = "direct.super.adviser.internalId";
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectOnboardingDtoServiceImpl.class);
    @Autowired
    private PermittedClientApplicationRepository clientApplicationRepository;
    @Autowired
    private ClientApplicationDtoHelperService clientApplicationHelperService;
    @Autowired
    private ClientApplicationFormDataConverterService clientApplicationFormDataConverterService;
    @Autowired
    private CisKeyClientApplicationRepositoryImpl cisKeyClientApplicationRepository;
    @Autowired
    private ProductIntegrationService productIntegrationService;
    @Autowired
    private BrokerIntegrationService brokerIntegrationService;
    @Autowired
    private UserProfileService profileService;
    @Autowired
    private DateTimeService dateTimeService;
    @Autowired
    private UserProfileService userProfileService;


    @Override
    public ClientApplicationDto submit(ClientApplicationDto keyedObject, ServiceErrors serviceErrors) {
        ClientApplication application = createClientApplication(keyedObject, serviceErrors);
        clientApplicationRepository.save(application);

        ClientApplicationDto clientApplicationDto;
        Object convertedFormData;

        if (!keyedObject.isJsonSchemaSupported()) {
            convertedFormData = clientApplicationFormDataConverterService.convertFormDataForDirect((Map<String, Object>) keyedObject.getFormData());
        } else {
            convertedFormData = keyedObject.getFormData();
            updateDirectFormDataForSubmit((DirectClientApplicationFormData) convertedFormData);
        }

        application.setFormData(convertedFormData);
        keyedObject.setFormData(convertedFormData);
        LOGGER.info(LoggingConstants.DIRECT_ONBOARDING_SUBMIT + "begin");
        clientApplicationDto = clientApplicationHelperService.submitDraftAccount(keyedObject, serviceErrors, application);
        updateCisKeyClientApplicationRepository(application, application.getClientApplicationForm());
        return clientApplicationDto;
    }

    public void updateDirectFormDataForSubmit(DirectClientApplicationFormData directClientApplicationFormData) {
        final Customer customer = directClientApplicationFormData.getInvestors().get(0);// since direct is only for individual. Will need to refactor when we have direct for other account types.
        if (customer.getKey() != null) {
            customer.setGcmId(userProfileService.getGcmId());
        } else {
            ContactValue value = getContactValue(customer);
            customer.setMobile(value);
        }
    }

    private ContactValue getContactValue(Customer customer) {
        final String encodedValue = customer.getMobile().getValue();
        ContactValue value = new ContactValue();
        value.setValid(true);
        value.setValue(EncodedString.toPlainText(encodedValue));
        return value;
    }

    private void updateCisKeyClientApplicationRepository(ClientApplication clientApplication, IClientApplicationForm clientApplicationForm) {
        List<IPersonDetailsForm> genericPersonDetails = clientApplicationForm.getGenericPersonDetails();
        for (IPersonDetailsForm genericPersonDetail : genericPersonDetails) {
            CisKeyClientApplication cisKeyClientApplication = new CisKeyClientApplication(genericPersonDetail.getCisId(), clientApplication);
            cisKeyClientApplicationRepository.save(cisKeyClientApplication);
        }
    }

    private ClientApplication createClientApplication(ClientApplicationDto clientApplicationDto, ServiceErrors serviceErrors) {
        LOGGER.info(LoggingConstants.DIRECT_ONBOARDING_CREATE + "begin");
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setFormData(clientApplicationDto.getFormData());
        String adviserId = getAdviserIdFromEnvProperties(clientApplicationDto.getAccountType());
        clientApplication.setAdviserPositionId(adviserId);
        final String lastModifiedId = getLastModifiedId();
        clientApplication.setLastModifiedId(lastModifiedId);
        clientApplication.setLastModifiedAt(dateTimeService.getCurrentDateTime());
        Product product = getDealerGroupProductForAdviser(serviceErrors, adviserId, clientApplicationDto.getAccountType());
        String productId = product.getProductKey().getId();
        clientApplication.setProductId(productId);

        LOGGER.info(LoggingConstants.DIRECT_ONBOARDING_CREATE + "adviserIdString=" + adviserId + ",productIdString=" + productId);
        LOGGER.info(LoggingConstants.DIRECT_ONBOARDING_CREATE + "lastModifiedIdString=" + lastModifiedId);
        LOGGER.info(LoggingConstants.DIRECT_ONBOARDING_CREATE + "end");
        return clientApplication;
    }

    private String getLastModifiedId() {
        final SamlToken samlToken = profileService.getSamlToken();
        return samlToken.getCISKey().getId();
    }

    private Product getDealerGroupProductForAdviser(ServiceErrors serviceErrors, String adviserId, String accountType) {
        Broker broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(adviserId), serviceErrors);
        List<Product> productList = productIntegrationService.getDealerGroupProductList(broker.getDealerKey(), serviceErrors);
        final boolean isAccountTypeSuper = isSuperAccount(accountType);
        final Product product = Lambda.selectUnique(productList, new LambdaMatcher<Product>() {

            @Override
            protected boolean matchesSafely(Product product) {
                if (isAccountTypeSuper) {
                    return product.isSuper() && product.getProductLevel().equals(ProductLevel.WHITE_LABEL);
                } else {
                    return !product.isSuper() && product.getProductLevel().equals(ProductLevel.WHITE_LABEL);
                }
            }
        });
        return product;
    }

    private String getAdviserIdFromEnvProperties(String accountType) {
        final boolean isAccountTypeSuper = isSuperAccount(accountType);
        return isAccountTypeSuper ?  Properties.getString(ADVISER_ID_PROPERTY_SUPER) : Properties.getString(ADVISER_ID_PROPERTY);
    }

    private boolean isSuperAccount(String accountType){

        return accountType != null && StringUtils.containsIgnoreCase(accountType, "super");
    }
}
