package com.bt.nextgen.core.security.api.controller;

import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.security.api.model.RoleKey;
import com.bt.nextgen.core.security.api.service.RoleDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class RoleApiController
{
	@Autowired
	private RoleDtoService roleService;

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ROLES)
	public @ResponseBody
	ApiResponse getRoles() throws Exception
	{
		return new FindAll <>(ApiVersion.CURRENT_VERSION, roleService).performOperation();
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ROLE)
	public @ResponseBody
	KeyedApiResponse <RoleKey> getRole(@PathVariable(UriMappingConstants.ROLE_ID_URI_MAPPING) String roleId) throws Exception
	{
		RoleKey key = new RoleKey(roleId);
		return new FindByKey <>(ApiVersion.CURRENT_VERSION, roleService, key).performOperation();
	}
}
