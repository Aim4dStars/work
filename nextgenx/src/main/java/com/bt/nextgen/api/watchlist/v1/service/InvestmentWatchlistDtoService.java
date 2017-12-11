package com.bt.nextgen.api.watchlist.v1.service;

import com.bt.nextgen.api.watchlist.v1.model.InvestmentWatchlistDto;
import com.bt.nextgen.api.watchlist.v1.model.InvestmentWatchlistKey;
import com.bt.nextgen.api.watchlist.v1.service.InvestmentWatchlistDtoServiceImpl.AddAssetCodesPartialUpdate;
import com.bt.nextgen.api.watchlist.v1.service.InvestmentWatchlistDtoServiceImpl.RemoveAssetCodesPartialUpdate;
import com.bt.nextgen.core.api.dto.CreateDtoService;
import com.bt.nextgen.core.api.dto.DeleteDtoService;
import com.bt.nextgen.core.api.dto.FindAllDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.PartialUpdateDtoService;

public interface InvestmentWatchlistDtoService
        extends FindAllDtoService<InvestmentWatchlistDto>, FindByKeyDtoService<InvestmentWatchlistKey, InvestmentWatchlistDto>,
        CreateDtoService<InvestmentWatchlistKey, InvestmentWatchlistDto>,
        DeleteDtoService<InvestmentWatchlistKey, InvestmentWatchlistDto>,
        PartialUpdateDtoService<InvestmentWatchlistKey, InvestmentWatchlistDto> {

    AddAssetCodesPartialUpdate getAddAssetCodesPartialUpdateService();

    RemoveAssetCodesPartialUpdate getRemoveAssetCodesPartialUpdateService();

}