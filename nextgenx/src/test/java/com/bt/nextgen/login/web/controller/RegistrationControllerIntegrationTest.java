package com.bt.nextgen.login.web.controller;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.login.web.model.CredentialsModel;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.login.web.model.SmsCodeModel;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import static org.junit.Assert.*;


public class RegistrationControllerIntegrationTest extends BaseSecureIntegrationTest {
    @Autowired
    private RegistrationController registrationController;

    @SecureTestContext
    @Test
    public void testRegisterUserFails() throws Exception {
        AjaxResponse result = testRegistrationStep2("001", "Invalid_Password_1");
        assertNotNull(result);
        assertThat(result.getData(), IsNull.notNullValue());
        assertEquals(false, result.isSuccess());
    }

    @SecureTestContext(username = "cashAdviser")
    @Test
    public void testRegisterUserSucceeds() throws Exception {
        AjaxResponse result = testRegistrationStep2("001", "Password_1");
        assertNotNull(result);
        assertThat(result.getData(), IsNull.notNullValue());
        assertEquals(true, result.isSuccess());
    }

    private AjaxResponse testRegistrationStep2(String username, String password) throws Exception {
        RegistrationModel model = new RegistrationModel();
        model.setUserCode(username);
        model.setPassword(password);
        model.setConfirmPassword(password);
        model.setRequestedAction("SetPassword");
        model.setRequestedAction("SetPassword");
        BindingResult bindingResult = new BindException(new BeanPropertyBindingResult(new Object(), ""));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("userCode", "adviser");
        return registrationController.registerUser(model, bindingResult, request);


    }

    @SecureTestContext
    @Test
    public void testValidateRegistration() throws Exception {
        SmsCodeModel model = new SmsCodeModel();
        model.setPostcode("110011001100");
        model.setLastName("test");
        model.setUserCode("1010");
        BindingResult bindingResult = new BindException(new BeanPropertyBindingResult(new Object(), ""));

        AjaxResponse result = registrationController.validateRegistration(model, bindingResult, new MockHttpServletRequest(), new MockHttpSession());
        assertNotNull(result);
        assertThat(result.getData(), IsNull.notNullValue());
    }

    @SecureTestContext(username = "iccexplodes")
    @Test
    public void testValidateRegistrationForSoapFault() throws Exception {
        SmsCodeModel model = new SmsCodeModel();
        model.setPostcode("110011001100");
        model.setLastName("test");
        model.setUserCode("1010");
        BindingResult bindingResult = new BindException(new BeanPropertyBindingResult(new Object(), ""));

        AjaxResponse result = registrationController.validateRegistration(model, bindingResult, new MockHttpServletRequest(), new MockHttpSession());
        assertNotNull(result);
        assertThat(result.getData(), IsNull.notNullValue());
        String message = (String) result.getData();
        assertTrue(message.startsWith("We are currently experiencing technical difficulties. Sorry for the inconvenience. Please try again."));
    }

    @SecureTestContext
    @Test
    public void testValidateRegistration_fail() throws Exception {
        SmsCodeModel model = new SmsCodeModel();
        model.setPostcode("00001");
        model.setLastName("Lastname");
        model.setUserCode("1");
        BindingResult bindingResult = new BindException(new BeanPropertyBindingResult(new Object(), ""));

        AjaxResponse result = registrationController.validateRegistration(model, bindingResult, new MockHttpServletRequest(), new MockHttpSession());
        assertNotNull(result);
        assertEquals(false, result.isSuccess());
    }

    @Test
    @SecureTestContext(jobRole = "ADVISER", customerId = "297129090", jobId = "", profileId = "")
    public void testVerifySmsAndRegistration() throws Exception {
        SmsCodeModel model = new SmsCodeModel();
        model.setPostcode("00001");
        model.setLastName("Lastname");
        model.setUserCode("1");
        model.setSmsCode("2345");
        MockHttpServletRequest request = new MockHttpServletRequest();

        BindingResult bindingResult = new BindException(new BeanPropertyBindingResult(new Object(), ""));
        AjaxResponse result = registrationController.verifySmsAndRegistration(model, bindingResult, request);
        String message = (String) result.getData();
        assertTrue(message.startsWith("We are currently experiencing technical difficulties. Sorry for the inconvenience. Please try again."));
    }

    @SecureTestContext
    @Test
    public void testValidateCredentials() throws Exception {
        CredentialsModel model = new CredentialsModel();
        model.setPostcode("110011001100");
        model.setLastName("test");
        model.setUserCode("1010");

        MockHttpServletRequest request = new MockHttpServletRequest();

        BindingResult bindingResult = new BindException(
                new BeanPropertyBindingResult(new Object(), ""));

        AjaxResponse response = registrationController.validateCredentials(
                model, bindingResult, request);

        assertNotNull(response);
        assertThat(response.getData(), IsNull.notNullValue());
    }

    @SecureTestContext
    @Test
    public void testValidateCredentialsFail() throws Exception {
        CredentialsModel model = new CredentialsModel();
        model.setPostcode("00001");
        model.setLastName("Lastname");
        model.setUserCode("1");
        BindingResult bindingResult = new BindException(
                new BeanPropertyBindingResult(new Object(), ""));

        AjaxResponse result = registrationController.validateCredentials(model,
                bindingResult, new MockHttpServletRequest());
        assertNotNull(result);
        assertEquals(false, result.isSuccess());
    }

    @SecureTestContext
    @Test
    public void testForgotPassword() throws Exception {
        CredentialsModel model = new CredentialsModel();
        model.setPostcode("110011001100");
        model.setLastName("test");
        model.setUserCode("1010");

        MockHttpServletRequest request = new MockHttpServletRequest();

        BindingResult bindingResult = new BindException(
                new BeanPropertyBindingResult(new Object(), ""));

        AjaxResponse response = registrationController.forgotPassword(
                model, bindingResult, request);

        assertNotNull(response);
        assertThat(response.getData(), IsNull.notNullValue());
    }

    @SecureTestContext
    @Test
    public void testForgotPasswordFail() throws Exception {
        CredentialsModel model = new CredentialsModel();
        model.setPostcode("00001");
        model.setLastName("Lastname");
        model.setUserCode("1");
        BindingResult bindingResult = new BindException(
                new BeanPropertyBindingResult(new Object(), ""));

        AjaxResponse result = registrationController.forgotPassword(model,
                bindingResult, new MockHttpServletRequest());
        assertNotNull(result);
        assertEquals(false, result.isSuccess());
    }

}
