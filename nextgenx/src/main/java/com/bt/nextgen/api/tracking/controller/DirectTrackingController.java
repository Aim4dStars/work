package com.bt.nextgen.api.tracking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.tracking.service.DirectTrackingDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;

@Controller
@RequestMapping(value = { UriMappingConstants.CURRENT_VERSION_API, UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API }, produces = MediaType.APPLICATION_JSON_VALUE)
public class DirectTrackingController {

    @Autowired
    private DirectTrackingDtoService directTrackingDtoService;

    @RequestMapping(method = RequestMethod.GET, value =  "tracking/direct")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getApplicationStatus(@RequestParam("applicationid") String applicationId) {
        ClientApplicationKey key = new ClientApplicationKey(Long.valueOf(applicationId));
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, directTrackingDtoService, key).performOperation();
    }
}