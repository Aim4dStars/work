package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.builder.CreateOneTimePasswordSendEmailRequestMsgTypeBuilder;
import com.bt.nextgen.api.draftaccount.model.SendEmailDto;
import com.bt.nextgen.api.draftaccount.util.ResendRegistrationCodeTransactor;
import com.bt.nextgen.api.draftaccount.util.ServiceErrorsUtil;
import com.bt.nextgen.core.repository.OnboardingCommunication;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.DateTimeService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import ns.btfin_com.sharedservices.integration.common.response.errorresponsetype.v3.ErrorResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.Date;

import static com.bt.nextgen.api.draftaccount.LoggingConstants.ONBOARDING_SEND;
import static com.bt.nextgen.web.controller.cash.util.Attribute.RESEND_EXISTING_REGISTRATION_CODE_KEY;
import static com.bt.nextgen.web.controller.cash.util.Attribute.RESEND_REGISTRATION_ONBOARDING_KEY;
import static java.util.Collections.singletonList;

@Service
public class SendEmailServiceImpl implements SendEmailService {

    private static final String SUCCESS = "Success";

    private static final String ERROR = "Error";

    @Autowired
    @Qualifier("createOneTimePasswordSendEmailRequestMsgTypeBuilder")
    private CreateOneTimePasswordSendEmailRequestMsgTypeBuilder createOneTimePasswordSendEmailRequestMsgTypeBuilder;

    @Autowired
    @Qualifier("resendRegistrationCodeSendEmailRequestMsgTypeBuilder")
    private CreateOneTimePasswordSendEmailRequestMsgTypeBuilder resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder;

    @Autowired
    private WebServiceProvider provider;

    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    @Autowired
    private PermittedClientApplicationRepository permittedClientApplicationRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ResendRegistrationCodeTransactor resendRegistrationCodeTransactor;

    @Autowired
    private DateTimeService dateTimeService;

