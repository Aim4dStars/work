package com.bt.nextgen.api.holdingbreach.v1.controller;

import com.bt.nextgen.api.holdingbreach.v1.service.HoldingBreachDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("HoldingBreachApiControllerV1")
@RequestMapping(produces = "application/json")
public class HoldingBreachApiController {
    @Autowired
    private HoldingBreachDtoService holdingBreachDtoService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.holdingbreach.v1.uri.breaches}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_reports')")
    public @ResponseBody ApiResponse getHoldingBreaches() {
        return new FindOne<>(ApiVersion.CURRENT_VERSION, holdingBreachDtoService).performOperation();
    }
}
