package com.bt.nextgen.api.advisermodel.v1.controller;

import com.bt.nextgen.api.advisermodel.v1.service.AdviserModelDtoService;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindOne;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * AdviserModelApiController provides to the UI information required when creating adviser models
 */
@Controller("AdviserModelApiControllerV1")
@RequestMapping(produces = "application/json")
@Api(description = "Provide parameters used to create and maintain adviser models")
public class AdviserModelApiController {

    @Autowired
    private AdviserModelDtoService adviserModelDtoService;

    // TODO: Update with adviser model permission when available
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.advisermodel.v1.uri.cashasset}")
    @ApiOperation(value = "Load adviser model cash asset", response = AssetDto.class, responseContainer = "List")
    public @ResponseBody
    ApiResponse getCashAsset() {
        return new FindOne<>(ApiVersion.CURRENT_VERSION, adviserModelDtoService).performOperation();
    }
}
