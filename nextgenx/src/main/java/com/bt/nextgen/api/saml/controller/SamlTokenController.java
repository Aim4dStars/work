package com.bt.nextgen.api.saml.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.saml.service.SamlTokenRefreshDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;


@Controller
@RequestMapping(value = {UriMappingConstants.CURRENT_VERSION_API, UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API}, produces = MediaType.APPLICATION_JSON_VALUE)
public class SamlTokenController {

    public static final String IV_SERVER_NAME = "iv_server_name";
    @Autowired
    private SamlTokenRefreshDtoService samlTokenRefreshDtoService;

    /**
     * Force a refresh of the user's SAML token
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.SAML_TOKEN_REFRESH)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse refreshSamlToken(HttpServletRequest request) {
        final String websealAppServerId = request.getHeader(IV_SERVER_NAME);
        return new ApiResponse(ApiVersion.CURRENT_VERSION, samlTokenRefreshDtoService.refreshSamlToken(websealAppServerId, new ServiceErrorsImpl()));
    }
}
