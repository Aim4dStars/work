package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.api.draftaccount.model.FundEstablishmentDto;
import com.bt.nextgen.api.draftaccount.model.TransitionStateDto;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.util.ReferenceNumberFormatter;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingAccount;
import com.bt.nextgen.core.repository.OnboardingAccountRepository;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.type.DateFormatType;
import com.bt.nextgen.core.type.DateUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.btfin.panorama.service.avaloq.broker.BrokerIdentifierImpl;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationIdentifierImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.ApplicationIdentifier;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;


@Service
@Transactional(value = "springJpaTransactionManager")
public class ClientApplicationDetailsDtoServiceImpl implements ClientApplicationDetailsDtoService {

    @Autowired
    private ClientApplicationRepository clientApplicationRepositoryWithoutPermissions;

    @Autowired
    private PermittedClientApplicationRepository permittedClientApplicationRepository;

    @Autowired
    private OnboardingAccountRepository onboardingAccountRepository;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private AccountsPendingApprovalService accountsPendingApprovalService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private PdsService pdsService;

    @Autowired
    private ClientApplicationDetailsDtoConverterService clientApplicationDetailsDtoConverterService;

    @Autowired
    private AccActivationIntegrationService accActivationIntegrationService;

    @Autowired
    private ViewClientApplicationDetailsService viewClientApplicationDetailsService;

