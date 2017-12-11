package com.bt.nextgen.api.draftaccount.controller;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = MediaType.APPLICATION_JSON_VALUE)
public class SimulateClientApplicationApiController {

    public static final String SIMULATE_CLIENT_APPLICATION_URL = "/simulateClientApplication";

    @Autowired
    private ClientApplicationDtoService clientApplicationDtoService;




    @RequestMapping(method = RequestMethod.GET, value = SIMULATE_CLIENT_APPLICATION_URL)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse simulateDraftAccount(
            @RequestParam("copyClientApplicationId") Long draftAccountId){

        return new ApiResponse(ApiVersion.CURRENT_VERSION, clientApplicationDtoService.simulateDraftAccount(new ClientApplicationKey(draftAccountId), new ServiceErrorsImpl()));
    }



}
