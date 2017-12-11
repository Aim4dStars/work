package com.bt.nextgen.api.tracking.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.model.ApplicationClientStatus;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IContactValue;
import com.bt.nextgen.api.draftaccount.model.form.IPersonDetailsForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.ApprovalTypeEnum;
import com.bt.nextgen.api.tracking.model.PersonInfo;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingAccount;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@Service
@Transactional
public class TrackingDtoConverterService {

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private InvestorStatusService investorStatusService;

    @Autowired
    private AccountStatusService accountStatusService;

    private static final Logger logger = LoggerFactory.getLogger(TrackingDtoConverterService.class);

    private static String PANORAMA_SUPPORT = "panoramasupport";

    @SuppressWarnings("squid:S00107")
    public TrackingDto convertToDto(ClientApplication application, Map<String, ApplicationDocument> applicationDocumentMap, Map<ProductKey, Product> productKeyProductMap, Map<Long, Map<String, OnboardingParty>> onboardingApplicationIdByGcmPanMap,
                                    Map<Long, OnboardingAccount> onboardingAccountByApplicationid, Map<String, PersonInfo> adviserPositionIdMap, Map<String, BrokerUser> lastModifiedIdMap, ServiceErrors serviceErrors) {
        IClientApplicationForm form = application.getClientApplicationForm();
        TrackingDto trackingDto = new TrackingDto(application.getLastModifiedAt(), form.getAccountType().value(), new ClientApplicationKey(application.getId()));

        String accountId = getAccountId(application, onboardingAccountByApplicationid);
        trackingDto.setAccountId(accountId);
        if (StringUtils.isNotBlank(accountId)) {
            trackingDto.setEncodedAccountId(EncodedString.fromPlainText(accountId).toString());
        }
        trackingDto.setEncryptedBpId(getBpId(application, applicationDocumentMap));
        trackingDto.setAdviser(adviserPositionIdMap.get(application.getAdviserPositionId()));
        trackingDto.setLastModifiedBy(getLastModifiedBy(application, form, applicationDocumentMap, lastModifiedIdMap, serviceErrors));
        trackingDto.setPrimaryContact(getPrimaryContact(form));
        trackingDto.setApprovalType(getApprovalType(application));
        trackingDto.setReferenceNumber(formReferenceNumber(application.getId()));
        trackingDto.setOrderId(getOrder(application));

        OnboardingApplicationStatus status = accountStatusService.getApplicationStatus(application, applicationDocumentMap);
        if (status == null) {
            List<TrackingDto.Investor> investors = retrieveInvestors(application, applicationDocumentMap, onboardingApplicationIdByGcmPanMap);
            List<TrackingDto.Investor> accountApprovers = Lambda.filter(new LambdaMatcher<TrackingDto.Investor>() {
                @Override
                protected boolean matchesSafely(TrackingDto.Investor investor) {
                    return investor.isApprover();
                }
            }, investors);
            OnboardingApplicationStatus accountStatusByInvestorsStatus = accountStatusService.getAccountStatusByInvestorsStatuses(accountApprovers);
            accountStatusByInvestorsStatus = getOfflineUploadInvestorStatus(application, accountStatusByInvestorsStatus);
            OnboardingApplicationStatus accountStatusByAccountType = accountStatusService.getStatusForAccountType(accountStatusByInvestorsStatus, application, applicationDocumentMap);
            trackingDto.setStatus(accountStatusByAccountType);

            trackingDto.setInvestors(investors);
            trackingDto.setDisplayName(getDisplayName(application));

        } else {
            trackingDto.setStatus(status);
            trackingDto.setDisplayName(form.getAccountName());
        }

        final Product product = productKeyProductMap.get(ProductKey.valueOf(application.getProductId()));
        trackingDto.setProductName(product == null ? null : product.getProductName());
        trackingDto.setParentProductName(product == null ? null : product.getParentProductName());

        if (form.hasTrust()) {
            trackingDto.setTrustType(form.getTrustType());
        }

        if (application.getOnboardingApplication() != null) {
            trackingDto.setOnboardingApplicationKey(application.getOnboardingApplication().getKey());
        }

        return trackingDto;
    }

    private OnboardingApplicationStatus getOfflineUploadInvestorStatus(ClientApplication application, OnboardingApplicationStatus applicationStatus) {
        if (application.getOnboardingApplication().isOffline() && application.getStatus().equals(ClientApplicationStatus.docuploaded)) {
            return OnboardingApplicationStatus.active;
        }
        return applicationStatus;
    }

    private String getOrder(ClientApplication application) {
        if(application.getOnboardingApplication()!=null && application.getOnboardingApplication().getAvaloqOrderId()!=null){
            return application.getOnboardingApplication().getAvaloqOrderId();
        }
        return null;
    }

    private ApprovalTypeEnum getApprovalType(ClientApplication application) {
        if(application.getOnboardingApplication()!=null && application.getOnboardingApplication().isOffline())
            return ApprovalTypeEnum.OFFLINE;
        else
            return ApprovalTypeEnum.ONLINE;
    }

