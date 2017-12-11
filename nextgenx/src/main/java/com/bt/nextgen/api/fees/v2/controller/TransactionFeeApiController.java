package com.bt.nextgen.api.fees.v2.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.fees.v2.service.TransactionFeeDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("TransactionFeeApiControllerV2")
@RequestMapping(produces = "application/json")
public class TransactionFeeApiController {

    @Autowired
    private TransactionFeeDtoService transactionFeeDtoService;

    @Value("${api.fees.v2.version}")
    private String version;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.fees.v2.uri.account.transactionfees}")
    public @ResponseBody ApiResponse getAccountTransactionFees(@PathVariable("account-id") String accountId) {

        return new FindByKey<>(version, transactionFeeDtoService, new AccountKey(accountId)).performOperation();
    }
}
