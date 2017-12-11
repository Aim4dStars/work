package com.bt.nextgen.api.anzsiccodes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.draftaccount.service.ANZSICCodeDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;

@Controller
@RequestMapping(value = { UriMappingConstants.CURRENT_VERSION_API, UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API }, produces = MediaType.APPLICATION_JSON_VALUE)
public class AnzsicCodesApiController {

    @Autowired
    private ANZSICCodeDtoService anzsicCodeDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ANZSIC_CODES)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getANZSICCodes() {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, anzsicCodeDtoService).performOperation();
    }


}
