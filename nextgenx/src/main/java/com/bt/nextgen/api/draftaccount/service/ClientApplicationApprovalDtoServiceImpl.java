package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.account.v2.model.LinkedAccountDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationApprovalDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.util.IntegrationUtil;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationRepository;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AvaloqCacheManagedAccountIntegrationService;
import com.bt.nextgen.service.avaloq.accountactivation.AccountActivationRequest;
import com.bt.nextgen.service.avaloq.accountactivation.AccountActivationRequestImpl;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedAccountStatus;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.client.CacheAvaloqClientIntegrationServiceImpl;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.bt.nextgen.service.integration.accountactivation.ActivationAccountIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditionsKey;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepository;
import com.bt.nextgen.service.integration.user.CISKey;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.bt.nextgen.api.draftaccount.LoggingConstants.ONBOARDING_APPROVAL;
import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("squid:S1200")
@Service
@Transactional("springJpaTransactionManager")
public class ClientApplicationApprovalDtoServiceImpl implements ClientApplicationApprovalDtoService {

    private static final Logger LOGGER = getLogger(ClientApplicationApprovalDtoServiceImpl.class);

    @Autowired
    private ActivationAccountIntegrationService activationService;

    @Autowired
    private OnboardingApplicationRepository onboardingApplicationRepository;

    @Autowired
    private ClientApplicationRepository clientApplicationRepository;

    @Autowired
    private InvestorProfileService profileService;

    @Autowired
    private AvaloqCacheManagedAccountIntegrationService cacheManagedAccountIntegrationService;

    @Autowired
    private CacheAvaloqClientIntegrationServiceImpl cacheClientIntegrationService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private UserRoleTermsAndConditionsRepository userRoleTermsAndConditionsRepository;

    @Autowired
    private ClientApplicationDetailsDtoService clientApplicationDetailsDtoService;

    @Autowired
    @Qualifier("customerDataManagementService")
    private CustomerDataManagementIntegrationService customerDataManagementIntegrationService;

    private IntegrationUtil util;

    @Override
    public ClientApplicationApprovalDto submit(ClientApplicationApprovalDto dto, ServiceErrors serviceErrors) {
        LOGGER.info("{}begin", ONBOARDING_APPROVAL);
        final OnboardingApplicationKey applicationKey = dto.getKey();
        LOGGER.info("{}keyToFindID={}", ONBOARDING_APPROVAL, applicationKey);
        final OnBoardingApplication onBoardingApplication = onboardingApplicationRepository.find(applicationKey);

        final String gcmId = profileService.getGcmId();
        final String avaloqOrderId = onBoardingApplication.getAvaloqOrderId();
        LOGGER.info("{}gcmId={},avaloqOrderId={}", ONBOARDING_APPROVAL, gcmId, avaloqOrderId);
        final AccountActivationRequest request = new AccountActivationRequestImpl(gcmId, avaloqOrderId, null);
        final UserProfile activeProfile = profileService.getActiveProfile();
        LOGGER.info("{}effectiveprofile={}", ONBOARDING_APPROVAL, profileService.getEffectiveProfile());
        if(onBoardingApplication.getApplicationType().equalsIgnoreCase(String.valueOf(IClientApplicationForm.AccountType.INDIVIDUAL))) {
            CISKey cisKey = profileService.getEffectiveProfile().getToken().getCISKey();
            LOGGER.info("{}cisKey={}", ONBOARDING_APPROVAL, cisKey);
            updateInvestorLinkedAccountDetails(cisKey, request, serviceErrors);
        }

        final Boolean open = activationService.submitAccountActivation(request, serviceErrors);
        LOGGER.info("{}open={}", ONBOARDING_APPROVAL, open);

        if (open) {
            final Collection<Broker> advisers = brokerHelperService.getAdviserListForInvestor(activeProfile, serviceErrors);
            LOGGER.info("{}gcmId={},onBoardingApplicationKeyID={},adviserKeys={}", ONBOARDING_APPROVAL, gcmId, applicationKey.getId(),
                brokerIds(advisers));
            if (!advisers.isEmpty()) {
                final ClientApplication clientApplication = clientApplicationRepository
                    .findByOnboardingApplicationKey(applicationKey, advisers);
                clientApplication.markActive();
            } else {
                LOGGER.warn("No advisers found for customer {} [CIS:{}]", activeProfile.getBankReferenceId(),
                    activeProfile.getCISKey().getId());
            }
            cacheManagedAccountIntegrationService.clearAccountListCache();
            cacheManagedAccountIntegrationService.clearOnlineAccountListCache();
            cacheManagedAccountIntegrationService.clearContainerListCache();
            cacheClientIntegrationService.clearClientListCache();
        }

        updateInvestorTncDetails(activeProfile);
        LOGGER.info("{}end", ONBOARDING_APPROVAL);
        return new ClientApplicationApprovalDto(applicationKey, open);
    }

