package com.bt.nextgen.api.morningstar.service;

import com.bt.nextgen.api.morningstar.model.MorningstarUrlDto;
import com.bt.nextgen.api.morningstar.model.MorningstarUrlKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface MorningstarUrlDtoService extends
		FindByKeyDtoService<MorningstarUrlKey, MorningstarUrlDto> {
}
