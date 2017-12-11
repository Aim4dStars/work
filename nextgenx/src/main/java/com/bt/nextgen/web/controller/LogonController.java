package com.bt.nextgen.web.controller;

import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.security.TamOperationCode;
import com.bt.nextgen.core.security.UIErrorCode;
import com.bt.nextgen.core.security.api.service.PermissionBaseDtoService;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.CredentialService;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.LogMarkers;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.validation.ValidationFormatter;
import com.bt.nextgen.core.web.RequestQuery;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.logon.service.LogonService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.btfin.panorama.core.security.integration.customer.UserGroup;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.service.security.SmsService;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;

import static com.bt.nextgen.core.util.SETTINGS.SECURITY_BRAND_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HALGM_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_JS_OBFUSCATION_URL_DEV;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_JS_OBFUSCATION_URL_PRD;

@Controller
public class LogonController extends ParentAuthenticationController {
    /**
     * The offical LOGON page url
     */
    public static final String LOGON = "/public/page/logon";
    /**
     * Use this to redirect someone to the logon page
     */
    public static final String REDIRECT_LOGON = "redirect:" + LOGON;
    /**
     * The key for failed cms message
     */
    public static final String LOGIN_INCORRECT_KEY = "login.incorrectKey";
    /**
     * The key for access denied cms message
     */
    public static final String ACCESS_DENIED_KEY = "UIM.0110";
    /**
     * The logout successfully cms message
     */
    public static final String LOGOUT_MSG_KEY = "logoutMsgKey";
    /**
     * The key for blocking user login access
     */
    public static final String LOGIN_BLOCKED_KEY = "Err0083";

    public static final String SYSTEM_EVENT_MSG = "systemEventMessage";
    public static final String SYSTEM_EVENT_MSG_FOUND = "systemEventMessageFound";

    @SuppressWarnings("squid:S2068")
    public static final String FAILED_RESET_PASSWORD = "failedResetPassword";

    private static final String FAILED_RESET_USRNAME = "failedResetUsername";

    private static final String MSG_WS_ERROR = "Err.IP-0314";

    public static final String MSG_UI_API_ERROR = "Err.IP-0333";

    private static final Logger logger = LoggerFactory.getLogger(LogonController.class);
    @SuppressWarnings("squid:S2068")
    private static final String PASSWORD = "password";
    @SuppressWarnings("squid:S2068")
    private static final String NEW_PASSWORD = "newPassword";
    @SuppressWarnings("squid:S2068")
    private static final String CONFIRM_PASSWORD = "confirmPassword";
    private static final String NEW_USERNAME = "newUserName";

    private static final String UI_ERROR = "ERROR";
    private static final String CHARSET = "UTF-8";

    /*
          * Error Message for closed account
    */
    private static final String CLOSEDMESSAGE = "Ins-OP-0078";
    private static final String CLOSED = "closed";
    public static final String CLOSEDLOGON = "/public/page/closed";

    /* Error message for Avaloq connection failure */
    private static final String AVALOQ_CONNECTION_FAILURE_MESSAGE = "Err.IP-0365";
    public static final String AVALOQ_CONNECTION_FAILURE_LOGON = "/public/page/serverfailure";

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationFacade;
    @Autowired
    private CmsService cmsService;
    @Autowired
    private LogonService logonService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private RequestQuery requestQuery;
    @Autowired
    private UserProfileService profileService;
    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;
    @Autowired
    private CustomerLoginManagementIntegrationService customerLoginService;
    @Autowired
    private PrmService prmService;

    @Autowired
    private PermissionBaseDtoService permissionBaseService;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    @RequestMapping(value = CLOSEDLOGON, method = RequestMethod.GET, params =
            {
                    "closedStatus"
            })
    public String handleClosedAccount(ModelMap model, @RequestParam(value = "closedStatus") String closedStatus,
                                      HttpServletRequest request, HttpServletResponse response) {
        if (null != closedStatus && closedStatus.equals(CLOSED)) {
            prepareModelWithErrorMessage(request, response, model, cmsService.getContent(CLOSEDMESSAGE));
        }
        return View.LOGON;
    }

