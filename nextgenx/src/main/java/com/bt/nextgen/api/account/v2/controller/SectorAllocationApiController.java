package com.bt.nextgen.api.account.v2.controller;

import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.api.account.v2.model.ParameterisedDatedValuationKey;
import com.bt.nextgen.api.account.v2.service.allocation.AllocationBySectorDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Deprecated
@Controller("SectorAllocationApiControllerV2")
@RequestMapping(produces = "application/json")
public class SectorAllocationApiController {
    @Autowired
    private AllocationBySectorDtoService allocationService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.allocationSector}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    KeyedApiResponse<DatedValuationKey> getAssetAllocationBySectorDetails(@PathVariable("account-id") String accountId,
            @RequestParam(value = "effective-date", required = false) String dateString,
            @RequestParam(value = "include-external", required = false) Boolean includeExternal,
            @RequestParam(value = "cache", defaultValue = "false") String useCache) {
        DatedValuationKey key;
        DateTime effectiveDate;
        if (dateString != null) {
            effectiveDate = new DateTime(dateString);
        } else {
            effectiveDate = new DateTime();
        }

        HashMap<String, String> parameters = new HashMap<>();

        if (StringUtils.isNotEmpty(useCache) && "true".equalsIgnoreCase(useCache))
        {
            parameters.put("serviceType", "cache");
        }

        key = new ParameterisedDatedValuationKey(accountId, effectiveDate, includeExternal,parameters);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, allocationService, key).performOperation();
    }

}
