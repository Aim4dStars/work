package com.bt.nextgen.api.option.v1.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.option.v1.service.AccountOptionDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(value="Provides services to support account options")
@Controller("AccountOptionsApiControllerV1")
public class AccountOptionsApiController {

    @Autowired
    private AccountOptionDtoService optionService;

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value="Retrieve options for a specific account ID")
    @RequestMapping(method = RequestMethod.GET, value = "${api.option.v1.uri.account}")
    public @ResponseBody ApiResponse getOptions(@PathVariable("account-id") @ApiParam(value="Encoded ID of account to load options for", required = true) String accountId) {
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, optionService, new AccountKey(accountId)).performOperation();
    }
}
