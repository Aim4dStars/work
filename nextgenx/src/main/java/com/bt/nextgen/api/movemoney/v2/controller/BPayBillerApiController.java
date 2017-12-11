package com.bt.nextgen.api.movemoney.v2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v3.validation.BpayBillerDtoErrorMapper;
import com.bt.nextgen.api.movemoney.v2.model.BillerKey;
import com.bt.nextgen.api.movemoney.v2.model.BpayBillerDto;
import com.bt.nextgen.api.movemoney.v2.service.BPayBillerDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.Validate;

@Controller("BPayBillerApiControllerV2")
@RequestMapping(produces = "application/json")
public class BPayBillerApiController {

    @Autowired
    private BPayBillerDtoService bpayBillerDtoService;

    private BpayBillerDtoErrorMapper bpayErrorMapper;

    @Value("${api.movemoney.v2.version}")
    private String version;

    @RequestMapping(method = RequestMethod.GET, value = "${api.movemoney.v2.uri.bpayBillerCodes}")
    @ResponseBody
    @PreAuthorize("isAuthenticated()")
    public ApiResponse getBillerCodeList() {
        return new FindAll<>(version, bpayBillerDtoService).performOperation();
    }

    /**
     * This API checks that the provided bpay biller code is a valid code, then returns the biller code and name
     * 
     * Twiki: <a href="http://dwgps0026/twiki/bin/view/NextGen/RESTBpayService">REST BPAY Service</a>
     * 
     * @param billerCode
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "${api.movemoney.v2.uri.bpayBiller}")
    @ResponseBody
    public ApiResponse getBiller(@PathVariable("${api.movemoney.v2.param.billerId}") String billerCode) {
        return new Validate<>(version, bpayBillerDtoService, bpayErrorMapper, new BpayBillerDto(new BillerKey(billerCode)))
                .performOperation();
    }
}