    @RequestMapping(value = AVALOQ_CONNECTION_FAILURE_LOGON, method = RequestMethod.GET)
    public String handleAvaloqConnectionFailure(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
        prepareModelWithErrorMessage(request, response, model, cmsService.getContent(AVALOQ_CONNECTION_FAILURE_MESSAGE));
        return View.LOGON;
    }

    private void setLoginStatusCookie(HttpServletResponse response) {
        Cookie loginStatusCookie = new Cookie("LoginStatusCookie", "true");
        loginStatusCookie.setSecure(true);
        loginStatusCookie.setPath("/");
        response.addCookie(loginStatusCookie);
    }

    @RequestMapping(value = LOGON, method = RequestMethod.GET, params = {"TAM_OP"})
    public String handleTamOperation(ModelMap model,
                                     @RequestParam("TAM_OP") String tamOperation, HttpServletRequest request,
                                     final RedirectAttributes redirectAttributes, HttpServletResponse response) {
        logger.info("Received TAM_OP {}", tamOperation);
        model.addAttribute(SYSTEM_EVENT_MSG_FOUND, Boolean.FALSE);
        model.addAttribute("tamOperation", tamOperation);
        try {
            TamOperationCode tamOp = TamOperationCode.get(tamOperation);
            switch (tamOp) {
                case LOGIN_SUCCESS:
                    setLoginStatusCookie(response);
                    return HomePageController.REDIRECT_HOMEPAGE;
                case LOGIN:
                case HELP:
                    prepareModel(model);
                    break;
                case LOGOUT:
                    return setLogoutUrl(request, HomePageController.REDIRECT_LOGOUT);
                case TEMP_PASSWORD:
                    prepareModel(model);
                    return HomePageController.REDIRECT_TEMP_PASSWORD;
                case AUTH_INFO:
                case ERROR:
                case AUTH_FAILURE:
                case AUTH_SUSP:
                case AUTH_TIMEOUT:
                case EAI_AUTH_ERROR:
                case LOGIN_BLOCKED:
                    prepareModelWithErrorMessage(request, response, model, cmsService.getContent(tamOp.getTamMessageId()));
                    break;
                case PASSWD_REP_FAILURE:
                case TEMP_PASSWD_EXP:
                case PASSWD_POLICY_INHIST:
                case PASSWD_POLICY_MAXCONREPCHAR:
                case PASSWD_POLICY_MINOTHER:
                case PASSWD_POLICY_MINLENGTH:
                case PASSWD_POLICY_MINALPHA:
                    redirectAttributes.addFlashAttribute(MSG_KEY, cmsService.getContent(tamOp.getTamMessageId()));
                    redirectAttributes.addFlashAttribute(MSG_TYPE, Attribute.ERROR_MESSAGE);
                    return HomePageController.REDIRECT_TEMP_PASSWORD;
                default:
                    logger.warn("TAM_op ({}) Did not match any expected action.", tamOperation);
                    prepareModelWithErrorMessage(request, response, model, cmsService.getContent(ACCESS_DENIED_KEY));
                    break;
            }
        } catch (Exception e) {
            logger.error("Invalid TAM_op ({}) passed back to the controller. What do I do with this?", tamOperation, e);
            prepareModelWithErrorMessage(request, response, model, cmsService.getContent("err00100"));
        }
        //If the User is authenticated on the login page display an appropriate message as this will cause weird behaviour
        if (requestQuery.isUserAuthenticated()) {
            //If the user is on the correct site for their user type
            if (requestQuery.isInvestorOnInvestorSite() || requestQuery.isAdviserOnAdviserSite()) {
                addErrorMessage(request, response, model, cmsService.getContent(ValidationErrorCode.ISAUTHENTICATE));
            } else //the user is on the wrong site for their login type and will not be able to proceed
            {
                addErrorMessage(request, response, model, cmsService.getContent(MSG_WS_ERROR));
            }
        }
        return View.LOGON;
    }

