package com.bt.nextgen.api.draftaccount.controller;

import com.bt.nextgen.api.draftaccount.model.JsonSchemaEnumsDto;
import com.bt.nextgen.api.draftaccount.service.JsonSchemaHelperService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by M040398 on 23/08/2016.
 */
@Controller
@RequestMapping(value = {
        UriMappingConstants.CURRENT_VERSION_API + UriMappingConstants.SCHEMA_ENUMS,
        UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API + UriMappingConstants.SCHEMA_ENUMS
} , produces = MediaType.APPLICATION_JSON_VALUE)
public class JsonSchemaApiController {

    @Autowired
    private JsonSchemaHelperService schemaHelper;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getSchemaEnums() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return new ApiResponse(ApiVersion.CURRENT_VERSION, schemaHelper.getJsonSchemaEnums());
    }
}
