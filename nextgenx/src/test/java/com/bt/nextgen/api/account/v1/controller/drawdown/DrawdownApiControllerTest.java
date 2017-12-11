package com.bt.nextgen.api.account.v1.controller.drawdown;

import static org.junit.Assert.*;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import com.bt.nextgen.api.account.v1.model.DrawdownDto;
import com.bt.nextgen.api.account.v1.service.drawdown.DrawdownDtoService;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.security.api.model.PermissionAccountKey;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * @deprecated Use V2
 */
@Deprecated
@RunWith(MockitoJUnitRunner.class)
public class DrawdownApiControllerTest {
    @InjectMocks
    private DrawdownApiController drawdownApiController;

    @Mock
    private DrawdownDtoService drawdownDtoService;

    @Mock
    private PermissionAccountDtoService permissionAccountDtoService;

    private MockHttpServletRequest mockHttpServletRequest;
    private MockHttpServletResponse mockHttpServletResponse;

    @Mock
    private static AnnotationMethodHandlerAdapter annotationMethodHandler;

    @Mock
    private UserProfileService profileService;

    @Mock
    private PermissionsDto permissionsDto;

    @Before
    public void setUp() throws Exception {

        mockHttpServletRequest = new MockHttpServletRequest(RequestMethod.GET.name(), "/");
        mockHttpServletResponse = new MockHttpServletResponse();
        annotationMethodHandler = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = { new MappingJackson2HttpMessageConverter() };
        annotationMethodHandler.setMessageConverters(messageConverters);

    }

    @Test
    public final void testGetDrawdown() throws Exception {
        DrawdownDto drawdownDto = new DrawdownDto(new AccountKey("accountId"), "prorata");
        Mockito.when(drawdownDtoService.find(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                drawdownDto);
        mockHttpServletRequest
                .setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/drawdown");
        mockHttpServletRequest.setMethod("GET");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, drawdownApiController);
    }

    @Test
    public final void testUpdate() throws Exception {
        DrawdownDto drawdownDto = new DrawdownDto(new AccountKey("accountId"), "prorata");
        Mockito.when(profileService.isEmulating()).thenReturn(false);
        Mockito.when(drawdownDtoService.update(Mockito.any(DrawdownDto.class), Mockito.any(ServiceErrors.class))).thenReturn(
                drawdownDto);
        mockHttpServletRequest.setParameter(UriMappingConstants.DRAWDOWN_URI_MAPPING, "drawdown");
        mockHttpServletRequest
                .setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/update/drawdown");
        mockHttpServletRequest.setMethod("POST");
        annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, drawdownApiController);
    }

    @Test
    public final void testUpdateAccessDeniedException() {
        Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(false);
        Mockito.when(profileService.isEmulating()).thenReturn(false);
        Mockito.when(permissionAccountDtoService.find(Mockito.any(PermissionAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(permissionsDto);
        DrawdownDto drawdownDto = new DrawdownDto(new AccountKey("accountId"), "prorata");
        Mockito.when(drawdownDtoService.update(Mockito.any(DrawdownDto.class), Mockito.any(ServiceErrors.class))).thenReturn(
                drawdownDto);
        mockHttpServletRequest.setParameter(UriMappingConstants.DRAWDOWN_URI_MAPPING, "drawdown");
        mockHttpServletRequest
                .setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/update/drawdown");
        mockHttpServletRequest.setMethod("POST");

        try {
            annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, drawdownApiController);
        } catch (AccessDeniedException e1) {
            assert (true);
        } catch (Exception e) {
            fail("Access Denied Exception Not thrown");
        }
    }

    @Test
    public final void testUpdateAccessDeniedWhenEmulation() {
        Mockito.when(permissionsDto.hasPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileService.isEmulating()).thenReturn(true);
        Mockito.when(permissionAccountDtoService.find(Mockito.any(PermissionAccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(permissionsDto);
        DrawdownDto drawdownDto = new DrawdownDto(new AccountKey("accountId"), "prorata");
        Mockito.when(drawdownDtoService.update(Mockito.any(DrawdownDto.class), Mockito.any(ServiceErrors.class))).thenReturn(
                drawdownDto);
        mockHttpServletRequest.setParameter(UriMappingConstants.DRAWDOWN_URI_MAPPING, "drawdown");
        mockHttpServletRequest
                .setRequestURI("/secure/api/v1_0/accounts/C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C/update/drawdown");
        mockHttpServletRequest.setMethod("POST");

        try {
            annotationMethodHandler.handle(mockHttpServletRequest, mockHttpServletResponse, drawdownApiController);
        } catch (AccessDeniedException e1) {
            assert (true);
        } catch (Exception e) {
            fail("Access Denied Exception Not thrown");
        }
    }

}
