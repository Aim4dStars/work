package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.security.api.model.PermissionAccountKey;
import com.bt.nextgen.core.security.api.model.PermissionsDto;

public interface PermissionAccountDtoService extends FindByKeyDtoService<PermissionAccountKey, PermissionsDto>
{
    public boolean canTransact(String portfolioId, String authority);
}
