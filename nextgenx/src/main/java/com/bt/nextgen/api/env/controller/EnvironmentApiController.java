package com.bt.nextgen.api.env.controller;

import com.bt.nextgen.api.env.service.EnvironmentDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindOne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a environment service which retrieves the environment on the application and host URL of the AEM CMS.
 */

@Controller
@RequestMapping(value = {UriMappingConstants.CURRENT_VERSION_API, UriMappingConstants
        .CURRENT_DIRECT_ONBOARDING_VERSION_API}, produces = MediaType.APPLICATION_JSON_VALUE)
public class EnvironmentApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentApiController.class);
    @Autowired
    private EnvironmentDtoService environmentService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ENV)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getEnvironmentDetails(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("Start extracting the environment details.");
        return new FindOne<>(ApiVersion.CURRENT_VERSION, environmentService).performOperation();
    }
}
