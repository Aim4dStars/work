package com.bt.nextgen.api.drawdown.v2.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.api.drawdown.v2.service.DrawdownDetailsDtoService;
import com.bt.nextgen.api.drawdown.v2.service.DrawdownDtoService;
import com.bt.nextgen.api.drawdown.v2.validation.DrawdownErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller("DrawdownApiControllerV2")
@RequestMapping(produces = "application/json")
public class DrawdownApiController {

    @Autowired
    private DrawdownDtoService drawdownDtoService;

    @Autowired
    private DrawdownDetailsDtoService drawdownDetailsService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private DrawdownErrorMapper drawdownErrorMapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.drawdown.v2.uri.retrieval}")
    public @ResponseBody
    KeyedApiResponse<AccountKey> getDrawdown(@PathVariable("account-id") String accountId) {
        AccountKey key = new AccountKey(accountId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, drawdownDtoService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.drawdown.v2.uri.update}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.details.update')")
    public @ResponseBody
    KeyedApiResponse<AccountKey> updateDrawdownStrategy(@PathVariable("account-id") String accountId,
            @RequestParam(value = "drawdown", required = true) String drawdown) {
        if (!profileService.isEmulating()) {
            AccountKey key = new AccountKey(accountId);
            DrawdownDetailsDto drawdownDto = new DrawdownDetailsDto(key, drawdown, null);
            return new Update<>(ApiVersion.CURRENT_VERSION, drawdownDtoService, null, drawdownDto).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.drawdown.v2.uri.assetpriority}", produces = "application/json")
    @ApiOperation(value = "Retrieve drawdown option based on asset-priority for the specified account.", response = DrawdownDetailsDto.class)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody
    KeyedApiResponse<AccountKey> getPriorityDrawdown(
            @PathVariable("account-id") @ApiParam(value = "Encoded ID of account to load received contributions for", required = true) String accountId) {
        AccountKey key = new AccountKey(accountId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, drawdownDetailsService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.drawdown.v2.uri.assetpriority.submit}", produces = "application/json")
    @ApiOperation(value = "Validate or submit asset priority list for the specified account.", response = DrawdownDetailsDto.class)
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody
    KeyedApiResponse<AccountKey> createPriorityDrawdown(
            @PathVariable("account-id") @ApiParam(value = "Encoded ID of account to to create asset-priority based drawdown", required = true) String accountId,
            @RequestParam(value = "x-ro-validate-only", required = false) @ApiParam(value = "Flag to indicate if the data is for validation only", required = true) String validateOnly,
            @ModelAttribute("drawdownPriority") @ApiParam(value = "DrawdownDetailsDtoV2 in model attribute format", required = true) DrawdownDetailsDto ddDto)
            throws IOException {

        ddDto.setKey(new AccountKey(accountId));
        if (!profileService.isEmulating()) {
            if ("true".equals(validateOnly)) {
                return new Validate<>(ApiVersion.CURRENT_VERSION, drawdownDetailsService, drawdownErrorMapper, ddDto)
                        .performOperation();
            } else {
                return new Submit<>(ApiVersion.CURRENT_VERSION, drawdownDetailsService, drawdownErrorMapper, ddDto)
                        .performOperation();
            }
        }
        throw new AccessDeniedException("Access Denied");
    }

    @InitBinder("drawdownPriority")
    public void drawdownPriorityInitBinder(WebDataBinder binder) {
        binder.setAllowedFields("drawdownType", "key.accountId", "priorityDrawdownList", "priorityDrawdownList[*].assetId",
                "priorityDrawdownList[*].assetName", "priorityDrawdownList[*].assetCode", "priorityDrawdownList[*].status",
                "priorityDrawdownList[*].assetType", "priorityDrawdownList[*].drawdownPriority", "warnings[*]*");
    }
}
