package com.bt.nextgen.api.account.v1.controller;

import com.bt.nextgen.api.account.v1.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v1.service.ValuationMovementDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
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
 * @deprecated Use V2
 */
@Deprecated
@Controller("ValuationMovementApiControllerV1")
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class ValuationMovementApiController {
    @Autowired
    private ValuationMovementDtoService valuationMovementDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_MOVEMENTS, produces = "application/json")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    ApiResponse getValuationMovement(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(value = "start-date", required = false) String startDateStr,
            @RequestParam(value = "end-date", required = false) String endDateStr) {
        DateTime startDate = new DateTime(startDateStr);
        DateTime endDate = new DateTime(endDateStr);
        DateRangeAccountKey key = new DateRangeAccountKey(accountId, startDate, endDate);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, valuationMovementDtoService, key).performOperation();
    }
}
