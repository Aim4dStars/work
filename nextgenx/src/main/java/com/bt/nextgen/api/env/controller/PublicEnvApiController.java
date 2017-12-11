package com.bt.nextgen.api.env.controller;

import com.bt.nextgen.api.env.service.EnvironmentDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 * User: L069552
 * Date: 22/01/16
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = { UriMappingConstants.CURRENT_VERSION_API_PUBLIC}, produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicEnvApiController
{

    @Autowired
    private EnvironmentDtoService environmentService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ENV)
    public @ResponseBody
    ApiResponse getEnvDetailsForUnauthenticated() {
        return new FindOne<>(ApiVersion.CURRENT_VERSION, environmentService).performOperation();
    }

}
