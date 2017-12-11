package com.bt.nextgen.lifecentral.web.controller;

import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.util.ApplicationProperties;
import com.bt.nextgen.lifecentral.model.LifecentralDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by M041926 on 5/08/2016.
 */
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class LifecentralController {

    private static final String LIFECENTRAL_URL = "lifecentral.url";

    @Autowired
    private ApplicationProperties applicationProperties;

    public LifecentralController() {}

    public LifecentralController(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/lifecentral")
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody ApiResponse lifeCentralUrl(@RequestParam(value = "lcplus", required = false, defaultValue = "false") boolean lcplus) {
        LifecentralDto dto = new LifecentralDto();

        String lifeCentralUrl = applicationProperties.get(LIFECENTRAL_URL);
        String eamLifeCentralUrl = "/eam/servlet/getjwt?partner=btlc";

        if (lcplus) {
            lifeCentralUrl += "?target=/tools/quoteClientLauncher.htm";
            eamLifeCentralUrl += "&target=/tools/quoteClientLauncher.htm";
        }

        dto.setLifeCentralUrl(lifeCentralUrl);
        dto.setEamLifeCentralUrl(eamLifeCentralUrl);

        return new ApiResponse(ApiVersion.CURRENT_VERSION, dto);
    }
}
