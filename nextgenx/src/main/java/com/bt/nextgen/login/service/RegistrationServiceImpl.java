package com.bt.nextgen.login.service;

import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.exception.ServiceException;
import com.bt.nextgen.core.repository.User;
import com.bt.nextgen.core.repository.UserRepository;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.login.web.controller.RegistrationResponse;
import com.bt.nextgen.login.web.model.CredentialsModel;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.login.web.model.SmsCodeModel;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.rules.*;
import com.bt.nextgen.service.group.customer.*;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.registration.model.UserRoleTermsAndConditions;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepository;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepositoryImpl;
import com.bt.nextgen.service.onboarding.*;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiChallengeRequest;
import com.bt.nextgen.service.security.converter.HttpRequestConverter;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.service.web.UrlProxyService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import com.btfin.panorama.core.util.StringUtil;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.OTPPartyDetailsType;
import org.apache.commons.ssl.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static com.bt.nextgen.core.security.ServiceTokenExchangeAuthorityServiceImpl.STS_SAML_TOKEN_XPATH;
import static com.bt.nextgen.core.util.SETTINGS.ADVISER_URL_MATCH;
import static com.bt.nextgen.core.util.SETTINGS.HTTP_REDIRECT_TEMPLATE;
import static com.bt.nextgen.core.xml.XmlUtil.extractXPathFromXml;
import static com.bt.nextgen.core.xml.XmlUtil.transformXML;

/**
 * This service implementation has the methods related to registration validation and SMScode validation.
 */
