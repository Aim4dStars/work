package com.bt.nextgen.api.portfolio.v3.controller;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.movement.ValuationMovementDto;
import com.bt.nextgen.api.portfolio.v3.service.ValuationMovementDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("ValuationMovementApiControllerV3")
@RequestMapping(produces = "application/json")
@Api(description = "Load the valuation movements for an account")
public class ValuationMovementApiController {
    @Autowired
    private ValuationMovementDtoService valuationMovementDtoService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.portfolio.v3.uri.movements}", produces = "application/json")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    @ApiOperation(value = "Find valuation movement", response = ValuationMovementDto.class)
    public @ResponseBody
    ApiResponse getValuationMovement(
            @PathVariable("account-id") @ApiParam(value = "Encoded Account ID to return movements for", required = true) String accountId,
            @RequestParam(value = "start-date", required = false) @ApiParam(value = "Optional - Account open date is used if not provided", example = "2016-01-31", required = false) String startDateStr,
            @RequestParam(value = "end-date", required = false) @ApiParam(value = "Optional - Current date is used if not provided", example = "2016-01-31", required = false) String endDateStr) {

        DateTime startDate = new DateTime(startDateStr);
        DateTime endDate = new DateTime(endDateStr);
        DateRangeAccountKey key = new DateRangeAccountKey(accountId, startDate, endDate);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, valuationMovementDtoService, key).performOperation();
    }
}