    @Autowired
    private UserProfileService userProfileService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplicationDetailsDtoServiceImpl.class);

    @Override
    public ClientApplicationDetailsDto findByAccountNumber(String accountNumber, ServiceErrors serviceErrors) {
        return getClientApplicationDetails(Arrays.asList(accountNumber), serviceErrors);
    }

    @Override
    public ClientApplicationDetailsDto findOne(ServiceErrors serviceErrors) {
        List<WrapAccount> accounts = getWrapAccountForCurrentUser(serviceErrors);
        List<String> accountNumbers = null;
        accountNumbers = Lambda.convert(accounts, new Converter<WrapAccount, String>() {
            @Override
            public String convert(WrapAccount account) {
                return account.getAccountNumber();
            }
        });
        return getClientApplicationDetails(accountNumbers, serviceErrors);
    }

    @Override
    public ClientApplicationDetailsDto findByClientApplicationId(Long clientApplicationId, ServiceErrors serviceErrors) {
        // Using permittedClientApplicationRepository since this call is invoked from tracking page.
        ClientApplication clientApplication = permittedClientApplicationRepository.find(clientApplicationId);
        if (clientApplication != null && clientApplication.getOnboardingApplication() != null) {
            OnBoardingApplication onboardingApplication = clientApplication.getOnboardingApplication();
            OnboardingAccount onboardingAccount = getOnboardingAccount(onboardingApplication.getKey());

            if (onboardingAccount != null && !Strings.isNullOrEmpty(onboardingAccount.getAccountNumber())) {
                LOGGER.info(
                    "Retrieving application details from avaloq with ClientApplication Id : {}, OnboardingApplicationId : {}, Account number : {}  ",
                    clientApplicationId, onboardingApplication.getKey(), onboardingAccount.getAccountNumber());
                final String accountNumber = onboardingAccount.getAccountNumber();
                return getClientApplicationDetails(accountNumber,clientApplication,serviceErrors);
            }
            return clientApplicationDetailsDtoConverterService.convert(clientApplication, serviceErrors);
        }
        return null;
    }

    private List<WrapAccount> getWrapAccountForCurrentUser(ServiceErrors serviceErrors) {
        List<WrapAccount> usersAccounts = accountsPendingApprovalService.getUserAccountsPendingApprovals(serviceErrors);
        if (usersAccounts.isEmpty()) {
            serviceErrors.addError(new ServiceErrorImpl("There is no pending account"));
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, "Unable to find pending applications");
        }
        return usersAccounts;
    }

    private String getPdsValue(BrokerKey adviserPositionId, String productId, ServiceErrors serviceErrors) {
        ProductKey productKey = ProductKey.valueOf(productId);
        return pdsService.getUrl(productKey, adviserPositionId, serviceErrors);
    }

    private TransitionStateDto getTransitionDto(String state, Date date) {
        return new TransitionStateDto(state, DateUtil.toFormattedDate(date, DateFormatType.DATEFORMAT_FRONT_END));
    }

    private TransitionStateDto getTransitionDtoForText(String state, String text) {
        return new TransitionStateDto(state, text);
    }

    private FundEstablishmentDto setTransitionStates(List<ApplicationDocument> applicationDocuments,
                                                     FundEstablishmentDto fundEstablishmentDto) {
        List<TransitionStateDto> fundestablishmentStates = new ArrayList<TransitionStateDto>();

        if (!applicationDocuments.isEmpty()) {
            ApplicationDocument applicationDocument = applicationDocuments.get(0);
            fundestablishmentStates.add(getTransitionDto("Submit application", applicationDocument.getAppSubmitDate()));
            fundestablishmentStates.add(getTransitionDto("Approve application", applicationDocument.getSignedDate()));

            if (IClientApplicationForm.AccountType.NEW_CORPORATE_SMSF.equals(fundEstablishmentDto.getAccountType())) {
                fundEstablishmentDto.setCompanyRegisteredName(applicationDocument.getAsicRegisteredName());
                fundEstablishmentDto.setCompanyACN(applicationDocument.getCompanyACN());
                fundestablishmentStates.add(getTransitionDto("Set up Corporate Trustee", applicationDocument.getCompanySetupDate()));
            }

            if (applicationDocument.getValidatedDate() != null) {
                fundestablishmentStates.add(getTransitionDto("Upload and validate documents", applicationDocument.getValidatedDate()));
                fundestablishmentStates.add(getTransitionDtoForText("Establish SMSF with ATO", "In progress"));
            } else {
                fundestablishmentStates.add(getTransitionDtoForText("Upload and validate documents", "In progress"));
                fundestablishmentStates.add(getTransitionDtoForText("Establish SMSF with ATO", ""));
            }
            fundEstablishmentDto.setStatus(getFundEstablishmentStatus(applicationDocument.getAppState()));
            fundEstablishmentDto.setFundestablishmentStates(fundestablishmentStates);
        }
        return fundEstablishmentDto;
    }

    @Override
    public FundEstablishmentDto findFundEstablishmentStatusByClientApplicationId(Long clientApplicationId, String accountNumber,
                                                                                 ServiceErrors serviceErrors) {
        ClientApplication clientApplication = permittedClientApplicationRepository.find(clientApplicationId);
        final IClientApplicationForm form = clientApplication.getClientApplicationForm();
        final OnBoardingApplication onboardingApplication = clientApplication.getOnboardingApplication();

        List<ApplicationIdentifier> identifierList = new ArrayList<>();
        ApplicationIdentifier appId = new ApplicationIdentifierImpl();
        appId.setDocId(onboardingApplication.getAvaloqOrderId());
        identifierList.add(appId);

        FundEstablishmentDto fundEstablishmentDto = new FundEstablishmentDto();
        fundEstablishmentDto.setAccountName(form.getAccountName());
        fundEstablishmentDto.setAccountType(form.getAccountType());
        fundEstablishmentDto.setAdviserName(form.getAccountSettings().getAdviserName());
        fundEstablishmentDto.setClientAppId(String.format("R%09d", clientApplicationId));

        // Modified the Service call, as BTFG$UI_DOC_CUSTR_LIST.BP#DOC is deemed to be faster than BTFG$UI_DOC_CUSTR_LIST.DOC#DOC

        WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
        wrapAccountIdentifier.setBpId(new EncodedString(accountNumber).plainText());
        UserProfile activeProfile = userProfileService.getActiveProfile();
        List<ApplicationDocument> applicationDocuments = accActivationIntegrationService
            .loadAccApplicationForPortfolio(Arrays.asList(wrapAccountIdentifier), activeProfile.getJobRole(),activeProfile.getClientKey(),serviceErrors);

        setTransitionStates(applicationDocuments, fundEstablishmentDto);

        return fundEstablishmentDto;
    }

    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1142"})
    private String getFundEstablishmentStatus(ApplicationStatus status) {
        String fundStatus;
        switch (status) {
            case AWAITING_DOCUMENTS:
            case DOCUMENTS_RECIEVED:
            case DOCUMENTS_VERIFICATION:
                fundStatus = "DocumentVerificationInProgress";
                break;
            case HELD_READY_FOR_ABR:
            case READY_FOR_ABR:
            case HELD_ABR_VERIFICATION:
            case ABR_VERIFICATION:
            case HELD_ABR_SUBMITTED:
                fundStatus = "ATOInProgress";
                break;
            case ASIC_REGISTRATION:
            case ASIC_SUBMISSION:
                fundStatus = "CompanySetupInProgress";
                break;
            case ABR_SUBMITTED:
                fundStatus = "Completed";
                break;
            default:
                fundStatus = "Error";
        }
        return fundStatus;
    }

    private OnboardingAccount getOnboardingAccount(OnboardingApplicationKey key) {
        try {
            return onboardingAccountRepository.findByOnboardingApplicationId(key);
        } catch (NoResultException ex) {
            return null;
        }
    }

    private ClientApplicationDetailsDto getClientApplicationDetails(String accountNumber,ClientApplication clientApplication, ServiceErrors serviceErrors) {
        LOGGER.info("Populating ClientApplicationDetailsDto");
        ClientApplicationDetailsDto clientApplicationDetailsDto = viewClientApplicationDetailsService
            .viewClientApplicationByAccountNumber(accountNumber, serviceErrors);
        if(null != clientApplication.getClientApplicationForm().getShareholderAndMembers()){
         clientApplicationDetailsDto.withMajorShareHolderFlag(clientApplication.getClientApplicationForm().getShareholderAndMembers().getMajorShareholder());
        }
        updatePdsAndReferenceNumber(clientApplication, serviceErrors, clientApplicationDetailsDto);
        return clientApplicationDetailsDto;
    }

    private ClientApplicationDetailsDto getClientApplicationDetails(List<String> accountNumbers, ServiceErrors serviceErrors) {
        LOGGER.info("Populating ClientApplicationDetailsDto");
        ClientApplicationDetailsDto clientApplicationDetailsDto;
        if(accountNumbers.size() == 1) {
            clientApplicationDetailsDto = viewClientApplicationDetailsService.viewClientApplicationByAccountNumber(accountNumbers.get(0), serviceErrors);
        } else {
            clientApplicationDetailsDto = viewClientApplicationDetailsService.viewOnlineClientApplicationByAccountNumbers(accountNumbers, serviceErrors);
        }
        assertApplicationIsNotDiscarded(clientApplicationDetailsDto, serviceErrors);
        ClientApplication clientApplication = getClientApplicationWithoutPermissions(clientApplicationDetailsDto.getAccountKey(), clientApplicationDetailsDto);
        // required during application approval
        OnboardingApplicationKey onboardingApplicationKey = clientApplication.getOnboardingApplication().getKey();
        clientApplicationDetailsDto.withOnboardingApplicationKey(EncodedString.fromPlainText(onboardingApplicationKey.getId().toString()).toString());
        updatePdsAndReferenceNumber(clientApplication, serviceErrors, clientApplicationDetailsDto);
        return clientApplicationDetailsDto;
    }

    private void assertApplicationIsNotDiscarded(ClientApplicationDetailsDto clientApplicationDetailsDto, ServiceErrors serviceErrors) {
        if(AccountStatus.DISCARD.getStatus().equals(clientApplicationDetailsDto.getAccountAvaloqStatus())) {
            serviceErrors.addError(new ServiceErrorImpl("There is no pending application. Application is discarded"));
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, "Unable to find pending applications. Application is discarded");
        }
    }

    private void updatePdsAndReferenceNumber(ClientApplication clientApplication, ServiceErrors serviceErrors,
                                             ClientApplicationDetailsDto clientApplicationDetailsDto) {
        String pdsUrl = getPdsValue(BrokerKey.valueOf(clientApplication.getAdviserPositionId()), clientApplication.getProductId(),
            serviceErrors);
        clientApplicationDetailsDto.withReferenceNumber(ReferenceNumberFormatter.formatReferenceNumber(clientApplication.getId()));
        clientApplicationDetailsDto.withPdsUrl(pdsUrl);
    }

    private ClientApplication getClientApplicationWithoutPermissions(String accountNumber,
                                                                     ClientApplicationDetailsDto clientApplicationDetailsDto) {
        OnboardingApplicationKey onboardingApplicationKey = getOnboardingApplicationKey(accountNumber);
        return clientApplicationRepositoryWithoutPermissions.findByOnboardingApplicationKey(onboardingApplicationKey,
            singletonList(getBrokerIdentifier(clientApplicationDetailsDto.getAdviser().getKey().getBrokerId())));
    }

    private BrokerIdentifier getBrokerIdentifier(String brokerId) {
        return new BrokerIdentifierImpl(EncodedString.toPlainText(brokerId));
    }

    private OnboardingApplicationKey getOnboardingApplicationKey(String accountNumber) {
        OnboardingAccount onboardingAccount = onboardingAccountRepository.findByAccountNumber(accountNumber);
        return onboardingAccount.getOnboardingApplicationKey();
    }
}
