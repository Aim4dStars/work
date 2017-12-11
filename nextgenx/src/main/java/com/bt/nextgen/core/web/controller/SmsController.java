package com.bt.nextgen.core.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.logon.service.LogonService;
import com.bt.nextgen.service.security.SmsService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;

@Deprecated
@Controller
/**
 * This class has been deprecated .
 * User TwoFactorAuthenticationController class instead.
 * @author L053201
 *
 */
public class SmsController {

    private static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    @Autowired
    private SmsService smsService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private LogonService logonService;

	/**
	 * *************************************************************************************************************************************
	 * *********************************** Analyze Sms Code ********************************************************************************
	 * *************************************************************************************************************************************
	 */
	//@RequestMapping(value = "/public/api/analyze", method = RequestMethod.POST)
	public @ResponseBody AjaxResponse analyze(@ModelAttribute(Attribute.EVENT_MODEL) EventModel eventModel) throws Exception
	{
		logger.info("Analyzing 2FA required or not");
		return new AjaxResponse(smsService.analyzeFromSafi(eventModel), null);
	}
	
	/**
	 * *************************************************************************************************************************************
	 * *********************************** Challenge Sms Code *******************************************************************************
	 * *************************************************************************************************************************************
	 */
    //@RequestMapping(value = "/public/api/sendSmsCode", method = RequestMethod.POST)
    public @ResponseBody
    AjaxResponse sendSmsCode() throws Exception {
        return new AjaxResponse();
    }

    /**
     * Challenge Sms Code.
     *
     * @param userCode Username.
     * @param lastName Last name of the user.
     * @param postcode Postal code.
     * @param request  {@link HttpServletRequest}
     * @return * {@link AjaxResponse}
     * @throws Exception
     */
//    @RequestMapping(value = "/public/api/sendSmsCode", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    AjaxResponse sendSmsCode(String userCode, @RequestParam("lastName")String lastName, @RequestParam("postcode")int postcode,
//                             HttpServletRequest request) throws Exception {
//
//        logger.info("Sending SMS code: ");
//        String useStatus = logonService.validateUserName(userCode);
//        if ((null != useStatus) && !useStatus.equals(Attribute.ERROR_MESSAGE)) {
//            if (logonService.validateUser(userCode, lastName, postcode)) {
//                return new AjaxResponse(smsService.sendSmsCodeFromSafi());
//            } else {
//                int attempts = 0;
//                if (null != request.getSession().getAttribute("attempts")) {
//                    attempts = Integer.parseInt(request.getSession().getAttribute("attempts").toString());
//                    if (attempts >= 2) {
//                        logonService.lockUser(userCode);
//                        return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.FORGETPASSWORD_ACCOUNT_LOCKED));
//                    }
//                    attempts++;
//                } else {
//                    attempts = 1;
//                }
//                request.getSession().setAttribute("attempts", attempts);
//            }
//        }
//        return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.FORGETPASSWORD_INVALID_USER_DETAILS));
//
//    }

  
	/**
	 * *************************************************************************************************************************************
	 * *********************************** Validate Sms Code *******************************************************************************
	 * *************************************************************************************************************************************
	 */
	//@RequestMapping(value = "/public/api/verify", method = RequestMethod.GET)
	public @ResponseBody AjaxResponse verifySmsCode(String smsCode) throws Exception
	{
		logger.info("SmsCode: " + smsCode);

		//TODO Call back-end service to verify the sms code.
        if(smsCode != null && !smsCode.equals("") && !smsService.authenticateSmsCodeFromSafi(smsCode))
        {
            return new AjaxResponse(false, cmsService.getContent(ValidationErrorCode.INVALID_SMS_CODE));
        }

        return new AjaxResponse(true, "");
    }
}
