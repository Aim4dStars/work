package com.bt.nextgen.api.portfolio.v3.controller;

import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.valuation.CacheableValuationDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.CacheableFindByKey;
import com.bt.nextgen.core.api.operation.FindByKey;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller("ValuationApiControllerV3")
@RequestMapping(produces = "application/json")
public class ValuationApiController {
    @Autowired
    @Qualifier("ValuationDtoServiceV3")
    private CacheableValuationDtoService valuationService;

    @Value("${environment}")
    private String env;

    private static final Logger logger = LoggerFactory.getLogger(ValuationApiController.class);

    @RequestMapping(method = RequestMethod.GET, value = "${api.portfolio.v3.uri.valuation}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody KeyedApiResponse<DatedValuationKey> getValuation(@PathVariable("account-id") String accountId,
            @RequestParam(value = "effective-date", required = false) String dateString,
            @RequestParam(value = "include-external", required = false) Boolean includeExternal,
            @RequestParam(value = "use-cache", required = false) Boolean useCache,
            @RequestParam(value = "clear-cache", required = false) Boolean clearCache,
            HttpServletRequest request) {
        DateTime effectiveDate;
        KeyedApiResponse<DatedValuationKey> keyedApiResponse;
        if (dateString != null) {
            effectiveDate = new DateTime(dateString);
        } else {
            effectiveDate = DateMidnight.now().toDateTime();
        }

        DatedValuationKey key = new DatedValuationKey(accountId, effectiveDate, includeExternal);

        if (Boolean.TRUE.equals(useCache)) {
           keyedApiResponse =  new CacheableFindByKey<>(ApiVersion.CURRENT_VERSION, valuationService, key, Boolean.TRUE.equals(clearCache))
                    .performOperation();
        } else {
            keyedApiResponse = new FindByKey<>(ApiVersion.CURRENT_VERSION, valuationService, key).performOperation();
        }

        //Logger for debugging performance issue.
        if(StringUtils.isNotEmpty(env) && "dev".equalsIgnoreCase(env)){
            logger.info("ValueApiController Request URI::{}", request.getRequestURI());
            logger.info("ValueApiController Request Params:: accountId={};effective-date={}:include-external={}:use-cache{}:clear-cache={}",
                    accountId , effectiveDate, dateString, includeExternal, useCache, clearCache);
            logger.info("ValueApiController Response ::{}", keyedApiResponse.getData().toString());
        } else {
            logger.debug("ValueApiController Request URI::{}", request.getRequestURI());
            logger.debug("ValueApiController Request Params:: accountId={};effective-date={}:include-external={}:use-cache{}:clear-cache={}",
                    accountId , effectiveDate, dateString, includeExternal, useCache, clearCache);
            logger.debug("ValueApiController Response ::{}", keyedApiResponse.getData().toString());
        }
        return keyedApiResponse;
    }
}
