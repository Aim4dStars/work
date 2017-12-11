package com.bt.nextgen.api.portfolio.v3.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ParameterisedDatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.allocation.AllocationBySectorDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;

@Controller("SectorAllocationApiControllerV3")
@RequestMapping(produces = "application/json")
public class SectorAllocationApiController {
    @Autowired
    private AllocationBySectorDtoService allocationService;

    @Value("${environment}")
    private String env;

    private static final Logger logger = LoggerFactory.getLogger(SectorAllocationApiController.class);

    @RequestMapping(method = RequestMethod.GET, value = "${api.portfolio.v3.uri.allocationSector}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    KeyedApiResponse<DatedValuationKey> getAssetAllocationBySectorDetails(@PathVariable("account-id") String accountId,
            @RequestParam(value = "effective-date", required = false) String dateString,
            @RequestParam(value = "include-external", required = false) Boolean includeExternal,
            @RequestParam(value = "cache", defaultValue = "false") String useCache,
            HttpServletRequest request) {
        DatedValuationKey key;
        DateTime effectiveDate;
        if (dateString != null) {
            effectiveDate = new DateTime(dateString);
        } else {
            effectiveDate = DateMidnight.now().toDateTime();
        }

        HashMap<String, String> parameters = new HashMap<>();

        if (StringUtils.isNotEmpty(useCache) && useCache.equalsIgnoreCase("true"))
        {
            parameters.put("serviceType", "cache");
        }

        key = new ParameterisedDatedValuationKey(accountId, effectiveDate, includeExternal, parameters);
        KeyedApiResponse<DatedValuationKey> keyedApiResponse = new FindByKey<>(ApiVersion.CURRENT_VERSION,
                allocationService, key).performOperation();
        //Logger for debugging performance issue.
        if(org.apache.commons.lang.StringUtils.isNotEmpty(env) && "dev".equalsIgnoreCase(env)){
            logger.info("SectorAllocationApiController Request URI::{}", request.getRequestURI());
            logger.info("SectorAllocationApiController Request Params:: accountId={};effective-date={}:include-external={}:use-cache{}",
                    accountId , effectiveDate, dateString, includeExternal, useCache);
            logger.info("SectorAllocationApiController Response ::{}", keyedApiResponse.getData().toString());
        } else {
            logger.debug("SectorAllocationApiController Request URI::{}", request.getRequestURI());
            logger.debug("SectorAllocationApiController Request Params:: accountId={};effective-date={}:include-external={}:use-cache{}",
                    accountId , effectiveDate, dateString, includeExternal, useCache);
            logger.debug("SectorAllocationApiController Response ::{}", keyedApiResponse.getData().toString());
        }
        return keyedApiResponse;
    }

}
