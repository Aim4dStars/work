package com.bt.nextgen.api.portfolio.v3.controller;

import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.allocation.AllocationByExposureDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
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

@Controller("ExposureAllocationApiControllerV3")
@RequestMapping(produces = "application/json")
public class ExposureAllocationApiController {

    @Autowired
    private AllocationByExposureDtoService allocationService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.portfolio.v3.uri.allocationExposure}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    KeyedApiResponse<DatedValuationKey> getAssetAllocationByExposureDetails(@PathVariable("account-id") String accountId,
            @RequestParam(value = "effective-date", required = false) String dateString,
            @RequestParam(value = "include-external", required = false) Boolean includeExternal) {
        DatedValuationKey key;
        DateTime effectiveDate;
        if (dateString != null) {
            effectiveDate = new DateTime(dateString);
        } else {
            effectiveDate = new DateTime();
        }

        key = new DatedValuationKey(accountId, effectiveDate, includeExternal);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, allocationService, key).performOperation();
    }

}
