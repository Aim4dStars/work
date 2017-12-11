package com.bt.nextgen.api.logon.controller;

import com.bt.nextgen.api.logon.model.LogonDtoKey;
import com.bt.nextgen.api.logon.model.LogonUpdatePasswordDto;
import com.bt.nextgen.api.logon.model.LogonUpdateUserNameDto;
import com.bt.nextgen.api.logon.service.LogonUpdatePasswordDtoService;
import com.bt.nextgen.api.logon.service.LogonUpdateUserNameDtoService;
import com.bt.nextgen.api.logon.validation.LogonDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.Update;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.prm.service.PrmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.bt.nextgen.core.exception.AccessDeniedException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This API re set the password for the user.
 * <p>
 * Sample request:
 * http://localhost:9080/ng/secure/api/v1_0/
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class LogonApiController {
    private static final Logger logger = LoggerFactory.getLogger(LogonApiController.class);

    @Autowired
    private LogonUpdatePasswordDtoService logonDtoService;
    @Autowired
    private LogonUpdateUserNameDtoService logonUpdateUserNameDtoService;
    @Autowired
    private LogonDtoErrorMapper logonDtoErrorMapper;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;
    @Autowired
    PrmService prmService;
    @Autowired
    private FeatureTogglesService featureTogglesService;

    @SuppressWarnings({"squid:S1166"})
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.UPDATE_PASSWORD)
    @PreAuthorize("isAuthenticated() and @twoFactorAuthenticationService.is2FAVerified('USER_DETAILS')")
    public
    @ResponseBody
    ApiResponse update(@RequestParam(value = "newPassword", required = true) String newPassword, @RequestParam(value = "currentPassword", required = true) String currentPassword,
                       @RequestParam(value = "halgm", required = true) String halgm
    ) throws Exception {
        if (!userProfileService.isEmulating()) {
            ServiceErrors serviceErrors = new ServiceErrorsImpl();
            LogonDtoKey logonDtoKey = new LogonDtoKey(userProfileService.getCredentialId(serviceErrors));
            LogonUpdatePasswordDto logonUpdatePasswordDto = new LogonUpdatePasswordDto(logonDtoKey, newPassword, currentPassword, halgm);
            ApiResponse response = new Update<>(ApiVersion.CURRENT_VERSION, logonDtoService, logonDtoErrorMapper, logonUpdatePasswordDto).performOperation();
            LogonUpdatePasswordDto logonUpdatePasswordDto1 = (LogonUpdatePasswordDto) response.getData();
            logger.info("Checking for feature toggling for PRM events : LogonApiController");
            if (logonUpdatePasswordDto1.isUpdateFlag() && featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.PRM_VIEW)) {
                prmService.triggerChgPwdPrmEvent(serviceErrors);
            }
            return response;
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.UPDATE_USERNAME)
    @PreAuthorize("isAuthenticated() and @twoFactorAuthenticationService.is2FAVerified('USER_DETAILS')")
    public
    @ResponseBody
    ApiResponse update(@RequestParam(value = "userName", required = true) String userName,
                       @RequestParam(value = "newUserName", required = true) String newUserName
    ) {
        if (!userProfileService.isEmulating()) {
            ServiceErrors serviceErrors = new ServiceErrorsImpl();
            LogonDtoKey logonDtoKey = new LogonDtoKey(userProfileService.getCredentialId(serviceErrors));
            LogonUpdateUserNameDto logonUpdateUserNameDto = new LogonUpdateUserNameDto();
            logonUpdateUserNameDto.setKey(logonDtoKey);
            logonUpdateUserNameDto.setUserName(userName);
            logonUpdateUserNameDto.setNewUserName(newUserName);
            return new Update<>(ApiVersion.CURRENT_VERSION, logonUpdateUserNameDtoService, logonDtoErrorMapper, logonUpdateUserNameDto).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

}
