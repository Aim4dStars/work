package com.bt.nextgen.api.portfolio.v3.controller;

import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.cashmovements.CashMovementsDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * AvailableCashController is used to display the available cash for the order
 * 
 * @author L061041
 * 
 */
@Controller("CashMovementApiControllerV3")
@RequestMapping(produces = "application/json")
public class CashMovementApiController {
    @Autowired
    private CashMovementsDtoService dtoService;

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.portfolio.v3.uri.cashMovements}")
    public @ResponseBody ApiResponse getCashMovements(@PathVariable("account-id") String accountId,
          @RequestParam(value = "effective-date", required = false) String dateString) {
        DateTime effectiveDate;
        if (dateString != null) {
            effectiveDate = new DateTime(dateString);
        } else {
            effectiveDate = new LocalDate().toDateTimeAtStartOfDay();
        }

        DatedValuationKey key = new DatedValuationKey(accountId, effectiveDate, false);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, dtoService, key).performOperation();
    }
}