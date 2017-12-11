package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.security.api.model.RoleDto;
import com.bt.nextgen.core.security.api.model.RoleKey;

public interface RoleDtoService extends FindByKeyDtoService <RoleKey, RoleDto>, FindAllDtoService <RoleDto>
{

}