    private String getBpId(ClientApplication application, Map<String, ApplicationDocument> applicationDocumentMap) {
        if (isApplicationsEmpty(applicationDocumentMap, application)) {
            return null;
        }
        OnBoardingApplication onboardingApplication = application.getOnboardingApplication();
        ApplicationDocument applicationDocument = applicationDocumentMap.get(onboardingApplication.getAvaloqOrderId());
        if (applicationDocument != null && applicationDocument.getBpid() != null) {
            return EncodedString.fromPlainText(applicationDocument.getBpid().getId()).toString();
        }
        return null;
    }

    private String getAccountId(ClientApplication application, Map<Long, OnboardingAccount> onboardingAccountByApplicationid) {
        if (isApplicationsEmpty(onboardingAccountByApplicationid, application)) {
            return null;
        }
        OnboardingAccount a = onboardingAccountByApplicationid.get(application.getOnboardingApplication().getKey().getId());
        return a == null ? null : a.getAccountNumber();
    }

    private boolean isApplicationsEmpty(Map applicationMap, ClientApplication application) {
        return applicationMap == null || isApplicationKeyEmpty(application);
    }

    private boolean isApplicationKeyEmpty(ClientApplication application) {
        return application.getOnboardingApplication() == null || application.getOnboardingApplication().getKey() == null;
    }

    private PersonInfo getLastModifiedBy(ClientApplication application, IClientApplicationForm form, Map<String, ApplicationDocument> applicationDocumentMap, Map<String, BrokerUser> lastModifiedIdMap, ServiceErrors serviceErrors) {
        if (form.isDirectAccount()) {
            return getPersonInfoForDirectAccount(application);
        } else {
            return getPersonInfoForAdvisedAccount(application, applicationDocumentMap,lastModifiedIdMap, serviceErrors);
        }
    }

    private PersonInfo getPersonInfoForDirectAccount(ClientApplication application) {
        final String cisKey = application.getLastModifiedId();
        // Returning id again since a WPL user cannot invoke cached clients to retrieve its name. It fails due to STS policy.
        return new PersonInfo(cisKey, "");
    }

    private PersonInfo getPersonInfoForAdvisedAccount(ClientApplication application, Map<String, ApplicationDocument> applicationDocumentMap, Map<String, BrokerUser> lastModifiedIdMap, ServiceErrors serviceErrors) {
        BrokerUser lastModifiedByUser;
        OnBoardingApplication onBoardingApplication = application.getOnboardingApplication();
        if (onBoardingApplication != null && isWithDrawn(application, applicationDocumentMap) && isUpdatedBySmartClient(application, applicationDocumentMap, serviceErrors)) {
            ApplicationDocument applicationDocument = applicationDocumentMap.get(onBoardingApplication.getAvaloqOrderId());
            String appLastUpdatedById = applicationDocument.getAppLastUpdatedBy();
            lastModifiedByUser = new BrokerUserImpl(UserKey.valueOf(appLastUpdatedById));
            ((BrokerUserImpl) lastModifiedByUser).setFirstName(PANORAMA_SUPPORT);
        } else {
            lastModifiedByUser = lastModifiedIdMap.get(application.getLastModifiedId());
        }

        if (lastModifiedByUser != null) {
            return new PersonInfo(lastModifiedByUser.getFirstName(), lastModifiedByUser.getLastName(), lastModifiedByUser.getBankReferenceKey() == null ? null : lastModifiedByUser.getBankReferenceKey().getId());
        } else {
            logger.error("Can not populate last modified details. UserKey(=GCM id) " + application.getLastModifiedId() + " not mapped to BrokerUser");
            return new PersonInfo("", "");
        }
    }

    private boolean isUpdatedBySmartClient(ClientApplication application, Map<String, ApplicationDocument> applicationDocumentMap, ServiceErrors serviceErrors) {
        OnBoardingApplication onBoardingApplication = application.getOnboardingApplication();
        if (onBoardingApplication != null) {
            ApplicationDocument applicationDocument = applicationDocumentMap.get(onBoardingApplication.getAvaloqOrderId());
            if (applicationDocument != null) {
                String appLastUpdatedById = applicationDocument.getAppLastUpdatedBy();
                if (appLastUpdatedById != null) {
                    UserKey userKey = UserKey.valueOf(appLastUpdatedById);
                    BrokerUser brokeruser = brokerIntegrationService.getBrokerUser(userKey, serviceErrors);
                    if (brokeruser == null)
                        return true;
                }
            }
        }
        return false;
    }

