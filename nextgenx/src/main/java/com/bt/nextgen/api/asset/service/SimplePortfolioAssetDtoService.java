package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import org.springframework.stereotype.Service;

@Service
public interface SimplePortfolioAssetDtoService extends FindAllDtoService<ManagedPortfolioAssetDto> {
}
