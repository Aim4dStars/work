package com.bt.nextgen.api.movemoney.v3.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v3.model.DepositDto;
import com.bt.nextgen.api.movemoney.v3.model.DepositKey;
import com.bt.nextgen.api.movemoney.v3.model.RecurringDepositKey;
import com.bt.nextgen.api.movemoney.v3.service.DepositDtoService;
import com.bt.nextgen.api.movemoney.v3.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.Create;
import com.bt.nextgen.core.api.operation.Delete;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.service.integration.movemoney.OrderType;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller("DepositsApiControllerv3")
@RequestMapping(produces = "application/json")
public class DepositsApiController {

    @Autowired
    private DepositDtoService depositDtoService;

    @Autowired
    private MovemoneyDtoErrorMapper movemoneytDtoErrorMapper;

    @Autowired
    private UserProfileService profileService;

    @Value("${api.movemoney.v3.version}")
    private String version;

    @RequestMapping(method = RequestMethod.GET, value = "${api.movemoney.v3.uri.deposits}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.report.view')")
    public @ResponseBody ApiResponse getAccountDeposits(
            @RequestParam(value = "search-criteria", required = false) String searchCriteria,
            @PathVariable("account-id") String accountId) {
        List<ApiSearchCriteria> criteria = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, searchCriteria);
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));

        return new SearchByCriteria<>(version, depositDtoService, criteria).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v3.uri.confirmDeposit}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.deposit.linked.create')")
    public @ResponseBody KeyedApiResponse<DepositKey> confirmDeposit(@PathVariable("account-id") String accountId,
            @RequestBody DepositDto depositDto) {

        depositDto.setAccountKey(new AccountKey(accountId));
        return new Validate<>(version, depositDtoService, movemoneytDtoErrorMapper, depositDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v3.uri.submitDeposit}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.deposit.linked.create')")
    public @ResponseBody KeyedApiResponse<DepositKey> submitDeposit(@PathVariable("account-id") String accountId,
            @RequestBody DepositDto depositDto) {

        depositDto.setAccountKey(new AccountKey(accountId));
        return new Submit<>(version, depositDtoService, movemoneytDtoErrorMapper, depositDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v3.uri.saveDeposit}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.deposit.linked.create')")
    public @ResponseBody KeyedApiResponse<DepositKey> saveDeposit(@PathVariable("account-id") String accountId,
            @RequestBody DepositDto depositDto) {

        depositDto.setAccountKey(new AccountKey(accountId));
        return new Create<>(version, depositDtoService, depositDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v3.uri.updateDeposit}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.deposit.linked.create')")
    public @ResponseBody KeyedApiResponse<DepositKey> updateDeposit(@PathVariable("account-id") String accountId,
            @RequestBody DepositDto depositDto) {

        depositDto.setAccountKey(new AccountKey(accountId));
        return new Update<>(version, depositDtoService, movemoneytDtoErrorMapper, depositDto).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.movemoney.v3.uri.deleteDeposit}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.deposit.linked.create')")
    public @ResponseBody ApiResponse deleteDeposit(@RequestParam(value = "contributionType") String contributionType,
            @PathVariable("account-id") String accountId, @PathVariable("deposit-id") String depositId) {
        if (!profileService.isEmulating()) {
            if (OrderType.SUPER_ONE_OFF_CONTRIBUTION.getName().equals(contributionType)) {
                DepositKey key = new DepositKey(depositId);
                return new Delete<>(ApiVersion.CURRENT_VERSION, depositDtoService, key).performOperation();
            } else if (OrderType.SUPER_RECURRING_CONTRIBUTION.getName().equals(contributionType)) {
                RecurringDepositKey key = new RecurringDepositKey(depositId);
                return new Delete<>(ApiVersion.CURRENT_VERSION, depositDtoService, key).performOperation();
            }
            throw new BadRequestException(ApiVersion.CURRENT_VERSION,
                    "Unable to delete the contribution due to unknown contribution type: " + contributionType);
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }
}
