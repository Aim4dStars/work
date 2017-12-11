package com.bt.nextgen.api.cashratehistory.controller;

import com.bt.nextgen.api.cashratehistory.service.CashRateHistoryDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;

/**
 * Created by L072457 on 30/12/2014.
 */
@Controller
@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CURRENT_VERSION_API + "/history", produces = "application/json")
public class CashRateHistoryApiController {

    @Autowired
    private CashRateHistoryDtoService cashRateHistoryDtoService;


    @RequestMapping(method = RequestMethod.GET, value = "/cashRates", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    ApiResponse loadCashRates() {
              //To get the cash rates from CMS - uncomment the following line.
              //return new ApiResponse(ApiVersion.CURRENT_VERSION, new ResultListDto<>(cashRateHistoryDtoService.getCashRates(servletContext.getRealPath(""))));
              //To get cash rates from Avaloq - uncomment the following line.
                return new ApiResponse(ApiVersion.CURRENT_VERSION, new ResultListDto<>(cashRateHistoryDtoService.loadCashRates()));
    }

}