    /**
     * ***************************************************************************************************************************************
     * ******************************US612(Reset Password)*******************************************************************
     * ****************************************************************************************************************************************
     *
     * @param userReset
     * @param bindingResult
     */
    @RequestMapping(value = "/public/api/resetPassword", method = RequestMethod.POST)
    public
    @ResponseBody
    AjaxResponse resetPassword(@Valid @ModelAttribute(Attribute.USER_RESET_MODEL) UserReset userReset, BindingResult bindingResult)
            throws Exception {
        logger.debug("inside method: resetPassword()");
        if (bindingResult.hasErrors()) {
            Collection<Object> formattedResults = checkForBindingErrorExcludingField(bindingResult, "newUserName");
            if (!formattedResults.isEmpty())
                return new AjaxResponse(false, formattedResults);
        }
        String status;
        try {
            ServiceErrors serviceErrors = new ServiceErrorsImpl();
            userReset.setRequestedAction(ServiceConstants.SET_PASSWORD);
            userReset.setCredentialId(profileService.getCredentialId(serviceErrors));
            status = logonService.updatePassword(userReset, serviceErrors);
            if (serviceErrors.hasErrors()) {
                return new AjaxResponse(false, serviceErrors.getErrorList());
            }
        } catch (SoapFaultClientException ex) {
            logger.warn("Error from group ESB while updating password: {}", ex.getFaultStringOrReason(), ex);
            return new AjaxResponse(false, cmsService.getContent(FAILED_RESET_PASSWORD));
        }
        if (!Attribute.SUCCESS.equalsIgnoreCase(status)) {
            //Audit for updating forgotten password
            logger.info(LogMarkers.AUDIT,
                    "Forgotten Password Step2: Trying to update Password. Status [{}] Reason [{}]",
                    LogMarkers.Status.FAILED,
                    cmsService.getContent(status));
            return new AjaxResponse(false, cmsService.getContent(status));
        }
        //Audit for updating forgotten password
        logger.info(LogMarkers.AUDIT,
                "Forgotten Password Step2: Trying to update Password. Status [{}]",
                LogMarkers.Status.SUCCESS);
        logger.debug("exiting changePassword method:");
        return new AjaxResponse(true, userReset);
    }

