package com.bt.nextgen.api.tracking.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.tracking.model.PersonInfo;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingAccount;
import com.bt.nextgen.core.repository.OnboardingAccountRepository;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyRepository;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationIdentifierImpl;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.ApplicationIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static com.bt.nextgen.draftaccount.repository.ClientApplicationStatus.docuploaded;
import static com.bt.nextgen.draftaccount.repository.ClientApplicationStatus.processing;

@Service
@Transactional(value = "springJpaTransactionManager")
@SuppressWarnings("squid:UnusedProtectedMethod")//Suppressing this as matchSafely method has been defined in anonymous class
public class TrackingDtoServiceImpl implements TrackingDtoService {

    @Autowired
    private ClientApplicationRepository clientApplicationsRepository;

    @Autowired
    private TrackingDtoConverterService trackingDtoConverter;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private AccActivationIntegrationService accActivationIntegrationService;

    @Autowired
    private OnboardingPartyRepository partyRepository;

    @Autowired
    private OnboardingAccountRepository accountRepository;

    private static final Logger logger = LoggerFactory.getLogger(TrackingDtoServiceImpl.class);

    @Override
    public List<TrackingDto> search(List<ApiSearchCriteria> criteriaList, final ServiceErrors serviceErrors) {
        final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        Date fromDate = null;
        Date toDate = null;
        String adviserPositionId = null;
        for (ApiSearchCriteria criteria : criteriaList) {
            final String value = criteria.getValue();
            switch (criteria.getProperty()) {
                case "fromdate":
                    fromDate = formatter.parseDateTime(value).toDate();
                    break;
                case "todate":
                    toDate = formatter.parseDateTime(value).plusDays(1).minusSeconds(1).toDate();
                    break;
                case "adviserPositionId":
                    adviserPositionId = value;
                    break;
                default:
                    break;
            }
        }
        Collection<BrokerIdentifier> adviserIds = getAdvisersForUser(serviceErrors);
        if (adviserPositionId != null) {
            final String decodedAdviserPositionId = EncodedString.toPlainText(adviserPositionId);
            adviserIds = Lambda.filter(new LambdaMatcher<BrokerIdentifier>() {

                @Override
                protected boolean matchesSafely(BrokerIdentifier brokerIdentifier) {
                    return decodedAdviserPositionId.equals(brokerIdentifier.getKey().getId());
                }
            }, adviserIds);
        }
        List<ClientApplication> applications = clientApplicationsRepository.findNonActiveApplicationsBetweenDates(fromDate, toDate, adviserIds);
        return getTrackingDtos(applications, true, false, serviceErrors);
    }

    @Override
    public List<TrackingDto> searchForUnapprovedApplications(Date fromDate, Date toDate, final ServiceErrors serviceErrors) {
        List<ClientApplication> applications = clientApplicationsRepository.findNonActiveApplicationsBetweenDates(fromDate, toDate);
        return getTrackingDtos(applications, true, true, serviceErrors);
    }

    @Override
    public List<TrackingDto> getTrackingDtos(List<ClientApplication> applications, boolean doMapAccounts, final boolean doFetchAdviserDetailInfo, final ServiceErrors serviceErrors) {
        List<ApplicationIdentifier> applicationIdentifiers = getProcessingApplicationIdentifiers(applications);
        final List<BrokerKey> brokerKeyList = getBrokerKeyList(applications);
        final List<UserKey> userKeysList = getUserKeyList(applications);
        final Map<String, ApplicationDocument> applicationDocumentsByOrderId;
        if (applicationIdentifiers.isEmpty()) {
            applicationDocumentsByOrderId = Collections.emptyMap();
        } else {
            applicationDocumentsByOrderId = getApplicationDocumentMap(applicationIdentifiers, serviceErrors);
        }

        final Map<ProductKey, Product> productKeyProductMap = productIntegrationService.loadProductsMap(serviceErrors);

        List<Long> onboardingApplicationIds = getOnboardingApplicationIds(applications);
        List<OnboardingParty> onboardingPartiesByApplicationIds = new ArrayList<>();
        List<OnboardingAccount> onboardingAccountsByApplicationIds = new ArrayList<>();
        if (!onboardingApplicationIds.isEmpty()) {
            onboardingPartiesByApplicationIds = partyRepository.findOnboardingPartiesByApplicationIds(onboardingApplicationIds);
            if (doMapAccounts) {
                onboardingAccountsByApplicationIds = accountRepository.findByOnboardingApplicationIds(onboardingApplicationIds);
            }
        }

        final Map<Long, Map<String, OnboardingParty>> onboardingApplicationIdByGcmPanMap = getOnboardingApplicationIdByGcmPanMap(onboardingApplicationIds, onboardingPartiesByApplicationIds);
        final Map<Long, OnboardingAccount> onboardingAccountByApplicationid = Lambda.index(onboardingAccountsByApplicationIds, on(OnboardingAccount.class).getOnboardingApplicationKey().getId());
        final Map<String, PersonInfo> adviserIdMap = getAdviserIdMap(brokerKeyList, doFetchAdviserDetailInfo, serviceErrors);
        final Map<String, BrokerUser> lastModifiedIdMap = getLastModifiedIdMap(userKeysList, serviceErrors);

        return Lambda.convert(applications, new Converter<ClientApplication, TrackingDto>() {
            @Override
            public TrackingDto convert(ClientApplication application) {
                return trackingDtoConverter.convertToDto(application, applicationDocumentsByOrderId, productKeyProductMap, onboardingApplicationIdByGcmPanMap,
                        onboardingAccountByApplicationid, adviserIdMap, lastModifiedIdMap,serviceErrors);
            }
        });
    }