    @Autowired
    private ClientApplicationRepository clientApplicationRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailServiceImpl.class);

    @Override
    public SendEmailDto submit(SendEmailDto sendEmailDto, ServiceErrors serviceErrors) {
        LOGGER.info(ONBOARDING_SEND + "begin");
        Long clientApplicationId = sendEmailDto.getClientApplicationId();
        Collection<BrokerIdentifier> adviserIds = sendEmailDto.getAdviserIds();
        LOGGER.info(ONBOARDING_SEND + "clientApplicationId=" + clientApplicationId + ",adviserIds=" + adviserIds);
        ClientApplication clientApplication = retrieveClientApplication(clientApplicationId,
                adviserIds);
        Object request = buildResendExistingRegistrationCodeSendEmailRequestMsg(sendEmailDto.getKey().getId(),
            clientApplication.getAdviserPositionId(), sendEmailDto.getRole(), serviceErrors);
        CorrelatedResponse correlatedResponse = resendRegistrationCodeSendEmailRequestMsg(request, serviceErrors);
        String statusToSend = parseCorrelatedResponse(request, correlatedResponse, getOnboardingApplicationId(clientApplication), serviceErrors);
        sendEmailDto.setStatus(statusToSend);
        LOGGER.info(ONBOARDING_SEND + "statusToSend=" + statusToSend);
        LOGGER.info(ONBOARDING_SEND + "end");
        return sendEmailDto;
    }

    private ClientApplication retrieveClientApplication(Long clientApplicationId, Collection<BrokerIdentifier> adviserIds) {
        if (adviserIds != null && !adviserIds.isEmpty()) {
            return clientApplicationRepository.find(clientApplicationId, adviserIds);
        } else {
            return permittedClientApplicationRepository.find(clientApplicationId);
        }

    }

    private Object buildCreateOneTimePasswordSendEmailRequestMsg(String clientId, String adviserPositionId, String role, ServiceErrors serviceErrors) {
        return createOneTimePasswordSendEmailRequestMsgTypeBuilder.build(clientId, adviserPositionId, role, serviceErrors);
    }
    
    private Object buildResendExistingRegistrationCodeSendEmailRequestMsg(String clientId, String adviserPositionId, String role, ServiceErrors serviceErrors) {
        return resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.build(clientId, adviserPositionId, role, serviceErrors);
    }

    private CorrelatedResponse sendCreateOneTimePasswordSendEmailRequestMsg(Object request, ServiceErrors serviceErrors) {
        JAXBElement createOneTimePasswordSendEmailRequestMsg = createOneTimePasswordSendEmailRequestMsgTypeBuilder.wrap(request);
        return provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(userSamlService.getSamlToken(), RESEND_REGISTRATION_ONBOARDING_KEY, createOneTimePasswordSendEmailRequestMsg, serviceErrors);
    }

    private CorrelatedResponse resendRegistrationCodeSendEmailRequestMsg(Object request, ServiceErrors serviceErrors) {
        JAXBElement resendRegistrationCodeSendEmailRequestMsg = resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.wrap(request);
        return provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(userSamlService.getSamlToken(), RESEND_EXISTING_REGISTRATION_CODE_KEY, resendRegistrationCodeSendEmailRequestMsg, serviceErrors);
    }
    
    private Long getOnboardingApplicationId(ClientApplication clientApplication) {
        return clientApplication.getOnboardingApplication().getKey().getId();
    }

    @SuppressWarnings("unchecked")
    private String parseCorrelatedResponse(Object request, CorrelatedResponse correlatedResponse, Long onboardingApplicationId, ServiceErrors serviceErrors) {
        final Object response = correlatedResponse.getResponseObject();
        final boolean success = resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.isSuccessful(response);
        if (success) {
            final String emailAddress = resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.extractInvestorEmailAddress(request);
            final Date communicationInitiationTime = correlatedResponse.getEsbHeaderInformationWrapper().getRequestCreationDate();
            return insertSuccessfulCommunication(response, onboardingApplicationId, emailAddress, communicationInitiationTime, serviceErrors);
        } else {
            ServiceErrorsUtil.updateResponseServiceErrors(serviceErrors, resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.getErrorResponses(response));
            return ERROR;
        }
    }

    private String insertSuccessfulCommunication(Object response, Long onboardingApplicationId, String emailAddress, Date communicationInitiationTime, ServiceErrors serviceErrors) {
        final OnboardingCommunication communication = createOnboardingCommunication(response, onboardingApplicationId, emailAddress, communicationInitiationTime);
        return insertOnboardingCommunication(communication, serviceErrors);
    }

    private String insertOnboardingCommunication(OnboardingCommunication communication, ServiceErrors serviceErrors) {
        try {
            resendRegistrationCodeTransactor.save(communication);
        } catch (EntityExistsException entityExistsException) {
            return updateServiceErrorWithPersistenceExceptionAndReturnStatus("Communication id already exists. Primary key violation in Onboarding_Communication table.", entityExistsException, serviceErrors);
        } catch (PersistenceException persistenceException) {
            return updateServiceErrorWithPersistenceExceptionAndReturnStatus("Error while inserting communication details.", persistenceException, serviceErrors);
        } catch (NullPointerException nullPointerException) {
            return updateServiceErrorWithPersistenceExceptionAndReturnStatus("Exception while creating onboarding communication object.", nullPointerException, serviceErrors);
        }
        return SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private OnboardingCommunication createOnboardingCommunication(Object response, Long onboardingApplicationId, String emailAddress, Date communicationInitiationTime) {
        final OnboardingCommunication communication = resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.extractCommunicationDetails(response);
        communication.setOnboardingApplicationId(onboardingApplicationId);
        communication.setEmailAddress(emailAddress);
        communication.setCommunicationInitiationTime(communicationInitiationTime);
        communication.setCreatedDate(dateTimeService.getCurrentDateTime().toDate());
        communication.setLastModifiedId(userProfileService.getGcmId());
        LOGGER.info(ONBOARDING_SEND + "createOnboardingCommunication communication= " + communication.toString());
        return communication;
    }

    private String updateServiceErrorWithPersistenceExceptionAndReturnStatus(String errorMessage, Exception e, ServiceErrors serviceErrors) {
        final ErrorResponseType errorResponse = new ErrorResponseType();
        errorResponse.setDescription(errorMessage + e.getMessage());
        ServiceErrorsUtil.updateResponseServiceErrors(serviceErrors, singletonList(errorResponse));
        return ERROR;
    }

    /**
     * Method used to call ICC SendEmail Service for Investor from service operator desktop.
     */
    @Override
    public String sendEmailFromServiceOpsDesktopForInvestor(String clientId, String adviserPositionId, String role, ServiceErrors serviceErrors) {
        LOGGER.info(ONBOARDING_SEND + "sendEmailFromServiceOpsDesktopForInvestor begin");
        LOGGER.info(ONBOARDING_SEND + "clientId=" + clientId + ", adviserPositionId=" + adviserPositionId);
        try {
            Object request = buildCreateOneTimePasswordSendEmailRequestMsg(clientId, adviserPositionId, role, serviceErrors);
            return getResponseStatusForCreateOneTimePasswordSendEmail(request, serviceErrors);
        } finally {
            LOGGER.info(ONBOARDING_SEND + "sendEmailFromServiceOpsDesktopForInvestor end");
        }
    }

    /**
     * Method used to call ICC SendEmail Service for Investor from service operator desktop.
     */
    @Override
    public String sendEmailFromServiceOpsDesktopForAdviser(String gcmId, String role, ServiceErrors serviceErrors) {
        LOGGER.info(ONBOARDING_SEND + "sendEmailFromServiceOpsDesktopForAdviser begin");
        LOGGER.info(ONBOARDING_SEND + "gcmId=" + gcmId);
        try {
            Object request = createOneTimePasswordSendEmailRequestMsgTypeBuilder.buildForAdviser(gcmId, role, serviceErrors);
            return getResponseStatusForCreateOneTimePasswordSendEmail(request, serviceErrors);
        } finally {
            LOGGER.info(ONBOARDING_SEND + "sendEmailFromServiceOpsDesktopForAdviser end");
        }
    }

    @Override
    public String sendEmailWithExistingRegoCodeForInvestor(String clientId, String adviserPositionId, String role, ServiceErrors serviceErrors) {
        LOGGER.info("sendEmailWithExistingRegoCodeForInvestor begin: clientId={}, adviserPositionId={}", clientId, adviserPositionId);
        try {
            Object request = buildResendExistingRegistrationCodeSendEmailRequestMsg(clientId, adviserPositionId, role, serviceErrors);
            return getResponseStatusForResendRegistrationCodeEmail(request, serviceErrors);
        }
        finally {
            LOGGER.info("sendEmailWithExistingRegoCodeForInvestor end");
        }
    }

    @Override
    public String sendEmailWithExistingRegoCodeForAdviser(String gcmId, String role, ServiceErrors serviceErrors) {
        LOGGER.info("sendEmailWithExistingRegoCodeForAdviser begin: gcmId={}", gcmId);
        try {
            Object request = resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.buildForAdviser(gcmId, role, serviceErrors);
            return getResponseStatusForResendRegistrationCodeEmail(request, serviceErrors);
        }
        finally {
            LOGGER.info("sendEmailWithExistingRegoCodeForAdviser end");
        }
    }

    @SuppressWarnings("unchecked")
    private String getResponseStatusForCreateOneTimePasswordSendEmail(Object request, ServiceErrors serviceErrors) {
        final CorrelatedResponse correlatedResponse = sendCreateOneTimePasswordSendEmailRequestMsg(request, serviceErrors);
        final Object response = correlatedResponse.getResponseObject();
        if (!createOneTimePasswordSendEmailRequestMsgTypeBuilder.isSuccessful(response)) {
            ServiceErrorsUtil.updateResponseServiceErrors(serviceErrors, createOneTimePasswordSendEmailRequestMsgTypeBuilder.getErrorResponses(response));
            return ERROR;
        }
        return SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private String getResponseStatusForResendRegistrationCodeEmail(Object request, ServiceErrors serviceErrors) {
        final CorrelatedResponse correlatedResponse = resendRegistrationCodeSendEmailRequestMsg(request, serviceErrors);
        final Object response = correlatedResponse.getResponseObject();
        if (!resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.isSuccessful(response)) {
            ServiceErrorsUtil.updateResponseServiceErrors(serviceErrors, createOneTimePasswordSendEmailRequestMsgTypeBuilder.getErrorResponses(response));
            return ERROR;
        }
        return SUCCESS;
    }

    /**
     * Method used to call ICC SendEmail Service for Investor from service operator desktop.
     */
    @Override
    public String resendRegistrationEmail(String gcmId, String role, ServiceErrors serviceErrors) {
        LOGGER.info(ONBOARDING_SEND + "sendEmailFromServiceOpsDesktopForAdviser begin");
        try {
            Object request = resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.buildForAdviser(gcmId, role, serviceErrors);
            return getResponseStatusForResendRegistrationCodeEmail(request, serviceErrors);
        } finally {
            LOGGER.info(ONBOARDING_SEND + "sendEmailFromServiceOpsDesktopForAdviser end");
        }
    }
}
