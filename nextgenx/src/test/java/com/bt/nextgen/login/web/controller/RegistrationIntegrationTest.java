package com.bt.nextgen.login.web.controller;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.login.service.RegistrationService;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.login.web.model.SmsCodeModel;
import org.apache.struts.mock.MockHttpSession;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import static org.junit.Assert.*;

public class RegistrationIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    RegistrationController registrationController;

    @Autowired
    RegistrationService service;

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    MockHttpSession mockHttpSession;


    @Test
    public void testRegistrationWorkflow() throws Exception {
        SmsCodeModel smsCodeModel = new SmsCodeModel();
        smsCodeModel.setPostcode("1111");
        smsCodeModel.setLastName("Test");
        smsCodeModel.setUserCode("1");
        request = new MockHttpServletRequest();
        mockHttpSession = new MockHttpSession();
        BindingResult bindingResult = new BindException(new BeanPropertyBindingResult(new Object(), ""));
        request.setSession(mockHttpSession);

        AjaxResponse smsCodeResponse = registrationController.validateRegistration(smsCodeModel, bindingResult, request, mockHttpSession);

        validateResponse(smsCodeResponse);

        AjaxResponse smsCodeVerificationResponse = null;

        if (smsCodeResponse.isSuccess()) {
            smsCodeVerificationResponse = registrationController.verifySmsCode(smsCodeModel, bindingResult);
            validateResponse(smsCodeVerificationResponse);
        }

        if (smsCodeResponse.isSuccess() && smsCodeVerificationResponse.isSuccess()) {
            RegistrationModel registrationModel = new RegistrationModel();
            registrationModel.setUserCode("001");
            registrationModel.setPassword("abc1234");
            registrationModel.setConfirmPassword("abcd1234");
            ModelMap modelMap = new ModelMap();

            bindingResult = new BindException(new BeanPropertyBindingResult(new Object(), ""));

            AjaxResponse userRegistrationResponse = registrationController.registerUser(registrationModel,
                    bindingResult, null);

            assertTrue("Expect sucess response", userRegistrationResponse.isSuccess());
        }
    }

    private void validateResponse(AjaxResponse ajaxResponse) {
        assertThat(ajaxResponse, IsNull.notNullValue());
        assertThat(ajaxResponse.getData(), IsNull.notNullValue());
        assertThat(ajaxResponse.isSuccess(), Is.is(false));
    }
}

	
	
	