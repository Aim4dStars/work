package com.bt.nextgen.api.account.v1.controller.movemoney;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v1.model.BillerKey;
import com.bt.nextgen.api.account.v1.model.BpayBillerDto;
import com.bt.nextgen.api.account.v1.service.BPayBillerDtoService;
import com.bt.nextgen.api.account.v1.validation.BpayBillerDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.Validate;

/**
 * @deprecated Use V2
 */
@Deprecated
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class BPayBillerController {

    @Autowired
    @Qualifier("BPAYBillerDtoServiceV1")
    private BPayBillerDtoService bpayBillerDtoService;

    private BpayBillerDtoErrorMapper bpayErrorMapper;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.GET_BILLER_CODES)
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ApiResponse getBillerCodeList() {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, bpayBillerDtoService).performOperation();
    }

    /**
     * This API checks that the provided bpay biller code is a valid code, then returns the biller code and name
     * 
     * Twiki: <a href="http://dwgps0026/twiki/bin/view/NextGen/RESTBpayService">REST BPAY Service</a>
     * 
     * @param billerCode
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.BPAY_BILLER)
    @ResponseBody
    public ApiResponse getBiller(@PathVariable(UriMappingConstants.BILLER_CODE_URI_MAPPING) String billerCode) {
        return new Validate<>(ApiVersion.CURRENT_VERSION, bpayBillerDtoService, bpayErrorMapper,
                new BpayBillerDto(new BillerKey(billerCode))).performOperation();
    }
}
