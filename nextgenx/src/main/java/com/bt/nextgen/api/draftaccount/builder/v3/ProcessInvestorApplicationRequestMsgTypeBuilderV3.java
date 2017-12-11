package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.builder.ProcessInvestorApplicationRequestMsgTypeBuilder;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import ns.btfin_com.product.common.investmentaccount.v2_0.AdviceTypeType;
import ns.btfin_com.product.common.investmentaccount.v2_0.OwnershipTypeType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ApplicationType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.InvestmentAccountType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBApplicationApprovalType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBApplicationOriginType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OBApplicationTypeType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.ProcessInvestorApplicationRequestMsgType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.ProcessInvestorApplicationErrorResponseType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.ProcessInvestorApplicationResponseMsgType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.btfin.panorama.onboarding.helper.InvestmentAccountHelper.investmentAccount;
import static ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.StatusTypeCode.SUCCESS;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Builds on-boarding SOAP requests using version 3.0 of the BT ESB Panorama WSDL.
 */
@SuppressWarnings("squid:S1200")
@Service
public class ProcessInvestorApplicationRequestMsgTypeBuilderV3 implements ProcessInvestorApplicationRequestMsgTypeBuilder<ProcessInvestorApplicationRequestMsgType, ProcessInvestorApplicationResponseMsgType, ProcessInvestorApplicationErrorResponseType> {

    private static final Logger LOGGER = getLogger(ProcessInvestorApplicationRequestMsgTypeBuilderV3.class);

    @Autowired
    private InvestorsTypeBuilder investorsTypeBuilder;

    @Autowired
    private AdvisersTypeBuilder advisersTypeBuilder;

    @Autowired
    private CashAccountsBuilder cashAccountsBuilder;

    @Autowired
    private PaymentInstructionsBuilder paymentInstructionsBuilder;

    @Autowired
    private InvestmentProductTypeBuilder investmentProductTypeBuilder;

    @Autowired
    private AddressV2CacheService addressV2CacheService;

    @Autowired
    private FeatureTogglesService togglesService;

    /**
     * Map between {@code IClientApplicationForm.AccountType} and {@code OBApplicationTypeType}.
     *
     * @param accountType account type from the form.
     * @return corresponding account type to set in the BT ESB message.
     */
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1142"})
    private static OBApplicationTypeType applicationType(IClientApplicationForm.AccountType accountType) {
        OBApplicationTypeType result = null;
        switch (accountType) {
            case INDIVIDUAL:
                result = OBApplicationTypeType.INDIVIDUAL;
                break;
            case COMPANY:
                result = OBApplicationTypeType.COMPANY;
                break;
            case JOINT:
                result = OBApplicationTypeType.JOINT;
                break;
            case INDIVIDUAL_SMSF:
                result = OBApplicationTypeType.INDIVIDUAL_SMSF;
                break;
            case NEW_INDIVIDUAL_SMSF:
                result = OBApplicationTypeType.INDIVIDUAL_NEW_SMSF;
                break;
            case CORPORATE_SMSF:
                result = OBApplicationTypeType.CORPORATE_SMSF;
                break;
            case CORPORATE_TRUST:
                result = OBApplicationTypeType.CORPORATE_TRUST;
                break;
            case INDIVIDUAL_TRUST:
                result = OBApplicationTypeType.INDIVIDUAL_TRUST;
                break;
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
                result = OBApplicationTypeType.SUPERANNUATION;
                break;
            case NEW_CORPORATE_SMSF:
                result = OBApplicationTypeType.CORPORATE_NEW_SMSF;
                break;
            default:
                throw new UnsupportedOperationException("Invalid Account Type : " + accountType);
        }
        return result;
    }

    @Override
    public boolean isSuccessful(ProcessInvestorApplicationResponseMsgType response) {
        return SUCCESS == response.getStatus();
    }

    @Override
    public List<ProcessInvestorApplicationErrorResponseType> getErrorResponses(ProcessInvestorApplicationResponseMsgType response) {
        return response.getResponseDetails().getErrorResponses().getErrorResponse();
    }

    @Override
    public ProcessInvestorApplicationRequestMsgType buildFromForm(IClientApplicationForm clientApplicationForm,
                                                                  BrokerUser brokerUser, OnboardingApplicationKey key,
                                                                  String productId, Broker dealer, ServiceErrors serviceErrors) {
        ApplicationType application = getApplication(clientApplicationForm, brokerUser, productId, dealer, serviceErrors);
        application.setSubmissionID(key.getId().toString());
        application.setApplicationType(applicationType(clientApplicationForm.getAccountType()));
        application.setApplicationOrigin(applicationOrigin(clientApplicationForm.getApplicationOrigin()));
        application.setApplicationApprovalType(applicationApprovalType(clientApplicationForm.getApplicationApprovalType()));
        ProcessInvestorApplicationRequestMsgType processInvestorsRequestMsgType = new ProcessInvestorApplicationRequestMsgType();
        processInvestorsRequestMsgType.setApplication(application);
        addressV2CacheService.clearMap();
        return processInvestorsRequestMsgType;
    }

