package com.bt.nextgen.api.accountassociates.controller;

import com.bt.nextgen.api.accountassociates.model.AccountAssociates;
import com.bt.nextgen.api.accountassociates.service.AccountAssociatesDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a service which retrieves the all the clients associated with account
 */

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AccountAssociatesApiController {

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_ASSOCIATES)
    public @ResponseBody ApiResponse getModEncryptedValue() {
        return new FindAll<>(ApiVersion.CURRENT_VERSION, accountAssociatesDtoService).performOperation();
    }

    @Autowired
    private AccountAssociatesDtoService accountAssociatesDtoService;
}
