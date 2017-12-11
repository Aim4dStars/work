package com.bt.nextgen.serviceops.service;

import com.bt.nextgen.api.draftaccount.service.SendEmailService;
import com.bt.nextgen.api.tracking.service.InvestorStatusServiceForTechnicalSupport;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyStatus;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.StatusTypeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
public class ResendRegistrationEmailServiceImpl implements ResendRegistrationEmailService {

    @Autowired
    private InvestorStatusServiceForTechnicalSupport investorStatusServiceForTechnicalSupport;

    @Autowired
    private SendEmailService sendEmailService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    private static final Logger logger = LoggerFactory.getLogger(ResendRegistrationEmailServiceImpl.class);

    /**
     * This method will call the SendEmailService submit method to resend the registration mail for Investor and Update the Onboarding Party
     * Status and Communication Status if returns success message. gcmId - gcmId of investor
     *
     * @return SUCCESS, FAILURE as per response from service.
     */
    @Override
    public String resendRegistrationEmailForInvestor(String clientId, final String gcmId, final String role, ServiceErrors serviceErrors) {
        return resendRegistrationEmailForInvestor(clientId, gcmId, role, true, serviceErrors);
    }

    private String resendRegistrationEmailForInvestor(String clientId, final String gcmId, final String role, boolean shouldSendNewCode, ServiceErrors serviceErrors) {
        logger.info("Resending Registration Mail for Investor {}", clientId);
        String status = Attribute.ERROR_MESSAGE;
        OnboardingParty onboardingParty = investorStatusServiceForTechnicalSupport.getInvestorOnboardingPartyDetails(gcmId);
        Broker adviser = brokerHelperService.getAdviserForInvestor(getBankingCustomerIdentifier(gcmId), serviceErrors);

        if (onboardingParty != null) {
            if (OnboardingPartyStatus.ExistingPanoramaOnlineUser.equals(onboardingParty.getStatus())) {
                logger.info("client : {} is an existing online user, so cant' perform Resend Registration operation", gcmId);
                serviceErrors.addError(new ServiceErrorImpl("client : {} is an existing online user, so cant' perform Resend Registration operation", gcmId));
            } else {
                status = resendRegistrationEmailForNewUser(gcmId, onboardingParty, adviser, clientId, role, shouldSendNewCode, serviceErrors);
            }
        } else {
            logger.info("Onboarding Party details not exist for client  : {}", gcmId);

            String response;
            if (shouldSendNewCode) {
                response = sendEmailService.sendEmailFromServiceOpsDesktopForInvestor(clientId, adviser.getKey().getId(), role, serviceErrors);
            } else {
                response = sendEmailService.sendEmailWithExistingRegoCodeForInvestor(clientId, adviser.getKey().getId(), role, serviceErrors);
            }

            if (StatusTypeCode.SUCCESS.value().equals(response)) {
                status = Attribute.SUCCESS_MESSAGE;
            }
        }
        return status;
    }

    private String resendRegistrationEmailForNewUser(String gcmId, OnboardingParty onboardingParty, Broker adviser, String clientId,
                                                     String role, boolean shouldSendNewCode, ServiceErrors serviceErrors) {
        String status = Attribute.ERROR_MESSAGE;
        Collection<BrokerIdentifier> adviserIds = Arrays.asList((BrokerIdentifier) adviser);
        ClientApplication clientApplication = investorStatusServiceForTechnicalSupport.getClientApplicationDetailsForOnboardingApplicationId(
                OnboardingApplicationKey.valueOf(onboardingParty.getOnboardingApplicationId()), adviserIds);
        if (clientApplication != null) {

            String resultStatus;
            if (shouldSendNewCode) {
                resultStatus = sendEmailService.sendEmailFromServiceOpsDesktopForInvestor(clientId, adviser.getKey().getId(), role, serviceErrors);
            } else {
                resultStatus = sendEmailService.sendEmailWithExistingRegoCodeForInvestor(clientId, adviser.getKey().getId(), role, serviceErrors);
            }

            if (StatusTypeCode.SUCCESS.value().equals(resultStatus)) {
                investorStatusServiceForTechnicalSupport.updatePartyStatusWhenResendRegistrationCodeSuccess(onboardingParty);
                status = Attribute.SUCCESS_MESSAGE;
            }

        } else {
            logger.error("Not able to fetch client application details for client  : {}", gcmId);
            serviceErrors.addError(new ServiceErrorImpl("Not able to fetch Onboarding Party details for client  : {}", gcmId));
        }
        return status;
    }

    @Override
    public String resendRegistrationEmailWithExistingCodeForInvestor(String clientId, String gcmId, String role, ServiceErrors serviceErrors) {
        return resendRegistrationEmailForInvestor(clientId, gcmId, role, false, serviceErrors);
    }

    @Override
    public String resendRegistrationEmailWithExistingCodeForAdviser(String clientId, String gcmId, String role, ServiceErrors serviceErrors) {
        logger.info("Resending Existing Registration Code for Adviser {}", clientId);
        String status = sendEmailService.sendEmailWithExistingRegoCodeForAdviser(gcmId, role, serviceErrors);
        return status;
    }

    private BankingCustomerIdentifier getBankingCustomerIdentifier(final String gcmId) {
        return new BankingCustomerIdentifier() {
            @Override
            public String getBankReferenceId() {
                return gcmId;
            }

            @Override
            public UserKey getBankReferenceKey() {
                return UserKey.valueOf(gcmId);
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }
        };
    }

    /**
     * This method will call the service to resend the registration code for adviser gcmId - gcmId of adviser
     *
     * @return SUCCESS, FAILURE as per response from service.
     */
    @Override
    public String resendRegistrationEmailForAdviser(String clientId, String gcmId, String role, ServiceErrors serviceErrors) {
        logger.info("Resending Registration Mail for Adviser {}", clientId);
        String status = sendEmailService.sendEmailFromServiceOpsDesktopForAdviser(gcmId, role, serviceErrors);
        if (StatusTypeCode.SUCCESS.value().equals(status)) {
            return Attribute.SUCCESS_MESSAGE;
        }
        return status;
    }
}
