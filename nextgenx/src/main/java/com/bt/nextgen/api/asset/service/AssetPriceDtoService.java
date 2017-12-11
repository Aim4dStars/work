package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.AssetPriceDto;
import com.bt.nextgen.api.asset.model.AssetPriceDtoKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

public interface AssetPriceDtoService extends FindByKeyDtoService<AssetPriceDtoKey, AssetPriceDto>,
		SearchByCriteriaDtoService<AssetPriceDto> {
}
