package com.bt.nextgen.api.safi.controller;

import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.api.safi.model.SafiResponseDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.payments.domain.PayeeType;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.safi.model.*;
import com.bt.nextgen.service.security.converter.HttpRequestConverter;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by L072457 on 24/11/2015.
 */
@SuppressWarnings("squid:S1200") //Single Responsibility Principle
@Controller
public class TwoFactorAuthenticationBaseController {

    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthenticationBaseController.class);

    @Autowired
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationFacade;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    protected ClientIntegrationService clientIntegrationService;

    @Autowired
    protected UserProfileService profileService;

    @Autowired
    public CmsService cmsService;


    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck")
    @RequestMapping(method = RequestMethod.GET, value = "/safiAnalyze", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    AjaxResponse safiAnalyze(@RequestParam(required = true, value = "eventType") String eventType,
                             HttpServletRequest request, HttpSession session) {
        logger.info("Start attempting to safi analyze. session id {}", session.getId());
        try {
            EventModel eventModel = getEventModel(eventType);
            HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
            SafiAnalyzeRequest analyzeRequest = new SafiAnalyzeRequest();
            analyzeRequest.setEventModel(eventModel);
            analyzeRequest.setHttpRequestParams(requestParams);

            ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
            SafiAnalyzeAndChallengeResponse analyzeResult = twoFactorAuthenticationFacade.analyze(analyzeRequest, serviceErrors);
            set2FAStatusSession(session, eventType, false);
            if (null != analyzeResult) {
                analyzeResult.setEventType(eventType);
                session.setAttribute("analyze-result-" + analyzeResult.getTransactionId(), analyzeResult);
                String deviceNumber = populateDeviceNumber();// Get Device
                // Number & Mask it
                int length = deviceNumber.length();
                deviceNumber = deviceNumber.substring(0, length - 8) + "#####" + deviceNumber.substring(length - 3, length);
                if (analyzeResult.getActionCode()) {
                    return new AjaxResponse(true, deviceNumber + "|" + analyzeResult.getTransactionId());
                } else {
                    set2FAStatusSession(session, eventType, true);
                    return new AjaxResponse(false, deviceNumber + "|" + analyzeResult.getTransactionId());
                }
            } else if (serviceErrors.hasErrors()) {
                return new AjaxResponse(false, serviceErrors.getErrorMessagesForScreenDisplay());
            } else {
                logger.info("SAFI analyzeResult not found from session id {}", session.getId());
            }
        } catch (Exception e) {
            logger.error("SAFI Analyze Exception", e);
        }
        return new AjaxResponse(false, Attribute.SYSTEM_UNAVAILABLE);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/safiChallenge", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    AjaxResponse safiChallenge(@RequestParam(required = true, value = Attribute.TRANSACTION_ID) String transactionId,
                               HttpSession session, HttpServletRequest request) {
        logger.info("Start attempting to challenge user (sms code send). session id {}", session.getId());
        HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
        if (StringUtils.isBlank(transactionId))
            return new AjaxResponse(false, "Mandatory field is missing.");

        SafiAnalyzeAndChallengeResponse analyzeResult = (SafiAnalyzeAndChallengeResponse) session
                .getAttribute("analyze-result-" + transactionId);
        if (analyzeResult != null && analyzeResult.getActionCode()) {
            logger.info("analyse result user names are main {} identificationData {}", analyzeResult.getUserName(),
                    analyzeResult.getIdentificationData().getUserName());

            SafiChallengeRequest challengeRequest = new SafiChallengeRequest(requestParams, analyzeResult);
            SafiAnalyzeAndChallengeResponse challengeResponse = twoFactorAuthenticationFacade.challenge(challengeRequest);

            if (challengeResponse == null) {
                logger.info("SAFI challenge request response null.");
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, ValidationErrorCode.SYSTEM_UNAVAILABLE);
            } else if (challengeResponse.getActionCode() == false) {
                return new AjaxResponse(false, "Unable to perform challenge");
            }
        } else {
            logger.info("analyse result from session is null. session id {}", session.getId());
            return new AjaxResponse(false, "SAFI challenge request failed.");
        }
        return new AjaxResponse(true, "SAFI challenge request successful.");
    }

    @SuppressWarnings("squid:S00112") // Generic exceptions should never be thrown
    @RequestMapping(method = RequestMethod.POST, value = "/verify", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    AjaxResponse verifySmsCode(@RequestParam(required = false, value = Attribute.TRANSACTION_ID) String transactionId,
                               @RequestParam(required = false, value = Attribute.SMS_CODE) String smsCode,
                               HttpSession session, HttpServletRequest request) throws Exception {
        SafiResponseDto safiResponseDto = safiAuthenticate(transactionId, smsCode, session, request);
        return new AjaxResponse(safiResponseDto.isSuccessFlag(), safiResponseDto.isSuccessFlag() ? "SAFI verification is successful." : safiResponseDto.getErrorMessage());
    }

    /**
     * @param transactionId
     * @param smsCode
     * @param session
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressWarnings("squid:S00112") // Generic exceptions should never be thrown
    public SafiResponseDto safiAuthenticate(String transactionId, String smsCode,
                                            HttpSession session, HttpServletRequest request) throws Exception {
        SafiResponseDto safiResponseDto = new SafiResponseDto();
        SafiAnalyzeAndChallengeResponse analyzeResult = (SafiAnalyzeAndChallengeResponse) session
                .getAttribute("analyze-result-" + transactionId);

        if (analyzeResult != null) {
            // only if action code is true and sms code is not blank
            if (analyzeResult.getActionCode() && StringUtils.isNotBlank(smsCode)) {
                HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
                // matching request transaction id
                if (StringUtils.isNotBlank(analyzeResult.getTransactionId())
                        && analyzeResult.getTransactionId().equals(transactionId)) {
                    SafiAuthenticateRequest authenticateRequest = new SafiAuthenticateRequest(requestParams, analyzeResult,
                            smsCode);
                    SafiAuthenticateResponse authResult = twoFactorAuthenticationFacade.authenticate(authenticateRequest);
                    // TODO - If authentication is failed three time.
                    if (!authResult.isSuccessFlag()) {
                        logger.info("SMS code is mismatched.");
                        safiResponseDto.setSuccessFlag(false);
                        safiResponseDto.setErrorId(Constants.INVALID_SMS_CODE);
                        safiResponseDto.setErrorMessage(cmsService.getContent(authResult.getDisplayMessageCode()));
                    } else {
                        // removing session entry in case of success
                        safiResponseDto.setSuccessFlag(true);
                        if (transactionId != null) {
                            set2FAStatusSession(session, analyzeResult.getEventType(), true);
                            session.removeAttribute("analyze-result-" + transactionId);
                        }
                    }
                } else {
                    // in case of transaction id is not same
                    logger.info("Passed transaction id is not valid.");
                    safiResponseDto.setErrorMessage("Transaction Id is not valid");
                }
            } else if (analyzeResult.getActionCode() && StringUtils.isBlank(smsCode)) {
                // if authentication (getActionCode is true) is required and sms code is blank.
                logger.info("SAFI authentication is required.");
                safiResponseDto.setErrorMessage("Sms code can not be null or empty");
            }
        } else {
            logger.info("SAFI analyze result is null.");
            safiResponseDto.setErrorMessage("Analyze request result not found from session.");
        }
        return safiResponseDto;
    }

    private String populateDeviceNumber() {
        IndividualDetailImpl clientDetail = (IndividualDetailImpl) clientIntegrationService.loadClientDetails(profileService
                .getActiveProfile().getClientKey(), new FailFastErrorsImpl());
        if (null != clientDetail) {
            List<Phone> listPhone = clientDetail.getPhones();
            for (Phone phone : listPhone) {
                if (phone.getType().equals(AddressMedium.MOBILE_PHONE_PRIMARY))
                    return phone.getNumber();
            }
        }
        return null;
    }


    private EventModel getEventModel(String eventType) {
        EventModel eventModel = new EventModel();
        switch (eventType) {
            case "LINKED":
                eventModel.setPayeeType(PayeeType.LINKED);
                eventModel.setClientDefinedEventType("ADD_PAYEE");
                eventModel.setEventDescription("LINKED");
                break;
            case "BPAY":
                eventModel.setPayeeType(PayeeType.BPAY);
                eventModel.setClientDefinedEventType("ADD_PAYEE");
                eventModel.setEventDescription("BPAY");
                break;
            case "PAY_ANYONE":
                eventModel.setPayeeType(PayeeType.PAY_ANYONE);
                eventModel.setClientDefinedEventType("ADD_PAYEE");
                eventModel.setEventDescription("PAY_ANYONE");
                break;
            case "USER_DETAILS":
            case "SUPER_SEARCH_CONSENT":
                eventModel.setClientDefinedEventType(eventType);
                break;
            default:
                eventModel.setPayeeType(PayeeType.PAY_ANYONE);
                eventModel.setClientDefinedEventType("CHANGE_DAILY_LIMIT");
                eventModel.setEventDescription("CHANGE_DAILY_LIMIT");
                break;
        }
        return eventModel;
    }

    private void set2FAStatusSession(HttpSession session, String eventType, boolean status) {
        if (("USER_DETAILS").equalsIgnoreCase(eventType) ||
                ("LINKED").equalsIgnoreCase(eventType))
            session.setAttribute(eventType + "_2FA_VERIFIED", status);
    }
//    private List<ServiceError> populateErrorCode(SafiAuthenticateResponse authResult) {
//        List<ServiceError> errors = new ArrayList<>();
//        ServiceError serviceError = new ServiceErrorImpl();
//        serviceError.setId(Constants.INVALID_SMS_CODE);
//        if (null != authResult.getStatusCode()) {
//            serviceError.setReason(cmsService.getContent(authResult.getDisplayMessageCode()));
//        }
//
//        errors.add(serviceError);
//        return errors;
//    }
}
