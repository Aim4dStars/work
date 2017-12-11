package com.bt.nextgen.core.security.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.FindOne;
import com.bt.nextgen.core.security.api.model.PermissionAccountKey;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.bt.nextgen.core.security.api.service.PermissionBaseDtoService;

import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = { UriMappingConstants.CURRENT_VERSION_API, UriMappingConstants.CURRENT_DIRECT_ONBOARDING_VERSION_API }, produces = MediaType.APPLICATION_JSON_VALUE)
public class PermissionApiController
{
    @Autowired
    private PermissionBaseDtoService permissionBaseService;

    @Autowired
    private PermissionAccountDtoService permissionAccountService;

    @PreAuthorize("isAuthenticated()")
	@RequestMapping(method = GET, value = UriMappingConstants.PERMISSION)
    public @ResponseBody ApiResponse getBasePermission()
    {
        return new FindOne<>(CURRENT_VERSION, permissionBaseService).performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = GET, value = UriMappingConstants.ACCOUNT_PERMISSION)
    public @ResponseBody ApiResponse getAccountPermission(
        @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId)
    {
        return new FindByKey<>(CURRENT_VERSION, permissionAccountService, new PermissionAccountKey(accountId)).performOperation();
    }
}
