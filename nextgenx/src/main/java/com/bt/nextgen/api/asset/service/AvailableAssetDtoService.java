package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.dto.FilterableDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

public interface AvailableAssetDtoService extends SearchByCriteriaDtoService<AssetDto>, FilterableDtoService<AssetDto> {
}
