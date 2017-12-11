package com.bt.nextgen.api.registration.controller;

import com.bt.nextgen.api.registration.model.UserRoleDto;
import com.bt.nextgen.api.registration.service.UserRoleDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.Submit;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class UserRoleApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleApiController.class);

    @Autowired
    private UserRoleDtoService userRoleDtoService;

    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ACCEPT_TERMS_AND_CONDITIONS)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    ApiResponse getAccountApplicationStatus(@PathVariable(UriMappingConstants.JOB_PROFILE_ID_URI_MAPPING) String jobProfileId,
                                            HttpServletRequest request, HttpServletResponse response) {
        UserRoleDto userRoleTncDto = new UserRoleDto();
        userRoleTncDto.setAccepted("Y");
        userRoleTncDto.setJobProfileId(EncodedString.toPlainText(jobProfileId));
        userRoleTncDto.setVersion("1");
        return new Submit<>(ApiVersion.CURRENT_VERSION, userRoleDtoService, null, userRoleTncDto).performOperation();
    }
}