    /**
     * Change username functionlity for investors, and advisers (not actually resetting anything).
     * Note that this will change the customer defined login field for the user record in EAM.
     * The customer defined login is a convienience over the actual gcm_id (user can log in with either).
     * The avaloq gcm_id and the user_id in EAM remains the same (that nine digit number)
     */
    @RequestMapping(value = "/secure/api/resetUsername", method = RequestMethod.POST)
    public
    @ResponseBody
    AjaxResponse resetUsername(@Valid @ModelAttribute(Attribute.USER_RESET_MODEL) UserReset userReset, BindingResult bindingResult)
            throws Exception {
        logger.debug("Start of LogonController.resetUsername()");
        logger.info("Attempt to change current username {} to {}.", userReset.getUserName(), userReset.getNewUserName());
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                if (!fieldError.getField().equalsIgnoreCase(NEW_PASSWORD)) {
                    logger.error("validation errors : " + fieldError.getObjectName());
                    return new AjaxResponse(false, ValidationFormatter.format(fieldError.getField(),
                            fieldError.getDefaultMessage()));
                }
            }
        }
        if (StringUtils.equals(userReset.getUserName(), userReset.getNewUserName())) {
            return new AjaxResponse(false,
                    ValidationFormatter.format(NEW_USERNAME, ValidationErrorCode.USERNAME_SAME_AS_EXISTING));
        }
        String response;
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        userReset.setCredentialId(profileService.getCredentialId(serviceErrors));
        response = logonService.modifyUserAlias(userReset, serviceErrors);
        if (serviceErrors.hasErrors() || response == null) {
            return new AjaxResponse(false, cmsService.getContent(FAILED_RESET_USRNAME));
        }
        // Force rebuild of SAML token upon the next request to webseal
        // This allows the username to be propogated correctly to the view/presentation layer
        Profile currentProfile = (Profile) SecurityContextHolder.getContext().getAuthentication().getDetails();
        currentProfile.forceExpiry();
        if (!response.equalsIgnoreCase(Attribute.SUCCESS_MESSAGE)) {
            if (response.equalsIgnoreCase(ValidationErrorCode.USER_NAME_NOT_UNIQUE)) {
                return new AjaxResponse(false, ValidationFormatter.format(NEW_USERNAME, ValidationErrorCode.USER_NAME_NOT_UNIQUE));
            } else {
                return new AjaxResponse(false, cmsService.getContent(FAILED_RESET_USRNAME));
            }
        }
        //Audit reset username/password
        LogMarkers.audit_resetUser(userReset.getUserName(), LogMarkers.Status.SUCCESS, logger);
        logger.debug("End of LogonController.resetUsername()");
        return new AjaxResponse(userReset);
    }

    @RequestMapping(value = "/public/page/resetTemporaryPassword")
    public String registrationStepTwo(ModelMap modelMap, final RedirectAttributes redirectAttributes) {
        boolean isWebSealRequest = requestQuery.isWebSealRequest();
        modelMap.addAttribute(Attribute.OBFUSCATION_URL, isWebSealRequest
                ? SECURITY_JS_OBFUSCATION_URL_PRD.value()
                : SECURITY_JS_OBFUSCATION_URL_DEV.value());
        modelMap.addAttribute(Attribute.BRAND_FIELD_NAME, SECURITY_BRAND_PARAM.value());
        modelMap.addAttribute(Attribute.HALGM_FIELD_NAME, SECURITY_HALGM_PARAM.value());
        //modelMap.addAttribute("message", redirectAttributes.getFlashAttributes());
        //modelMap.addAttribute("message", redirectAttributes.get("message"));
        if (null != profileService.getUsername())
            modelMap.put("userId", profileService.getUsername());
        if (null != profileService.getSamlToken())
            modelMap.put("userType", getUserType(profileService.getSamlToken().getUserGroup()));
        //todo: Prm Temp password event.
        /*ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        triggerChgPwdPrmEvent(profileService,"TMPPWDCHANGE", serviceErrors);*/
        return "tempPassword";
    }

    public String getUserType(List userGroup) {
        if (Properties.getSafeBoolean("wpl.integration.enabled"))
            for (int i = 0; i < userGroup.size(); i++)
                if (userGroup.get(i).equals(UserGroup.WPL_USER))
                    return UserGroup.WPL_USER.toString();
        return UserGroup.PAN_USER.toString();
    }

    @RequestMapping(value = "/public/page/logout")
    public String logoutStep(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
        prepareLogoutModel(model, request, response);
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        //todo: Prm Logoff event.
        //triggerChgPwdPrmEvent(profileService,"PRMLOGOUT", serviceErrors);
        return "logout";
    }

    private String setLogoutUrl(HttpServletRequest request, String url) {
        try {
            if (StringUtils.isNotBlank(request.getQueryString())) {
                String queryString = URLDecoder.decode(request.getQueryString(), CHARSET);
                List<NameValuePair> params = URLEncodedUtils.parse(new URI(
                        request.getRequestURL().toString() + "?" + queryString), CHARSET);
                for (NameValuePair param : params) {
                    if (param.getName().equals(UI_ERROR)) {
                        url += "?" + param.toString();
                        break;
                    }
                }
            }
        } catch (UnsupportedEncodingException | URISyntaxException e) {
            logger.error("Error decoding and extracting UI error code from URL: {}", request.getRequestURL());
        }
        return url;
    }

    private void prepareLogoutModel(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
        //Process error code sent by UI and display on logout page
        String errorCode = request.getParameter(UI_ERROR);
        if (StringUtils.isNotBlank(errorCode)) {
            try {
                UIErrorCode uiErrorCode = UIErrorCode.get(errorCode);
                if (uiErrorCode.equals(UIErrorCode.API_LOAD_ERROR)) {
                    logger.error("User has been logged out after an error occurred whilst loading their profile.");
                    prepareModelWithErrorMessage(request, response, model, cmsService.getContent(MSG_UI_API_ERROR));
                }
            } catch (Exception e) {
                logger.error("Invalid UI error passed back to the controller: ({})", errorCode, e);
                prepareModelWithErrorMessage(request, response, model, cmsService.getContent(MSG_UI_API_ERROR));
            }
        } else {
            prepareModel(model);
        }
    }

    @RequestMapping(value = "/public/page/termsAndConditions")
    public String termsAndConditions() {
        return "termsandconditions";
    }

    /**
     * To Change password after validating the SMS code.
     *
     * @param userReset     - {@link UserReset}
     * @param bindingResult - {@link BindingResult}
     * @return {@link AjaxResponse} - updated userReset or error.
     * @throws Exception
     */
    @RequestMapping(value = "/secure/api/changePassword", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse changePassword(@Valid @ModelAttribute(Attribute.USER_RESET_MODEL) UserReset userReset, BindingResult bindingResult)  {
        logger.debug("inside method: changePassword()");
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                if (!fieldError.getField().equalsIgnoreCase(NEW_USERNAME)) {
                    logger.error("validation errors : " + fieldError.getObjectName());
                    return new AjaxResponse(false, ValidationFormatter.format(fieldError.getField(),
                            fieldError.getDefaultMessage()));
                }
            }
        }
        //TODO Need Clarity about the credentialID
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        UserAccountStatusModel userAccountStatusModel = credentialService.lookupStatus(userReset.getUserName(), serviceErrors);
        if (serviceErrors.hasErrors()) {
            return new AjaxResponse(false, serviceErrors.getErrorList());
        }
        if (userAccountStatusModel.getUserAccountStatus().equals(UserAccountStatus.BLOCKED)) {
            return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.FORGETPASSWORD_ACCOUNT_LOCKED));
        }
        //To validate new password is matching with confirm password or not.
        return updatePassword(userReset);
    }

    /**
     * Update the password in GEBS and Avaloq.
     *
     * @param userReset user information
     * @return AjaxResponse of the user information if successful. Otherwise returns the error messages.
     */
    private AjaxResponse updatePassword(UserReset userReset) {
        if (StringUtils.equals(userReset.getNewpassword(), userReset.getConfirmPassword())) {
            String status;
            try {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                userReset.setRequestedAction(ServiceConstants.UPDATE_PASSWORD);
                userReset.setCredentialId(profileService.getCredentialId(serviceErrors));
                status = logonService.updatePassword(userReset, serviceErrors);
                if (serviceErrors.hasErrors()) {
                    return new AjaxResponse(false, serviceErrors.getErrorList());
                }
            } catch (SoapFaultClientException ex) {
                logger.info("Got the server error:", ex);
                return new AjaxResponse(false, cmsService.getContent(FAILED_RESET_PASSWORD));
            }
            //TODO Need clarity for EAM Provider Error codes
            if (!Attribute.SUCCESS.equalsIgnoreCase(status)) {
                return getValidationErrorResponse(userReset, status);
            }
        } else {
            return new AjaxResponse(false, ValidationFormatter.format(CONFIRM_PASSWORD, ValidationErrorCode.REPEATED_PASSWORD_NOT_MATCH));
        }
        logger.debug("exiting resetPassword method:");
        //Audit reset username/password
        LogMarkers.audit_resetUser(userReset.getUserName(), LogMarkers.Status.SUCCESS, logger);
        return new AjaxResponse(userReset);
    }
    /**
     * Get the validation error messages.
     *
     * @param userReset user information
     * @param status    Error status
     * @return AjaxResponse of the user information if successful. Otherwise returns the error messages.
     */
    private AjaxResponse getValidationErrorResponse(UserReset userReset, String status) {
        LogMarkers.audit_resetUser(userReset.getUserName(), LogMarkers.Status.FAILED, logger);
        if (status.equalsIgnoreCase(ValidationErrorCode.INVALID_CURRENT_PASSWORD)) {
            return new AjaxResponse(false, ValidationFormatter.format(PASSWORD, ValidationErrorCode.INVALID_CURRENT_PASSWORD));
        } else if (status.equalsIgnoreCase(ValidationErrorCode.PASSWORD_NOT_UNIQUE)) {
            return new AjaxResponse(false, ValidationFormatter.format(NEW_PASSWORD, ValidationErrorCode.PASSWORD_NOT_UNIQUE));
        }
        return new AjaxResponse(false, cmsService.getContent(status));
    }

    @RequestMapping(value = "/secure/page/accountStatus", method = RequestMethod.GET)
    public String accountStatus(ModelMap model) {
        prepareModel(model);
        return View.STATUS;
    }

    @RequestMapping(value = "/secure/page/accountActivation", method = RequestMethod.GET)
    public String accountActivation(ModelMap model) {
        prepareModel(model);
        return View.ACTIVATION;
    }
}