    private void updateInvestorLinkedAccountDetails(CISKey cisKey, AccountActivationRequest request, ServiceErrors serviceErrors) {
        ClientApplicationDetailsDto clientApplicationDetailsDto = clientApplicationDetailsDtoService.findOne(serviceErrors);
        final List<LinkedAccountDto> linkedAccountList = clientApplicationDetailsDto.getLinkedAccounts();
        final List<BankAccount> bankAccountList = IntegrationUtil.getBankAccountList(cisKey, customerDataManagementIntegrationService, serviceErrors);
        List<LinkedAccountStatus> linkedAccountStatusList = new ArrayList<>();
        LOGGER.info("{}bankAccountList={}", ONBOARDING_APPROVAL, bankAccountList);
        LOGGER.info("{}linkedAccountList={}", ONBOARDING_APPROVAL, linkedAccountList);
        if(null!= linkedAccountList && !linkedAccountList.isEmpty() && null != bankAccountList && !bankAccountList.isEmpty()) {
            LOGGER.info("{}bankAccountList size={}", ONBOARDING_APPROVAL, bankAccountList.size());
            LOGGER.info("{}linkedAccountList size={}", ONBOARDING_APPROVAL, linkedAccountList.size());
            for (LinkedAccountDto linkedAccount : linkedAccountList) {
                for (BankAccount bankAccount : bankAccountList) {
                    linkedAccount.setBsb(linkedAccount.getBsb().contains("-")?linkedAccount.getBsb().replace("-",""):linkedAccount.getBsb());
                    if (linkedAccount.getBsb().equalsIgnoreCase(bankAccount.getBsb()) && linkedAccount.getAccountNumber().equalsIgnoreCase(bankAccount.getAccountNumber())) {
                        LinkedAccountStatus linkedAccountStatus = new LinkedAccountStatus();
                        linkedAccountStatus.setAccountNumber(linkedAccount.getAccountNumber());
                        linkedAccountStatus.setBsb(linkedAccount.getBsb());
                        linkedAccountStatus.setVerificationRequired(false); //TODO: update this as per avaloq object
                        linkedAccountStatusList.add(linkedAccountStatus);
                    }
                }
            }
        }
        LOGGER.info("{}linkedAccountStatusList size={}", ONBOARDING_APPROVAL, linkedAccountStatusList.size());
        request.setLinkedAccounts(linkedAccountStatusList);
    }

    private void updateInvestorTncDetails(UserProfile activeProfile) {
        UserRoleTermsAndConditionsKey userRoleKey = new UserRoleTermsAndConditionsKey(activeProfile.getBankReferenceId(),
            activeProfile.getProfileId());
        UserRoleTermsAndConditions user = userRoleTermsAndConditionsRepository.find(userRoleKey);

        if (null == user) {
            LOGGER.info("{}user T&Cs not found, creating new user record.", ONBOARDING_APPROVAL);
            user = new UserRoleTermsAndConditions();
            user.setUserRoleTermsAndConditionsKey(userRoleKey);
            user.setVersion(1);
        }
        user.setTncAccepted("Y");
        user.setTncAcceptedOn(new Date());
        user.setModifyDatetime(new Date());
        userRoleTermsAndConditionsRepository.save(user);
    }

    private static Object brokerIds(final Collection<? extends BrokerIdentifier> brokers) {
        return new Object() {
            @Override
            public String toString() {
                final StringBuilder builder = new StringBuilder();
                boolean first = true;
                builder.append("[");
                for (BrokerIdentifier broker : brokers) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append(",");
                    }
                    builder.append(broker.getKey().getId());
                }
                return builder.append("]").toString();
            }
        };
    }
}
