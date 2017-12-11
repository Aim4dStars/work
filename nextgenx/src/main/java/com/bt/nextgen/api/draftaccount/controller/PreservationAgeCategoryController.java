package com.bt.nextgen.api.draftaccount.controller;

import com.bt.nextgen.api.draftaccount.service.PreservationAgeService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import static com.bt.nextgen.core.api.UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by F058391 on 15/06/2016.
 */

@Controller
@RequestMapping(value = { UriMappingConstants.CURRENT_VERSION_API,CURRENT_DIRECT_ONBOARDING_VERSION_API}, produces = MediaType.APPLICATION_JSON_VALUE)
public class PreservationAgeCategoryController {

    @Autowired
    private PreservationAgeService preservationAgeService;

    @RequestMapping(method = RequestMethod.GET, value = "/client_application/preservation_age")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getPreservationAge(){

        return new FindAll<>(ApiVersion.CURRENT_VERSION, preservationAgeService).performOperation();
    }

}
