package com.bt.nextgen.api.account.v2.controller;

import com.bt.nextgen.api.account.v2.model.BglDataDto;
import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.service.BglDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.web.ApiFormatter;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Bgl is the api used to load download the BGL data for an account for a
 * specified date range
 * 
 */
@Deprecated
@Controller("BglApiControllerV2")
@RequestMapping(produces = "application/json")
public class BglApiController {
    @Autowired
    private BglDtoService bglDtoService;

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.bgl}", produces = "application/xml")
    public void getBGLData(@PathVariable("account-id") String accountId,
            @RequestParam(value = "start-date", required = false) String startDateStr,
            @RequestParam(value = "end-date", required = false) String endDateStr, HttpServletResponse response)
            throws IOException {

        DateTime startDate = new DateTime(startDateStr);
        DateTime endDate = new DateTime(endDateStr);
        DateRangeAccountKey key = new DateRangeAccountKey(accountId, startDate, endDate);

        KeyedApiResponse<DateRangeAccountKey> result = new FindByKey<>(ApiVersion.CURRENT_VERSION, bglDtoService, key)
                .performOperation();
        BglDataDto data = (BglDataDto) result.getData(); // TODO the fact that
                                                         // we need to cast here
                                                         // indicates a failing
                                                         // in the api
                                                         // framework.

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + "Panorama BGL extract for period " + ApiFormatter.asFileDateFormat(startDate.toDate())
                        + " to " + ApiFormatter.asFileDateFormat(endDate.toDate()) + ".xml");

        IOUtils.copy(data.getStream(), response.getOutputStream());

    }
}
