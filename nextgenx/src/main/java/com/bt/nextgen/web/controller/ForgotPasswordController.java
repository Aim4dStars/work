package com.bt.nextgen.web.controller;

import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.util.LogMarkers;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.validation.ValidationFormatter;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.login.service.RegistrationService;
import com.bt.nextgen.login.web.controller.RegistrationResponse;
import com.bt.nextgen.login.web.model.SmsCodeModel;
import com.bt.nextgen.logon.service.LogonService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerTokenRequestModel;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.btfin.panorama.core.security.integration.customer.UserGroup;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAuthenticateRequest;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.security.converter.HttpRequestConverter;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

//Old Cash Controller, suppressing warnings on:
//Complexity (too many classes used)
//The throwing of Exceptions
//Invalid password hardcoding detection
@Controller
@SuppressWarnings({"squid:S2068", "squid:S1200", "squid:S00112"})
public class ForgotPasswordController extends ParentAuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    @Autowired
    private LogonService logonService;

    @Autowired
    private UserProfileService profileService;

    private static final String NEW_PWD_LABEL = "newPassword";

    private static final String CONFIRM_PWD_LABEL = "confirmPassword";

    @Autowired
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationFacade;

    private static final String USER_CODE = "userCode";

    @Autowired
    private RegistrationService registrationService;

    @Value("${forgotpassword.etp.path}")
    private String etpUrlPath;

    @Value("${forgotpassword.etp.path.adviser}")
    private String adviserEtpUrlPath;

    @Value("${forgotpassword.relaystate.path}")
    private String relayStatePath;

    /**
     * Forgot Password -- Step two
     * Submission of new password for a user that has forgotten their original passwprd
     *
     * @param userReset
     * @param bindingResult
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/secure/api/setForgotPassword", method = RequestMethod.POST)
    public
    @ResponseBody
    AjaxResponse setForgotPassword(@Valid @ModelAttribute(Attribute.USER_RESET_MODEL) UserReset userReset,
                                   BindingResult bindingResult) throws Exception {
        logger.debug("inside method: setForgotPassword()");

        if (bindingResult.hasErrors()) {
            Collection<Object> formattedResults = checkForBindingErrorOnField(bindingResult, NEW_PWD_LABEL);
            if (!formattedResults.isEmpty())
                return new AjaxResponse(false, formattedResults);
        }

        if (!StringUtils.equals(userReset.getNewpassword(), userReset.getConfirmPassword()))
            return new AjaxResponse(false, ValidationFormatter.format(CONFIRM_PWD_LABEL,
                    ValidationErrorCode.REPEATED_PASSWORD_NOT_MATCH));

        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        userReset.setRequestedAction(ServiceConstants.SET_PASSWORD);
        userReset.setCredentialId(profileService.getCredentialId(serviceErrors));

        String status = logonService.updatePassword(userReset, serviceErrors);
        if (!Attribute.SUCCESS.equalsIgnoreCase(status)) {
            return processUpdatePasswordFailure(status, serviceErrors);
        }

        logger.info(LogMarkers.AUDIT,
                "Forgotten Password Step2: Trying to update Password. Status [{}]",
                LogMarkers.Status.SUCCESS);

        logger.debug("exiting setForgotPassword method:");
        return new AjaxResponse(true, userReset);
    }


    private AjaxResponse processUpdatePasswordFailure(String status, ServiceErrors serviceErrors) {
        //Audit for updating forgotten password
        logger.info(LogMarkers.AUDIT,
                "Forgotten Password Step2: Trying to update Password. Status [{}] Reason [{}]",
                LogMarkers.Status.FAILED,
                cmsService.getContent(status));
        Iterator<ServiceError> serviceError = serviceErrors.getErrorList().iterator();
        while (serviceError.hasNext()) {
            ServiceError serror = serviceError.next();
            return new AjaxResponse(false, serror.getReason());
        }

        return new AjaxResponse(false, cmsService.getContent(status));
    }


    /**
     * Forgot Password - Step 1 - Verification of user input and then sends SMS code to user
     *
     * @param model
     * @param bindingResult
     * @param request
     * @return
     * @throws Exception
     */
    //TODO Too complex, needs to be broken down
    @RequestMapping(value = "/public/api/verifySmsAndForgotPassword", method = RequestMethod.GET)
    public
    @ResponseBody
    AjaxResponse verifySmsAndForgotPassword(@Valid @ModelAttribute(Attribute.SMS_CODE_MODEL) SmsCodeModel model,
                                            BindingResult bindingResult, HttpServletRequest request) throws Exception {
        logger.info("Forgot Password Step-1 Data: " + model.toString());
        if (bindingResult.hasErrors()) {
            request.getSession().removeAttribute(USER_CODE);
            return new AjaxResponse(false, ValidationFormatter.format(bindingResult));
        }

        HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
        SafiAnalyzeAndChallengeResponse analyzeResult = (SafiAnalyzeAndChallengeResponse) request.getSession()
                .getAttribute("safiAnalyzeResult");

        if (analyzeResult != null) {
            logger.info("analyse result usernames are main{} identificationData {}",
                    analyzeResult.getUserName(),
                    analyzeResult.getIdentificationData().getUserName());
            //Set the transaction ID for the next request
            analyzeResult.setTransactionId(UUID.randomUUID().toString());
        }

        SafiAuthenticateRequest authRequest = new SafiAuthenticateRequest(requestParams, analyzeResult, model.getSmsCode());
        SafiAuthenticateResponse authResult = twoFactorAuthenticationFacade.authenticate(authRequest);

        if (model.getSmsCode() != null && !"".equals(model.getSmsCode()) && !authResult.isSuccessFlag()) {
            LogMarkers.audit_registration(logger,
                    model.getUserCode(),
                    model.getLastName(),
                    model.getPostcode(),
                    model.getSmsCode(),
                    LogMarkers.Status.FAILED,
                    cmsService.getContent(ValidationErrorCode.INVALID_SMS_CODE));

            request.getSession().removeAttribute(USER_CODE);
            return new AjaxResponse(false, cmsService.getContent(authResult.getDisplayMessageCode()));
        } else {

            LogMarkers.audit_registration(logger,
                    model.getUserCode(),
                    model.getLastName(),
                    model.getPostcode(),
                    model.getSmsCode(),
                    LogMarkers.Status.SUCCESS);
            request.getSession().setAttribute(USER_CODE, model.getUserCode());

            CustomerTokenRequestModel customerTokenRequest = new CustomerTokenRequestModel();
            String zNumber = analyzeResult.getzNumber();
            customerTokenRequest.populateCustomerNumber(zNumber, analyzeResult.getIdentificationData().getUserName());
            customerTokenRequest.setxForwardedHost(requestQuery.getOriginalHost());
            customerTokenRequest.setToken(requestQuery.getSamlToken());

            RegistrationResponse registrationResponse = registrationService.createRegistrationResponse(customerTokenRequest,
                    relayStatePath,
                    etpUrlPath,
                    adviserEtpUrlPath,
                    "");

            return new AjaxResponse(registrationResponse);
        }
    }

    /**
     * validate user and send SMS Code before forget password.
     *
     * @param conversation SmsCodeModel.
     * @return * {@link AjaxResponse}
     * @throws Exception
     */
    @RequestMapping(value = "/public/api/forgetPasswordValidate", method = RequestMethod.GET)
    public
    @ResponseBody
    AjaxResponse forgetPasswordValidate(@Valid @ModelAttribute(Attribute.SMS_CODE_MODEL) SmsCodeModel conversation)
            throws Exception {
        logger.info("Sending SMS code: ");
        String status = logonService.validateUser(conversation.getUserCode(),
                conversation.getLastName(),
                Integer.parseInt(conversation.getPostcode()));
        logger.info("Received status: {}", status);
        //TODO: Need to handle the maximum 2FA attempts.  Error message is ValidationErrorCode.MAX_2FA_ATTEPTS_EXCEEDED.
        switch (status) {
            case Attribute.SUCCESS_MESSAGE:
                //TODO: Call the new 2FA service........
                //return new AjaxResponse(true, smsService.sendSmsCodeFromSafi());
                return new AjaxResponse(true, "true");
            case Attribute.ACCOUNT_LOCKED_MESSAGE:
                return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.FORGETPASSWORD_ACCOUNT_LOCKED));
            default:
                return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.FORGETPASSWORD_INVALID_USER_DETAILS));
        }
    }

    /**
     * Verify SMS Code before forget password.
     *
     * @param conversation {@link SmsCodeModel}
     * @return * {@link AjaxResponse}
     * @throws Exception
     */
    @RequestMapping(value = "/public/api/forgetPasswordVerifySms", method = RequestMethod.GET)
    public
    @ResponseBody
    AjaxResponse forgetPasswordVerifySms(@Valid @ModelAttribute(Attribute.SMS_CODE_MODEL) SmsCodeModel conversation,
                                         BindingResult bindingResult) throws Exception {
        logger.info("Forget Password Conversation: {}" + conversation.toString());

        logger.info("SmsCode: " + conversation.getSmsCode());
        if (bindingResult.hasErrors()) {
            return new AjaxResponse(false, ValidationFormatter.format(bindingResult));
        }

        String status = logonService.verifySmsCode(conversation.getUserCode(),
                conversation.getLastName(),
                Integer.parseInt(conversation.getPostcode()),
                conversation.getSmsCode());
        String errorMessage = "";

        switch (status) {
            case Attribute.SUCCESS_MESSAGE:
                LogMarkers.audit_forgottenPassword(logger, conversation.getUserCode(), conversation.getLastName(), conversation.getPostcode(),
                        conversation.getSmsCode(), LogMarkers.Status.SUCCESS);
                return new AjaxResponse(true, true);
            case Attribute.FAILURE_MESSAGE:
                errorMessage = cmsService.getContent(ValidationErrorCode.FORGETPASSWORD_INVALID_USER_DETAILS);
                break;
            case Attribute.ERROR_MESSAGE:
                errorMessage = cmsService.getContent(ValidationErrorCode.FORGETPASSWORD_SMS_CODE_EXPIRED);
                break;
            default:
                errorMessage = cmsService.getContent(ValidationErrorCode.FORGETPASSWORD_INVALID_DATA);
                break;
        }
        //Log Failure and return
        LogMarkers.audit_forgottenPassword(logger, conversation.getUserCode(),
                conversation.getLastName(), conversation.getPostcode(), conversation.getSmsCode(), LogMarkers.Status.FAILED,
                errorMessage);
        return new AjaxResponse(false, errorMessage);
    }

    @RequestMapping(value = "/secure/page/forgotpasswordsteptwo", method = RequestMethod.GET)
    public String registrationStepTwo(ModelMap model) {

        prepareModel(model);
        if (null != profileService.getSamlToken())
            model.put("userType", getUserType(profileService.getSamlToken().getUserGroup()));
        if (null != profileService.getUsername())
            model.put("userId", profileService.getUsername());

        return View.FORGOT_PASSWORD_STEP_TWO;
    }

    public String getUserType(List userGroup) {
        if (Properties.getSafeBoolean("wpl.integration.enabled"))
            for (int i = 0; i < userGroup.size(); i++)
                if (userGroup.get(i).equals(UserGroup.WPL_USER))
                    return UserGroup.WPL_USER.toString();
        return UserGroup.PAN_USER.toString();
    }

    @ExceptionHandler(ServiceException.class)
    public
    @ResponseBody
    AjaxResponse processServiceException(ServiceException e) {
        return new AjaxResponse(false, e.getErrors());

    }

    @ExceptionHandler(Exception.class)
    public
    @ResponseBody
    AjaxResponse processGeneralException(Exception e) {
        logger.error("Error while processing forgotten password", e);
        return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.ERROR_IN_REGISTRATION));

    }

    @ExceptionHandler(SoapFaultClientException.class)
    public
    @ResponseBody
    AjaxResponse processSoapFaultException(SoapFaultClientException ex) {
        logger.warn("Error from downstream webservice while updating password: {}", ex.getFaultStringOrReason(), ex);
        return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.ERROR_IN_REGISTRATION));

    }

}
