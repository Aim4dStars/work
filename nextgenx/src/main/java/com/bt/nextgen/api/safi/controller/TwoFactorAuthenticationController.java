
package com.bt.nextgen.api.safi.controller;

import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.util.LogMarkers;
import com.bt.nextgen.core.validation.ValidationFormatter;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.login.web.controller.RegistrationResponse;
import com.bt.nextgen.login.web.model.SmsCodeModel;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.safi.model.*;
import com.bt.nextgen.service.security.converter.HttpRequestConverter;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/*
    Single Responsibility Principle,
    Generic exceptions should never be thrown
*/
@SuppressWarnings({"squid:S1200", "squid:S00112",
                "checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck"})
@Controller
public class TwoFactorAuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthenticationController.class);

    @Autowired
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationFacade;

    @Autowired
    private CmsService cmsService;


    /**
     * 
     * @param eventModel
     * @param session
     * @return
     * @throws Exception
     */
	@RequestMapping(value = {"/secure/api/analyze", "/public/api/analyze"}, method = RequestMethod.POST)
	public @ResponseBody AjaxResponse analyze(@Valid @ModelAttribute(Attribute.EVENT_MODEL) EventModel eventModel, BindingResult bindingResult, HttpSession session, HttpServletRequest request) throws Exception
	{
		logger.info("Safi Analyze request starts");

		if (bindingResult.hasErrors()) {
			logger.info("analyse request contains input errors", eventModel.toString());
			return new AjaxResponse(false, ValidationFormatter.format(bindingResult));
		}
		
		HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
		SafiAnalyzeRequest analyzeRequest = new SafiAnalyzeRequest();
		analyzeRequest.setEventModel(eventModel);
		analyzeRequest.setHttpRequestParams(requestParams);
		
		ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
		SafiAnalyzeAndChallengeResponse result = twoFactorAuthenticationFacade.analyze(analyzeRequest, serviceErrors);		
		session.setAttribute("safiAnalyzeResult", result);
			
		AjaxResponse ajaxResponse = null;
		
		if (serviceErrors.hasErrors())
			ajaxResponse = new AjaxResponse(false, serviceErrors.getErrorMessagesForScreenDisplay());
		else
			ajaxResponse = new AjaxResponse(result.getActionCode());
		
		return ajaxResponse;
	}
	

	/**
	 * Invoke SAFI challenge request which will result in an SMS challenge request being sent to the user
	 * @param session
	 * @return
	 * @throws Exception
	 */
    @RequestMapping(value = {"/secure/api/sendSmsCode", "/public/api/sendSmsCode"})
    public @ResponseBody
    AjaxResponse sendSmsCode(HttpServletRequest request, HttpSession session) throws Exception 
    {
    	logger.info("Controller: START OF Attempting to challenge user (sms code send)");
    	
    	HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
    	SafiAnalyzeAndChallengeResponse analyzeResult = (SafiAnalyzeAndChallengeResponse) session.getAttribute("safiAnalyzeResult");
    	SafiChallengeRequest challengeRequest = new SafiChallengeRequest(requestParams, analyzeResult);
    	    	
    	ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
    	SafiAnalyzeAndChallengeResponse result = twoFactorAuthenticationFacade.challenge(challengeRequest, serviceErrors);
		session.setAttribute("safiAnalyzeResult", result);
    	
    	logger.info("Controller: END OF Attempting to challenge user (sms code send)");
    	
    	
    	AjaxResponse ajaxResponse = null;
    	
    	if (serviceErrors.hasErrors())
			ajaxResponse = new AjaxResponse(result.getActionCode(), cmsService.getContent("err00100") + " (" + serviceErrors.getErrorMessagesForScreenDisplay() + ")");
		else
			ajaxResponse = new AjaxResponse(result.getActionCode());
		
    	return ajaxResponse;
    }


  
	/**
	 * Validates that the SMS code entered by the user matches the one issued by SAFI challenge request
	 * @param smsCodeModel
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"/public/api/verify", "/secure/api/verify"}, method = RequestMethod.GET)
	public @ResponseBody AjaxResponse verifySmsCode(@Valid @ModelAttribute(Attribute.SMS_CODE_MODEL) SmsCodeModel smsCodeModel, BindingResult bindingResult, HttpServletRequest request, HttpSession session) throws Exception
	{
		if (bindingResult.hasErrors()) {
			logger.info("Authentication request contains input errors", smsCodeModel.toString());
			return new AjaxResponse(false, ValidationFormatter.format(bindingResult));
		}

		logger.info("SmsCode: " + smsCodeModel.getSmsCode());
		
		HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
		
		SafiAnalyzeAndChallengeResponse analyzeResult = (SafiAnalyzeAndChallengeResponse) session.getAttribute("safiAnalyzeResult");
    	if(analyzeResult != null)
    	{
    		analyzeResult.setUserName(smsCodeModel.getUserCode());
    	}

		//    	String samlToken = new String(Base64.encodeBase64(applicationSamlService.getSamlToken().toString().getBytes()));
    	SafiAuthenticateRequest authRequest = new SafiAuthenticateRequest(requestParams, analyzeResult, smsCodeModel.getSmsCode());
    	
    	
    	SafiAuthenticateResponse authResult = twoFactorAuthenticationFacade.authenticate(authRequest);
    	    	
		//TODO Call back-end service to verify the sms code.
        if(smsCodeModel.getSmsCode() != null && !"".equals(smsCodeModel.getSmsCode()) && !authResult.isSuccessFlag())
        {
            return new AjaxResponse(false, cmsService.getContent(authResult.getDisplayMessageCode()));
        }
        else
        {
            try{
                LogMarkers.audit_registration(logger, smsCodeModel.getUserCode(), smsCodeModel.getLastName(), smsCodeModel.getPostcode(), smsCodeModel.getSmsCode(), LogMarkers.Status.SUCCESS);
                //TODO: Need to change the implementation during integration.

                //SamlToken userSaml = null;
                //SamlToken userSaml = registrationService.generateUserSAML(authResult.getUsername(), request.getHeader(SECURITY_HEADER_XFORWARDHOST.value()), getSamlTokenFromRequest(request));

                /*
                RegistrationResponse registrationResponse = registrationService.createRegistrationResponse(userSaml,
                        request.getHeader(SECURITY_HEADER_XFORWARDHOST.value()), forgotPasswordStep2,etpUrlPath,adviserEtpUrlPath, request.getContextPath() );
				*/
                RegistrationResponse registrationResponse = null;
                return new AjaxResponse(registrationResponse);
            }
            catch (Exception err)
            {
                logger.error("There was an error with forgotten password, user {} could not log in.  ",authResult.getUsername(),err);
                return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.ERROR_IN_REGISTRATION));
            }
            
        }
    }

//    private SamlToken getSamlTokenFromRequest(HttpServletRequest request)
//    {
//        String samlString = request.getHeader(SAML_HEADER_WBC.value());
//        SamlToken token = new SamlToken(samlString);
//        return token;
//    }
}
