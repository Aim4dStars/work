package com.bt.nextgen.api.draftaccount.controller;

import com.bt.nextgen.api.draftaccount.service.HoldingApplicationDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = MediaType.APPLICATION_JSON_VALUE)
public class HoldingApplicationController {

    @Autowired
    private HoldingApplicationDtoService holdingApplicationDtoService;

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.DRAFT_ACCOUNTS + UriMappingConstants.HOLDING_APP + "/{accountId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getHoldingApplicationDetails(@PathVariable String accountId) {
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, holdingApplicationDtoService, AccountKey.valueOf(EncodedString.toPlainText(accountId))).performOperation();
    }
}
