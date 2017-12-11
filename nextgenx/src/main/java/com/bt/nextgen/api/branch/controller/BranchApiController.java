package com.bt.nextgen.api.branch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.branch.model.BranchKey;
import com.bt.nextgen.api.branch.service.BranchDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;


@Controller
@RequestMapping(value = { UriMappingConstants.CURRENT_VERSION_API, UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API }, produces = MediaType.APPLICATION_JSON_VALUE)
public class BranchApiController {

    @Autowired
    BranchDtoService branchDtoService;

    @RequestMapping(method = RequestMethod.GET, value = "/branches/{bsb}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody public KeyedApiResponse<BranchKey> getBranch(@PathVariable String bsb)
    {
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, branchDtoService, new BranchKey(bsb)).performOperation();
    }

}
