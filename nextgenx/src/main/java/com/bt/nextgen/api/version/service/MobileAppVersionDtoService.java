package com.bt.nextgen.api.version.service;

import com.bt.nextgen.api.version.model.MobileAppVersionDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.core.domain.key.StringIdKey;

public interface MobileAppVersionDtoService extends FindByKeyDtoService<StringIdKey, MobileAppVersionDto>,
        FindAllDtoService<MobileAppVersionDto>, UpdateDtoService<StringIdKey, MobileAppVersionDto> {
}
