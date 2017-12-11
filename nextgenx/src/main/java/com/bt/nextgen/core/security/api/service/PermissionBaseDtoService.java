package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.core.api.dto.FindOneDtoService;
import com.bt.nextgen.core.security.api.model.PermissionsDto;

public interface PermissionBaseDtoService extends FindOneDtoService<PermissionsDto>
{
    boolean hasBasicPermission(String permission);
    boolean hasProductPermission(String permission);
}
