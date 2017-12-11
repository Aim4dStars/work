package com.bt.nextgen.api.movemoney.v2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.service.DepositDtoService;
import com.bt.nextgen.api.movemoney.v2.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.Create;
import com.bt.nextgen.core.api.operation.SearchByKey;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.api.operation.Validate;

@Controller("DepositsApiControllerV2")
@RequestMapping(produces = "application/json")
public class DepositsApiController {

    @Autowired
    private DepositDtoService depositDtoService;

    @Autowired
    private MovemoneyDtoErrorMapper movemoneytDtoErrorMapper;

    @Value("${api.movemoney.v2.version}")
    private String version;

    @RequestMapping(method = RequestMethod.GET, value = "${api.movemoney.v2.uri.depositPayees}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.report.view')")
    public @ResponseBody ApiResponse getPayeesForAccount(@PathVariable("account-id") String accountId) {
        return new SearchByKey<>(version, depositDtoService, new AccountKey(accountId)).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.confirmDeposit}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.deposit.linked.create')")
    public @ResponseBody KeyedApiResponse<AccountKey> confirmDeposit(@PathVariable("account-id") String accountId,
            @RequestBody DepositDto depositDto) {

        depositDto.setKey(new AccountKey(accountId));
        return new Validate<>(version, depositDtoService, movemoneytDtoErrorMapper, depositDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.submitDeposit}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.deposit.linked.create')")
    public @ResponseBody KeyedApiResponse<AccountKey> submitDeposit(@PathVariable("account-id") String accountId,
            @RequestBody DepositDto depositDto) {

        depositDto.setKey(new AccountKey(accountId));
        return new Submit<>(version, depositDtoService, movemoneytDtoErrorMapper, depositDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.saveDeposit}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.deposit.linked.create')")
    public @ResponseBody KeyedApiResponse<AccountKey> saveDeposit(@PathVariable("account-id") String accountId,
            @RequestBody DepositDto depositDto) {

        depositDto.setKey(new AccountKey(accountId));
        return new Create<>(version, depositDtoService, depositDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v2.uri.updateDeposit}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.deposit.linked.create')")
    public @ResponseBody KeyedApiResponse<AccountKey> updateDeposit(@PathVariable("account-id") String accountId,
            @RequestBody DepositDto depositDto) {

        depositDto.setKey(new AccountKey(accountId));
        return new Update<>(version, depositDtoService, movemoneytDtoErrorMapper, depositDto).performOperation();
    }
}
