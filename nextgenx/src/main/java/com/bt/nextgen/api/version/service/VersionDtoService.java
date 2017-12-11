package com.bt.nextgen.api.version.service;

import com.bt.nextgen.api.version.model.ModuleKey;
import com.bt.nextgen.api.version.model.ModuleVersionDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface VersionDtoService extends FindByKeyDtoService<ModuleKey, ModuleVersionDto>, FindAllDtoService<ModuleVersionDto> {
}