@Service
@EnableAsync
public class RegistrationServiceImpl implements RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private CustomerLoginManagementIntegrationService customerLoginService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleTermsAndConditionsRepository userRoleTncRepository;

    @Resource(name = "marshaller")
    Jaxb2Marshaller jaxb2Marshaller;

    @Autowired
    private OnboardingIntegrationService btEsbService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private CustomerUserNameManagementIntegrationService customerUserNameManagement;

    @Autowired
    private CustomerPasswordManagementIntegrationService customerPasswordManagement;

    @Autowired
    private CustomerTokenIntegrationService customerTokenIntegrationService;

    @Autowired
    private AvaloqRulesIntegrationService avaloqRulesIntegrationService;

    @Autowired
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationFacade;

    @Autowired
    public PrmService prmService;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    @Autowired
    private UrlProxyService urlProxyService;

    @Override
    public ValidateCredentialsResponse validateParty(CredentialsModel model) {
        ValidatePartyRequest validatePartyRequest = new FirstTimeRegistrationRequestModel(model.getUserCode(), model.getLastName(),
                model.getPostcode());
        ValidatePartyResponse response = null;

        try {
            response = btEsbService.validateParty(validatePartyRequest);
        } catch (Exception e) {
            logger.error("Can't validate party in ICC for registrationCode:{}, lastName:{}, postalCode:{}",
                    validatePartyRequest.getRegistrationCode(), validatePartyRequest.getLastName(), validatePartyRequest.getPostalCode(), e);
        }

        return new ValidateCredentialsResponse(response, getStatusCodeForResponse(response));
    }

    /**
     * To validate the  registration code and send email with registration code.
     *
     * @return String
     * @throws Exception
     */
    public SafiAnalyzeAndChallengeResponse validRegistration(SmsCodeModel model, HttpRequestParams requestParams) throws Exception {
        FirstTimeRegistrationResponse response = null;
        FirstTimeRegistrationRequest registrationRequest = new FirstTimeRegistrationRequestModel();
        registrationRequest.setDeviceToken(model.getDeviceToken());
        registrationRequest.setHttpRequestParams(requestParams);
        registrationRequest.setLastName(model.getLastName());
        registrationRequest.setPostalCode(model.getPostcode());
        registrationRequest.setUserName(model.getUserCode());

        try {
            response = btEsbService.validateRegistrationDetails(registrationRequest);
        } catch (Exception e) {
            logger.error("Error retreiving ICC response {}", registrationRequest.getUserName(), e);
        }
        SafiAnalyzeAndChallengeResponse safiObj = toSafiResultObject(response);

        return safiObj;
    }

    private SafiAnalyzeAndChallengeResponse toSafiResultObject(FirstTimeRegistrationResponse response) {
        SafiAnalyzeAndChallengeResponse safiObj = new SafiAnalyzeAndChallengeResponse();
        String statusCode = getStatusCodeForResponse(response);
        safiObj.setStatusCode(statusCode);

        if (statusCode.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
            safiObj.setDevicePrint(response.getDeviceToken());
            safiObj.getIdentificationData().setSessionId(response.getSessionId());
            safiObj.getIdentificationData().setTransactionId(response.getTransactionId());
            safiObj.setDeviceToken(response.getDeviceToken());
            safiObj.getIdentificationData().setUserName(response.getUserName());
            safiObj.setDeviceId(response.getDeviceId());
            safiObj.setzNumber(response.getzNumber());
        }

        return safiObj;
    }

    /**
     * Returns status code for the service response
     *
     * @param response service response
     * @return status code
     */
    private String getStatusCodeForResponse(Response response) {
        String statusCode;

        if (null == response) {
            statusCode = cmsService.getContent(ValidationErrorCode.SYSTEM_UNAVAILABLE);
        } else if (response.getServiceErrors() != null && response.getServiceErrors().hasErrors()) {
            String message = Constants.EMPTY_STRING;
            for (ServiceError serviceError : response.getServiceErrors().getErrorList()) {
                if (Attribute.ERROR_CODE_INVALID_PARAMETER.equals(serviceError.getId()) || Attribute.ERROR_CODE_INVALID_REGISTRATION_NUMBER
                        .equals(serviceError.getId())) {
                    message = cmsService.getContent(ValidationErrorCode.INVALID_PARAMETERS);
                } else {
                    message = cmsService
                            .getDynamicContent(ValidationErrorCode.ERROR_MSG_WITH_CORRELATIONID, new String[]{serviceError.getId()});
                }
            }
            statusCode = message;
        } else if ((response instanceof ValidatePartyResponse) && ((ValidatePartyResponse) response).getInvalidPartyDetails() != null) {
            final OTPPartyDetailsType invalidPartyDetails = ((ValidatePartyResponse) response).getInvalidPartyDetails();

            logInvalidPartyDetailsMessage(invalidPartyDetails);

            statusCode = cmsService.getContent(ValidationErrorCode.FORGETPASSWORD_INVALID_DATA);
        } else {
            statusCode = Attribute.SUCCESS_MESSAGE;
        }

        return statusCode;
    }

    /**
     * Logs invalid party details message
     *
     * @param invalidPartyDetails
     */
    private static void logInvalidPartyDetailsMessage(OTPPartyDetailsType invalidPartyDetails) {
        StringBuilder builder = new StringBuilder("Invalid party details: ");

        final String regoCode = invalidPartyDetails.getCredentialDetails() != null ?
                invalidPartyDetails.getCredentialDetails().getOneTimePassword() : null;

        if (regoCode != null) {
            builder.append("regoCode: ").append(regoCode).append(" ");
        }

        if (invalidPartyDetails.getLastName() != null) {
            builder.append("lastname: ").append(invalidPartyDetails.getLastName()).append(" ");
        }

        if (invalidPartyDetails.getPostcode() != null) {
            builder.append("postalcode: ").append(invalidPartyDetails.getPostcode());
        }

        logger.error(builder.toString().trim());
    }

    /**
     * Method to create an investor.
     *
     * @param registrationModel,serviceErrors
     * @param serviceErrors
     * @return String
     * @throws Exception
     */
    @Override
    public String createUser(RegistrationModel registrationModel, ServiceErrors serviceErrors) throws Exception {

        registrationModel.setCredentialId(customerLoginService.getCustomerInformation(null, serviceErrors).getCredentialId());
        CustomerCredentialManagementInformation response = customerUserNameManagement.createUsername(registrationModel, serviceErrors);
        if (Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(response.getServiceLevel())) {
            CustomerCredentialManagementInformation passwordResponse = customerPasswordManagement
                    .updatePassword(registrationModel, serviceErrors);
            if (Attribute.SUCCESS_MESSAGE.equalsIgnoreCase(passwordResponse.getServiceLevel())) {
                return Attribute.SUCCESS_MESSAGE;
            }
            logger.warn("A call to setup user failed, updatePassword:{}", passwordResponse.getServiceLevel());
        } else {
            logger.warn("A call to setup user failed, updateUsername{}", response.getServiceLevel());
        }

        return Attribute.FAILURE_MESSAGE;
    }

    /**
     * Method to get/generate SAML.
     *
     * @param customerTokenRequest
     * @return SamlToken
     * @throws Exception
     */
    public SamlToken generateUserSAML(CustomerTokenRequest customerTokenRequest) throws Exception {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        String tmpSamlToken = customerTokenIntegrationService.getCustomerSAMLToken(customerTokenRequest, serviceErrors);
        String samlToken = extractXPathFromXml(tmpSamlToken, STS_SAML_TOKEN_XPATH);
        logger.debug("Loaded SAML token:\n{}", samlToken);
        return new SamlToken(samlToken);
    }

    /**
     * Method to Create Registration Response
     *
     * @param customerTokenRequest
     * @param nextStepUrl
     * @param investorEtpPath
     * @param advisorEtpPath
     * @param appContext
     * @return RegistrationResponse
     * @throws Exception
     */
    @Override
    public RegistrationResponse createRegistrationResponse(CustomerTokenRequest customerTokenRequest, String nextStepUrl,
                                                           String investorEtpPath, String advisorEtpPath, String appContext)
            throws Exception {
        SamlToken userSaml = generateUserSAML(customerTokenRequest);
        logger.debug("Loaded User Saml :{}", userSaml);
        String finalXAML = transformXML(userSaml.getToken(), "SOAP_to_SAMLResponse.xsl");
        logger.debug("Final Saml to be encoded is :\n{}", finalXAML);

        String base = HTTP_REDIRECT_TEMPLATE.value();
        String samlDestServer = MessageFormat.format(base, customerTokenRequest.getxForwardedHost());

        String saml = new String(Base64.encodeBase64(finalXAML.getBytes()));
        String relayState = StringUtil.concatStrings(samlDestServer, appContext, "/", nextStepUrl);
        String samlPostUrl = StringUtil.concatStrings(samlDestServer, "/", investorEtpPath);

        if (samlDestServer.contains(ADVISER_URL_MATCH.value(""))) {
            samlPostUrl = StringUtil.concatStrings(samlDestServer, "/", advisorEtpPath);
        }

        logger.debug("Creating Registration response with post url:{}\n with SAML token {}\n and relay state {}", samlPostUrl, saml,
                relayState);
        return RegistrationResponse.buildForNextStep(relayState, saml, samlPostUrl);
    }

    @Override
    public RegistrationResponse registrationResponseForOptionalTwoFA(CustomerTokenRequest customerTokenRequest, HttpServletRequest request, String nextStepUrl, String investorEtpPath, String advisorEtpPath, String appContext) throws Exception {
        RegistrationResponse result;
        boolean isTwoFaRequired = customerTokenRequest.isForgotPassword();
        String ruleId = null;

        if (!isTwoFaRequired) {
            RuleImpl rule = avaloqRulesIntegrationService.retrieveTwoFaRule(RuleType.ACC_ACTIV, Collections.singletonMap(RuleCond.GCM_ID, customerTokenRequest.getPanNumber()), new FailFastErrorsImpl());
            logger.info("Avaloq rule {}", rule);
            if (rule != null && (rule.getAction() == RuleAction.CHK || rule.getAction() == RuleAction.CHK_UPD)) {
                ruleId = rule.getRuleId();
                logger.info("Found avaloq rule with id:{}, type:{}, action:{}", ruleId, rule.getType(), rule.getAction());
                isTwoFaRequired = true;
            }
        }

        if (isTwoFaRequired) {
            logger.info("2FA is required");
            //Challenge SAFI here
            SafiAnalyzeAndChallengeResponse analyzeResult = getSafiAnalyzeResponse(customerTokenRequest);

            if (analyzeResult != null && analyzeResult.getActionCode()) {
                SafiChallengeRequest challengeRequest = new SafiChallengeRequest(HttpRequestConverter.toHttpRequestParams(request), analyzeResult);

                SafiAnalyzeAndChallengeResponse challengeResponse = twoFactorAuthenticationFacade.challengeFromNotAuthCtx(challengeRequest);
                challengeResponse.setRuleId(ruleId);
                challengeResponse.getIdentificationData().setUserName(challengeResponse.getUserName());

                HttpSession session = request.getSession();
                session.setAttribute("safiAnalyzeResult", challengeResponse);
            }
            result = RegistrationResponse.buildForCurrentStepWithSMS();
        } else {
            result = createRegistrationResponse(customerTokenRequest, nextStepUrl, investorEtpPath, advisorEtpPath, appContext);
        }

        return result;
    }

    private SafiAnalyzeAndChallengeResponse getSafiAnalyzeResponse(CustomerTokenRequest customerTokenRequest) throws Exception {

        SafiAnalyzeAndChallengeResponse safiAnalyzeAndChallengeResponse = new SafiAnalyzeAndChallengeResponse();

        safiAnalyzeAndChallengeResponse.setTransactionId(getTransactionId());
        safiAnalyzeAndChallengeResponse.setDeviceId(customerTokenRequest.getDeviceId());
        safiAnalyzeAndChallengeResponse.setActionCode(true);
        safiAnalyzeAndChallengeResponse.setUserName(customerTokenRequest.getPanNumber());
        safiAnalyzeAndChallengeResponse.setzNumber(customerTokenRequest.getzNumber());
        safiAnalyzeAndChallengeResponse.setDevicePrint(customerTokenRequest.getDevicePrint());
        safiAnalyzeAndChallengeResponse.getIdentificationData().setClientSessionId(getSessionId());
        safiAnalyzeAndChallengeResponse.getIdentificationData().setClientTransactionId(getTransactionId());

        return safiAnalyzeAndChallengeResponse;
    }

    private String getSessionId() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        return session.getId();
    }

    private String getTransactionId() throws Exception {
        return UUID.randomUUID().toString();
    }

    /**
     * Update Registration Status in avaloq, and create User entries in the personalisation database.
     *
     * @param isTnCAccepted
     * @param serviceErrors TODO: Some of the services might change need to revisit this.
     */
    @Override
    public void updateOnlineRegistrationStatus(boolean isTnCAccepted, ServiceErrors serviceErrors) {

        //Person person = userProfileService.getPerson();
        User user = new User(userProfileService.getGcmId());
        //user.setId(person.getAdviserId().plainText());
        user.setFirstTimeLoggedIn(true);
        //The value would come from JSP in Model.
        user.setTncAccepted(isTnCAccepted);

        if (isTnCAccepted) {
            user.setTncAcceptedOn(new Date());
        }

        userRepository.update(user);

        // Create record in the user_role_tnc table for acceptance of terms and conditions for this job.
        UserRoleTermsAndConditions userRoleTnc = UserRoleTermsAndConditionsRepositoryImpl
                .createNewUserRoleTermsAndConditions(userProfileService.getGcmId(), userProfileService.getActiveProfile().getProfileId());

        // Investors see their T&C's within panorama secured site.
        if (userProfileService.isInvestor()) {
            userRoleTnc.setTncAccepted("N");
        } // Other roles, including intermediaries see their T&C's on the step 2 registration page
        else {
            userRoleTnc.setTncAccepted("Y");
            userRoleTnc.setTncAcceptedOn(new Date());
        }

        userRoleTnc.setModifyDatetime(new Date());
        userRoleTnc.setVersion(1);
        userRoleTncRepository.save(userRoleTnc);

        //CHECKSTYLE:OFF
        try {
            UserProfile activeProfile =userProfileService.getActiveProfile();
            clientIntegrationService.updateRegisterOnline(activeProfile.getClientKey(), activeProfile.getJobRole(),serviceErrors);
            // triggering  Registration Success PRM Event
            if (featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.PRM_VIEW)) {
                prmService.triggerRegistrationPrmEvent();
            }
        } catch (Exception e) {
            logger.error("Unable to update register online flag for user {}", userProfileService.getGcmId(), e);
        }
        //CHECKSTYLE:ON
    }


    @Override
    @Async
    public void updateUserTwoFAStatusAsync(String gcmId) {
        try {
            logger.info("Going to update ACC_ACTIV rule for gcmId: {}", gcmId);
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            RuleImpl rule = avaloqRulesIntegrationService.retrieveTwoFaRule(RuleType.ACC_ACTIV, Collections.singletonMap(RuleCond.GCM_ID, gcmId), serviceErrors);
            final String ruleId = rule.getRuleId();
            logger.info("Updating ACC_ACTIV rule with id: {}", ruleId);
            avaloqRulesIntegrationService.updateAvaloqRule(ruleId, Collections.singletonMap(RuleUpdateParams.STATUS, Boolean.valueOf(true)), serviceErrors);
        } catch (ServiceException e) {
            logger.error(String.format("Failed to update avaloq rule for gcmId:%s", gcmId), e);
        }
    }
}
