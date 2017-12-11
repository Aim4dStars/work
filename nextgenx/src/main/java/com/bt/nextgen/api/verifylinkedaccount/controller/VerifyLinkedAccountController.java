package com.bt.nextgen.api.verifylinkedaccount.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.verifylinkedaccount.model.LinkedAccountDetailsDto;
import com.bt.nextgen.api.verifylinkedaccount.service.VerifyLinkedDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.InitBinder;



import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;

/**
 * Created by l078480 on 22/08/2017.
 */

@Controller
@RequestMapping(produces = "application/json")
public class VerifyLinkedAccountController {

     @Autowired
    VerifyLinkedDtoService verifyLinkedDtoService;

    @InitBinder("linkedAccountsDtoModel")
    public void linkedAccountsDtoModelBinder(WebDataBinder binder) {
        binder.setAllowedFields("accountNumber", "bsb", "verificationCode", "verificationAction");
    }



    @RequestMapping(method = RequestMethod.POST, value = "${api.verifylinkedaccount.v1.uri.verifyLinkedAccount}")
    @PreAuthorize("@acctPermissionService.canTransact(#accountId, 'account.payee.view')")
    public @ResponseBody
    KeyedApiResponse<AccountKey> verifyLinkedAccount(@PathVariable("account-id") String accountId,
                                                     @ModelAttribute("linkedAccountsDtoModel") LinkedAccountDetailsDto linkedAccountsDto) {
        linkedAccountsDto.setKey(new AccountKey(accountId));
        return new Submit<>(ApiVersion.CURRENT_VERSION,verifyLinkedDtoService,new ErrorMapper() {

            @Override
            public List<DomainApiErrorDto> map(List<ValidationError> errors) {
                return new ArrayList<DomainApiErrorDto>();
            }
        },linkedAccountsDto).performOperation();



    }





}