    private Map<String, PersonInfo> getAdviserIdMap(List<BrokerKey> brokerKeyList, final boolean doFetchAdviserDetailInfo, final ServiceErrors serviceErrors) {
        List<PersonInfo> personInfoList = getAdviser(doFetchAdviserDetailInfo, brokerKeyList, serviceErrors);
        return Lambda.index(personInfoList, on(PersonInfo.class).getId());
    }

    private List<BrokerKey> getBrokerKeyList(List<ClientApplication> applications) {
        HashSet<String> adviserIds = new HashSet<>();
        for(ClientApplication clientApplication: applications){
            adviserIds.add(clientApplication.getAdviserPositionId());
        }

        return Lambda.convert(adviserIds, new Converter<String, BrokerKey>() {
            @Override
            public BrokerKey convert(String brokerId) {
                return BrokerKey.valueOf(brokerId);
            }
        });
    }

    /**
     * Method to retrieve list of person info by using a list of broker keys.
     * @param doFetchAdviserDetailInfo
     * @param brokerKeyList
     * @param serviceErrors
     * @return List<PersonInfo>
     */
    private List<PersonInfo> getAdviser(boolean doFetchAdviserDetailInfo, List<BrokerKey> brokerKeyList, ServiceErrors serviceErrors) {
        List<PersonInfo> listOfPersonInfo = new ArrayList<>();
        try {
            logger.debug("loading adviser details for position Id {} ", brokerKeyList.size());
            logger.info("loading adviser details for position Id {} ", brokerKeyList.size());
            Map<BrokerKey, BrokerWrapper> advBrokerKeyBrokerWrapperMap = brokerService.getAdviserBrokerUser(brokerKeyList, serviceErrors);

            for (BrokerKey brokerKey : advBrokerKeyBrokerWrapperMap.keySet()) {
                PersonInfo personInfo = null;
                try {
                    BrokerUser adviserUser = advBrokerKeyBrokerWrapperMap.get(brokerKey).getBrokerUser();
                    String dealerGroup = null;
                    if (adviserUser != null) {
                        dealerGroup = advBrokerKeyBrokerWrapperMap.get(brokerKey).getDealerGroupPositionName();
                        personInfo = new PersonInfo(adviserUser.getFirstName(), adviserUser.getLastName(), brokerKey.getId(), dealerGroup);
                        if (doFetchAdviserDetailInfo) {
                            setEmailIdAndPhoneNumbersForTheAdviser(personInfo, adviserUser);
                        }
                    } else {
                        logger.error("Adviser user null for BrokerKey:{}", brokerKey);
                        personInfo = new PersonInfo("", "", brokerKey.getId());
                    }

                } catch (Exception e) {
                    logger.error("Can not populate adviser details: " + e.getMessage(), e);
                    personInfo = new PersonInfo("", "", brokerKey.getId());
                }
                listOfPersonInfo.add(personInfo);
            }

        } catch (Exception ex) {
            logger.error("Can not populate adviser details: " + ex.getMessage(), ex);
        }
        return listOfPersonInfo;
    }

    private void setEmailIdAndPhoneNumbersForTheAdviser(PersonInfo personInfo, BrokerUser adviserUser) {
        if (adviserUser != null) {
            setEmailId(personInfo, adviserUser.getEmails());
            setPhoneNumber(personInfo, adviserUser.getPhones(), AddressMedium.MOBILE_PHONE_PRIMARY);
            setPhoneNumber(personInfo, adviserUser.getPhones(), AddressMedium.BUSINESS_TELEPHONE);
        }
    }

    private void setEmailId(PersonInfo personInfo, List<Email> emails) {
        if (emails != null && !emails.isEmpty()) {
            final Email primaryEmail = Lambda.selectFirst(emails, new LambdaMatcher<Email>() {
                @Override
                protected boolean matchesSafely(Email email) {
                    return email.getType() == AddressMedium.EMAIL_PRIMARY;
                }
            });
            if (primaryEmail != null) {
                personInfo.setEmailId(primaryEmail.getEmail());
            }
        }
    }

