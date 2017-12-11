package com.bt.nextgen.login.web.controller;

import static com.bt.nextgen.core.util.SETTINGS.SAML_HEADER_WBC;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_BRAND_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_DEFAULT_BRAND;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HALGM_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HEADER_XFORWARDHOST;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_JS_OBFUSCATION_URL_DEV;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_JS_OBFUSCATION_URL_PRD;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_PASSWORD_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_USERNAME_PARAM;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.prm.service.PrmService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.core.util.LogMarkers;
import com.bt.nextgen.core.validation.ValidationFormatter;
import com.bt.nextgen.core.web.RequestQuery;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.login.service.RegistrationService;
import com.bt.nextgen.login.service.ValidateCredentialsResponse;
import com.bt.nextgen.login.web.model.CredentialsModel;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.login.web.model.SmsCodeModel;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.group.customer.CustomerTokenRequestModel;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAuthenticateRequest;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.security.converter.HttpRequestConverter;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;

/**
 * This controller handles:
 * 1. Registration
 * 2. Account Activation
 */
@Controller
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    public static final String SAFI_ANALYZE_RESULT_ATTRIBUTE = "safiAnalyzeResult";

    private static final String USER_CODE = "userCode";

    @Value("${label.registration}")
    private String labelRegistration;

    @Value("${label.fgpwd}")
    private String labelFGPwd;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationFacade;

    @Value("${registration.eam.path}")
    private String eamUrlPath;

    @Value("${registration.etp.path}")
    private String etpUrlPath;

    @Value("${registration.nextgen.step2}")
    private String nextgenRegStep2;

    @Value("${saml.xpath.registration.recipient}")
    private String samlRecipientXPath;

    @Value("${registration.etp.path.adviser}")
    private String adviserEtpUrlPath;

    @Autowired
    private RequestQuery requestQuery;

    @Autowired
    private PrmService prmService;

    private Map<String, String> cisKeyMap = new HashMap<>();

    @RequestMapping(value = "/public/api/validateCredentials", method = RequestMethod.GET)
    public
    @ResponseBody
    AjaxResponse validateCredentials(@Valid @ModelAttribute(Attribute.REGISTRATION_MODEL) CredentialsModel model,
                                     BindingResult bindingResult, HttpServletRequest request) throws Exception {
        return validateCredentials(model, bindingResult, request, false);
    }

    @RequestMapping(value = "/public/api/forgotPassword", method = RequestMethod.GET)
    public
    @ResponseBody
    AjaxResponse forgotPassword(@Valid @ModelAttribute(Attribute.REGISTRATION_MODEL) CredentialsModel model,
                                BindingResult bindingResult, HttpServletRequest request) throws Exception {
        return validateCredentials(model, bindingResult, request, true);
    }

    /**
     * Validates user's credentials in ICC, used for both registration and forgot password functionalities
     *
     * @param model
     * @param bindingResult
     * @param request
     * @param isForgotPassword
     * @return
     * @throws Exception
     */
    private AjaxResponse validateCredentials(CredentialsModel model, BindingResult bindingResult, HttpServletRequest request,
                                             boolean isForgotPassword) throws Exception {

        String opType = isForgotPassword ? labelFGPwd : labelRegistration;
        
        logger.info("{} Step 1 - validating details - {}", opType, model.toString());

        if (bindingResult.hasErrors()) {
            logger.info("{} Step 1 - {} details contain client input errors", opType, opType);
            return new AjaxResponse(false, ValidationFormatter.format(bindingResult));
        }

        final SamlToken samlReponse = getSamlTokenFromRequest(request);
        logger.info("Saml from request: {}", samlReponse);

        if (samlReponse.isAuthenticated()) {
            logger.warn("{} Step 1 - User already has an authenticated session. Requires sign out", opType);
            return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.ISAUTHENTICATE));
        }

        ValidateCredentialsResponse result = registrationService.validateParty(model);
        cisKeyMap.put(model.getUserCode(), result.getCisKey());
        logger.info("{} Step 1 - details validated. Result - {}", opType, result.getStatusCode());

        if (!result.getStatusCode().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
            return new AjaxResponse(false, result.getStatusCode());
        }

        return createRegistrationResponse(model, request, isForgotPassword, opType, samlReponse, result);
    }

    private AjaxResponse createRegistrationResponse(CredentialsModel model, HttpServletRequest request, boolean isForgotPassword,
                                                    String opType, SamlToken samlReponse, ValidateCredentialsResponse result) {
        try {
            CustomerTokenRequestModel customerToken = new CustomerTokenRequestModel();
            customerToken.populateCustomerNumber(result.getZNumber(), result.getUserName());
            customerToken.setPanNumber(result.getUserName());
            customerToken.setToken(samlReponse);
            customerToken.setxForwardedHost(request.getHeader(SECURITY_HEADER_XFORWARDHOST.value()));
            customerToken.setForgotPassword(isForgotPassword);
            customerToken.setDeviceId(result.getValidatePartyResponse().getDeviceId());
            customerToken.setzNumber(result.getZNumber());
            customerToken.setDevicePrint(model.getDeviceToken());

            RegistrationResponse registrationResponse = registrationService.registrationResponseForOptionalTwoFA(customerToken, request,
                    nextgenRegStep2,
                    etpUrlPath,
                    adviserEtpUrlPath,
                    request.getContextPath());

            logger.info("{} Step 1 - details validation finished. 2FA required: {}", opType, registrationResponse.isShowSMS());

            return new AjaxResponse(registrationResponse);
        } catch (Exception err) {
            logger.error("{} Step 1 - Error Occurred", opType, err);
            return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.SYSTEM_UNAVAILABLE));
        }
    }

    /**
     * *************************************************************************************************************************************
     * ******************* To validate registration and Send SMS Code **********************************************************************
     * *************************************************************************************************************************************
     */
    @RequestMapping(value = "/public/api/validateRegistration", method = RequestMethod.GET)
    public
    @ResponseBody
    AjaxResponse validateRegistration(@Valid @ModelAttribute(Attribute.REGISTRATION_MODEL) SmsCodeModel model,
                                      BindingResult bindingResult, HttpServletRequest request, HttpSession session) throws Exception {
        logger.info("Registration Step 1 - Validating registration details - {}", model.toString());
        if (bindingResult.hasErrors()) {
            logger.info("Registration Step 1 - Registration details contain client input errors");
            return new AjaxResponse(false, ValidationFormatter.format(bindingResult));
        }

        SamlToken samlReponse = getSamlTokenFromRequest(request);

        if (samlReponse.isAuthenticated()) {
            logger.warn("Registration Step 1 - User already has an authenticated session. Requires sign out");
            return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.ISAUTHENTICATE));
        }

        HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);

        //Decode the encoded last name and then send it for the Safi validation

        if (!StringUtils.isEmpty(model.getLastName())) {
            String lastName = model.getLastName().replaceAll("&#39;", "'");
            model.setLastName(lastName);
        }

        final SafiAnalyzeAndChallengeResponse result = registrationService.validRegistration(model, requestParams);
        session.setAttribute(SAFI_ANALYZE_RESULT_ATTRIBUTE, result);

        logger.info("Registration Step 1 - Details validated. Result - {}", result.getStatusCode());

        if (result.getStatusCode().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
            return new AjaxResponse(true);
        } else {
            return new AjaxResponse(false, result.getStatusCode());
        }
    }

    /**
     * *************************************************************************************************************************************
     * * *********************************** Verify Sms Code *********************************************************************************
     * * *************************************************************************************************************************************
     */
    @RequestMapping(value = "/public/api/verifySmsCode", method = RequestMethod.GET)
    public
    @ResponseBody
    AjaxResponse verifySmsCode(@Valid @ModelAttribute(Attribute.SMS_CODE_MODEL) SmsCodeModel conversation,
                               BindingResult bindingResult) throws IOException {
        logger.info("Registration Conversation: {}", conversation.toString());
        if (bindingResult.hasErrors()) {
            return new AjaxResponse(false, ValidationFormatter.format(bindingResult));
        }
        //TODO Call back-end service to verify the sms code.
        LogMarkers.audit_register_smsCode(conversation, logger);
        return new AjaxResponse(conversation);
    }

    /**
     * SAFI authenticate then if it was successful update 2fa avaloq rule
     *
     * @param conversation
     * @param bindingResult
     * @param request
     * @return Ajax response with the data to move to secure URL.
     * @throws Exception
     */
    @RequestMapping(value = "/public/api/verifyDealerGroupSmsAndRegistration", method = RequestMethod.GET)
    public
    @ResponseBody
    AjaxResponse verifyDealerGroupSmsAndRegistration(@Valid @ModelAttribute(Attribute.SMS_CODE_MODEL) SmsCodeModel conversation,
                                                     BindingResult bindingResult, HttpServletRequest request) throws Exception {

        AjaxResponse resp = verifySmsAndRegistration(conversation, bindingResult, request);

        HttpSession session = request.getSession();
        final SafiAnalyzeAndChallengeResponse analyzeResult = (SafiAnalyzeAndChallengeResponse) session.getAttribute(SAFI_ANALYZE_RESULT_ATTRIBUTE);

        if (resp.isSuccess()) {
            String ruleId = analyzeResult.getRuleId();
            logger.info("2FA success. ACC_ACTIV rule with ruleId:{} will need to be updated in avaloq", ruleId);
        }

        session.removeAttribute(SAFI_ANALYZE_RESULT_ATTRIBUTE);

        return resp;
    }

    /**
     * URL: /public/page/logon?TAM_OP=login#jq-register
     * Method called on click of next button in registration step 1.
     *
     * @param conversation
     * @param bindingResult
     * @param request
     * @return Ajax response with the data to move to secure URL.
     * @throws Exception
     */
    //TODO There is too much logic in this controller, please refactor this into the service layer
    @RequestMapping(value = "/public/api/verifySmsAndRegistration", method = RequestMethod.GET)
    public
    @ResponseBody
    AjaxResponse verifySmsAndRegistration(@Valid @ModelAttribute(Attribute.SMS_CODE_MODEL) SmsCodeModel conversation,
                                          BindingResult bindingResult, HttpServletRequest request) throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        SafiAuthenticateRequest authRequest = null;
        SafiAuthenticateResponse authResult = null;

        logger.info("Registration Step 1 - authenticate sms code - {}", conversation.toString());
        if (bindingResult.hasErrors()) {
            request.getSession().removeAttribute(USER_CODE);
            return new AjaxResponse(false, ValidationFormatter.format(bindingResult));
        }


        HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
        SafiAnalyzeAndChallengeResponse analyzeResult = (SafiAnalyzeAndChallengeResponse) request.getSession()
                .getAttribute(SAFI_ANALYZE_RESULT_ATTRIBUTE);

        if (analyzeResult != null) {
            logger.info("analyse result usernames are main{} identificationData {}",
                    analyzeResult.getUserName(),
                    analyzeResult.getIdentificationData().getUserName());
            //Set the transaction ID for the next request
            analyzeResult.setTransactionId(UUID.randomUUID().toString());
        }

        try {
            authRequest = new SafiAuthenticateRequest(requestParams, analyzeResult, conversation.getSmsCode());
            authResult = twoFactorAuthenticationFacade.authenticate(authRequest);
            logger.info("Registration Step 1 - authenticate sms code with result {}", authResult.getStatusCode());
            if(authResult.isSuccessFlag()){
                prmService.triggerTwoFactorPrmEvent(cisKeyMap.remove(conversation.getUserCode()));
            }

            if (conversation.getSmsCode() != null && !conversation.getSmsCode().equals("") && !authResult.isSuccessFlag()) {
                LogMarkers.audit_registration(logger,
                        conversation.getUserCode(),
                        conversation.getLastName(),
                        conversation.getPostcode(),
                        conversation.getSmsCode(),
                        LogMarkers.Status.FAILED,
                        cmsService.getContent(ValidationErrorCode.INVALID_SMS_CODE));

                request.getSession().removeAttribute(USER_CODE);
                logger.info("Registration Step 1 - authenticate sms code FAILED with reason {}", authResult.getDisplayMessageCode());
                return new AjaxResponse(false, cmsService.getContent(authResult.getDisplayMessageCode()));
            } else {
                LogMarkers.audit_registration(logger,
                        conversation.getUserCode(),
                        conversation.getLastName(),
                        conversation.getPostcode(),
                        conversation.getSmsCode(),
                        LogMarkers.Status.SUCCESS);
                if (null != conversation.getUserCode()) {
                    request.getSession().setAttribute(USER_CODE, conversation.getUserCode());
                }
                CustomerTokenRequestModel customerTokenRequest = new CustomerTokenRequestModel();
                String zNumber = analyzeResult.getzNumber();
                customerTokenRequest.populateCustomerNumber(zNumber, analyzeResult.getIdentificationData().getUserName());
                customerTokenRequest.setxForwardedHost(request.getHeader(SECURITY_HEADER_XFORWARDHOST.value()));
                customerTokenRequest.setToken(getSamlTokenFromRequest(request));

                RegistrationResponse registrationResponse = registrationService.createRegistrationResponse(customerTokenRequest,
                        nextgenRegStep2,
                        etpUrlPath,
                        adviserEtpUrlPath,
                        request.getContextPath());

                logger.info("Registration Step 1 - COMPLETED - Redirecting user to step 2 page");
                return new AjaxResponse(registrationResponse);

            }
        } catch (Exception err) {
            logger.error("Registration Step 1 - Error Occurred", err);
            return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.SYSTEM_UNAVAILABLE));
        }
    }

    private static SamlToken getSamlTokenFromRequest(HttpServletRequest request) {
        String samlString = request.getHeader(SAML_HEADER_WBC.value());
        SamlToken token = new SamlToken(samlString);
        return token;
    }

    /**
     * URL: /secure/page/registrationStepTwo
     * Method called when save and sign in clicked on registration step 2.
     *
     * @param registrationModel
     * @param bindingResult
     * @param request           contain the userCode in session which is the registration code.
     * @return Ajax Response
     * @throws Exception
     */
    @RequestMapping(value = "/secure/api/registerUser", method = RequestMethod.POST)
    public
    @ResponseBody
    AjaxResponse registerUser(@Valid @ModelAttribute(Attribute.REGISTRATION_MODEL) RegistrationModel registrationModel,
                              BindingResult bindingResult, HttpServletRequest request) throws Exception {

        if (bindingResult.hasErrors()) {
            logger.warn("Registration Step 2 - Username or Password validation failed");
            return new AjaxResponse(false, ValidationFormatter.format(bindingResult));
        }

        logger.info("Registration Step 2 - Registration of User: {}", registrationModel.getUserCode());

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        registrationModel.setRequestedAction(ServiceConstants.SET_PASSWORD);
        String response = registrationService.createUser(registrationModel, serviceErrors);

        // Force rebuild of SAML token upon the next request to webseal
        // This allows the username to be propogated correctly to the view/presentation layer
        Profile currentProfile = (Profile) SecurityContextHolder.getContext().getAuthentication().getDetails();
        currentProfile.forceExpiry();

        if (null != response && response.equals(Attribute.SUCCESS_MESSAGE)) {
            LogMarkers.audit_registerUser(registrationModel, logger);

            //Create a user in personalised DB as well
            try {
                registrationService.updateOnlineRegistrationStatus(registrationModel.isTncAccepted(), serviceErrors);
            } catch (Exception e) {
                //Keeping it as warning as registration is already successful
                logger.warn("Registration Step 2 - Unable to create USER [{}] in personalised DB ", registrationModel.getUserCode());
                logger.info("Error in user creation in personlised DB", e);
                return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.SYSTEM_UNAVAILABLE));
            }

            String gcmId = userProfileService.getGcmId();
            registrationService.updateUserTwoFAStatusAsync(gcmId);

            logger.info("Registration Step 2 - Successful registration of user: {}", registrationModel.getUserCode());
            return new AjaxResponse(response);
        }
        logger.warn("There was no response from the calls to update Username and Password or they failed");

        return new AjaxResponse(false, serviceErrors.getErrorList());
    }

    /**
     * Render registration step 2- Select username and password
     *
     * @param modelMap
     * @return
     */
    @RequestMapping(value =
            {
                    "/secure/util/registrationStepTwo", "/secure/updateDetails"
            })
    public String registrationStepTwo(ModelMap modelMap) {

        String nextStep = "registrationStepTwo";

        //User user = userRepository.loadUser(profileService.getUsername());
        modelMap.addAttribute(Attribute.REGISTRATION_MODEL, new RegistrationModel());
        modelMap.addAttribute(Attribute.LOGON_BRAND, SECURITY_DEFAULT_BRAND.value());
        boolean isWebSealRequest = requestQuery.isWebSealRequest();
        modelMap.addAttribute(Attribute.PASSWORD_FIELD_NAME, SECURITY_PASSWORD_PARAM.value());
        modelMap.addAttribute(Attribute.USERNAME_FIELD_NAME, SECURITY_USERNAME_PARAM.value());
        modelMap.addAttribute(Attribute.BRAND_FIELD_NAME, SECURITY_BRAND_PARAM.value());
        modelMap.addAttribute(Attribute.HALGM_FIELD_NAME, SECURITY_HALGM_PARAM.value());
        modelMap.addAttribute(Attribute.OBFUSCATION_URL, isWebSealRequest
                ? SECURITY_JS_OBFUSCATION_URL_PRD.value()
                : SECURITY_JS_OBFUSCATION_URL_DEV.value());

        //added for including role
        modelMap.put("userRole", getCurrentUserRole());
        modelMap.put("showTerms", !isCurrentUserInvestor());

        if (userProfileService.getUsername() != null) {
            String userName = userProfileService.getUsername();
            logger.info("Setting userName:{}", userName);
            modelMap.put(Attribute.USER_NAME, userName);
        }

        logger.info("Registration Step 2 page rendered successfully");
        return nextStep;
    }

    private static boolean isCurrentUserInvestor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().toString().equalsIgnoreCase(Roles.ROLE_INVESTOR.name()))
                return true;
        }
        return false;
    }

    private String getCurrentUserRole() {
        logger.info("Fetching current active profile to get job role.");
        UserProfile profile = userProfileService.getActiveProfile();

        String role = "";

        if (profile != null && profile.getJobRole() != null)
            role = StringUtil.toProperCase(profile.getJobRole().name());

        logger.info("After getting job role from active profile...{}", role);
        return role;
    }
}