    private OBApplicationApprovalType applicationApprovalType(IClientApplicationForm.ApprovalType applicationApprovalType) {
        return IClientApplicationForm.ApprovalType.OFFLINE.equals(applicationApprovalType) ?
            OBApplicationApprovalType.OFFLINE : OBApplicationApprovalType.ONLINE;
    }

    private OBApplicationOriginType applicationOrigin(String applicationOrigin) {
        switch (applicationOrigin) {
            case FormDataConstants.VALUE_APPLICATION_ORIGIN_DIRECT:
                return OBApplicationOriginType.WESTPAC_LIVE;
            case FormDataConstants.VALUE_APPLICATION_ORIGIN_ADVISED:
                return OBApplicationOriginType.BT_PANORAMA;
            default:
                throw new UnsupportedOperationException("Application origin type " + applicationOrigin + " is not implemented yet.");
        }
    }

    private ApplicationType getApplication(IClientApplicationForm clientApplicationForm, BrokerUser brokerUser,
                                           String productId, Broker dealer, ServiceErrors serviceErrors) {
        ApplicationType applicationType = new ApplicationType();
        final InvestmentAccountType investmentAccountType = getInvestmentAccountType(clientApplicationForm, brokerUser, dealer,
            productId, serviceErrors);
        if (investmentAccountType.getInvestmentProduct() != null) {
            applicationType.setPaymentInstructions(paymentInstructionsBuilder.getPaymentInstructionsForInvestmentAccount(clientApplicationForm));
        } else if(!clientApplicationForm.getLinkedAccounts().isEmpty()){
            applicationType.setPaymentInstructions(paymentInstructionsBuilder.getPaymentInstructions(clientApplicationForm.getLinkedAccounts()));

        }
        applicationType.setInvestmentAccount(investmentAccountType);
        return applicationType;
    }

    private InvestmentAccountType getInvestmentAccountType(IClientApplicationForm clientApplicationForm,
                                                           BrokerUser adviser, Broker dealer, String productId, ServiceErrors serviceErrors) {
        InvestmentAccountType account = investmentAccount("0", productId, getOwnershipTypeType(clientApplicationForm.getAccountType()),
            adviceType(clientApplicationForm.getAdviceType()));
        account.setInvestors(investorsTypeBuilder.getInvestorsType(clientApplicationForm, adviser, dealer, serviceErrors));
        account.setAdvisers(advisersTypeBuilder.getAdvisersType(clientApplicationForm, adviser));
        if(!clientApplicationForm.getLinkedAccounts().isEmpty()) {
            account.setCashAccounts(cashAccountsBuilder.getCashAccounts(clientApplicationForm.getLinkedAccounts()));
        }
        account.setInvestmentProduct(investmentProductTypeBuilder.getAccountInvestmentProductType(clientApplicationForm, dealer));
        account.setAccountPurpose(AccountPurposeBuilder.getAccountPurpose(clientApplicationForm));

        //CMA submit
        final FeatureToggles toggles = togglesService.findOne(new FailFastErrorsImpl());
        if (toggles.getFeatureToggle("onboardingCMA")) {
            account.setAccountProperties(AccountPropertiesTypeBuilder.getAccountPropertyListType(clientApplicationForm));
        }
        return account;
    }

    private AdviceTypeType adviceType(String adviceType) {
        switch (adviceType) {
            case "PersonalAdvice":
                return AdviceTypeType.PERSONAL_ADVICE;
            case "FactualInformation":
                return AdviceTypeType.FACTUAL_INFORMATION;
            case "GeneralAdvice":
                return AdviceTypeType.GENERAL_ADVICE;
            case "NoAdvice":
                return AdviceTypeType.NO_ADVICE;
            default:
                throw new UnsupportedOperationException("Advice type " + adviceType + " is not implemented yet.");
        }
    }

    @SuppressFBWarnings(
        value = "squid:RightCurlyBraceStartLineCheck",
        justification = "Simply maps from one enum to another. No easy way to re-write"
    )
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1142"})
    private OwnershipTypeType getOwnershipTypeType(IClientApplicationForm.AccountType accountType) {
        OwnershipTypeType result = null;
        switch (accountType) {
            case JOINT:
                result = OwnershipTypeType.JOINT_ACCOUNT;
                break;
            case INDIVIDUAL:
            case SUPER_ACCUMULATION:
            case SUPER_PENSION:
                result = OwnershipTypeType.SINGLE_OWNER_ACCOUNT;
                break;
            case INDIVIDUAL_SMSF:
            case NEW_INDIVIDUAL_SMSF:
            case CORPORATE_SMSF:
            case NEW_CORPORATE_SMSF:
            case CORPORATE_TRUST:
            case INDIVIDUAL_TRUST:
                result = OwnershipTypeType.TRUST;
                break;
            case COMPANY:
                result = OwnershipTypeType.CORPORATION;
                break;
            default:
                throw new UnsupportedOperationException("Account type " + accountType + " is not implemented yet.");
        }
        return result;
    }
}
