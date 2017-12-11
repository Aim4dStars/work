package com.bt.nextgen.api.account.v1.controller;

import com.bt.nextgen.api.account.v1.model.BglDataDto;
import com.bt.nextgen.api.account.v1.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v1.service.BglDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
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
 * @deprecated Use V2
 */
@Deprecated
@Controller("BglApiControllerV1")
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class BglApiController
{
	@Autowired
	private BglDtoService bglDtoService;

	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_BGL_DOWNLOAD, produces = "application/xml")
	public void getBGLData(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = "start-date", required = false) String startDateStr,
            @RequestParam(value = "end-date", required = false) String endDateStr, HttpServletResponse response)
            throws IOException
	{
        DateTime startDate = new DateTime(startDateStr);
        DateTime endDate = new DateTime(endDateStr);
		DateRangeAccountKey key = new DateRangeAccountKey(accountId, startDate, endDate);

		KeyedApiResponse <DateRangeAccountKey> result = new FindByKey <>(ApiVersion.CURRENT_VERSION, bglDtoService, key).performOperation();
		BglDataDto data = (BglDataDto)result.getData(); //TODO the fact that we need to cast here indicates a failing in the api framework.

		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition",
			"attachment; filename=" + "Panorama BGL extract for period " + ApiFormatter.asFileDateFormat(startDate.toDate()) + " to "
				+ ApiFormatter.asFileDateFormat(endDate.toDate()) + ".xml");

		IOUtils.copy(data.getStream(), response.getOutputStream());

	}
}
