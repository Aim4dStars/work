package com.bt.nextgen.api.draftaccount.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.adviser.model.AdviserSearchDto;
import com.bt.nextgen.api.adviser.service.AdviserSearchDtoService;
import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.draftaccount.builder.ProcessInvestorApplicationRequestMsgTypeBuilder;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ApprovalTypeEnum;
import com.bt.nextgen.api.draftaccount.util.IdInsertion;
import com.bt.nextgen.api.draftaccount.util.ServiceErrorsUtil;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.NotAllowedException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationRepository;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.DateTimeService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;

import static com.bt.nextgen.web.controller.cash.util.Attribute.APPLICATION_SUBMISSION_KEY;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.util.ObjectUtils.nullSafeEquals;

@Service
@Transactional(value = "springJpaTransactionManager")
@SuppressWarnings("squid:S1200")
public class ClientApplicationDtoHelperService {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private DateTimeService dateTimeService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private WebServiceProvider provider;

    @Autowired
    private ProcessInvestorApplicationRequestMsgTypeBuilder requestMsgTypeBuilder;

    @Autowired
    private OnboardingApplicationRepository onboardingApplicationRepository;

    @Autowired
    private OnboardingPartyService onboardingPartyService;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    @Autowired
    private ClientApplicationDtoConverterService clientApplicationDtoConverterService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private AdviserSearchDtoService adviserSearchDtoService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplicationDtoHelperService.class);

    @SuppressWarnings("unchecked")
    public ClientApplicationDto submitDraftAccount(ClientApplicationDto clientApplicationDto, ServiceErrors serviceErrors,
            ClientApplication draftAccount) {
        // Note: We save formData again even though we don't expect that it has changed. This is to ensure that you always
        // submit the data that you are looking at on the summary page (in the case that multiple users are concurrently modifying
        // the same application). Joe accepted this approach in lieu of a complex locking solution.
        Object formData = clientApplicationDto.getFormData();
        // the schema supported form will generate random correlation IDs in their implementation
        if (!clientApplicationDto.isJsonSchemaSupported()) {
            IdInsertion.mergeIds((Map<String, Object>) formData);
        }
        if (formData instanceof OnboardingApplicationFormData){
            ((OnboardingApplicationFormData) formData).setApprovalType(clientApplicationDto.isOffline() ?
                    ApprovalTypeEnum.OFFLINE : ApprovalTypeEnum.ONLINE);
        }
        draftAccount.setFormData(formData);
        IClientApplicationForm clientApplicationForm = draftAccount.getClientApplicationForm();

        final BrokerKey brokerKey = BrokerKey.valueOf(draftAccount.getAdviserPositionId());
        final BrokerUser brokerUser = brokerIntegrationService.getAdviserBrokerUser(brokerKey, serviceErrors);
        final Broker dealer = getDealerGroup(brokerKey, serviceErrors);

        OnboardingApplicationKey key = createOrGetOnboardingApplicationKey(draftAccount, clientApplicationDto);
        String submissionIDString = "submissionID=" + key.getId();
        LOGGER.info(LoggingConstants.ONBOARDING_SUBMIT + submissionIDString);

        Object request = requestMsgTypeBuilder.buildFromForm(clientApplicationForm, brokerUser, key, draftAccount.getProductId(),
            dealer, serviceErrors);

        onboardingPartyService.createOnboardingPartyForExistingUsers(clientApplicationForm, key.getId());
        Object response = sendProcessInvestorRequest(request);

        ClientApplicationDto keyedDto = clientApplicationDto;
        if (requestMsgTypeBuilder.isSuccessful(response)) {
            draftAccount.markSubmitted();
            updateLastModified(draftAccount, clientApplicationForm);
            keyedDto = clientApplicationDtoConverterService.convertToMinimalDto(draftAccount);
            LOGGER.info(LoggingConstants.ONBOARDING_SUBMIT + submissionIDString + " is successful");
        } else {
            ServiceErrorsUtil.updateResponseServiceErrors(serviceErrors, requestMsgTypeBuilder.getErrorResponses(response));
            LOGGER.info(LoggingConstants.ONBOARDING_SUBMIT + submissionIDString + " NOT successful");
        }
        LOGGER.info(LoggingConstants.ONBOARDING_SUBMIT + "end");
        return keyedDto;
    }

    private Object sendProcessInvestorRequest(Object request) {
        return provider.sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(), APPLICATION_SUBMISSION_KEY, request);
    }

    private OnboardingApplicationKey createOrGetOnboardingApplicationKey(ClientApplication draftAccount, ClientApplicationDto dto) {
        boolean save = false;
        final String applicationType = dto.getAccountType();
        final boolean offline = dto.isOffline();
        OnBoardingApplication application = draftAccount.getOnboardingApplication();
        if (application == null) {
            save = true;
            application = new OnBoardingApplication(applicationType, offline);
        } else if (!nullSafeEquals(applicationType, application.getApplicationType()) || offline != application.isOffline()) {
            save = true;
            application.setApplicationType(applicationType);
            application.setOffline(offline);
        }
        if (save) {
            application = onboardingApplicationRepository.save(application);
            draftAccount.setOnboardingApplication(application);
        }
        return application.getKey();
    }

    private void updateLastModified(ClientApplication draftAccount, IClientApplicationForm clientApplicationForm) {
        draftAccount.setLastModifiedAt(dateTimeService.getCurrentDateTime());
        if (clientApplicationForm.isDirectAccount()) {
            final SamlToken samlToken = userProfileService.getSamlToken();
            draftAccount.setLastModifiedId(samlToken.getCISKey().getId());
            LOGGER.info(LoggingConstants.DIRECT_ONBOARDING_SUBMIT + " lastmodifiedId " + samlToken.getCISKey());
        } else {
            draftAccount.setLastModifiedId(userProfileService.getGcmId());
            LOGGER.info(LoggingConstants.ONBOARDING_SUBMIT + " lastmodifiedId " + userProfileService.getGcmId());
        }
    }

    private Broker getDealerGroup(BrokerKey brokerKey, ServiceErrors errors) {
        final Broker broker = brokerIntegrationService.getBroker(brokerKey, errors);
        if (broker != null) {
            return brokerIntegrationService.getBroker(broker.getDealerKey(), errors);
        }
        return null;
    }

    public Set<String> getAssetIdSetForAdviser(Broker broker, final ServiceErrors serviceErrors) {
        final Set<String> assetIds = new HashSet<>();
        List<Product> productList = new ArrayList<>();
        if (broker != null) {
            productList = productIntegrationService.getDealerGroupProductList(broker.getDealerKey(), serviceErrors);
        }
        ProductListConverter productListConverter = new ProductListConverter(broker, serviceErrors, assetIds);

        Lambda.map(productList, productListConverter);

        return assetIds;
    }

    class ProductListConverter implements Converter<Product, Set<String>> {

        private Broker broker;

        private ServiceErrors serviceErrors;

        private Set<String> assetIds;

        public ProductListConverter(Broker broker, ServiceErrors serviceErrors, Set<String> assetIds) {
            this.broker = broker;
            this.serviceErrors = serviceErrors;
            this.assetIds = assetIds;
        }

        @Override
        public Set<String> convert(Product product) {
            return new HashSet<>(assetIntegrationService.loadAvailableAssetsForBrokerAndProduct(broker.getDealerKey(),
                    product.getProductKey(), serviceErrors));
        }
    }

    public void setBrokerNames(ClientApplication draftAccount, ClientApplicationDto dto, ServiceErrors serviceErrors) {
        dto.setLastModifiedByName(getBrokerNameByGcmId(draftAccount.getLastModifiedId(), serviceErrors));
        dto.setAdviserName(getBrokerNameByPositionId(draftAccount.getAdviserPositionId(), serviceErrors));
    }

    public void checkProductIdAndAdviserIdAreAllowedForLoggedInUser(String adviserId, String productId){
        if(!canModifyApplicationForAdviser(adviserId)) {
            throw new NotAllowedException(ApiVersion.CURRENT_VERSION, "The user cannot update application for this adviser");
        }

        if(!canModifyApplicationForProduct(adviserId, productId)) {
            throw new NotAllowedException(ApiVersion.CURRENT_VERSION, "The user cannot update application for this product");
        }
    }

    private boolean canModifyApplicationForAdviser(final String adviserPositionId){
        List<ApiSearchCriteria> apiSearchCriterias = new ArrayList<>();
        List<AdviserSearchDto> advisersList = adviserSearchDtoService.search(apiSearchCriterias, new FailFastErrorsImpl());
        AdviserSearchDto matchedAdviser = Lambda.selectFirst(advisersList, new LambdaMatcher<AdviserSearchDto>() {
            @Override
            protected boolean matchesSafely(AdviserSearchDto adviser) {
                return EncodedString.toPlainText(adviser.getAdviserPositionId()).equals(adviserPositionId);
            }
        });
        return matchedAdviser != null;
    }

    private boolean canModifyApplicationForProduct(String adviserPositionId, String productId){
        Broker broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(adviserPositionId), new FailFastErrorsImpl());
        List<Product> productList = productIntegrationService.getDealerGroupProductList(broker.getDealerKey(), new FailFastErrorsImpl());
        return Lambda.exists(productList, hasProperty("productKey", equalTo(ProductKey.valueOf(productId))));
    }


    private String getBrokerNameByPositionId(String positionId, ServiceErrors serviceErrors) {
        final BrokerKey brokerKey = BrokerKey.valueOf(positionId);
        try {
            BrokerUser adviser = brokerIntegrationService.getAdviserBrokerUser(brokerKey, serviceErrors);
            return StringUtils.isEmpty(adviser.getFullName())
                    ? String.format("%s %s", adviser.getFirstName(), adviser.getLastName()) : adviser.getFullName();
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("Can not find adviser name", ex);
            return "";
        }
    }

    private String getBrokerNameByGcmId(String gcmId, ServiceErrors serviceErrors) {
        BrokerUser brokerUser = brokerIntegrationService.getBrokerUser(UserKey.valueOf(gcmId), serviceErrors);
        if (brokerUser == null) {
            LOGGER.warn(String.format("Null BrokerUser (from gcmId = %s)", gcmId));
            return "";
        }
        return brokerUser.getFullName();
    }

}
