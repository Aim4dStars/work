package com.bt.nextgen.web.controller;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.web.RequestQuery;
import com.bt.nextgen.login.web.model.PasswordResetModel;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.bt.nextgen.core.util.SETTINGS.SECURITY_BRAND_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_DEFAULT_BRAND;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HALGM_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_JS_OBFUSCATION_URL_DEV;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_JS_OBFUSCATION_URL_PRD;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_PASSWORD_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_USERNAME_PARAM;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link ParentAuthenticationController}
 */
@RunWith(MockitoJUnitRunner.class)
public class ParentAuthenticationControllerTest {

    @InjectMocks
    private ParentAuthenticationController parentAuthenticationController;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private CmsService cmsService;
    @Mock
    private RequestQuery requestQuery;
    @Mock
    private MockHttpServletRequest request;
    @Mock
    private MockHttpServletResponse response;

    @Before
    public void setup() {
        when(request.getHeaderNames()).thenReturn(new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public String nextElement() {
                return null;
            }
        });
    }

    @Test
    public void testcheckForNoBindingErrorTypeOnField() throws Exception {
        List<FieldError> listError = new ArrayList<>();
        FieldError error = new FieldError("X", "Y", "Z");
        listError.add(error);
        when(bindingResult.getFieldErrors()).thenReturn(listError);
        when(cmsService.getContent(anyString())).thenReturn("Testing for checkForBindingErrorOnField");
        List<Object> list = parentAuthenticationController.checkForBindingErrorOnField(bindingResult, "payeeModel");
        Assert.assertTrue(list.size() == 0);
    }

    @Test
    public void testcheckForBindingErrorOnField() throws Exception {
        List<FieldError> listError = new ArrayList<>();
        FieldError error = new FieldError("payeeDetails", "payeeModel", "Testing for checkForBindingErrorOnField");
        listError.add(error);
        when(bindingResult.getFieldErrors()).thenReturn(listError);
        when(cmsService.getContent(anyString())).thenReturn("Testing for checkForBindingErrorOnField");
        List<Object> list = parentAuthenticationController.checkForBindingErrorOnField(bindingResult, "payeeModel");
        Assert.assertTrue(list.size() > 0);
        Assert.assertEquals(((ValidationError) list.get(0)).getMessage(), "Testing for checkForBindingErrorOnField");
    }

    @Test
    public void testcheckForBindingErrorExcludingField() throws Exception {
        List<FieldError> listError = new ArrayList<>();
        FieldError error = new FieldError("payeeDetails", "payeeModel32", "Testing");
        listError.add(error);
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(listError);
        Mockito.when(cmsService.getContent(anyString())).thenReturn("Testing in ForBindingErrorExcludingField()");
        List list = parentAuthenticationController.checkForBindingErrorExcludingField(bindingResult, "payeeModel");
        Assert.assertTrue(list.size() > 0);
        Assert.assertEquals(((ValidationError) list.get(0)).getMessage(), "Testing in ForBindingErrorExcludingField()");
    }

    @Test
    public void testPrepareModelForDev() throws Exception {
        when(requestQuery.isWebSealRequest()).thenReturn(false);
        ModelMap mock = mock(ModelMap.class);
        parentAuthenticationController.prepareModel(mock);
        verify(mock, times(1)).addAttribute(eq(Attribute.PASSWORD_RESET_MODEL), any(PasswordResetModel.class));
        verify(mock, times(1)).addAttribute(eq(Attribute.REGISTRATION_MODEL), any(RegistrationModel.class));
        verify(mock, times(1)).addAttribute(eq(Attribute.LOGON_BRAND), eq(SECURITY_DEFAULT_BRAND.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.OBFUSCATION_URL), eq(SECURITY_JS_OBFUSCATION_URL_DEV.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.PASSWORD_FIELD_NAME), eq(SECURITY_PASSWORD_PARAM.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.USERNAME_FIELD_NAME), eq(SECURITY_USERNAME_PARAM.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.BRAND_FIELD_NAME), eq(SECURITY_BRAND_PARAM.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.HALGM_FIELD_NAME), eq(SECURITY_HALGM_PARAM.value()));
    }

    @Test
    public void testPrepareModelForWebseal() throws Exception {
        when(requestQuery.isWebSealRequest()).thenReturn(true);
        ModelMap mock = mock(ModelMap.class);
        parentAuthenticationController.prepareModel(mock);
        verify(mock, times(1)).addAttribute(eq(Attribute.PASSWORD_RESET_MODEL), any(PasswordResetModel.class));
        verify(mock, times(1)).addAttribute(eq(Attribute.REGISTRATION_MODEL), any(RegistrationModel.class));
        verify(mock, times(1)).addAttribute(eq(Attribute.LOGON_BRAND), eq(SECURITY_DEFAULT_BRAND.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.OBFUSCATION_URL), eq(SECURITY_JS_OBFUSCATION_URL_PRD.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.PASSWORD_FIELD_NAME), eq(SECURITY_PASSWORD_PARAM.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.USERNAME_FIELD_NAME), eq(SECURITY_USERNAME_PARAM.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.BRAND_FIELD_NAME), eq(SECURITY_BRAND_PARAM.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.HALGM_FIELD_NAME), eq(SECURITY_HALGM_PARAM.value()));
    }

    @Test
    public void testPrepareModelWithErrorMessage() throws Exception {
        when(requestQuery.isWebSealRequest()).thenReturn(false);
        when(request.getCookies()).thenReturn(new Cookie[]{});
        ModelMap mock = mock(ModelMap.class);
        parentAuthenticationController.prepareModelWithErrorMessage(request, response, mock, "Error Msg");
        verify(mock, times(1)).addAttribute(eq("message"), eq("Error Msg"));
        verify(mock, times(1)).addAttribute(eq("messageType"), eq("ERROR"));
    }

    @Test
    public void testAddErrorMessageWithNoError() throws Exception {
        when(requestQuery.isWebSealRequest()).thenReturn(false);
        when(request.getCookies()).thenReturn(new Cookie[]{});
        ModelMap mock = mock(ModelMap.class);
        parentAuthenticationController.addErrorMessage(request, response, mock, null);
        verify(mock, never()).addAttribute(anyString(), anyObject());
    }

    @Test
    public void testAddErrorMessageWithError() throws Exception {
        when(requestQuery.isWebSealRequest()).thenReturn(false);
        when(request.getCookies()).thenReturn(new Cookie[]{});
        ModelMap mock = mock(ModelMap.class);
        parentAuthenticationController.addErrorMessage(request, response, mock, "Error Msg");
        verify(mock, times(1)).addAttribute(eq("message"), eq("Error Msg"));
        verify(mock, times(1)).addAttribute(eq("messageType"), eq("ERROR"));
    }

    @Test
    public void testAddErrorMessageWithBadRequest() throws Exception {
        Cookie cookie = new Cookie("process_timer_LOGON", URLEncoder.encode("invalid\n", "UTF-8"));
        when(requestQuery.isWebSealRequest()).thenReturn(false);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        ModelMap mock = mock(ModelMap.class);
        parentAuthenticationController.addErrorMessage(request, response, mock, "Error Msg");
        verify(mock, times(1)).addAttribute(eq("message"), eq("Error Msg"));
        verify(mock, times(1)).addAttribute(eq("messageType"), eq("ERROR"));
    }

}
