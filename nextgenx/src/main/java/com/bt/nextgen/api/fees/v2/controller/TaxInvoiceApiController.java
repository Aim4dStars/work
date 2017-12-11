package com.bt.nextgen.api.fees.v2.controller;

import com.bt.nextgen.api.fees.v2.service.TaxInvoiceDtoService;
import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
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

import java.io.IOException;

@Controller
@RequestMapping(produces = "application/json")
public class TaxInvoiceApiController {

    @Autowired
    private TaxInvoiceDtoService taxInvoiceDtoService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.fees.v2.uri.account.pmf}")
    public @ResponseBody
    ApiResponse getAccountPortfolioManagementFees(
            @PathVariable("account-id") @ApiParam(value = "ID of account making the transfer", required = true) String accountId,
            @RequestParam(value = "start-date", required = true) @ApiParam(value = "start date of the recent month when fees applied - yyyy-mm-dd format", required = true) String startDateStr,
            @RequestParam(value = "end-date", required = true) @ApiParam(value = "end date of the recent month when fees applied - yyyy-mm-dd format", required = true) String endDateStr)
            throws IOException {

        DateTime startDate = new DateTime(startDateStr);
        DateTime endDate = new DateTime(endDateStr);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, taxInvoiceDtoService, new DateRangeAccountKey(accountId, startDate,
                endDate)).performOperation();
    }

}