    private boolean isWithDrawn(ClientApplication application, Map<String, ApplicationDocument> applicationDocumentMap) {
        OnBoardingApplication onboardingApplication = application.getOnboardingApplication();
        if (onboardingApplication != null) {
            ApplicationDocument applicationDocument = applicationDocumentMap.get(onboardingApplication.getAvaloqOrderId());
            if (applicationDocument != null) {
                if (ApplicationStatus.DISCARDED.equals(applicationDocument.getAppState())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getDisplayName(ClientApplication application) {
        return application.getClientApplicationForm().getAccountName();
    }

    private TrackingDto.Contact getPrimaryContact(IClientApplicationForm form) {
        IAccountSettingsForm accountSettings = form.getAccountSettings();

        int primaryContactIndex;
        if (isSingleInvestorAccount(form)) {
            primaryContactIndex = 0;
        } else if (accountSettings.getPrimaryContact() != null) {
            primaryContactIndex = accountSettings.getPrimaryContact();
        } else {
            return null;
        }
        List<IPersonDetailsForm> allContactPersons = form.getGenericPersonDetails();
        if (allContactPersons == null || allContactPersons.size() <= primaryContactIndex) {
            return null;
        }
        IPersonDetailsForm primaryContactPerson = allContactPersons.get(primaryContactIndex);

        TrackingDto.ContactMethod contactMethodEmail = null;
        if (primaryContactPerson.hasEmail()) {
            IContactValue email = primaryContactPerson.getEmail();
            contactMethodEmail = new TrackingDto.ContactMethod(TrackingDto.ContactMethodType.EMAIL, email.getValue());
        }

        TrackingDto.ContactMethod contactMethodMobile = null;
        if (primaryContactPerson.hasMobile()) {
            contactMethodMobile = new TrackingDto.ContactMethod(
                    TrackingDto.ContactMethodType.MOBILE,
                    primaryContactPerson.getMobile().getValue());
        }
        return new TrackingDto.Contact(primaryContactPerson.getFirstName(), primaryContactPerson.getLastName(),
                Arrays.asList(contactMethodMobile, contactMethodEmail));
    }

    private boolean isSingleInvestorAccount(IClientApplicationForm form) {
        return form.getAccountType() == IClientApplicationForm.AccountType.INDIVIDUAL ||
                form.getAccountType() == IClientApplicationForm.AccountType.SUPER_ACCUMULATION ||
                form.getAccountType() == IClientApplicationForm.AccountType.SUPER_PENSION;
    }

    private String formReferenceNumber(Long draftAccountId) {
        return String.format("R%09d", draftAccountId);
    }

    private TrackingDto.Investor associatedPersonToInvestor(long applicationId, AssociatedPerson person, Map<String, OnboardingParty> partyMapByGcmPan) {
        final OnboardingParty onboardingParty = partyMapByGcmPan.get(person.getGcmId());
        if (onboardingParty != null) {
            ApplicationClientStatus status = investorStatusService.getInvestorStatus(applicationId, person, onboardingParty);
            return new TrackingDto.Investor(
                    person.getClientKey(),
                    status,
                    StringUtils.defaultString(person.getLastName()),
                    StringUtils.defaultString(person.getFirstName()),
                    StringUtils.defaultString(person.getEmail()),
                    person.isHasToAcceptTnC(),
                    person.hasTFNEntered()
            );
        }

        return null;
    }

    private List<TrackingDto.Investor> retrieveInvestors(final ClientApplication application, Map<String, ApplicationDocument> applicationDocumentMap, final Map<Long, Map<String, OnboardingParty>> onboardingApplicationIdByGcmPanMap) {
        OnBoardingApplication onboardingApplication = application.getOnboardingApplication();
        ApplicationDocument applicationDocument = applicationDocumentMap.get(onboardingApplication.getAvaloqOrderId());
        if (applicationDocument == null) {
            return Collections.emptyList();
        }

        List<AssociatedPerson> people = applicationDocument.getPersonDetails();
        final Long applicationId = onboardingApplication.getKey().getId();

        people = Lambda.filter(new LambdaMatcher<AssociatedPerson>() {
            @Override
            protected boolean matchesSafely(AssociatedPerson associatedPerson) {
                return hasInvestorRole(associatedPerson.getPersonRel()) && hasName(associatedPerson);
            }
        }, people);

        List<TrackingDto.Investor> investors = Lambda.convert(people, new Converter<AssociatedPerson, TrackingDto.Investor>() {
            @Override
            public TrackingDto.Investor convert(AssociatedPerson person) {
                return associatedPersonToInvestor(applicationId, person, onboardingApplicationIdByGcmPanMap.get(applicationId));
            }
        });

        return Lambda.filter(not(is(nullValue())), investors);
    }

    private boolean hasName(AssociatedPerson person) {
        return person.getFirstName() != null || person.getLastName() != null;
    }

    private boolean hasInvestorRole(PersonRelationship role) {
        if (role == null) {
            return false;
        }

        List<PersonRelationship> personRelationships = Arrays.asList(
                PersonRelationship.DIRECTOR,
                PersonRelationship.SECRETARY,
                PersonRelationship.SIGNATORY,
                PersonRelationship.TRUSTEE,
                PersonRelationship.AO);
        for (PersonRelationship personRelationship : personRelationships) {
            if (personRelationship.equals(role)) {
                return true;
            }
        }
        return false;
    }
}
