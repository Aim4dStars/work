package com.bt.nextgen.api.logon.controller;

import com.bt.nextgen.api.logon.model.LogonUpdatePasswordDto;
import com.bt.nextgen.api.logon.model.LogonUpdateUserNameDto;
import com.bt.nextgen.api.logon.service.LogonUpdatePasswordDtoService;
import com.bt.nextgen.api.logon.service.LogonUpdateUserNameDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.prm.service.PrmService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LogonApiControllerTest {

    @InjectMocks
    private LogonApiController logonApiController;
    @Mock
    private LogonUpdatePasswordDtoService logonUpdatePasswordDtoService;
    @Mock
    private LogonUpdateUserNameDtoService logonUpdateUserNameDtoService;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    PrmService prmService;
    @Mock
    private FeatureTogglesService featureTogglesService;
    @Mock
    private LogonUpdatePasswordDto updatePasswordDto;
    @Mock
    private LogonUpdateUserNameDto updateUserNameDto;
    @Mock
    private FeatureToggles featureToggles;


    private SamlToken samlToken;

    @Before
    public void setup() {
        when(userProfileService.getCredentialId(any(ServiceErrors.class))).thenReturn("1234");
        when(logonUpdatePasswordDtoService.update(any(LogonUpdatePasswordDto.class),
                any(ServiceErrorsImpl.class))).thenReturn(updatePasswordDto);
        when(logonUpdateUserNameDtoService.update(any(LogonUpdateUserNameDto.class),
                any(ServiceErrorsImpl.class))).thenReturn(updateUserNameDto);
        when(featureTogglesService.findOne(any(FailFastErrorsImpl.class))).thenReturn(featureToggles);
    }

    @Test
    public void testUpdatePasswordUpdatedAndPRM() throws Exception {
        when(updatePasswordDto.isUpdateFlag()).thenReturn(true);
        when(featureToggles.getFeatureToggle(FeatureToggles.PRM_VIEW)).thenReturn(true);
        ApiResponse response = logonApiController.update("passwd1234", "passwd4421", "halgm");
        verify(prmService).triggerChgPwdPrmEvent(any(ServiceErrors.class));
        verify(logonUpdatePasswordDtoService).update(any(LogonUpdatePasswordDto.class), any(ServiceErrors.class));
    }

    @Test
    public void testUpdatePasswordUpdatedAndNoPRM() throws Exception {
        when(updatePasswordDto.isUpdateFlag()).thenReturn(true);
        when(featureToggles.getFeatureToggle(FeatureToggles.PRM_VIEW)).thenReturn(false);
        ApiResponse response = logonApiController.update("passwd1234", "passwd4421", "halgm");
        verify(prmService, never()).triggerChgPwdPrmEvent(any(ServiceErrors.class));
        verify(logonUpdatePasswordDtoService).update(any(LogonUpdatePasswordDto.class), any(ServiceErrors.class));
    }

    @Test
    public void testUpdatePasswordNotUpdated() throws Exception {
        when(updatePasswordDto.isUpdateFlag()).thenReturn(false);
        when(featureToggles.getFeatureToggle(FeatureToggles.PRM_VIEW)).thenReturn(true);
        ApiResponse response = logonApiController.update("passwd1234", "passwd4421", "halgm");
        verify(prmService, never()).triggerChgPwdPrmEvent(any(ServiceErrors.class));
        verify(logonUpdatePasswordDtoService).update(any(LogonUpdatePasswordDto.class), any(ServiceErrors.class));
    }

    @Test
    public void testUpdatePasswordNotUpdatedAndNoPRM() throws Exception {
        when(updatePasswordDto.isUpdateFlag()).thenReturn(false);
        when(featureToggles.getFeatureToggle(FeatureToggles.PRM_VIEW)).thenReturn(false);
        ApiResponse response = logonApiController.update("passwd1234", "passwd4421", "halgm");
        verify(prmService, never()).triggerChgPwdPrmEvent(any(ServiceErrors.class));
        verify(logonUpdatePasswordDtoService).update(any(LogonUpdatePasswordDto.class), any(ServiceErrors.class));
    }

    @Test
    public void testUpdateUsername() throws Exception {
        when(updateUserNameDto.isUpdateFlag()).thenReturn(true);
        when(featureToggles.getFeatureToggle(FeatureToggles.PRM_VIEW)).thenReturn(true);
        ApiResponse response = logonApiController.update("UserName", "UserName");
        verify(logonUpdateUserNameDtoService).update(any(LogonUpdateUserNameDto.class), any(ServiceErrors.class));
    }

    @Test (expected = AccessDeniedException.class)
    public  void testUpdateUsernameWithEmulation() throws Exception {
        when(userProfileService.isEmulating()).thenReturn(true);
        ApiResponse response = logonApiController.update("UserName", "UserName");

    }

    @Test (expected = AccessDeniedException.class)
    public  void testUpdatePasswordWithEmulation() throws Exception {
        when(userProfileService.isEmulating()).thenReturn(true);
        ApiResponse response = logonApiController.update("passwd1234", "passwd4421", "halgm");

    }
}