    private void setPhoneNumber(PersonInfo personInfo, List<Phone> phones, final AddressMedium addressMedium) {
        if (phones != null && !phones.isEmpty()) {
            final Phone phone = Lambda.selectFirst(phones, new LambdaMatcher<Phone>() {
                @Override
                protected boolean matchesSafely(Phone phoneNumber) {
                    return phoneNumber.getType() == addressMedium;
                }
            });
            if (phone != null) {
                final String phoneNumber = StringUtils.defaultString(phone.getAreaCode()) + phone.getNumber();
                if (addressMedium.equals(AddressMedium.BUSINESS_TELEPHONE)) {
                    personInfo.setBusinessPhone(phoneNumber);
                } else {
                    personInfo.setMobilePhone(phoneNumber);
                }
            }
        }
    }

    private List<UserKey> getUserKeyList(List<ClientApplication> applications) {
        HashSet<String> userKeys = new HashSet<>();
        for(ClientApplication clientApplication: applications){
            if(!clientApplication.getClientApplicationForm().isDirectAccount()) {
                userKeys.add(clientApplication.getLastModifiedId());
            }
        }

        return Lambda.convert(userKeys, new Converter<String, UserKey>() {
            @Override
            public UserKey convert(String userId) {
                return UserKey.valueOf(userId);
            }
        });
    }

    private Map<String, BrokerUser> getLastModifiedIdMap(List<UserKey> userKeyList, final ServiceErrors serviceErrors) {
        logger.info("Fetch Details for {} no of Users ", userKeyList.size());
        List<BrokerUser> brokerUsers = Lambda.convert(userKeyList, new Converter<UserKey, BrokerUser>() {
            @Override
            public BrokerUser convert(UserKey userKey) {
                return brokerService.getBrokerUser(userKey, serviceErrors);
            }
        });
        return Lambda.index(brokerUsers, on(BrokerUser.class).getBankReferenceKey().getId());
    }

    private Map<Long, Map<String, OnboardingParty>> getOnboardingApplicationIdByGcmPanMap(List<Long> onboardingApplicationIds, List<OnboardingParty> onboardingPartiesByApplicationIds) {
        Map<Long, Map<String, OnboardingParty>> onboardingApplicationIdByGcmPanMap =  new HashMap<>();

        for (Long onboardingApplicationId : onboardingApplicationIds) {
            List<OnboardingParty> onboardingParties = Lambda.filter(Lambda.having(on(OnboardingParty.class).getOnboardingApplicationId(), Matchers.is(onboardingApplicationId)),onboardingPartiesByApplicationIds);
            Map<String, OnboardingParty> partyMapByGcmPan = Lambda.index(onboardingParties, on(OnboardingParty.class).getGcmPan());
            onboardingApplicationIdByGcmPanMap.put(onboardingApplicationId, partyMapByGcmPan);
        }

        return onboardingApplicationIdByGcmPanMap;
    }

    private Map<String, ApplicationDocument> getApplicationDocumentMap(List<ApplicationIdentifier> applicationIdentifiers, ServiceErrors serviceErrors) {
        UserProfile activeProfile = userProfileService.getActiveProfile();
        List<ApplicationDocument> applicationDocuments = accActivationIntegrationService.loadAccApplicationForApplicationId(applicationIdentifiers,activeProfile.getJobRole(),activeProfile.getClientKey(), serviceErrors);
        return Lambda.index(applicationDocuments, on(ApplicationDocument.class).getAppNumber());
    }

    List<ApplicationIdentifier> getProcessingApplicationIdentifiers(List<ClientApplication> clientApplications) {
        List<ApplicationIdentifier> applicationIdentifiersList = new ArrayList<>(clientApplications.size());
        for (ClientApplication clientApplication : clientApplications) {
                if (Arrays.asList(processing, docuploaded).contains(clientApplication.getStatus())) {
                OnBoardingApplication onboardingApplication = clientApplication.getOnboardingApplication();
                if (onboardingApplication.getAvaloqOrderId() != null) {
                    ApplicationIdentifier appId = new ApplicationIdentifierImpl();
                    appId.setDocId(onboardingApplication.getAvaloqOrderId());
                    applicationIdentifiersList.add(appId);
                }
            }
        }
        return applicationIdentifiersList;
    }

    List<Long> getOnboardingApplicationIds(List<ClientApplication> clientApplications) {
        List<Long> onboardingApplicationIds = new ArrayList<>(clientApplications.size());
        for (ClientApplication clientApplication : clientApplications) {
            if (Arrays.asList(processing, docuploaded).contains(clientApplication.getStatus())){
                OnBoardingApplication onboardingApplication = clientApplication.getOnboardingApplication();
                onboardingApplicationIds.add(onboardingApplication.getKey().getId());
            }
        }
        return onboardingApplicationIds;
    }

    private Collection<BrokerIdentifier> getAdvisersForUser(ServiceErrors serviceErrors) {
        return brokerService.getAdvisersForUser(userProfileService.getActiveProfile(), serviceErrors);
    }

    @Override
    public int countUnapprovedApplications(Date fromDate, Date toDate) {
        List<ClientApplication> applications = clientApplicationsRepository.findNonActiveApplicationsBetweenDates(fromDate, toDate);
        return getOnboardingApplicationIds(applications).size();
    }
}
