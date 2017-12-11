package com.bt.nextgen.api.account.v2.controller;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.ValuationMovementDto;
import com.bt.nextgen.api.account.v2.service.ValuationMovementDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @deprecated Use V3
 */
@Deprecated
@Controller("ValuationMovementApiControllerV2")
@RequestMapping(produces = "application/json")
@Api(description = "Load the valuation movements for an account")
public class ValuationMovementApiController {
    @Autowired
    private ValuationMovementDtoService valuationMovementDtoService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.movements}", produces = "application/json")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    @ApiOperation(value = "Find valuation movement", response = ValuationMovementDto.class)
    @Deprecated
    public @ResponseBody
    ApiResponse getValuationMovement(@PathVariable("account-id") String accountId,
            @RequestParam(value = "start-date", required = false) String startDateStr,
            @RequestParam(value = "end-date", required = false) String endDateStr) {

        DateTime startDate = new DateTime(startDateStr);
        DateTime endDate = new DateTime(endDateStr);
        DateRangeAccountKey key = new DateRangeAccountKey(accountId, startDate, endDate);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, valuationMovementDtoService, key).performOperation();
    }
}
