package com.bt.nextgen.web.controller;

import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.CredentialService;
import com.bt.nextgen.core.service.MessageService;
import com.bt.nextgen.core.web.RequestQuery;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.login.web.model.SmsCodeModel;
import com.bt.nextgen.logon.service.LogonService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.security.SmsService;
import com.bt.nextgen.util.SamlUtil;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ForgotPasswordControllerTest {

    private Base64 base64 = new Base64();

    private AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    private static final String GET_METHOD = RequestMethod.GET.name();
    private static final String POST_METHOD = RequestMethod.POST.name();

    @InjectMocks
    ForgotPasswordController forgotPasswordController;
    @Mock
    private LogonService mockLogonService;

    @Mock
    private CmsService mockCmsService;

    @Mock
    private SmsService mockSmsService;

    @Mock
    private RequestQuery requestQuery;

    @Mock
    private MessageService mockMessageService;

    @Mock
    private CredentialService credentialService;

    @Mock
    private UserProfileService profileService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    private static final String CMS_CONTENT = "cmsContent";

    @Before
    public void setup() throws Exception {
        request = new MockHttpServletRequest();

        request.setParameter("userName", "investor");
        request.setParameter("password", "investor");
        request.setParameter("modifiedUsername", "newuser");
        request.setParameter("confirmPassword", "hello1234");
        request.setParameter("newPassword", "hello1234");
        request.setParameter("halgm", new String(base64.encode("test1234".getBytes())));

        response = new MockHttpServletResponse();

        when(requestQuery.isWebSealRequest()).thenReturn(false);

        SamlToken token = new SamlToken(SamlUtil.loadSaml());

        when(requestQuery.getSamlToken()).thenReturn(token);
        annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
    }

    @Test
    public void testForgetPasswordValidation_ForInvalidUser() throws Exception {
        when(mockLogonService.validateUser(anyString(), anyString(), anyInt())).thenReturn(Attribute.FAILURE_MESSAGE);
        SmsCodeModel conversation = new SmsCodeModel();
        conversation.setUserCode("test1");
        conversation.setLastName("test");
        conversation.setPostcode("1111");
        AjaxResponse result = forgotPasswordController.forgetPasswordValidate(conversation);
        verify(mockCmsService, times(1)).getContent(ValidationErrorCode.FORGETPASSWORD_INVALID_USER_DETAILS);
        assertThat(result.isSuccess(), is(false));
    }

    @Test
    public void testForgetPasswordValidation_ForLockedUser() throws Exception {
        when(mockLogonService.validateUser(anyString(), anyString(), anyInt())).thenReturn(Attribute.ACCOUNT_LOCKED_MESSAGE);
        SmsCodeModel conversation = new SmsCodeModel();
        conversation.setUserCode("lockedUsername");
        conversation.setLastName("validLastname");
        conversation.setPostcode("1111");
        AjaxResponse result = forgotPasswordController.forgetPasswordValidate(conversation);
        verify(mockCmsService, times(1)).getContent(ValidationErrorCode.FORGETPASSWORD_ACCOUNT_LOCKED);
        assertThat(result.isSuccess(), is(false));
    }

    @Test
    public void testForgetPasswordVerifySms_InvalidSMSCode() throws Exception {
        SmsCodeModel conversation = new SmsCodeModel();
        conversation.setUserCode("valid");
        conversation.setLastName("valid");
        conversation.setPostcode("22222");
        conversation.setSmsCode("valid");
        //testing failure message
        String cmsMessage = "Any Valid message";
        when(mockLogonService.verifySmsCode("valid", "valid", 22222, "valid")).thenReturn(Attribute.FAILURE_MESSAGE);
        when(mockCmsService.getContent(anyString())).thenReturn(cmsMessage);
        BindingResult bindingResult = mock(BindingResult.class);
        AjaxResponse result;
        result = forgotPasswordController.forgetPasswordVerifySms(conversation, bindingResult);
        assertThat(result.isSuccess(), is(false));
        assertThat((String) result.getData(), is(cmsMessage));

        //testing error message
        when(mockLogonService.verifySmsCode("valid", "valid", 22222, "valid")).thenReturn(Attribute.ERROR_MESSAGE);
        result = forgotPasswordController.forgetPasswordVerifySms(conversation, bindingResult);
        assertThat(result.isSuccess(), is(false));
        assertThat((String) result.getData(), is(cmsMessage));

        //testing default message
        when(mockLogonService.verifySmsCode("valid", "valid", 22222, "valid")).thenReturn("default");
        result = forgotPasswordController.forgetPasswordVerifySms(conversation, bindingResult);
        assertThat(result.isSuccess(), is(false));
        assertThat((String) result.getData(), is(cmsMessage));
    }


    @Test
    public void testForgetPasswordVerifySmsResponse() throws Exception {
        when(mockLogonService.verifySmsCode("valid", "valid", 1111, "valid")).thenReturn(Attribute.SUCCESS_MESSAGE);
        when(mockCmsService.getContent(anyString())).thenReturn("Valid message");
        BindingResult bindingResult = mock(BindingResult.class);
        SmsCodeModel conversation = new SmsCodeModel();
        conversation.setUserCode("valid");
        conversation.setLastName("valid");
        conversation.setPostcode("1111");
        conversation.setSmsCode("valid");
        AjaxResponse result = forgotPasswordController.forgetPasswordVerifySms(conversation, bindingResult);
        assertThat(result.isSuccess(), is(true));
        assertNotNull(result.getData());
        assertThat((boolean) result.getData(), is(true));

        //Binding result has error.
        when(bindingResult.hasErrors()).thenReturn(true);
        List<FieldError> errors = new ArrayList<FieldError>();
        errors.add(new FieldError("error", "newPassword", "err00035"));
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(errors);
        result = forgotPasswordController.forgetPasswordVerifySms(conversation, bindingResult);
        assertThat(result.isSuccess(), is(false));
    }

    @Test
    public void testSetForgotPassword_WithResultErrors() throws Exception {
        Mockito.when(mockLogonService.updatePassword(any(UserReset.class), any(ServiceErrors.class)))
                .thenReturn(Attribute.SUCCESS_MESSAGE);

        UserProfile userProfile = getUserProfile();
        Mockito.when(profileService.getActiveProfile()).thenReturn(userProfile);
        UserReset userReset = new UserReset();
        userReset.setUserName("investor");
        userReset.setNewpassword("test1234");
        userReset.setConfirmPassword("test1234");
        userReset.setHalgm(new String(Base64.encodeBase64("test1234".getBytes())));
        BindingResult bindingResult = mock(BindingResult.class);
        AjaxResponse result = forgotPasswordController.setForgotPassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(true));

        //if binding result has error then response should be false
        when(bindingResult.hasErrors()).thenReturn(true);
        List<FieldError> errors = new ArrayList<FieldError>();
        errors.add(new FieldError("error", "newPassword", "err00026"));
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(errors);
        result = forgotPasswordController.setForgotPassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(false));
    }

    private UserProfile getUserProfile() {

        UserProfile userProfile = mock(UserProfile.class);
        when(userProfile.getClientKey()).thenReturn(ClientKey.valueOf("clientId"));
        return userProfile;
    }


    @Test
    public void testSetForgotPassword_FailStatus() throws Exception {
        UserProfile userProfile = getUserProfile();
        Mockito.when(profileService.getActiveProfile()).thenReturn(userProfile);
        UserReset userReset = new UserReset();
        userReset.setUserName("testuser");
        userReset.setNewpassword("test123");
        userReset.setConfirmPassword("test123");
        userReset.setHalgm(new String(Base64.encodeBase64("test1234".getBytes())));
        BindingResult bindingResult = mock(BindingResult.class);
        when(mockLogonService.updatePassword(any(UserReset.class), any(ServiceErrors.class))).thenReturn(Attribute.FAILURE_MESSAGE);
        when(mockCmsService.getContent(anyString())).thenReturn(CMS_CONTENT);
        AjaxResponse result;
        result = forgotPasswordController.setForgotPassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(false));
        assertThat(result.getData().toString(), is(CMS_CONTENT));
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testForgetPasswordValidate_IncorrectMethod() throws Exception {
        request.setRequestURI("/public/api/forgetPasswordValidate");
        request.setMethod(POST_METHOD);

        annotationMethodHandlerAdapter.handle(request, response, forgotPasswordController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }

    @Test
    public void testForgetPasswordValidateURL() throws Exception {

        HttpMessageConverter[] messageConverters =
                {
                        new MappingJackson2HttpMessageConverter()
                };
        annotationMethodHandlerAdapter.setMessageConverters(messageConverters);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/public/api/forgetPasswordValidate");
        request.setParameter("userCode", "validUsername");
        request.setParameter("lastName", "validLastname");
        request.setParameter("postcode", "1111");
        request.setMethod(GET_METHOD);
        when(mockLogonService.validateUser(anyString(), anyString(), anyInt())).thenReturn(Attribute.SUCCESS_MESSAGE);
        annotationMethodHandlerAdapter.handle(request, response, forgotPasswordController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }

}
